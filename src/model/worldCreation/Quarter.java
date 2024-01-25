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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Quarter extends ControlledArea implements Details {

    private HashMap<Status, LinkedList<Character>> populationMap;
    private PropertyTracker allProperties;
    private City city;
    private int numOfPeasants;
    private boolean isPopulationChanged = true;
    private String citizenCache;

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
    }

    public LinkedList<Character> getCharacterList(Status status) {
        return populationMap.getOrDefault(status, new LinkedList<>());
    }




    @Override
    public Area getHigher() {
        return city;
    }

    @Override
    public String getDetails() {
        int population = numOfPeasants;
        int contents = allProperties.getProperties().size();

        String popList = getCitizens();

        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+contents+" properties"+ "\n"+
                (popList.isBlank() ? "" : "Here Lives: "+ "\n")+
                popList

        );
    }



    @NotNull
    public String getCitizens() {

        //Citizens are only calculated the first model.time they are needed
        //and whenever the character list changes. List is stored at citizenCache

        if (isPopulationChanged || citizenCache == null) {
            citizenCache = calculateCitizens();
            isPopulationChanged = false;
        }
        return citizenCache;
    }

    @NotNull
    private String calculateCitizens() {

        StringBuilder sb = new StringBuilder();
        List<Status> statusOrder = getStatusRank();

        // Sort by Status
        populationMap.entrySet().stream()
                .filter(entry -> statusOrder.contains(entry.getKey())) // Filter based on the key (Status)
                .sorted(Comparator.comparingInt(entry -> statusOrder.indexOf(entry.getKey()))) // Sort based on the key's index in statusOrder
                .forEachOrdered(entry -> {
                    for (Character character : entry.getValue()) { // Iterate over the LinkedList<Character>
                        sb.append("    ").append(character).append("\n");
                    }
                });

        return sb.toString();
    }



    @NotNull
    public List<Character> getImportantCharactersList() {
        List<Character> characters = new ArrayList<>();
        List<Status> statusOrder = getImportantStatusRank();

        // Sort by Status
        populationMap.entrySet().stream()
                .filter(entry -> statusOrder.contains(entry.getKey())) // Include only important statuses
                .sorted(Comparator.comparingInt(entry -> statusOrder.indexOf(entry.getKey())))
                .forEachOrdered(entry -> {
                    for (Character character : entry.getValue()) {
                        characters.add(character);
                    }
                });

        return characters;
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
        LinkedList<Character> slaveList = new LinkedList<>();

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
        populationMap.put(Status.Slave, slaveList);


    }

    private void populateQuarter() {
        LinkedList<Character> farmerList = getCharacterList(Status.Farmer);
        LinkedList<Character> minerList = getCharacterList(Status.Miner);
        LinkedList<Character> merchantList = getCharacterList(Status.Merchant);

        QuarterAuthority quarterCaptain = (QuarterAuthority) this.authority;
        setUpCaptainAttributes(quarterCaptain);
        addCharacter(Status.Captain, quarterCaptain.getCharacter());

        peasantFactory(quarterCaptain, farmerList, minerList, merchantList);
    }


    private void setUpCaptainAttributes(QuarterAuthority quarterCaptain) {
        quarterCaptain.getCharacter().setNation(this.city.getProvince().getNation());
        quarterCaptain.setSupervisor(city.getAuthority());
    }

    private void peasantFactory(QuarterAuthority quarterCaptain, LinkedList<Character> farmers, LinkedList<Character> miners, LinkedList<Character> merchants) {
        Random random = new Random();
        int numberOfFarmers = random.nextInt(Settings.get("farmerAmountMax")) + Settings.get("farmerAmountMin");
        numOfPeasants += numberOfFarmers;
        int numberOfMiners = random.nextInt(Settings.get("minerAmountMax")) + Settings.get("minerAmountMin");
        numOfPeasants += numberOfMiners;
        int numberOfMerchants = random.nextInt(Settings.get("merchantAmountMax")) + Settings.get("merchantAmountMin");
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

    public void addCharacter(Status status, Character character){
        getPopulationMap().get(status).add(character);
        isPopulationChanged = true;
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

}
