package model.worldCreation;

import model.Model;
import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyFactory;
import model.buildings.PropertyTracker;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Character;
import model.characters.*;
import model.characters.authority.Authority;
import model.characters.authority.ProvinceAuthority;
import model.characters.npc.*;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Shop;
import model.stateSystem.MessageTracker;
import model.stateSystem.State;
import model.war.Military;
import model.war.War;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static model.worldCreation.Nation.TaxCalculator.getNationalTaxAmount;

@SuppressWarnings("CallToPrintStackTrace")
public class Nation extends ControlledArea implements Details {
    private Province[] provinces;
    private final Continent continent;
    private final Shop shop;
    protected LinkedList<Quarter> allQuarters;
    public int numberOfQuarters;
    private final Set<Person> slaverGuild = new HashSet<>();
    private final Set<Person> freedomFighters = new HashSet<>();
    private final Set<Area> claimedAreas = new HashSet<>();
    private final Wallet wallet = new Wallet(authorityHere);
    private boolean isAtWar = false;
    private Nation enemy = null; // Enemy is only set when in war, enemy is the opponent.
    private final HashSet<Person> warCommanders = new HashSet<>();
    private Nation overlord; // Overlord is set if nation loses a war
    private final HashSet<Nation> vassals = new HashSet<>(); // Vassals are added if war is won
    private final ArrayList<War.WarNotes> warHistory = new ArrayList<>(); // War history collects final war report from all the wars

    private boolean nobleWarBonus = false; // noble war bonus gives momentarily boost for all nations militaries
    private boolean warTax = false; // war tax can be set at phase2 of the war to automatically tax all work wallets

    private War war;


    public Nation(String name, Continent continent, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.propertyTracker = new PropertyTracker();
        this.allQuarters = new LinkedList<>();
        this.nation = this;
        this.authorityHere = authority;
        this.createProvinces();
        this.shop = new Shop();
        for (Province province : provinces) {
            authority.setSubordinate(province.authorityHere);
        }
        authority.setSupervisor(authority);

    }
    @Override
    public String getDetails() {

        return null;
    }





    private void createProvinces() {
        Random random = Settings.getRandom();
        int numberOfProvinces = random.nextInt(Settings.getInt("provinceAmountMax")) + Settings.getInt("provinceAmountMin");
        provinces = new Province[numberOfProvinces];

        for (int i = 0; i < numberOfProvinces; i++) {

            // generates name for the province
            String provinceName = NameCreation.generateProvinceName();

            // generates governor
            Governor governor = governorFactory(provinceName);

            //assigns the governor as the authority of the province
            Authority authority = new ProvinceAuthority(governor);

            // new province is created
            Province province = new Province(provinceName, this, authority);

            // add into claimed area
            this.addClaimedArea(province);

            // authority position should know the area its controlling
            authority.setAreaUnderAuthority(province);

            provinces[i] = province;

            // set home for governor. Governor must live in his home province.
            setGovernorHome(random, province, governor);

            // generate mercenaries for governor
            mercenaryFactory(province, authority, random);

            // add sentinels as allies at the beginning
            // for now, sentinels remain sentinels for the authorityPosition, even if the person positioned there changes.
            RelationsManager relations = governor.getPerson().getRelationsManager();
            relations.getListOfSentinels().forEach(relations::addAlly);

        }
    }


    private Governor governorFactory(String provinceName) {
        Governor governor = new Governor(authorityHere);
        TransferPackage startingPackage = new TransferPackage(400, 800, 1200);
        governor.getPerson().getWallet().addResources(startingPackage);
        governor.getRole().setAuthority(getAuthorityHere());
        governor.getRole().setNation(nation);
        Property property = PropertyFactory.createProperty(provinceName, "Province", governor.getPerson());
        propertyTracker.addProperty(property);
        return governor;
    }

    private void setGovernorHome(Random random, Province province, Governor governor) {
        while (true) {
            int homeIndex = random.nextInt(nation.getAllQuarters().size());
            Quarter home = nation.getAllQuarters().get(homeIndex);
            if (home.getHigher().getHigher().equals(province)) {
                home.addCitizen(Status.Governor, governor.getPerson());
                NameCreation.generateMajorQuarterName(home);
                governor.getPerson().getProperty().setLocation(home);
                break;
            }
        }
    }
    public Wallet getWallet() {
        return wallet;
    }

