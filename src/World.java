import java.util.*;

interface Details {
    String getDetails(); //tuo nimen ja mihin yläluokkaan se kuuluu
}

enum Size {
    SMALL,
    MEDIUM,
    LARGE
}

abstract class Area implements Details {
    protected String name;
    protected String areaName = "Area";
    public PropertyTracker propertyTracker;

    abstract ArrayList getContents();
    abstract String getName();

    abstract Area getHigher();

    @Override
    public String toString() {
        return getName();
    }

}

abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }
    Authority authority;
}

public class World extends Area implements Details {
    private String name;
    @Override
    public String getName() {
        return this.name;
    }
    private Continent[] continents;

    @Override
    public Area getHigher() {
        return this;
    }

    public Size size;

    @Override
    public ArrayList<Continent> getContents() {
        return new ArrayList<>(Arrays.asList(continents));
    }

    public World(String name, Size size){
        this.name = name;
        this.size = size;
        this.createContinents();
    }



    // Method to create random number of continents
    private void createContinents() {
        Random random = new Random();
        int numberOfContinents = size == size.SMALL ? 2 : size == size.MEDIUM ? 4 : size == size.LARGE ? 6 : -1;
        continents = new Continent[numberOfContinents];
        for (int i = 0; i < numberOfContinents; i++) {
            continents[i] = new Continent(NameCreation.generateContinentNames(), this);
        }
    }

    // Print continent details from World object
    public void printContinents() {
        for (Continent continent : continents) {
            continent.getDetails();
        }
    }

    public String getDetails() {
        return("World Name: " + this.getName());
    }


}

class Continent extends Area implements Details {

    private String name;
    @Override
    public String getName() {
        return this.name;
    }
    private World world;
    @Override
    public Area getHigher() {
        return world;
    }
    private Nation[] nations;




    public Continent(String name, World world) {
        this.propertyTracker = new PropertyTracker();
        this.name = name;
        this.world = world;
        this.createNations();
    }



    public String getDetails() {
        return("Continent: " + name + " Belongs to: " + world.getName());
    }

    private void createNations() {

        Random random = new Random();
        int numberOfNations = random.nextInt(3) + 1;
        nations = new Nation[numberOfNations];

        for (int i = 0; i < numberOfNations; i++) {

            Orientation styleName = random.nextInt(2) == 0 ? Orientation.Imperial : Orientation.Democratic;
            OrientationStyle orientationStyle = new OrientationStyle(styleName); //uusi style objekti nation luontia varten
            String nationName = NameCreation.generateNationName(styleName);

            King king = new King();
            Property property = PropertyCreation.createProperty(name, "Nation");
            property.setOwner(king);
            propertyTracker.addProperty(property);

            Authority authority = new NationAuthority(king);

            Nation nation = new Nation(nationName, this, orientationStyle, authority);

            nations[i] = nation;

            int provinceAmount = nation.getContents().size();

            for (int nob = 0; nob < 4; nob++) {
                Noble noble = new Noble(authority);
                authority.addSupporter(noble);
            }

            for (int vang = 0; vang < provinceAmount ; vang++) {
                Vanguard vanguard = new Vanguard(authority);
                authority.addSupporter(vanguard);
                vanguard.getProperty().setLocation(nation.getContents().get(vang).getContents().get(0).getContents().get(0));
                nation.getContents().get(vang).getContents().get(0).getContents().get(0).propertyTracker.addProperty(vanguard.getProperty());
            }
        }
    }

    @Override
    public ArrayList<Nation> getContents() {
        return new ArrayList<>(Arrays.asList(nations));
    }

}

class Nation extends ControlledArea implements Details {
    private String name;
    @Override
    public String getName() {
        return this.name;
    }
    private Province[] provinces;
    private OrientationStyle orientationStyle; //Style luokka
    public Orientation orientation;
    private Continent continent;

    protected LinkedList<Quarter> allQuarters;
    public LinkedList<Quarter> getAllQuarters() {
        return allQuarters;
    }

    @Override
    public Area getHigher() {
        return continent;
    }

    public Nation(String name, Continent continent, OrientationStyle orientationStyle, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.orientationStyle = orientationStyle;
        this.orientation = orientationStyle.getName();
        this.propertyTracker = new PropertyTracker();
        this.allQuarters = new LinkedList<>();
        this.createProvinces();
        super.authority = authority;
        Authority king = this.authority;
        king.getCharacter().setNation(this);
        for (Province province : provinces) {
            king.setAuthOver(province.authority);
        }
    }

    public String getDetails() {
        return(orientation + " " + "Nation: " + name + " Belongs to: " + continent.getName());
    }

    private void createProvinces() {
        Random random = new Random();
        int numberOfProvinces = random.nextInt(7) + 2;
        provinces = new Province[numberOfProvinces];

        for (int i = 0; i < numberOfProvinces; i++) {

            String name = NameCreation.generateProvinceName(orientation);

            Governor governor = new Governor();
            Property property = PropertyCreation.createProperty(name, "Province");
            property.setOwner(governor);
            propertyTracker.addProperty(property);

            Authority authority = new ProvinceAuthority(governor);

            for (int merc = 0; merc < 6; merc++) {
                Mercenary mercenary = new Mercenary(authority);
                authority.addSupporter(mercenary);
                mercenary.setNation(this);
            }

            Province province = new Province(name, this, authority);
            provinces[i] = province;
        }
    }
    @Override
    public ArrayList<Province> getContents() {
        return new ArrayList<>(Arrays.asList(provinces));
    }
}

