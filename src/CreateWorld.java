import java.util.ArrayList;
import java.util.HashMap;


public class CreateWorld {

    public static void main(String[] args) {

        Property villa = new Villa("nice villa");
        Captain captain = new Captain();
        QuarterAuthority quarterAuthority = new QuarterAuthority(villa, captain);

        for (int i = 0; i<20; i++) {
            Farmer farmer = new Farmer(quarterAuthority);
            farmer.generate(110,0,0);
            quarterAuthority.addPeasant(farmer);
            quarterAuthority.collectTax(farmer.releaseTax(quarterAuthority.enforceTax()));

            Merchant merch = new Merchant(quarterAuthority);
            merch.generate(0,0,110);
            quarterAuthority.addPeasant(merch);
            quarterAuthority.collectTax(merch.releaseTax(quarterAuthority.enforceTax()));

            Miner miner = new Miner(quarterAuthority);
            miner.generate(0, 110, 0);
            quarterAuthority.addPeasant(miner);
            quarterAuthority.collectTax(miner.releaseTax(quarterAuthority.enforceTax()));
        }
        System.out.println("Captains wealth: "+quarterAuthority.property.getVault().getWalletValues());



        Property castle = new Castle("nice castle");
        AuthorityCharacter mayorr = new Mayor();
        CityAuthority mayor = new CityAuthority(castle, mayorr);

        mayor.collectTax(quarterAuthority);
        System.out.println("Mayors wealth: "+mayor.property.getVault().getWalletValues());
        System.out.println("Captains wealth: "+quarterAuthority.property.vault.getWalletValues());

        Property citadel = new Citadel("nice Citadel");
        Governor governor = new Governor();
        ProvinceAuthority govAuth = new ProvinceAuthority(citadel, governor);
        govAuth.collectTax(mayor);

        for (int i = 0; i<8; i++) {
            Mercenary mercenary = new Mercenary(govAuth);
            govAuth.addSupporter(mercenary);
            govAuth.paySupporters(mercenary);
            for (int ii = 0; ii < 5; ii++) {
                Slave slavei = new Slave(mercenary);
                slavei.generate(10, 10, 10);
                mercenary.addSlave(slavei);
                mercenary.collectResources(slavei, 1);
            }
        }


        System.out.println("Governor wealth: "+govAuth.property.vault.getWalletValues());
        ArrayList<Support> supporters = govAuth.getSupporters();

        for (int i = 0; i < supporters.size(); i++) {
            Support supporter = supporters.get(i);
            govAuth.paySupporters(supporter);
            System.out.println("Mercenary " + (i + 1) + " wealth: " + supporter.getWallet().getWalletValues());
        }
        System.out.println("Governor wealth: "+govAuth.property.vault.getWalletValues());



        System.out.println(" "+Food.getTotalFoodCount()+" "+Alloy.getTotalAlloyCount()+" "+Gold.getTotalGoldCount()+" ");




//        World world = new World("Medium World", Size.LARGE);
//
//
//        Time time = new Time();        String glock = time.getClock();
//        int days = 5000;
//        for (int i = 0; i < days; i++){
//            time.incrementDay();
//        }
//        glock = time.getClock();
//        System.out.println(glock);




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
