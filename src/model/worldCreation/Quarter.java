package model.worldCreation;

import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;

import java.util.*;

public class Quarter extends ControlledArea implements Details {

    private final HashMap<Status, LinkedList<Character>> populationMap;
    private final PropertyTracker allProperties;

    private City city;
    private int numOfPeasants;
    private boolean isPopulationChanged = true;


    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.nation = city.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        this.allProperties = new PropertyTracker();
        this.populationMap = new HashMap<>();
        initializePopulationMap();
        populateQuarter();
        createQuarterAlliances();
    }

    public LinkedList<Character> getCharacterList(Status status) {
        return populationMap.getOrDefault(status, new LinkedList<>());
    }

    public void createQuarterAlliances() {

        List<Character> allCharacters = populationMap.values().stream()
                .flatMap(Collection::stream)
                .toList();

        for (Character character : allCharacters) {
            for (Character potentialAlly : allCharacters) {
                if (!character.equals(potentialAlly)) {
                    character.getRelationshipManager().addAlly(potentialAlly);
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
        int population = numOfPeasants;
        int contents = allProperties.getProperties().size();

        String popList = getCitizensAsString();

        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
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
        ArrayList<Character> citizens = new ArrayList<>();
        List<Status> statusOrder = getStatusRank();

        // Sort by Status
        populationMap.entrySet().stream()
                .filter(entry -> statusOrder.contains(entry.getKey())) // Filter based on the key (Status)
                .sorted(Comparator.comparingInt(entry -> statusOrder.indexOf(entry.getKey()))) // Sort based on the key's index in statusOrder
                .forEachOrdered(entry -> citizens.addAll(entry.getValue())); // Add all Characters to the ArrayList

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
        LinkedList<Character> farmerList = new LinkedList<>();
        LinkedList<Character> minerList = new LinkedList<>();
        LinkedList<Character> merchantList = new LinkedList<>();
        LinkedList<Character> mayorList = new LinkedList<>();
        LinkedList<Character> kingList = new LinkedList<>();
        LinkedList<Character> mercenaryList = new LinkedList<>();
        LinkedList<Character> nobleList = new LinkedList<>();
        LinkedList<Character> vanguardList = new LinkedList<>();
        LinkedList<Character> governorList = new LinkedList<>();
        LinkedList<Character> captainList = new LinkedList<>();
        LinkedList<Character> peasantList = new LinkedList<>();

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

    private void populateQuarter() {
        LinkedList<Character> farmerList = getCharacterList(Status.Farmer);
        LinkedList<Character> minerList = getCharacterList(Status.Miner);
        LinkedList<Character> merchantList = getCharacterList(Status.Merchant);

        QuarterAuthority quarterCaptain = (QuarterAuthority) this.authority;
        setUpCaptainAttributes(quarterCaptain);
        addCitizen(Status.Captain, quarterCaptain.getCharacter());

        peasantFactory(quarterCaptain, farmerList, minerList, merchantList);
    }

    private void setUpCaptainAttributes(QuarterAuthority quarterCaptain) {
        quarterCaptain.getCharacter().setNation(this.city.getProvince().getNation());
        quarterCaptain.setSupervisor(city.getAuthority());
        quarterCaptain.getProperty().setLocation(this);
    }

    private void peasantFactory(QuarterAuthority quarterCaptain, LinkedList<Character> farmers, LinkedList<Character> miners, LinkedList<Character> merchants) {
        Random random = new Random();
        int numberOfFarmers = random.nextInt(Settings.getInt("farmerAmountMax")) + Settings.getInt("farmerAmountMin");
        numOfPeasants += numberOfFarmers;
        int numberOfMiners = random.nextInt(Settings.getInt("minerAmountMax")) + Settings.getInt("minerAmountMin");
        numOfPeasants += numberOfMiners;
        int numberOfMerchants = random.nextInt(Settings.getInt("merchantAmountMax")) + Settings.getInt("merchantAmountMin");
        numOfPeasants += numberOfMerchants;

        for (int peasant = 0; peasant < numberOfFarmers; peasant++) {
            Farmer farmer = new Farmer(quarterCaptain);
            farmer.setNation(nation);
            farmer.getProperty().setLocation(this);
            farmer.setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(farmer);
            farmers.add(farmer);
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(quarterCaptain);
            miner.setNation(nation);
            miner.getProperty().setLocation(this);
            miner.setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(miner);
            miners.add(miner);
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(quarterCaptain);
            merch.setNation(nation);
            merch.getProperty().setLocation(this);
            merch.setAuthority(quarterCaptain);
            quarterCaptain.addPeasant(merch);
            merchants.add(merch);
        }
    }

    public void addCitizen(Status status, Character character){
        getPopulationMap().get(status).add(character);
        isPopulationChanged = true;
        getNation().setGeneralsCacheValid(false);
    }

    public void removeCharacter(Status status, Character character) {
        getPopulationMap().get(status).remove(character);
        isPopulationChanged = true;
    }
    public HashMap<Status,LinkedList<Character>> getPopulationMap() {
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

    public void setCity(City city) {
        this.city = city;
    }

}
