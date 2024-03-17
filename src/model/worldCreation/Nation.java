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

import java.util.*;
import java.util.stream.Collectors;

public class Nation extends ControlledArea implements Details {
    private Province[] provinces;
    private final Continent continent;
    private final Shop shop;
    protected LinkedList<Quarter> allQuarters;
    private List<Character> nationsGenerals = null;
    private boolean isGeneralsCacheValid = false;

    public Nation(String name, Continent continent, Authority authority) {
        this.name = name;
        this.continent = continent;
        this.propertyTracker = new PropertyTracker();
        this.allQuarters = new LinkedList<>();
        this.nation = this;
        this.authorityHere = authority;
        this.createProvinces();
        this.shop = new Shop();
        for (Province province : provinces) {
            authority.setSubordinate(province.authorityHere);
        }
        authority.setSupervisor(authority);

    }
    @Override
    public String getDetails() {

        StringBuilder detailsBuilder = new StringBuilder();

        if (!nationsGenerals.isEmpty()) {
            detailsBuilder.append("Generals (").append(nationsGenerals.size()).append("):");
            for (Character general : nationsGenerals) {
                String generalDetails = String.format("\n- %s, Status: %s, Property: %s",
                        general.getName(),
                        general.getRole().getStatus(),
                        general.getPerson().getProperty().getClass().getSimpleName());
                detailsBuilder.append(generalDetails);
            }
        } else {
            detailsBuilder.append("\nNo generals found.");
        }

        return detailsBuilder.toString();
    }

    public void updateGenerals(){
        setGeneralsCacheValid(false);
        collectGenerals();
    }

    /**
     Generals are some of the main authority characters and their supporters who also have property capable of having an army
     **/
    public void collectGenerals() {
        if (isGeneralsCacheValid) {
            return;
        }
        Set<Status> generalStatuses = EnumSet.of(Status.Governor, Status.Vanguard, Status.Mercenary, Status.King);

        Set<String> validProperties = Set.of("Castle", "Citadel", "Fortress");

        nationsGenerals = new ArrayList<>();
        for (Quarter quarter : allQuarters) {
            quarter.updateCitizenCache();

            quarter.getPopulationMap().entrySet().stream()
                    .filter(entry -> generalStatuses.contains(entry.getKey()))
                    .flatMap(entry -> entry.getValue().stream())
                    .filter(character -> validProperties.contains(character.getProperty().getClass().getSimpleName()))
                    .forEach(person -> {
                        nationsGenerals.add(person.getCharacter());
                    });
        }
        isGeneralsCacheValid = true;
    }

    private void createProvinces() {
        Random random = new Random();
        int numberOfProvinces = random.nextInt(Settings.getInt("provinceAmountMax")) + Settings.getInt("provinceAmountMin");
        provinces = new Province[numberOfProvinces];

        for (int i = 0; i < numberOfProvinces; i++) {

            // generates name for the province
            String provinceName = NameCreation.generateProvinceName();

            // generates governor
            Governor governor = governorFactory(provinceName);

            //assigns the governor as the authority of the province
            Authority authority = new ProvinceAuthority(governor);

            // new province is created
            Province province = new Province(provinceName, this, authority);
            provinces[i] = province;

            // set home for governor. Governor must live in his home province.
            setGovernorHome(random, province, governor);

            // generate mercenaries for governor
            mercenaryFactory(province, authority, random);

        }
    }


    private Governor governorFactory(String provinceName) {
        Governor governor = new Governor(authorityHere);
        governor.getRole().setAuthority(getAuthorityHere());
        governor.getRole().setNation(nation);
        Property property = PropertyCreation.createProperty(provinceName, "Province", governor.getPerson());
        propertyTracker.addProperty(property);
        return governor;
    }

    private void setGovernorHome(Random random, Province province, Governor governor) {
        while (true) {
            int homeIndex = random.nextInt(nation.getAllQuarters().size());
            Quarter home = nation.getAllQuarters().get(homeIndex);
            if (home.getHigher().getHigher().equals(province)) {
                home.addCitizen(Status.Governor, governor.getPerson());
                NameCreation.generateMajorQuarterName(home);
                governor.getPerson().getProperty().setLocation(home);
                break;
            }
        }
    }
    public boolean isGeneralsCacheValid() {
        return isGeneralsCacheValid;
    }
    public void setGeneralsCacheValid(boolean generalsCacheValid) {
        isGeneralsCacheValid = generalsCacheValid;
    }


    private void mercenaryFactory(Province province, Authority authority, Random random) {
        int amountOfCities = province.getContents().size();

        for (int merc = 0; merc < amountOfCities; merc++) {
            Mercenary mercenary = new Mercenary(authority);
            mercenary.getRole().setNation(nation);
            authority.addSupporter(mercenary);


            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            mercenary.getPerson().getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(mercenary.getPerson().getProperty());
            quarter.addCitizen(Status.Mercenary,mercenary.getPerson());
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
                .filter(character -> statusOrder.contains(character.getRole().getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getRole().getStatus())))
                .collect(Collectors.toList());
    }


    @Override
    public List<Status> getImportantStatusRank() {
        return List.of(
                Status.King,
                Status.Vanguard,
                Status.Noble
        );
    }

    public List<Character> getNationsGenerals() {
        return nationsGenerals;
    }

    public void setNationsGenerals(List<Character> nationsGenerals) {
        this.nationsGenerals = nationsGenerals;
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
