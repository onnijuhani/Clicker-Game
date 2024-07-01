package model.worldCreation;

import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyTracker;
import model.buildings.properties.MilitaryProperty;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;
import model.war.Army;

import java.util.*;

public class Quarter extends ControlledArea implements Details {
    private final HashMap<Status, LinkedList<Person>> populationMap;

    private City city;
    private int numOfPeasants;
    private boolean isPopulationChanged = true;
    private int baseEconomy = 0;
    private int[] quarterWealth;


    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.nation = city.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authorityHere = authority;
        this.populationMap = new HashMap<>();
        initializePopulationMap();
        populateQuarter();
        createQuarterAlliances();
    }

    public Set<Person> getPersonsLivingHere(){
        Set<Person> allPersons = new HashSet<Person>();

        for (LinkedList<Person> personsList : populationMap.values()) {
            allPersons.addAll(personsList);
        }
        return allPersons;
    }


    public void calculateQuarterWealth() {
        quarterWealth = new int[3];

        for (LinkedList<Person> persons : populationMap.values()) {
            for (Person person : persons) {
                Wallet personsWallet = person.getWallet();
                Vault personsVault = person.getProperty().getVault();

                quarterWealth[0] += personsWallet.getFood();
                quarterWealth[1] += personsWallet.getAlloy();
                quarterWealth[2] += personsWallet.getGold();

                quarterWealth[0] += personsVault.getFood();
                quarterWealth[1] += personsVault.getAlloy();
                quarterWealth[2] += personsVault.getGold();
            }
        }
    }

    public List<MilitaryProperty> getMilitaryProperties(){
        ArrayList<MilitaryProperty> militaries = new ArrayList<>();
        for (Property property : getPropertyTracker().getProperties()){
            if (property instanceof MilitaryProperty militaryProperty){
                militaries.add(militaryProperty);
            }
        }
        return militaries;
    }


    public double getDefeatedMilitaryRatio(){
        int defeated = 0;
        for (MilitaryProperty militaryProperty : getMilitaryProperties()){
            if(militaryProperty.getState() == Army.ArmyState.DEFEATED){
                defeated++;
            }
        }
        return (double) defeated / getMilitaryProperties().size();
    }

    public List<MilitaryProperty> getDefeatedMilitaries(){
        ArrayList<MilitaryProperty> militaries = new ArrayList<>();
        for (MilitaryProperty militaryProperty : getMilitaryProperties()){
            if(militaryProperty.getState() == Army.ArmyState.DEFEATED){
                militaries.add(militaryProperty);
            }
        }
        return militaries;
    }

    public List<MilitaryProperty> getUndefeatedMilitaries(){
        ArrayList<MilitaryProperty> militaries = new ArrayList<>();
        for (MilitaryProperty militaryProperty : getMilitaryProperties()){
            if(militaryProperty.getState() != Army.ArmyState.DEFEATED){
                militaries.add(militaryProperty);
            }
        }
        return militaries;
    }


    public int getTotalMilitaryStrength(){
        int totalPower = 0;
        for (MilitaryProperty militaryProperty : getMilitaryProperties()){
            totalPower += militaryProperty.getMilitaryStrength();
        }
        return totalPower;
    }







    public void createQuarterAlliances() {

        List<Person> allPersons = populationMap.values().stream()
                .flatMap(Collection::stream)
                .toList();

        for (Person person : allPersons) {
            for (Person potentialAlly : allPersons) {
                if (!person.equals(potentialAlly)) {
                    person.getRelationsManager().addAlly(potentialAlly);
                }
            }
        }
    }

    public void createInitialRivalries() {

        List<Quarter> allQuarters = nation.getAllQuarters();

        List<Quarter> otherQuarters = allQuarters.stream()
                .filter(q -> !q.equals(this))
                .toList();

        if (otherQuarters.isEmpty()) {
            return;
        }

        Random random = Settings.getRandom();
        Quarter rivalQuarter = otherQuarters.get(random.nextInt(otherQuarters.size()));

        List<Person> thisPersons = populationMap.values().stream()
                .flatMap(Collection::stream)
                .toList();


        List<Person> rivalPersons = rivalQuarter.populationMap.values().stream()
                .flatMap(Collection::stream)
                .toList();


        for (Person ourPerson : thisPersons) {
            for (Person rivalPerson : rivalPersons) {
                ourPerson.getRelationsManager().addEnemy(rivalPerson);
                rivalPerson.getRelationsManager().addEnemy(ourPerson);
            }
        }
    }

    @Override
    public Area getHigher() {
        return city;
    }

    @Override
    public String getDetails() {
        calculateQuarterWealth();
        int population = numOfPeasants;
        int contents = propertyTracker.getProperties().size();

        String popList = getCitizensAsString();

        return ("Quarter wealth is:" + "\n" + Arrays.toString(quarterWealth) + "\n" +

                "Base economy is: " + baseEconomy + "\n" +

                "Authority here is: " + this.getAuthorityHere() + "\n"+
                "Living in a: " + this.getAuthorityHere().getCharacterInThisPosition().getPerson().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+contents+" properties"+ "\n"+
                (popList.isBlank() ? "" : "Here Lives: "+ "\n")+
                popList

        );
    }


    private String getCitizensAsString() {
        List<Character> citizens = calculateCitizens();
        StringBuilder sb = new StringBuilder();

        for (Character character : citizens) {
            sb.append("    ").append(character).append("\n");
        }

        return sb.toString();
    }

    @Override
    public void setNation(Nation nation){
        this.nation = nation;
    }

    /**
     Citizens are only calculated the first time they are needed
     and whenever the character list changes. List is stored at citizenCache
     **/
    @Override
    public void updateCitizenCache() {

        if (isPopulationChanged || citizenCache == null) {
            citizenCache = calculateCitizens();
            getCity().onCitizenUpdate();
            getCity().getProvince().onCitizenUpdate();
            getCity().getProvince().getNation().onCitizenUpdate();
            isPopulationChanged = false;
        }
    }


    public List<Character> calculateCitizens() {
        List<Character> citizens = new ArrayList<>();
        List<Status> statusOrder = getImportantStatusRank();

        // Sort by Status
        populationMap.entrySet().stream()
                .filter(entry -> statusOrder.contains(entry.getKey())) // Filter based on the key (Status)
                .sorted(Comparator.comparingInt(entry -> statusOrder.indexOf(entry.getKey()))) // Sort based on the key's index in statusOrder
                .flatMap(entry -> entry.getValue().stream()) // Stream all Persons in the value list
                .map(Person::getCharacter) // Map each Person to their Character
                .forEachOrdered(citizens::add); // Add all Characters to the List



        return citizens;
    }

    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>();
    }

    public String getFullHierarchyInfo() {
        Province prov = city.getProvince();
        Nation nat = prov.getNation();
        return name + "\n" +
                "Inside City - : " + city.getName() + "\n" +
                "Of the Province - : " + prov.getName() + "\n" +
                "Part of Nation - : " + nat.getName() + "\n" +
                "In the Continent - : " + nat.getHigher().getName() + "\n" +
                "Of the Mighty: " + nat.getHigher().getHigher().getName();
    }

    private void initializePopulationMap() {
        LinkedList<Person> farmerList = new LinkedList<>();
        LinkedList<Person> minerList = new LinkedList<>();
        LinkedList<Person> merchantList = new LinkedList<>();
        LinkedList<Person> mayorList = new LinkedList<>();
        LinkedList<Person> kingList = new LinkedList<>();
        LinkedList<Person> mercenaryList = new LinkedList<>();
        LinkedList<Person> nobleList = new LinkedList<>();
        LinkedList<Person> vanguardList = new LinkedList<>();
        LinkedList<Person> governorList = new LinkedList<>();
        LinkedList<Person> captainList = new LinkedList<>();
        LinkedList<Person> peasantList = new LinkedList<>();

        populationMap.put(Status.Farmer, farmerList);
        populationMap.put(Status.Miner, minerList);
        populationMap.put(Status.Merchant, merchantList);
        populationMap.put(Status.Mayor, mayorList);
        populationMap.put(Status.King, kingList);
        populationMap.put(Status.Mercenary, mercenaryList);
        populationMap.put(Status.Noble, nobleList);
        populationMap.put(Status.Vanguard, vanguardList);
        populationMap.put(Status.Governor, governorList);
        populationMap.put(Status.Captain, captainList);
        populationMap.put(Status.Peasant, peasantList);
    }

    private LinkedList<Person> getCharacterList(Status status) {
        return populationMap.getOrDefault(status, new LinkedList<>());
    }

    private void populateQuarter() {
        LinkedList<Person> farmerList = getCharacterList(Status.Farmer);
        LinkedList<Person> minerList = getCharacterList(Status.Miner);
        LinkedList<Person> merchantList = getCharacterList(Status.Merchant);

        QuarterAuthority quarterCaptain = (QuarterAuthority) this.authorityHere;
        setUpCaptainAttributes(quarterCaptain);
        addCitizen(Status.Captain, quarterCaptain.getCharacterInThisPosition().getPerson());

        peasantFactory(quarterCaptain, farmerList, minerList, merchantList);
    }

    private void setUpCaptainAttributes(QuarterAuthority quarterCaptain) {
        quarterCaptain.getCharacterInThisPosition().getRole().setNation(this.city.getProvince().getNation());
        quarterCaptain.setSupervisor(city.getAuthorityHere());
        quarterCaptain.getCharacterInThisPosition().getPerson().getProperty().setLocation(this);
    }

    private void peasantFactory(QuarterAuthority quarterCaptain, LinkedList<Person> farmers, LinkedList<Person> miners, LinkedList<Person> merchants) {
        Random random = Settings.getRandom();
        int numberOfFarmers = random.nextInt(Settings.getInt("farmerAmountMax")) + Settings.getInt("farmerAmountMin");
        numOfPeasants += numberOfFarmers;
        baseEconomy += numberOfFarmers;

        int numberOfMiners = random.nextInt(Settings.getInt("minerAmountMax")) + Settings.getInt("minerAmountMin");
        numOfPeasants += numberOfMiners;
        baseEconomy += numberOfMiners * 2;

        int numberOfMerchants = random.nextInt(Settings.getInt("merchantAmountMax")) + Settings.getInt("merchantAmountMin");
        numOfPeasants += numberOfMerchants;
        baseEconomy += numberOfMerchants * 3;

        for (int peasant = 0; peasant < numberOfFarmers; peasant++) {
            Farmer farmer = new Farmer(quarterCaptain);
            farmer.getRole().setNation(nation);
            farmer.getPerson().getProperty().setLocation(this);
            farmer.getRole().setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(farmer);
            farmers.add(farmer.getPerson());
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(quarterCaptain);
            miner.getRole().setNation(nation);
            miner.getPerson().getProperty().setLocation(this);
            miner.getRole().setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(miner);
            miners.add(miner.getPerson());
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(quarterCaptain);
            merch.getRole().setNation(nation);
            merch.getPerson().getProperty().setLocation(this);
            merch.getRole().setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(merch);
            merchants.add(merch.getPerson());
        }
    }
    public int[] getQuarterWealth() {
        return quarterWealth;
    }

    public void addCitizen(Status status, Person person){
        getPopulationMap().get(status).add(person);
        isPopulationChanged = true;
        getNation().setGeneralsCacheValid(false);

        if (person.getCharacter() instanceof Peasant) {
            addPeasantToBeTaxed(person);
        }
    }

    private void addPeasantToBeTaxed(Person person) {
        QuarterAuthority quarterCaptain = (QuarterAuthority) this.authorityHere;
        Peasant peasant = (Peasant) person.getCharacter();

        quarterCaptain.addPeasant(peasant);
    }
    public HashMap<Status,LinkedList<Person>> getPopulationMap() {
        return populationMap;
    }


    public int getNumOfPeasants() {
        return numOfPeasants;
    }
    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }
    public void setPopulationChanged(boolean populationChanged) {
        isPopulationChanged = populationChanged;
    }

    private void updateCitizenNationInfo(){
        for(Character citizen : citizenCache){
            citizen.getRole().setNation(nation);
        }
    }

    /**
     * This method cannot be called during the creation of the world
     */
    public void updateEverything() {
        setPopulationChanged(true);
        onCitizenUpdate();
        calculateCitizens();
        calculateQuarterWealth();
        updateCitizenNationInfo();
    }
}
