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

    private HashMap<Status, LinkedList<Character>> populationList;
    private PropertyTracker allProperties;
    private City city;
    private int numOfPeasants;

    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.nation = city.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        this.allProperties = new PropertyTracker();
        Authority captain = this.authority;
        this.populationList = new HashMap<>();
        captain.getCharacter().setNation(city.getProvince().getNation());
        captain.setSupervisor(city.getAuthority());
        createPeasants();
    }




    @Override
    public Area getHigher() {
        return city;
    }

    @Override
    public String getDetails() {
        int population = numOfPeasants;
        int contents = allProperties.getProperties().size();

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Status, LinkedList<Character>> entry : populationList.entrySet()) {
            Status status = entry.getKey();

            // Skip some statuses
            if (status == Status.Farmer || status == Status.Miner || status == Status.Merchant
                    || status == Status.Captain || status == Status.Slave) {
                continue;
            }

            LinkedList<Character> characters = entry.getValue();

            // Skip this status if empty
            if (characters.isEmpty()) {
                continue;
            }

            for (Character character : characters) {
                sb.append("    ").append(character).append("\n");
            }
        }

        String popList = sb.toString();


        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+contents+" properties"+ "\n"+
                (popList.isBlank() ? "" : "Here Lives: "+ "\n")+
                popList

        );
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

    private void createPeasants() {
        QuarterAuthority quarterCaptain = (QuarterAuthority) this.authority;
        quarterCaptain.getCharacter().setNation(this.city.getProvince().getNation());

        Random random = new Random();
        int numberOfFarmers = random.nextInt(Settings.get("farmerAmountMax")) + Settings.get("farmerAmountMin");
        numOfPeasants += numberOfFarmers;
        int numberOfMiners = random.nextInt(Settings.get("minerAmountMax")) + Settings.get("minerAmountMin");
        numOfPeasants += numberOfMiners;
        int numberOfMerchants = random.nextInt(Settings.get("merchantAmountMax")) + Settings.get("merchantAmountMin");
        numOfPeasants += numberOfMerchants;

        LinkedList<Character> farmers = new LinkedList<>();
        LinkedList<Character> miners = new LinkedList<>();
        LinkedList<Character> merchants = new LinkedList<>();
        LinkedList<Character> mayors = new LinkedList<>();
        LinkedList<Character> kings = new LinkedList<>();
        LinkedList<Character> mercenaries = new LinkedList<>();
        LinkedList<Character> nobles = new LinkedList<>();
        LinkedList<Character> vanguards = new LinkedList<>();
        LinkedList<Character> governors = new LinkedList<>();
        LinkedList<Character> captains = new LinkedList<>();
        LinkedList<Character> slaves = new LinkedList<>();

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
        populationList.put(Status.Farmer, farmers);
        populationList.put(Status.Miner, miners);
        populationList.put(Status.Merchant, merchants);
        populationList.put(Status.Mayor, mayors);
        populationList.put(Status.King, kings);
        populationList.put(Status.Mercenary, mercenaries);
        populationList.put(Status.Noble, nobles);
        populationList.put(Status.Vanguard, vanguards);
        populationList.put(Status.Governor, governors);
        populationList.put(Status.Captain, captains);
        populationList.put(Status.Slave, slaves);

        addPop(Status.Captain, quarterCaptain.getCharacter());
    }

    public void addPop(Status type, Character character){
        getPopulationList().get(type).add(character);
    }
    public HashMap<Status,LinkedList<Character>> getPopulationList() {
        return populationList;
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
