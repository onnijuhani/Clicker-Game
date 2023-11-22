import java.util.ArrayList;
import java.util.HashMap;


public class CreateWorld {

    public static void main(String[] args) {



            Property fortress = PropertyCreation.createProperty("naton", "Nation");
            King king = new King();
            NationAuthority nationAuthority = new NationAuthority(fortress, king);

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

                for (int city = 0; city < 6; city++) {

                    Property cityProperty = PropertyCreation.createProperty("city", "City");
                    AuthorityCharacter mayor = new Mayor();
                    CityAuthority cityAuthority = new CityAuthority(cityProperty, mayor);

                    for (int quarter = 0; quarter < 4; quarter++) {

                        Property quarterProperty = PropertyCreation.createProperty("quarter" + quarter, "Quarter");
                        Captain captain = new Captain();
                        QuarterAuthority quarterAuthority = new QuarterAuthority(quarterProperty, captain);
                        cityAuthority.setAuthOver(quarterAuthority);

                        for (int peasant = 0; peasant < 20; peasant++) {
                            Farmer farmer = new Farmer(quarterAuthority);
                            farmer.generate(110, 0, 0);
                            quarterAuthority.addPeasant(farmer);
                            quarterAuthority.collectTax(farmer.releaseTax(quarterAuthority.enforceTax()));

                            Merchant merch = new Merchant(quarterAuthority);
                            merch.generate(0, 0, 110);
                            quarterAuthority.addPeasant(merch);
                            quarterAuthority.collectTax(merch.releaseTax(quarterAuthority.enforceTax()));

                            Miner miner = new Miner(quarterAuthority);
                            miner.generate(0, 110, 0);
                            quarterAuthority.addPeasant(miner);
                            quarterAuthority.collectTax(miner.releaseTax(quarterAuthority.enforceTax()));
                        }
                        cityAuthority.collectTax(quarterAuthority);
                        System.out.println(captain.getClass().getSimpleName() + " " + captain.name + " Wealth: " + quarterAuthority.property.getVault().getWalletValues());
                    }

                    provAuthority.collectTax(cityAuthority);
                    System.out.println(mayor.getClass().getSimpleName() + " " + mayor.name + " Wealth: " + cityAuthority.property.getVault().getWalletValues());
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
                System.out.println("Governor wealth: " + provAuthority.property.vault.getWalletValues());
                ArrayList<Support> supporters = provAuthority.getSupporters();

                for (int iii = 0; iii < supporters.size(); iii++) {
                    Support supporter = supporters.get(iii);
                    provAuthority.paySupporters(supporter);
                    System.out.println(supporter.getClass().getSimpleName() + " " + supporter.name + " Wealth: " + supporter.getWallet().getWalletValues());
                }
                System.out.println("Governor wealth: " + provAuthority.property.vault.getWalletValues());
                nationAuthority.collectTax(provAuthority);
                System.out.println("Governor wealth: " + provAuthority.property.vault.getWalletValues());
            }

            System.out.println(king.getClass().getSimpleName() + " " + king.name + " Wealth: " + nationAuthority.property.getVault().getWalletValues());
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
                nationAuthority.paySupporters(supporter);
                System.out.println(supporter.getClass().getSimpleName() + " " + supporter.name + " Wealth: " + supporter.getWallet().getWalletValues());
            }


            System.out.println(king.getClass().getSimpleName() + " " + king.name + " Wealth: " + nationAuthority.property.getVault().getWalletValues());

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
