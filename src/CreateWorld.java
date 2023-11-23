import java.util.ArrayList;
import java.util.HashMap;


public class CreateWorld {

    public static void main(String[] args) {



            Property fortress = PropertyCreation.createProperty("nation", "Nation");
            King king = new King();
            NationAuthority nationAuthority = new NationAuthority(fortress, king);
            fortress.setOwner(king);

            for (int xx = 0; xx < 500; xx++) {
                Slave slavei = new Slave(king);
                slavei.generate(10, 10, 10);
                king.addSlave(slavei);
                king.collectResources(slavei, 1);
            }
            nationAuthority.property.getVault().addResources(king.wallet.getFood(), king.wallet.getAlloy(), king.wallet.getGold());

            for (int province = 0; province < 4; province++) {

                Property citadel = PropertyCreation.createProperty("province", "Province");
                Governor governor = new Governor();
                ProvinceAuthority provAuthority = new ProvinceAuthority(citadel, governor);
                nationAuthority.setAuthOver(provAuthority);
                citadel.setOwner(governor);

                for (int city = 0; city < 6; city++) {

                    Property cityProperty = PropertyCreation.createProperty("city", "City");
                    AuthorityCharacter mayor = new Mayor();
                    CityAuthority cityAuthority = new CityAuthority(cityProperty, mayor);
                    provAuthority.setAuthOver(cityAuthority);
                    cityProperty.setOwner(mayor);

                    for (int quarter = 0; quarter < 4; quarter++) {

                        Property quarterProperty = PropertyCreation.createProperty("quarter" + quarter, "Quarter");
                        Captain captain = new Captain();
                        QuarterAuthority quarterAuthority = new QuarterAuthority(quarterProperty, captain);
                        cityAuthority.setAuthOver(quarterAuthority);
                        quarterProperty.setOwner(captain);

                        for (int peasant = 0; peasant < 20; peasant++) {
                            Farmer farmer = new Farmer(quarterAuthority);
                            quarterAuthority.addPeasant(farmer);

                            Merchant merch = new Merchant(quarterAuthority);
                            quarterAuthority.addPeasant(merch);

                            Miner miner = new Miner(quarterAuthority);
                            quarterAuthority.addPeasant(miner);
                        }
                    }
                }

                for (int x = 0; x < 8; x++) {
                    Mercenary mercenary = new Mercenary(provAuthority);
                    provAuthority.addSupporter(mercenary);

                    for (int xx = 0; xx < 25; xx++) {
                        Slave slavei = new Slave(mercenary);
                        slavei.generate(10, 10, 10);
                        mercenary.addSlave(slavei);
                        mercenary.collectResources(slavei, 1);
                    }
                }
                ArrayList<Support> supporters = provAuthority.getSupporters();

                for (int iii = 0; iii < supporters.size(); iii++) {
                    Support supporter = supporters.get(iii);

                }

            }


            for (int x = 0; x < 4; x++) {
                Vanguard vanguard = new Vanguard(nationAuthority);
                nationAuthority.addSupporter(vanguard);

                for (int xx = 0; xx < 50; xx++) {
                    Slave slavei = new Slave(vanguard);
                    slavei.generate(10, 10, 10);
                    vanguard.addSlave(slavei);
                    vanguard.collectResources(slavei, 1);
                }
            }
            ArrayList<Support> supporters = nationAuthority.getSupporters();
            for (int iii = 0; iii < supporters.size(); iii++) {
                Support supporter = supporters.get(iii);
            }

            Time time = new Time();
            for (int i = 0; i < 500; i++) {
                time.incrementDay();

                System.out.println(time.getClock());

                System.out.println(king.getClass().getSimpleName() + " " + king.name + " Wealth: " + nationAuthority.property.getVault().getWalletValues());
                System.out.println("Governors wealth: "+nationAuthority.getAuthOver().get(0).getProperty().getVault().getWalletValues());
                System.out.println("Mayors wealth: "+nationAuthority.getAuthOver().get(0).getAuthOver().get(0).getProperty().getVault().getWalletValues());
                System.out.println("Captains wealth: "+nationAuthority.getAuthOver().get(0).getAuthOver().get(0).getAuthOver().get(0).getProperty().getVault().getWalletValues());
                System.out.println("Vanguards Wealth: "+nationAuthority.getSupporters().get(0).getWallet().getWalletValues());
                System.out.println("Mercenary's wealth: "+nationAuthority.getAuthOver().get(0).getSupporters().get(0).getWallet().getWalletValues());


                try {
                    // Sleep for 1000 milliseconds (1 second)
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            }
        System.out.println(" " + Food.getTotalFoodCount() + " " + Alloy.getTotalAlloyCount() + " " + Gold.getTotalGoldCount() + " ");



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
