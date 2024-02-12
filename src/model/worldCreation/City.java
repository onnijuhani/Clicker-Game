package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Captain;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class City extends ControlledArea implements Details {

    private Quarter[] quarters;
    private Province province;

    public City(String name, Province province, Authority authority) {
        this.name = name;
        this.province = province;
        this.nation = province.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        this.createQuarters();
        for (Quarter quarter : quarters) {
            authority.setSubordinate(quarter.authority);
        }
        authority.setSupervisor(province.getAuthority());
    }
    @Override
    public String getDetails() {
        int quarterAmount = getContents().size();
        int population = getCityPopulation();


        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+quarterAmount+" districts"

        );
    }




    protected void updateCitizenCache() {

        List<Character> characters = new ArrayList<>();

        for (Quarter quarter : quarters) {
            characters.addAll(quarter.getImportantCharacters());
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
                Status.Mayor,
                Status.Governor,
                Status.Mercenary
        );
        return statusOrder;
    }


    private int getCityPopulation() {
        int population = 0;
        for (Quarter quarter: getContents()) {
            population += quarter.getNumOfPeasants();
        }
        return population;
    }

    private void createQuarters() {
        Random random = new Random();
        int numberOfQuarters = random.nextInt(Settings.get("quarterAmountMax")) + Settings.get("quarterAmountMin");
        ArrayList<String> names = NameCreation.generateQuarterNames(numberOfQuarters);
        quarters = new Quarter[numberOfQuarters];

        for (int i = 0; i < numberOfQuarters; i++) {

            String quarterName = names.get(i);

            Captain captain = captainFactory(quarterName);

            Authority authority = new QuarterAuthority(captain);

            Quarter quarter = new Quarter(quarterName, this, authority);
            quarters[i] = quarter;

            this.nation.addQuarterToNation(quarter);
        }
    }

    @NotNull
    private Captain captainFactory(String quarterName) {
        Captain captain = new Captain();
        captain.setNation(nation);
        captain.setAuthority(getAuthority());
        Property property = PropertyCreation.createProperty(quarterName, "Quarter");
        property.setOwner(captain);
        propertyTracker.addProperty(property);
        return captain;
    }

    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>(Arrays.asList(quarters));
    }
    public Province getProvince() {
        return province;
    }
    @Override
    public Area getHigher() {
        return province;
    }

    public Quarter[] getQuarters() {
        return quarters;
    }

    public void setQuarters(Quarter[] quarters) {
        this.quarters = quarters;
    }
}


