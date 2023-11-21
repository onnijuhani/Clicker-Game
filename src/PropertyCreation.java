import java.util.Random;

public class PropertyCreation {

    public static Property createProperty(String name, String area) {

        Shack shack = new Shack("empty");

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (area.equals("Nation")) {
            Property property= (randomValue < 0.75) ? new Fortress(name) : new Citadel(name);
            return property;
        }else if (area.equals("Province")){
            Property property= (randomValue < 0.4) ? new Citadel(name) : (randomValue < 0.8) ? new Castle(name) : new Manor(name);
            return property;
        }else if (area.equals("City")) {
            Property property = (randomValue < 0.2) ? new Castle(name) : (randomValue < 0.5) ? new Manor(name) : (randomValue < 0.9) ? new Mansion(name) : new Villa(name);
            return property;
        }else if (area.equals("Quarter")) {
            Property property= (randomValue < 0.05) ? new Mansion(name) : (randomValue < 0.3) ? new Villa(name) : new Cottage(name);
            return property;
        }
        return shack;
    }

}
