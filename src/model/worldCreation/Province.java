package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.CityAuthority;
import model.characters.npc.Mayor;


import java.util.*;
import java.util.stream.Collectors;

public class Province extends ControlledArea implements Details {


    private City[] cities;
    @Override
    public Area getHigher() {
        return nation;
    }

    public Province(String name, Nation nation, Authority authority) {
        this.name = name;
        this.nation = nation;
        this.propertyTracker = new PropertyTracker();
        this.authorityHere = authority;
        this.createCities();
        for (City city : cities) {
            authority.setSubordinate(city.authorityHere);
        }
        authority.setSupervisor(nation.getAuthorityHere());
    }
    @Override
    public String getDetails() {
        return ("Province: " + name + " Belongs to: " + nation.getName());
    }

    private void createCities() {
        Random random = new Random();
        int numberOfCities = random.nextInt(Settings.getInt("cityAmountMax")) + Settings.getInt("cityAmountMin");
        cities = new City[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {

            String cityName = NameCreation.generateCityName();

            Mayor mayor = mayorFactory(cityName);

            Authority authority = new CityAuthority(mayor);

            City city = new City(cityName, this, authority);

            authority.setAreaUnderAuthority(city);

            cities[i] = city;

            setMayorHome(random, city, mayor);

        }
    }


    private Mayor mayorFactory(String cityName) {
        Mayor mayor = new Mayor(authorityHere);
        mayor.getRole().setNation(nation);
        mayor.getRole().setAuthority(getAuthorityHere());
        Property property = PropertyCreation.createProperty(cityName, "City", mayor.getPerson());
        propertyTracker.addProperty(property);
        return mayor;
    }

    private static void setMayorHome(Random random, City city, Mayor mayor) {
        int homeIndex = random.nextInt(city.getContents().size());
        Quarter home = city.getContents().get(homeIndex);
        home.addCitizen(Status.Mayor, mayor.getPerson());
        mayor.getPerson().getProperty().setLocation(home);
        NameCreation.generateMajorQuarterName(home);
    }

    @Override
    public ArrayList<City> getContents() {
        return new ArrayList<>(Arrays.asList(cities));
    }

    protected void updateCitizenCache() {

        List<Character> characters = new ArrayList<>();

        for (City city : cities) {
            for (Quarter quarter : city.getQuarters()) {
                characters.addAll(quarter.getImportantCharacters());
            }
        }

        List<Status> statusOrder = getImportantStatusRank();
        citizenCache = characters.stream()
                .filter(character -> statusOrder.contains(character.getRole().getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getRole().getStatus())))
                .collect(Collectors.toList());
    }


    @Override
    public List<Status> getImportantStatusRank() {
        return List.of(
                Status.King,
                Status.Vanguard,
                Status.Governor
        );
    }

    public City[] getCities() {
        return cities;
    }

    public void setCities(City[] cities) {
        this.cities = cities;
    }
}
