import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

interface Details {
    String getDetails(); //tuo nimen ja mihin yläluokkaan se kuuluu
}

enum Size {
    SMALL,
    MEDIUM,
    LARGE
}

abstract class Area {
    String name;
    String areaName = "Area";

    abstract ArrayList getContents();
    abstract String getName();
}

abstract class ControlledArea extends Area {
    Authority authority;


}

public class World extends Area implements Details {
    private String name;
    @Override
    public String getName() {
        return this.name;
    }
    private Continent[] continents;

    private Size size;

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
    private Nation[] nations;

    // Constructor for Continent
    public Continent(String name, World world) {
        this.name = name;
        this.world = world;
        this.createNations();
    }

    // Getters and setters...

    public String getDetails() {
        return("Continent: " + name + " Belongs to: " + world.getName());
    }

    private void createNations() {
        Random random = new Random();
        int numberOfNations = random.nextInt(3) + 1;
        nations = new Nation[numberOfNations];
        for (int i = 0; i < numberOfNations; i++) {
            Orientation styleName = random.nextInt(2) == 0 ? Orientation.Imperial : Orientation.Democratic;
            Style style = new Style(styleName); //uusi style objekti nation luontia varten
            String name = NameCreation.generateNationName(styleName);
            Property property = PropertyCreation.createProperty(name, "Nation");
            King king = new King();
            Authority authority = new NationAuthority(property, king);
            nations[i] = new Nation(name, this, style, authority);
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
    private Style style; //Style luokka
    public Orientation orientation;
    private Continent continent;

    private String areaName = "Nation";

    public Nation(String name, Continent continent, Style style, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.style = style;
        this.orientation = style.getName();
        this.createProvinces();
        super.authority = authority;
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
            Property property = PropertyCreation.createProperty(name, "Province");
            Governor governor = new Governor();
            Authority authority = new ProvinceAuthority(property, governor);
            provinces[i] = new Province(name, this, authority);
        }
    }
    @Override
    public ArrayList<Province> getContents() {
        return new ArrayList<>(Arrays.asList(provinces));
    }
}

class Province extends ControlledArea implements Details {
    private String name;

    private String areaName = "Province";
    @Override
    public String getName() {
        return this.name;
    }

    private City[] cities;

    private Nation nation;

    public Nation getNation() {
        return nation;
    }


    public Province(String name, Nation nation, Authority authority) {
        this.name = name;
        this.nation = nation;
        this.createCities();
        super.authority = authority;
    }

    public String getDetails() {
        return("Province: " + name + " Belongs to: " + nation.getName());
    }

    private void createCities() {
        Random random = new Random();
        int numberOfCities = random.nextInt(4) + 1;
        cities = new City[numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            String name = NameCreation.generateCityName(nation.orientation);
            Property property = PropertyCreation.createProperty(name, "City");
            Mayor mayor = new Mayor();
            Authority authority = new CityAuthority(property, mayor);
            cities[i] = new City(name, this, authority);
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

    public City(String name, Province province, Authority authority) {
        this.name = name;
        this.province = province;
        this.createQuarters();
        super.authority = authority;
    }

    public String getDetails() {
        return("City: " + name + " Belongs to: " + province.getName());
    }

    private void createQuarters() {
        Random random = new Random();
        int numberOfQuarters = random.nextInt(3) + 2;
        ArrayList<String> names = NameCreation.generateQuarterNames(numberOfQuarters);
        quarters = new Quarter[numberOfQuarters];
        for (int i = 0; i < numberOfQuarters; i++) {
            String name = names.get(i);
            Property property = PropertyCreation.createProperty(name, "Quarter");
            Captain captain = new Captain();
            Authority authority = new QuarterAuthority(property, captain);
            quarters[i] = new Quarter(name, this, authority);
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

    private City city;

    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        super.authority = authority;
    }

    public String getDetails() {
        return("Quarter: " + name + " Belongs to: " + city.getName());
    }
    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>();
    }

    public String fullHierarchyInfo(){
        Province prov = city.getProvince();
        Nation nat = prov.getNation();
        return name + " in a: " + city.getName() + " -city. Of the: " + prov.getName() +" -province. Part of: " + nat.getName() + " -Nation.";
    }
}