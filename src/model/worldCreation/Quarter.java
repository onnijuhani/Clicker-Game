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
    private String importantCharactersCache;

    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.nation = city.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        this.allProperties = new PropertyTracker();
        Authority captain = this.authority;
        this.populationMap = new HashMap<>();
        captain.getCharacter().setNation(city.getProvince().getNation());
        captain.setSupervisor(city.getAuthority());
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

        String popList = getImportantCharacters();

        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+contents+" properties"+ "\n"+
                (popList.isBlank() ? "" : "Here Lives: "+ "\n")+
                popList

        );
    }



    @NotNull
    public String getImportantCharacters() {

        //important characters are only calculated the first time they are needed
        //and whenever the character list changes. List is stored at importantCharactersCache.

        if (isPopulationChanged || importantCharactersCache == null) {
            importantCharactersCache = calculateImportantCharacters();
            isPopulationChanged = false;
        }
        return importantCharactersCache;
    }

    @NotNull
    private String calculateImportantCharacters() {

        StringBuilder sb = new StringBuilder();
        List<Status> statusOrder = getStatusRank();

        // Sort by Status
        populationMap.entrySet().stream()
                .filter(entry -> statusOrder.contains(entry.getKey())) // Include only important statuses
                .sorted(Comparator.comparingInt(entry -> statusOrder.indexOf(entry.getKey())))
                .forEachOrdered(entry -> {
                    for (Character character : entry.getValue()) {
                        sb.append("    ").append(character).append("\n");
                    }
                });

        return sb.toString();
    }

    @NotNull
    private static List<Status> getStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary, Status.Mayor
                //doesn't include unimportant ranks
        );
        return statusOrder;
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
        setCaptainHome(quarterCaptain);
        addCharacter(Status.Captain, quarterCaptain.getCharacter());

        peasantFactory(quarterCaptain, farmerList, minerList, merchantList);
    }


    private void setCaptainHome(QuarterAuthority quarterCaptain) {
        quarterCaptain.getCharacter().setNation(this.city.getProvince().getNation());
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
            quarterCaptain.addPeasant(farmer);
            farmers.add(farmer);
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(quarterCaptain);
            miner.setNation(nation);
            miner.getProperty().setLocation(this);
            quarterCaptain.addPeasant(miner);
            miners.add(miner);
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(quarterCaptain);
            merch.setNation(nation);
            merch.getProperty().setLocation(this);
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
