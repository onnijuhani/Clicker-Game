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
import model.shop.Shop;
import model.stateSystem.EventTracker;

import java.util.*;
import java.util.stream.Collectors;
@SuppressWarnings("CallToPrintStackTrace")
public class Nation extends ControlledArea implements Details {
    private Province[] provinces;
    private final Continent continent;
    private final Shop shop;
    protected LinkedList<Quarter> allQuarters;
    public int numberOfQuarters;
    private List<Character> nationsGenerals = null;
    private boolean isGeneralsCacheValid = false;
    private final Set<Person> slaverGuild = new HashSet<>();
    private final Set<Person> freedomFighters = new HashSet<>();


    private final Set<Area> claimedAreas = new HashSet<>();
    private final Set<Area> areasUnderEnemyOccupation = new HashSet<>();

    private boolean isAtWar = false;
    private Nation enemy = null;

    public List<Person> getWarGenerals() {
        return warGenerals;
    }

    private final List<Person> warGenerals = new ArrayList<>();


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

        StringBuilder detailsBuilder = new StringBuilder();

        if (!nationsGenerals.isEmpty()) {
            detailsBuilder.append("Generals (").append(nationsGenerals.size()).append("):");
            for (Character general : nationsGenerals) {
                String generalDetails = String.format("\n- %s, Status: %s, Property: %s",
                        general.getName(),
                        general.getRole().getStatus(),
                        general.getPerson().getProperty().getClass().getSimpleName());
                detailsBuilder.append(generalDetails);
            }
        } else {
            detailsBuilder.append("\nNo generals found.");
        }

        return detailsBuilder.toString();
    }

    public void updateGenerals(){
        setGeneralsCacheValid(false);
        collectGenerals();
    }

    /**
     Generals are some of the main authority characters and their supporters who also have property capable of having an army
     **/
    public void collectGenerals() {
        if (isGeneralsCacheValid) {
            return;
        }
        Set<Status> generalStatuses = EnumSet.of(Status.Governor, Status.Vanguard, Status.Mercenary, Status.King);

        Set<String> validProperties = Set.of("Castle", "Citadel", "Fortress");

        nationsGenerals = new ArrayList<>();
        for (Quarter quarter : allQuarters) {
            quarter.updateCitizenCache();

            quarter.getPopulationMap().entrySet().stream()
                    .filter(entry -> generalStatuses.contains(entry.getKey()))
                    .flatMap(entry -> entry.getValue().stream())
                    .filter(character -> validProperties.contains(character.getProperty().getClass().getSimpleName()))
                    .forEach(person -> {
                        nationsGenerals.add(person.getCharacter());
                    });
        }
        isGeneralsCacheValid = true;
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
    public boolean isGeneralsCacheValid() {
        return isGeneralsCacheValid;
    }
    public void setGeneralsCacheValid(boolean generalsCacheValid) {
        isGeneralsCacheValid = generalsCacheValid;
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
        person.getEventTracker().addEvent(EventTracker.Message("Minor", "Joined Slaver Guild"));
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).addBonus("Slaver Guild Bonus", 1);
    }
    public void joinLiberalGuild(Person person){
        if(person.getAiEngine().getProfile().containsKey(Trait.Slaver) && !person.isPlayer()){
            return;
        }
        if(!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) return;
        freedomFighters.add(person);
        person.getEventTracker().addEvent(EventTracker.Message("Minor", "Joined Liberal Guild"));
        person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.WorkerCenter).addBonus("Liberal Guild Bonus", 1);
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

    public List<Character> getNationsGenerals() {
        return nationsGenerals;
    }

    public void setNationsGenerals(List<Character> nationsGenerals) {
        this.nationsGenerals = nationsGenerals;
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
            if(nation2.isAtWar) {
                System.out.println(nation2 + " is already at war");
                return;
            }
            if(nation1 == nation2){
                String e = nation1 + " attempted to enter war against itself.";
                throw new RuntimeException(e);
            }

            // set enemies
            nation1.enemy = nation2;
            nation2.enemy = nation1;

            // set war flag
            nation1.setAtWar(true);
            nation2.setAtWar(true);


            // set up AreaStateManagers for claimed areas
            for(Area claimedArea : nation1.claimedAreas){
                Person defendingCommander = claimedArea.getHighestAuthority();
                claimedArea.getAreaStateManager().startWar(nation2, defendingCommander);
                nation1.getWarGenerals().add(defendingCommander);
            }

            for(Area claimedArea : nation2.claimedAreas){
                Person defendingCommander = claimedArea.getHighestAuthority();
                claimedArea.getAreaStateManager().startWar(nation1, defendingCommander);
                nation2.getWarGenerals().add(defendingCommander);
            }


            // set Nations War Generals (Kings)
            Person warGeneral1 = nation1.getHighestAuthority();
            nation1.getAreaStateManager().startWar(nation2, warGeneral1);
            nation1.getWarGenerals().add(warGeneral1);

            Person warGeneral2 = nation2.getHighestAuthority();
            nation2.getAreaStateManager().startWar(nation1, warGeneral2);
            nation2.getWarGenerals().add(warGeneral2);


        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public Set<Area> getClaimedAreas() {
        return claimedAreas;
    }
    public Set<Area> getAreasUnderEnemyOccupation() {
        return areasUnderEnemyOccupation;
    }
    public void addClaimedArea(Area claimedArea){
        try {
            if(isAtWar){
                String error = "Cannot claim area during a war. Use the handleAreaOwnershipChange method instead.";
                throw new RuntimeException(error);
            }
            claimedArea.getAreaStateManager().setClaimingNation(this);
            claimedAreas.add(claimedArea);
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    private void addClaimedAreaWar(Area claimedArea){
        try {
            claimedArea.getAreaStateManager().setClaimingNation(this);
            claimedAreas.add(claimedArea);
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    public static void handleAreaOwnershipChange(Area claimedArea, Nation newOwner, Nation oldOwner){
        try {
            if(!newOwner.isAtWar || !oldOwner.isAtWar){
                System.out.println("Cannot switch area claims if not at war");
                return;
            }
            if(oldOwner.claimedAreas.contains(claimedArea)){
                oldOwner.claimedAreas.remove(claimedArea);
                newOwner.addClaimedAreaWar(claimedArea);

                claimedArea.getAreaStateManager().triggerClaimChange();

                if(claimedArea instanceof ControlledArea controlledArea){
                    controlledArea.setNation(newOwner);
                }

            } else {
                System.out.println("Cannot switch area claims " + oldOwner + " doesn't claim the area.");
                return;
            }
            if(oldOwner.claimedAreas.isEmpty()){
                triggerTheEndOfTheNation(oldOwner);
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private static void triggerTheEndOfTheNation(Nation nation) {
        System.out.println(nation + " has been destroyed by it's enemies");
    }

    public void addEnemyOccupiedArea(Area occupiedArea){
        if(isAtWar) {
            if(claimedAreas.contains(occupiedArea)) {
                areasUnderEnemyOccupation.add(occupiedArea);
            }else{
                System.out.println("Cannot add area to be in enemy occupation since " + this + " doesn't claim the area.");
            }
        }else{
            System.out.println("Cannot add area to be in enemy occupation since " + this + " is not at war.");
        }
    }

    private void setAtWar(boolean atWar) {
        this.isAtWar = atWar;
    }

    public boolean isAtWar() {
        return isAtWar;
    }
}
