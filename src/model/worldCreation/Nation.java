package model.worldCreation;

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
import model.characters.npc.Governor;
import model.characters.npc.Mercenary;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Shop;
import model.stateSystem.MessageTracker;
import model.war.Military;
import model.war.War;

import java.util.*;
import java.util.stream.Collectors;
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
    private Nation enemy = null;
    private final List<Person> warCommanders = new ArrayList<>();

    private boolean nobleWarBonus = false;
    private boolean warTax = false;

    public War getWar() {
        return war;
    }



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
        person.getEventTracker().addEvent(MessageTracker.Message("Minor", "Joined Slaver Guild"));
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).addBonus("Slaver Guild Bonus", 1);
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).updatePaymentManager(person.getPaymentManager());
    }
    public void joinLiberalGuild(Person person){
        if(person.getAiEngine().getProfile().containsKey(Trait.Slaver) && !person.isPlayer()){
            return;
        }
        if(!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) return;
        freedomFighters.add(person);
        person.getEventTracker().addEvent(MessageTracker.Message("Minor", "Joined Liberal Guild"));
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

    public void setConquerorToWorkWallets(Nation nation){
        for(Character c :citizenCache){
            c.getPerson().getWorkWallet().setConqueror(nation);
        }
    }

    public void removeConquerorFromWorkWallets(){
        for(Character c :citizenCache){
            c.getPerson().getWorkWallet().setConqueror(null);
        }
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
                Status.King,
                Status.Vanguard,
                Status.Noble
        );
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

    public Nation getEnemy() {
        return enemy;
    }

    public static void handleStartWar(Nation nation1, Nation nation2) {
        try {
            if(nation1.isAtWar) {
                System.out.println(nation1 + " is already at war");
                return;
            }
            if(nation1 == nation2){
                String e = nation1 + " attempted to enter war against itself.";
                throw new RuntimeException(e);
            }

            nation1.enemy = nation2;

            // set war flag
            nation1.setAtWar();

            // add commanders who own militaries to war commanders
            for(Area claimedArea : nation1.claimedAreas){
                Person commander = claimedArea.getHighestAuthority();
                if(commander.getProperty() instanceof Military) {
                    nation1.getWarCommanders().add(commander);
                }
            }

        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }


    public Set<Area> getClaimedAreas() {
        return claimedAreas;
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


    private static void triggerTheEndOfTheNation(Nation nation) {
        System.out.println(nation + " has been destroyed by it's enemies");
    }
    public List<Person> getWarCommanders() {
        return warCommanders;
    }

    private void setAtWar() {
        this.isAtWar = true;
    }

    public boolean isAtWar() {
        return isAtWar;
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

    public int getQuarterAmount() {
        return allQuarters.size();
    }

    public int getMilitaryAmount() {
        int amount = 0;
        for(Quarter quarter : allQuarters){
            amount += quarter.getMilitaryProperties().size();
        }
        return amount;
    }

    public ArrayList<Military> getAllMilitaries() {
        ArrayList<Military> militaries = new ArrayList<>();
        for(Quarter quarter : allQuarters){
            militaries.addAll(quarter.getMilitaryProperties());
        }
        return militaries;
    }

    public ArrayList<Military> getMilitariesOwnedByCommanders() {
        ArrayList<Military> militariesOwnedByCommanders = new ArrayList<>();
        ArrayList<Military> allMilitaries = getAllMilitaries();
        for (Military military : allMilitaries) {
            if (warCommanders.contains(military.getOwner())) {
                    if(     !(military.getOwner().getRole().getStatus() == Status.Vanguard) || // king and his sentinels are excluded here
                            !(military.getOwner().getRole().getStatus() == Status.King) ||
                            !(military.getOwner().getRole().getStatus() == Status.Noble)) {
                        militariesOwnedByCommanders.add(military);
                }
            }
            if(military.getOwner().getRole().getStatus() == Status.Mercenary){ // mercenaries are added here
                militariesOwnedByCommanders.add(military);
            }
        }
        return militariesOwnedByCommanders;
    }

    public ArrayList<Military> getMilitariesOwnedByCivilians() {
        ArrayList<Military> militariesOwnedByCivilians = new ArrayList<>();
        ArrayList<Military> allMilitaries = getAllMilitaries();
        for (Military military : allMilitaries) {
            if (!warCommanders.contains(military.getOwner())) {
                if(     !(military.getOwner().getRole().getStatus() == Status.Vanguard) || // king and his sentinels are excluded here
                        !(military.getOwner().getRole().getStatus() == Status.King) ||
                        !(military.getOwner().getRole().getStatus() == Status.Noble) ||
                        !(military.getOwner().getRole().getStatus() == Status.Mercenary)) {
                    militariesOwnedByCivilians.add(military);
                }
            }
        }
        return militariesOwnedByCivilians;
    }

    public ArrayList<Military> getMilitariesOwnedByKingAndHisSentinels() {
        ArrayList<Military> militariesOwnedKingAndSentinels= new ArrayList<>();

        Military military = (Military) getAuthorityHere().getCharacterInThisPosition().getPerson().getProperty();

        militariesOwnedKingAndSentinels.add(military);

        for(Support support : getAuthorityHere().getSupporters()){
            if(support.getPerson().getProperty() instanceof Military m){
                militariesOwnedKingAndSentinels.add(m);
            }
        }

        return militariesOwnedKingAndSentinels;
    }



    public Optional<Military> getStrongestMilitary() {
        return getAllMilitaries().stream()
                .max(Comparator.comparingInt(Military::getMilitaryStrength));
    }

    public boolean isNobleWarBonus() {
        return nobleWarBonus;
    }

    public void setNobleWarBonus(boolean nobleWarBonus) {
        this.nobleWarBonus = nobleWarBonus;
    }


    public void startWar(Nation opponent, War war) {
        this.war = war;
        handleStartWar(this, opponent);


    }
}
