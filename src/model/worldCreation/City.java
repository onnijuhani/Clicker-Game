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
        int quarterAmount = getContents().size();
        int population = getCityPopulation();


        String popList = getCityImportantCharactersString();

        return ("Authority here is: " + this.getAuthority() + "\n"+
                "Living in a: " + this.getAuthority().getProperty() + "\n"+
                "Population: " + population + "\n"+
                "Comprised of "+quarterAmount+" districts" + "\n"+
                (popList.isBlank() ? "" : "Here Lives: "+ "\n")+
                popList
        );
    }

    public String getCityImportantCharactersString() {
        StringBuilder cityCharactersSb = new StringBuilder();

        for (Quarter quarter : quarters) {
            cityCharactersSb.append(quarter.getCitizens());
        }

        return cityCharactersSb.toString();
    }

    public List<Character> getImportantCharacters() {
        List<Character> characters = new ArrayList<>();

        // Collect all important characters from quarters
        for (Quarter quarter : quarters) {
            characters.addAll(quarter.getImportantCharactersList());
        }

        // Define the status rank order
        List<Status> statusOrder = getImportantStatusRank();

        // Sort the characters list based on the status rank
        return characters.stream()
                .filter(character -> statusOrder.contains(character.getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getStatus())))
                .collect(Collectors.toList());
    }
    @NotNull
    @Override
    public List<Status> getImportantStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary
                //doesn't include unimportant ranks also Mayor excluded since he will show up as authority anyway
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