    public Set<Person> getSlaverGuild() {
        return slaverGuild;
    }
    public void joinSlaverGuild(Person person){
        if(person.getAiEngine().getProfile().containsKey(Trait.Liberal) && !person.isPlayer()){
            return;
        }
        if(!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) return;
        slaverGuild.add(person);
        person.getMessageTracker().addMessage(MessageTracker.Message("Minor", "Joined Slaver Guild"));
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).addBonus("Slaver Guild Bonus", 1);
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).updatePaymentManager(person.getPaymentManager());
    }
    public void joinLiberalGuild(Person person){
        if(person.getAiEngine().getProfile().containsKey(Trait.Slaver) && !person.isPlayer()){
            return;
        }
        if(!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) return;
        freedomFighters.add(person);
        person.getMessageTracker().addMessage(MessageTracker.Message("Minor", "Joined Liberal Guild"));
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.WorkerCenter).addBonus("Liberal Guild Bonus", 1);
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.WorkerCenter).updatePaymentManager(person.getPaymentManager());
    }

    public Set<Person> getFreedomFighters() {
        return freedomFighters;
    }


    private void mercenaryFactory(Province province, Authority authority, Random random) {
        int amountOfCities = province.getContents().size();

        for (int merc = 0; merc < amountOfCities; merc++) {
            Mercenary mercenary = new Mercenary(authority);
            mercenary.getRole().setNation(nation);
            authority.addSupporter(mercenary);


            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            mercenary.getPerson().getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(mercenary.getPerson().getProperty());
            quarter.addCitizen(Status.Mercenary,mercenary.getPerson());
            NameCreation.generateMajorQuarterName(quarter);


            mercenaryBonus(mercenary);

        }
    }

    private static void mercenaryBonus(Mercenary mercenary) {
        mercenary.getPerson().getCombatStats().increaseOffence(3);
        mercenary.getPerson().getCombatStats().increaseDefence(3);
        mercenary.getPerson().getProperty().getUtilitySlot().addRandomUtilityBuilding(mercenary);
        TransferPackage bonus = new TransferPackage(500,5000,500);
        mercenary.getPerson().getWallet().addResources(bonus);
    }

    @Override
    protected void updateCitizenCache() {
        List<Character> characters = new ArrayList<>();
        for (Province province : provinces) {
            for (City city : province.getCities()) {
                for (Quarter quarter : city.getQuarters()) {
                    characters.addAll(quarter.getImportantCharacters());
                }
            }
        }
        List<Status> statusOrder = getImportantStatusRank();
        citizenCache = characters.stream()
                .filter(character -> statusOrder.contains(character.getRole().getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getRole().getStatus())))
                .collect(Collectors.toList());
    }

    private Set<Character> getAllImportantCharacters() {
        Iterable<? extends Person> characters = getAllCitizensOfTheNation();

        List<Status> statusOrder = getImportantStatusRank();

        return StreamSupport.stream(characters.spliterator(), false)
                .filter(person -> statusOrder.contains(person.getRole().getStatus()))
                .sorted(Comparator.comparingInt(person -> statusOrder.indexOf(person.getRole().getStatus())))
                .map(Person::getCharacter)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void nationalTax(int day){

        if(day == 1) {

            for (Character c : getAllCitizens()) {

                TransferPackage amount = getNationalTaxAmount(c);
                c.getPerson().getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.NATIONAL_TAX, amount ,30);
            }

        } else if (day == 30) {

            for (Character c : getAllCitizens()) {
                if(this.wallet.deposit(c.getPerson().getWallet(), getNationalTaxAmount(c))){
                    c.getPerson().getMessageTracker().addMessage(MessageTracker.Message("Minor", "National tax paid: " + getNationalTaxAmount(c).toShortString()));
                }else{
                    c.getPerson().loseStrike("National tax not paid: " + getNationalTaxAmount(c).toShortString());
                }
                c.getPerson().getPaymentManager().removePayment(PaymentManager.PaymentType.EXPENSE, Payment.NATIONAL_TAX);
            }
        }

    }




    public War getWar() {
        return war;
    }

    public void setOverlordToWorkWallets(Nation nation){
        for(Character c : getAllCitizens()){
            c.getPerson().getWorkWallet().setOverlord(nation);
        }
    }
    public void removeOverlordFromWorkWallets(){
        for(Character c : getAllCitizens()){
            c.getPerson().getWorkWallet().setOverlord(null);
        }
    }

    private final HashSet<Character> allCitizens = new HashSet<>();

    private HashSet<Character> getAllCitizens(){
        if(allCitizens.isEmpty()) { // Calculate this only once, since characters should never change.
            for (Quarter quarter : allQuarters) {
                allCitizens.addAll(quarter.getCharactersLivingHere());
            }
        }
        return allCitizens;
    }

    public void setWarTaxToWorkWallets(){
        warTax = true;
        for(Area area : claimedAreas) {
            if(area instanceof ControlledArea ca) {
                for (Character c : ca.citizenCache) {
                    c.getPerson().getWorkWallet().setWarTax(warTax, this);
                }
            }
        }
    }



    public void removeWarTaxFromWorkWallets(){
        if(!warTax){
            return;
        }
        warTax = false;
        for(Character c :citizenCache){
            c.getPerson().getWorkWallet().setWarTax(warTax);
        }
    }




    @Override
    public void setNation(Nation nation) {
        throw new RuntimeException("Attempted to set nation into nation");
    }

    @Override
    public List<Status> getImportantStatusRank() {
        return List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary,
                Status.Mayor,
                Status.Captain);
    }


    public Shop getShop() {
        return shop;
    }
    @Override
    public ArrayList<Province> getContents() {
        return new ArrayList<>(Arrays.asList(provinces));
    }
    @Override
    public Area getHigher() {
        return continent;
    }
    public LinkedList<Quarter> getAllQuarters() {
        return allQuarters;
    }

    public void addQuarterToNation(Quarter quarter){
        getAllQuarters().add(quarter);
        numberOfQuarters++;
    }
    public HashSet<Nation> getVassals() {
        return vassals;
    }


    public static void handleStartWar(Nation nation1, Nation nation2) {
        try {

            // checking for errors should be done already in WarService

            // set enemy
            nation1.enemy = nation2;

            // set war flag
            nation1.setAtWar();

            // set states
            nation1.addWarState();



        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void selectWarCommanders(Nation nation1) {
        // add authorities and mercenaries to war commanders
        for(Area claimedArea : nation1.claimedAreas){
            Person commander = claimedArea.getHighestAuthority();

            if(commander.getCharacter() instanceof King king){
                System.out.println(king);
                continue; // kings should be excluded
            }

            nation1.getWarCommanders().add(commander);

            if(commander.getCharacter() instanceof Governor governor){ // add mercenaries here
                for(Support support : governor.getAuthorityPosition().getSupporters()){
                    nation1.getWarCommanders().add(support.getPerson());
                }
            }
        }
    }

    public void handleEndingWar(Nation opponent, String winnerOrLoser) {
        try {

            // remove enemy
            enemy = null;

            // remove war flag
            setNotAtWar();

            // reset states
            removeWarState();

            // remove commanders
            resetWarCommanders();

            // remove warTax
            if(warTax) {
                removeWarTaxFromWorkWallets();
            }

            if(Objects.equals(winnerOrLoser, "Winner")){
                if(isVassal()){
                    removeOverlord();
                }else {
                    vassals.add(opponent); // They gained new vassal
                }
            }else{
                if(isOverlord()){
                    vassals.remove(opponent); // Since being the overlord and losing the war, they lose their vassal
                } else {
                    setOverlord(opponent); // Losing the war now makes them a Vassal
                }
            }

        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }


    public void addClaimedArea(Area claimedArea){
        try {
            if(isAtWar){
                String error = "Cannot claim area during a war. Use the handleAreaOwnershipChange method instead.";
                throw new RuntimeException(error);
            }
            claimedAreas.add(claimedArea);
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }


    public HashSet<Person> getWarCommanders() {
        return warCommanders;
    }

    private void setAtWar() {
        this.isAtWar = true;
    }
    private void setNotAtWar() {
        this.isAtWar = false;
    }

    public boolean isAtWar() {
        return isAtWar;
    }



    public int getQuarterAmount() {
        return allQuarters.size();
    }




    public void sendWalletBalanceToLeaders() {

        if(wallet.isEmpty()) return;
        if(wallet.isLowBalance()) return;
        if(vassals.isEmpty()) return; // paying starts only after winning a war
        if(wallet.getCombinedAmount() < 100_000) return;

        // Retrieve important characters
        Set<Character> importantCharacters = getAllImportantCharacters();

        int size = importantCharacters.size() * 2;

        TransferPackage amount = wallet.getBalance().divide(size);

        for (Character character : importantCharacters) {

            Wallet characterWallet = character.getPerson().getWallet();
            characterWallet.addResources(amount);

            if(character.getPerson().isPlayer()){
                character.getPerson().getMessageTracker().addMessage(MessageTracker.Message("Major", "National resource distribution added "+amount));
            }
            // Deduct from nation's wallet
            wallet.subtractResources(amount);
        }
    }

    private void addWarState(){
        for(Person person : getAllCitizensOfTheNation()){
            person.addState(State.AT_WAR);
        }
    }
    private void removeWarState(){
        for(Person person : getAllCitizensOfTheNation()){
            person.removeState(State.AT_WAR);
            person.removeState(State.ACTIVE_COMBAT);
        }
    }

    private Iterable<? extends Person> getAllCitizensOfTheNation() {
        HashSet<Person> set = new HashSet<>();
        for(Quarter quarter : allQuarters){
            set.addAll(quarter.getPersonsLivingHere());
        }
        return set;
    }


    public ArrayList<Military> getAllMilitaries() {
        ArrayList<Military> militaries = new ArrayList<>();
        for(Quarter quarter : allQuarters){
            militaries.addAll(quarter.getMilitaryProperties());
        }
        return militaries;
    }

    public List<Military> getMilitariesOwnedByCommanders() {
        selectWarCommanders(this);

        List<Military> militariesOwnedByCommanders = new ArrayList<>();

        for (Person person : getWarCommanders()) {
            if (person.getRole().getStatus().isCommander()) {
                if (person.getCharacter() instanceof King) {
                    continue;
                }
                if (person.getProperty() instanceof Military military) {
                    militariesOwnedByCommanders.add(military);
                    person.addState(State.ACTIVE_COMBAT);
                }
            }
        }
        return militariesOwnedByCommanders;
    }

    public List<Military> getMilitariesOwnedByCivilians() {
        List<Military> militariesOwnedByCivilians = new ArrayList<>();
        List<Military> allMilitaries = getAllMilitaries();

        for (Military military : allMilitaries) {
            Status status = military.getOwner().getRole().getStatus();
            if (status.isCivilian()) {
                militariesOwnedByCivilians.add(military);
                military.getOwner().addState(State.ACTIVE_COMBAT);
            }
        }

        return militariesOwnedByCivilians;
    }

    public List<Military> getRoyalMilitaries() {
        List<Military> royals = new ArrayList<>();
        List<Military> allMilitaries = getAllMilitaries();

        for (Military military : allMilitaries) {
            Status status = military.getOwner().getRole().getStatus();
            if (status.isRoyal()) {
                royals.add(military);
                military.getOwner().addState(State.ACTIVE_COMBAT);
            }
        }

        return royals;
    }



    public Optional<Military> getStrongestMilitary() {
        return getAllMilitaries().stream()
                .max(Comparator.comparingInt(Military::getMilitaryStrength));
    }

    public boolean isNobleWarBonus() {
        return nobleWarBonus;
    }


    public void startWar(Nation opponent, War war) {
        this.war = war;
        handleStartWar(this, opponent);
    }
    public void endWar(Nation opponent, String winnerOrLoser, War.WarNotes warNotes) {
        this.war = null;
        this.warHistory.add(warNotes);
        handleEndingWar(opponent, winnerOrLoser);
    }

    private void resetWarCommanders(){
        warCommanders.clear();
    }

    public Nation getOverlord() {
        return overlord;
    }

    public void setOverlord(Nation overlord) {
        this.overlord = overlord;
        setOverlordToWorkWallets(overlord);
        getAuthorityHere().setSupervisor(overlord.getAuthorityHere());

        addNameForUI(overlord);

    }

    private void addNameForUI(Nation overlord) {
        if (isPlayerNation() && !overlord.getName().contains(" (Overlord)")) {
            overlord.setName(overlord.getName() + " (Overlord)");
        }
        if (overlord.isPlayerNation() && !getName().contains(" (Vassal)")) {
            setName(getName() + " (Vassal)");
        }
    }

    private void removeOverlord() {
        removeUIName();

        overlord = null; // They won back their independence from the overlord
        getAuthorityHere().setSupervisor(getAuthorityHere()); // King is again his own Supervisor
        removeOverlordFromWorkWallets(); // No more overlord tax


    }

    private void removeUIName() {
        if (overlord != null) {
            if (isPlayerNation()) {
                String overlordName = overlord.getName();
                if (overlordName.endsWith(" (Overlord)")) {
                    overlord.setName(overlordName.substring(0, overlordName.length() - 11)); // Remove " (Overlord)" from the name
                }
            }
            if (overlord.isPlayerNation()) {
                String name = getName();
                if (name.endsWith(" (Vassal)")) {
                    setName(name.substring(0, name.length() - 9)); // Remove " (Vassal)" from the name
                }
            }
        }
    }

    public List<Person> searchCharactersByName(String query) {
        Iterable<? extends Person> characters = getAllCitizensOfTheNation();

        // Filter characters by name
        return StreamSupport.stream(characters.spliterator(), false)
                .filter(character -> character.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean isVassal() {
        return overlord != null;
    }

    public boolean isOverlord() {
        return !vassals.isEmpty();
    }

    public boolean isPlayerNation(){
        return Model.getPlayerRole().getNation() == this;
    }


    public int getWarsFoughtAmount() {
        return warHistory.size();
    }



    public int getMilitaryPower() {
        int totalPower = 0;
        for(Quarter quarter : allQuarters){
            for(Military military : quarter.getMilitaryProperties()){
                totalPower += military.getMilitaryStrength();
            }
        }
        return totalPower;
    }

    public TransferPackage calculateWarStartingCost(Nation other) {
        int distance = calculateDistance(other);

        int quarters = getQuarterAmount();
        int otherQuarters = other.getQuarterAmount();

        // Base cost factors
        double baseCostFactor = 100_000;

        // Adjust costs based on the difference in quarters
        int quarterDifference = quarters - otherQuarters;
        double adjustmentFactor = 1 + (quarterDifference * 0.1);

        // Calculate the base costs
        double cost = Math.max(baseCostFactor * distance * adjustmentFactor, baseCostFactor / 2);

        // Cost is increased for every Vassal either nation might have. This also makes it more expensive for Vassal to Challenge their Overlord.
        cost *= this.getVassals().size() + 1;
        cost *= other.getVassals().size()  + 1;

        return new TransferPackage( (int) cost, (int) cost,(int)  cost);
    }

    public int calculateDistance(Nation other) {
        int totalDistance = 0;

        Continent continent = this.continent;
        Continent otherContinent = other.continent;

        int continentDistanceShort = 20;
        int continentDistanceLong = 60;

        // Calculate distance based on continents
        if (continent == otherContinent) {
            totalDistance += continentDistanceShort;
        } else {
            totalDistance += continentDistanceLong;
        }

        // Calculate additional distance based on the first letters of nation names
        totalDistance += getAlphabeticalDistance(this.name, other.name);

        return totalDistance;
    }

    private int getAlphabeticalDistance(String name1, String name2) {
        char firstLetter1 = java.lang.Character.toUpperCase(name1.charAt(0));
        char firstLetter2 = java.lang.Character.toUpperCase(name2.charAt(0));
        return Math.abs(firstLetter1 - firstLetter2);
    }


    public static int countTotalMilitaryStrength(Set<Military> militaryset){
        int total = 0;
        for(Military military : militaryset){
            total += military.getMilitaryStrength();
        }
        return total;
    }
    public static int countTotalMilitaryStrength(List<Military> militaryList){
        int total = 0;
        for(Military military : militaryList){
            total += military.getMilitaryStrength();
        }
        return total;
    }


    public static class TaxCalculator {

        private static final Map<Class<? extends Character>, TransferPackage> taxAmounts = new HashMap<>();

        static {
            // Initialize the tax amounts for each character type
            taxAmounts.put(Farmer.class, new TransferPackage(100, 1, 1));
            taxAmounts.put(Miner.class, new TransferPackage(1, 50, 1));
            taxAmounts.put(Merchant.class, new TransferPackage(1, 1, 10));
            taxAmounts.put(Captain.class, new TransferPackage(50, 50, 50));
            taxAmounts.put(Support.class, new TransferPackage(15, 15, 15));
            taxAmounts.put(Mayor.class, new TransferPackage(100, 100, 100));
            taxAmounts.put(Governor.class, new TransferPackage(100, 100, 200));
            taxAmounts.put(King.class, new TransferPackage(100, 100, 400));
        }

        public static TransferPackage getNationalTaxAmount(Character c) {
            // Return the tax amount for the given character type, or a default value if not found
            return taxAmounts.getOrDefault(c.getClass(), new TransferPackage(10, 10, 10));
        }

    }

}


