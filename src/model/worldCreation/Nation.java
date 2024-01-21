package model.worldCreation;

import model.NameCreation;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.authority.Authority;
import model.characters.authority.ProvinceAuthority;
import model.characters.npc.Governor;
import model.characters.npc.Mercenary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Nation extends ControlledArea implements Details {
    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    private Province[] provinces;

    private Continent continent;

    protected LinkedList<Quarter> allQuarters;

    public LinkedList<Quarter> getAllQuarters() {
        return allQuarters;
    }

    @Override
    public Area getHigher() {
        return continent;
    }

    public Nation(String name, Continent continent, Authority authority) {
        this.name = name;
        this.continent = continent;
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
        return ("Nation: " + name + " Belongs to: " + continent.getName());
    }

    private void createProvinces() {
        Random random = new Random();
        int numberOfProvinces = random.nextInt(8) + 2;
        provinces = new Province[numberOfProvinces];

        for (int i = 0; i < numberOfProvinces; i++) {

            String name = NameCreation.generateProvinceName();

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
