package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyFactory;
import model.buildings.PropertyTracker;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Captain;
import model.resourceManagement.TransferPackage;


import java.util.*;
import java.util.stream.Collectors;

public class City extends ControlledArea implements Details {

    private Quarter[] quarters;
    private final Province province;

    public City(String name, Province province, Authority authority) {
        this.name = name;
        this.province = province;
        this.nation = province.getNation();
        this.propertyTracker = new PropertyTracker();
        this.authorityHere = authority;
        this.createQuarters();
        for (Quarter quarter : quarters) {
            authority.setSubordinate(quarter.authorityHere);
        }
        authority.setSupervisor(province.getAuthorityHere());
    }
    @Override
    public String getDetails() {
        int quarterAmount = getContents().size();
        int population = getCityPopulation();


        return ("Authority here is: " + this.getAuthorityHere() + "\n"+
                "Living in a: " + this.getAuthorityHere().getCharacterInThisPosition().getPerson().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+quarterAmount+" districts"

        );
    }



    @Override
    protected void updateCitizenCache() {

        List<Character> characters = new ArrayList<>();

        for (Quarter quarter : quarters) {
            characters.addAll(quarter.getImportantCharacters());
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
                Status.Mayor,
                Status.Governor,
                Status.Mercenary
        );
    }


    private int getCityPopulation() {
        int population = 0;
        for (Quarter quarter: getContents()) {
            population += quarter.getNumOfPeasants();
        }
        return population;
    }

    private void createQuarters() {
        Random random = Settings.getRandom();
        int numberOfQuarters = random.nextInt(Settings.getInt("quarterAmountMax")) + Settings.getInt("quarterAmountMin");
        List<String> names = NameCreation.generateQuarterNames(numberOfQuarters);
        quarters = new Quarter[numberOfQuarters];

        for (int i = 0; i < numberOfQuarters; i++) {

            String quarterName = names.get(i);

            Captain captain = captainFactory(quarterName);

            Authority authority = new QuarterAuthority(captain );

            Quarter quarter = new Quarter(quarterName, this, authority);

            // add into claimed area
            nation.addClaimedArea(quarter);

            authority.setAreaUnderAuthority(quarter);

            quarters[i] = quarter;

            this.nation.addQuarterToNation(quarter);
        }
    }


    private Captain captainFactory(String quarterName) {
        Captain captain = new Captain(authorityHere);
        TransferPackage startingPackage = new TransferPackage(100, 200, 300);
        captain.getPerson().getWallet().addResources(startingPackage);
        captain.getRole().setNation(nation);
        captain.getRole().setAuthority(getAuthorityHere());
        Property property = PropertyFactory.createProperty(quarterName, "Quarter", captain.getPerson()); //owner is set in the method
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

    @Override
    public void setNation(Nation nation){
        this.nation = nation;
    }


}


