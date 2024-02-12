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
import org.jetbrains.annotations.NotNull;

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
        this.authority = authority;
        this.createCities();
        for (City city : cities) {
            authority.setSubordinate(city.authority);
        }
        authority.setSupervisor(nation.getAuthority());
    }
    @Override
    public String getDetails() {
        return ("Province: " + name + " Belongs to: " + nation.getName());
    }

    private void createCities() {
        Random random = new Random();
        int numberOfCities = random.nextInt(Settings.get("cityAmountMax")) + Settings.get("cityAmountMin");
        cities = new City[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {

            String cityName = NameCreation.generateCityName();

            Mayor mayor = mayorFactory(cityName);

            Authority authority = new CityAuthority(mayor);

            City city = new City(cityName, this, authority);
            cities[i] = city;

            setMayorHome(random, city, mayor);

        }
    }

    @NotNull
    private Mayor mayorFactory(String cityName) {
        Mayor mayor = new Mayor();
        mayor.setNation(nation);
        mayor.setAuthority(getAuthority());
        Property property = PropertyCreation.createProperty(cityName, "City");
        property.setOwner(mayor);
        propertyTracker.addProperty(property);
        return mayor;
    }

    private static void setMayorHome(Random random, City city, Mayor mayor) {
        int homeIndex = random.nextInt(city.getContents().size());
        Quarter home = city.getContents().get(homeIndex);
        home.addCharacter(Status.Mayor, mayor);
        mayor.getProperty().setLocation(home);
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
                .filter(character -> statusOrder.contains(character.getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getStatus())))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Status> getImportantStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King,
                Status.Vanguard,
                Status.Governor
        );
        return statusOrder;
    }

    public City[] getCities() {
        return cities;
    }

    public void setCities(City[] cities) {
        this.cities = cities;
    }
}
