package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Captain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class City extends ControlledArea implements Details {
    private Quarter[] quarters;
    private Province province;

    public City(String name, Province province, Authority authority) {
        this.name = name;
        this.province = province;
        this.nation = province.getNation();
        this.propertyTracker = new PropertyTracker();
        this.createQuarters();
        super.authority = authority;
        Authority mayor = this.authority;
        mayor.getCharacter().setNation(getProvince().getNation());
        mayor.setSupervisor(province.getAuthority());
        for (Quarter quarter : quarters) {
            mayor.setSubordinate(quarter.authority);
        }
    }
    @Override
    public String getDetails() {
        int population = 0;
        int contents = getContents().size();

        for (Quarter quarter: getContents()){
            population += quarter.getNumOfPeasants();
        }

        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+contents+" districts"


        );
    }

    private void createQuarters() {
        Random random = new Random();
        int numberOfQuarters = random.nextInt(Settings.get("quarterAmountMax")) + Settings.get("quarterAmountMin");
        ArrayList<String> names = NameCreation.generateQuarterNames(numberOfQuarters);
        quarters = new Quarter[numberOfQuarters];

        for (int i = 0; i < numberOfQuarters; i++) {
            String name = names.get(i);

            Captain captain = new Captain();
            captain.setNation(nation);
            Property property = PropertyCreation.createProperty(name, "Quarter");
            property.setOwner(captain);
            propertyTracker.addProperty(property);

            Authority authority = new QuarterAuthority(captain);

            Quarter quarter = new Quarter(name, this, authority);
            quarters[i] = quarter;
            this.nation.addQuarterToNation(quarter);
        }
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
}
