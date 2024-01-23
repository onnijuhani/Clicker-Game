package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.ProvinceAuthority;
import model.characters.npc.Governor;
import model.characters.npc.Mercenary;
import model.shop.Exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Nation extends ControlledArea implements Details {
    private Province[] provinces;
    private Continent continent;
    private final Exchange exchange;
    protected LinkedList<Quarter> allQuarters;

    public Nation(String name, Continent continent, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.propertyTracker = new PropertyTracker();
        this.allQuarters = new LinkedList<>();
        this.nation = this;
        this.createProvinces();
        super.authority = authority;
        this.exchange = new Exchange();
        Authority king = this.authority;
        king.getCharacter().setNation(this);
        for (Province province : provinces) {
            king.setSubordinate(province.authority);
        }
    }
    @Override
    public String getDetails() {
        return ("Nation: " + name + " Belongs to: " + continent.getName());
    }

    private void createProvinces() {
        Random random = new Random();
        int numberOfProvinces = random.nextInt(Settings.get("provinceAmountMax")) + Settings.get("provinceAmountMin");
        provinces = new Province[numberOfProvinces];

        for (int i = 0; i < numberOfProvinces; i++) {

            String name = NameCreation.generateProvinceName();

            Governor governor = new Governor();
            Property property = PropertyCreation.createProperty(name, "Province");
            property.setOwner(governor);
            propertyTracker.addProperty(property);

            Authority authority = new ProvinceAuthority(governor);

            Province province = new Province(name, this, authority);
            provinces[i] = province;

            // set home for governor
            int homeIndex = random.nextInt(nation.getAllQuarters().size());
            Quarter home = nation.getAllQuarters().get(homeIndex);
            home.addPop(Status.Governor,governor);

            mercenaryFactory(province, authority, random);

        }
    }

    private void mercenaryFactory(Province province, Authority authority, Random random) {
        int amountOfCities = province.getContents().size();

        for (int merc = 0; merc < amountOfCities; merc++) {
            Mercenary mercenary = new Mercenary(authority);
            mercenary.setNation(nation);
            authority.addSupporter(mercenary);

            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            mercenary.getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(mercenary.getProperty());
            quarter.addPop(Status.Mercenary,mercenary);
        }
    }

    public Exchange getExchange() {
        return exchange;
    }
    @Override
    public ArrayList<Province> getContents() {
        return new ArrayList<>(Arrays.asList(provinces));
    }
    @Override
    public Area getHigher() {
        return continent;
    }
    public LinkedList<Quarter> getAllQuarters() {
        return allQuarters;
    }

    public void addQuarterToNation(Quarter quarter){
        getAllQuarters().add(quarter);
    }
}