class Province extends ControlledArea implements Details {
    private String name;
    @Override
    public String getName() {
        return this.name;
    }
    private City[] cities;
    private Nation nation;

    public Nation getNation() {
        return nation;
    }
    @Override
    public Area getHigher() {
        return nation;
    }

    public Province(String name, Nation nation, Authority authority) {
        this.name = name;
        this.nation = nation;
        this.propertyTracker = new PropertyTracker();
        this.createCities();
        super.authority = authority;
        Authority governor = this.authority;
        governor.getCharacter().setNation(nation);
        governor.setAuthUnder(nation.getAuthority());
        for (City city : cities) {
            governor.setAuthOver(city.authority);
        }
    }

    public String getDetails() {
        return("Province: " + name + " Belongs to: " + nation.getName());
    }

    private void createCities() {
        Random random = new Random();
        int numberOfCities = random.nextInt(8) + 2;
        cities = new City[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {

            String name = NameCreation.generateCityName(nation.orientation);

            Mayor mayor = new Mayor();
            Property property = PropertyCreation.createProperty(name, "City");
            property.setOwner(mayor);
            propertyTracker.addProperty(property);
            Authority authority = new CityAuthority(mayor);

            City city = new City(name, this, authority);

            cities[i] = city;

        }
    }
    @Override
    public ArrayList<City> getContents() {
        return new ArrayList<>(Arrays.asList(cities));
    }

}

class City extends ControlledArea implements Details {
    private String name;
    private String areaName = "City";
    @Override
    public String getName() {
        return this.name;
    }

    private Quarter[] quarters;

    private Province province;
    public Province getProvince() {
        return province;
    }
    @Override
    public Area getHigher() {
        return province;
    }

    public City(String name, Province province, Authority authority) {
        this.name = name;
        this.province = province;
        this.propertyTracker = new PropertyTracker();
        this.createQuarters();
        super.authority = authority;
        Authority mayor = this.authority;
        mayor.getCharacter().setNation(getProvince().getNation());
        mayor.setAuthUnder(province.getAuthority());
        for (Quarter quarter : quarters) {
            mayor.setAuthOver(quarter.authority);
        }
    }


    public String getDetails() {
        return("City: " + name + " Belongs to: " + province.getName());
    }

    private void createQuarters() {
        Random random = new Random();
        int numberOfQuarters = random.nextInt(30) + 10;
        ArrayList<String> names = NameCreation.generateQuarterNames(numberOfQuarters);
        quarters = new Quarter[numberOfQuarters];


        for (int i = 0; i < numberOfQuarters; i++) {
            String name = names.get(i);

            Captain captain = new Captain();
            Property property = PropertyCreation.createProperty(name, "Quarter");
            property.setOwner(captain);
            propertyTracker.addProperty(property);

            Authority authority = new QuarterAuthority(captain);

            Quarter quarter = new Quarter(name, this, authority);
            quarters[i] = quarter;
            this.getProvince().getNation().allQuarters.add(quarter);

        }
    }
    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>(Arrays.asList(quarters));
    }

}

class Quarter extends ControlledArea implements Details {

    private String name;
    private String areaName = "Quarter";
    @Override
    public String getName() {
        return this.name;
    }



    private HashMap<String, ArrayList<Character>> populationList;
    public HashMap<String, ArrayList<Character>> getPopulationList() {
        return populationList;
    }

    private PropertyTracker allProperties;
    private City city;
    private int numOfPeasants;

    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        Authority captain = this.authority;
        this.populationList = new HashMap<>();
        captain.getCharacter().setNation(city.getProvince().getNation());
        captain.setAuthUnder(city.getAuthority());
        createPeasants();
    }

    public String returnAllInformation(){
        Quarter theQuarter = this;
        City theCity = city;
        Province theProvince = city.getProvince();
        Nation theNation = theProvince.getNation();

        return(
                "Quarter: " + theQuarter +
                ". City: " + theCity +
                ". Province: " + theProvince +
                ". Nation: " + theNation +
                ". Population: " + numOfPeasants
        );
    }


    @Override
    public Area getHigher() {
        return city;
    }

    public String getDetails() {
        return("Quarter: " + name + " Belongs to city: " + city.getName());
    }
    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>();
    }

    public String fullHierarchyInfo(){
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
        QuarterAuthority captain = (QuarterAuthority) this.authority;
        captain.getCharacter().setNation(this.city.getProvince().getNation());

        Random random = new Random();
        int numberOfFarmers = random.nextInt(150) + 3;
        numOfPeasants += numberOfFarmers;
        int numberOfMiners = random.nextInt(130) + 3;
        numOfPeasants += numberOfMiners;
        int numberOfMerchants = random.nextInt(50) + 3;
        numOfPeasants += numberOfMerchants;

        ArrayList<Character> farmers = new ArrayList<>();
        ArrayList<Character> miners = new ArrayList<>();
        ArrayList<Character> merchants = new ArrayList<>();

        for (int peasant = 0; peasant < numberOfFarmers; peasant++) {
            Farmer farmer = new Farmer(captain);
            captain.addPeasant(farmer);
            farmers.add(farmer);
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(captain);
            captain.addPeasant(miner);
            miners.add(miner);
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(captain);
            captain.addPeasant(merch);
            merchants.add(merch);
        }
        populationList.put("farmers", farmers);
        populationList.put("miners", miners);
        populationList.put("merchants", merchants);
    }
}