import java.util.ArrayList;
import java.util.HashMap;


public class CreateWorld {

    public static void main(String[] args) {

        Farmer farmer = new Farmer();
        farmer.generate(100,0,0);
        System.out.println(farmer.getFood().getAmount());

        Slave slave = new Slave();
        slave.generate(10,10,10);


        Property property = PropertyCreation.createProperty("name", "Quarter");
        Captain captain = new Captain();
        QuarterAuthority quarterAuthority = new QuarterAuthority(property, captain);

        HashMap tax = quarterAuthority.enforceTax();
        System.out.println(tax);
        HashMap taxFood = farmer.releaseTax(tax);
        System.out.println(taxFood);

        Food taxedfood = (Food) taxFood.get(Resource.Food);
        Double taxamount = taxedfood.getAmount();
        System.out.println("amount: " + taxamount);

        quarterAuthority.collectTax(taxFood);
        quarterAuthority.collectTax(slave.releaseTax(quarterAuthority.enforceTax()));
        System.out.println("Wealth: "+quarterAuthority.property.vault.getWalletValues());



        World world = new World("Medium World", Size.LARGE);


        Time time = new Time();        String glock = time.getClock();
        int days = 5000;
        for (int i = 0; i < days; i++){
            time.incrementDay();
        }
        glock = time.getClock();
        System.out.println(glock);




//        for (int i = 0; i < 50; i++) {
//            int objectCount = 0;
//
//            ArrayList<Continent> continents = world.getContents();
//            for (Continent continent : continents) {
//                System.out.println("\n");
//                System.out.println("Continent:");
//                objectCount++; // Increment for the continent
//                ArrayList<Nation> nations = continent.getContents();
//                System.out.println(continent.getName());
//                for (Nation nation : nations) {
//                    objectCount += 3; // Increment for the nation
//                    ArrayList<Province> provinces = nation.getContents();
//                    System.out.println("\t" + "Nation:");
//                    System.out.println("\t" + nation.getName());
//                    System.out.println("\tAuthority Figure: " + nation.authority.getAuthorityType() + " In a: " + nation.authority.getPropertyType());
//                    for (Province province : provinces) {
//                        objectCount += 3; // Increment for the province
//                        ArrayList<City> cities = province.getContents();
//                        System.out.println("\t\t" + "Province:");
//                        System.out.println("\t\t" + province.getName());
//                        System.out.println("\t\tAuthority Figure: " + province.authority.getAuthorityType() + " In a: " + province.authority.getPropertyType());
//                        for (City city : cities) {
//                            objectCount += 3; // Increment for the city and its property
//                            System.out.println("\t\t\t" + "City:");
//                            System.out.println("\t\t\t" + city.getName());
//                            System.out.println("\t\t\tAuthority Figure: " + city.authority.getAuthorityType() + " In a: " + city.authority.getPropertyType());
//                            ArrayList<Quarter> quarters = city.getContents();
//                            for (Quarter quarter : quarters) {
//                                objectCount += 3; // Increment for the quarter and its property
//                                System.out.println("\t\t\t\t" + quarter.fullHierarchyInfo());
//                                System.out.println("\t\t\t\tAuthority Figure: " + quarter.authority.getAuthorityType() + " In a: " + quarter.authority.getPropertyType());
//                            }
//                        }
//                    }
//                }
//            }
//
//            System.out.println("Total objects printed: " + objectCount);
//        }
    }
}
