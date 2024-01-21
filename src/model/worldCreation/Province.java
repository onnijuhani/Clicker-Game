package model.worldCreation;

import model.NameCreation;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.authority.Authority;
import model.characters.authority.CityAuthority;
import model.characters.npc.Mayor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Province extends ControlledArea implements Details {
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
        return ("Province: " + name + " Belongs to: " + nation.getName());
    }

    private void createCities() {
        Random random = new Random();
        int numberOfCities = random.nextInt(8) + 2;
        cities = new City[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {

            String name = NameCreation.generateCityName();

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
