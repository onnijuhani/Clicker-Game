package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.ProvinceAuthority;
import model.characters.npc.Governor;
import model.characters.npc.Mercenary;
import model.shop.Shop;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Nation extends ControlledArea implements Details {
    private Province[] provinces;
    private Continent continent;
    private final Shop shop;
    protected LinkedList<Quarter> allQuarters;

    public Nation(String name, Continent continent, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.propertyTracker = new PropertyTracker();
        this.allQuarters = new LinkedList<>();
        this.nation = this;
        this.authority = authority;
        this.createProvinces();
        this.shop = new Shop();
        for (Province province : provinces) {
            authority.setSubordinate(province.authority);
        }
        authority.setSupervisor(authority);
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
            governor.setAuthority(getAuthority());
            governor.setNation(nation);
            Property property = PropertyCreation.createProperty(name, "Province");
            property.setOwner(governor);
            propertyTracker.addProperty(property);

            Authority authority = new ProvinceAuthority(governor);

            Province province = new Province(name, this, authority);
            provinces[i] = province;

            // set home for governor. inefficient way but governor must live in his home province
            while (true) {
                int homeIndex = random.nextInt(nation.getAllQuarters().size());
                Quarter home = nation.getAllQuarters().get(homeIndex);
                if (home.getHigher().getHigher().equals(province)) {
                    home.addCharacter(Status.Governor, governor);
                    NameCreation.generateMajorQuarterName(home);
                    break;
                }
            }

            mercenaryFactory(province, authority, random);

        }
    }

    private void mercenaryFactory(Province province, Authority authority, Random random) {
        int amountOfCities = province.getContents().size();

        for (int merc = 0; merc < amountOfCities; merc++) {
            Mercenary mercenary = new Mercenary(authority);
            mercenary.setNation(nation);
            authority.addSupporter(mercenary);
            mercenary.setAuthority(getAuthority());

            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            mercenary.getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(mercenary.getProperty());
            quarter.addCharacter(Status.Mercenary,mercenary);
            NameCreation.generateMajorQuarterName(quarter);
        }
    }

    @Override
    protected void updateCitizenCache() {
        List<Character> characters = new ArrayList<>();
        for (Province province : provinces) {
            for (City city : province.getCities()) {
                for (Quarter quarter : city.getQuarters()) {
                    characters.addAll(quarter.getImportantCharacters());
                }
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
                Status.Noble
        );
        return statusOrder;
    }


    public Shop getShop() {
        return shop;
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
