package model.worldCreation;

import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Person;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;

import java.util.*;

public class Quarter extends ControlledArea implements Details {
    private final HashMap<Status, LinkedList<Person>> populationMap;
    private final PropertyTracker allProperties;
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
        this.allProperties = new PropertyTracker();
        this.populationMap = new HashMap<>();
        initializePopulationMap();
        populateQuarter();
        createQuarterAlliances();
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


    public void createQuarterAlliances() {

        List<Person> allPersons = populationMap.values().stream()
                .flatMap(Collection::stream)
                .toList();

        for (Person person : allPersons) {
            for (Person potentialAlly : allPersons) {
                if (!person.equals(potentialAlly)) {
                    person.getRelationshipManager().addAlly(potentialAlly);
                }
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
        int contents = allProperties.getProperties().size();

        String popList = getCitizensAsString();

        return ("Quarter wealth is:" + "\n" + Arrays.toString(quarterWealth) + "\n" +

                "Base economy is: " + baseEconomy + "\n" +

                "Authority here is: " + this.getAuthorityHere() + "\n"+
                "Living in a: " + this.getAuthorityHere().getProperty() + "\n"+
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
                "Inside: " + city.getName() + " - City\n" +
                "Of the: " + prov.getName() + " - Province\n" +
                "Part of: " + nat.getName() + " - Nation\n" +
                "In the: " + nat.getHigher().getName() + " - Continent\n" +
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

    public LinkedList<Person> getCharacterList(Status status) {
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
        quarterCaptain.getCharacterInThisPosition().setNation(this.city.getProvince().getNation());
        quarterCaptain.setSupervisor(city.getAuthorityHere());
        quarterCaptain.getProperty().setLocation(this);
    }

    private void peasantFactory(QuarterAuthority quarterCaptain, LinkedList<Person> farmers, LinkedList<Person> miners, LinkedList<Person> merchants) {
        Random random = new Random();
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
            farmer.setNation(nation);
            farmer.getProperty().setLocation(this);
            farmer.setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(farmer);
            farmers.add(farmer.getPerson());
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(quarterCaptain);
            miner.setNation(nation);
            miner.getProperty().setLocation(this);
            miner.setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(miner);
            miners.add(miner.getPerson());
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(quarterCaptain);
            merch.setNation(nation);
            merch.getProperty().setLocation(this);
            merch.setAuthority(quarterCaptain);
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
    }

    public void removeCitizen(Status status, Person person) {
        getPopulationMap().get(status).remove(person);
        isPopulationChanged = true;
    }

    public void forceAddAnyCitizen(Person person) {
        addCitizen(person.getStatus(),person);
    }

    public void changeCitizenPosition(Person person, Status oldStatus){
        removeCitizen(oldStatus, person);
        forceAddAnyCitizen(person);
    }

    public HashMap<Status,LinkedList<Person>> getPopulationMap() {
        return populationMap;
    }
    public PropertyTracker getAllProperties() {
        return allProperties;
    }
    public void addProperty(Property property){
        allProperties.addProperty(property);
    }

    public int getNumOfPeasants() {
        return numOfPeasants;
    }

    public City getCity() {
        return city;
    }

    public int getBaseEconomy() {
        return baseEconomy;
    }

    public void setCity(City city) {
        this.city = city;
    }
    public boolean isPopulationChanged() {
        return isPopulationChanged;
    }

    public void setPopulationChanged(boolean populationChanged) {
        isPopulationChanged = populationChanged;
    }

}
