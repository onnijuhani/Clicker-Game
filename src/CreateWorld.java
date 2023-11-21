import java.util.ArrayList;
import java.util.HashMap;


public class CreateWorld {

    public static void main(String[] args) {

        Farmer farmer = new Farmer();
        farmer.generateFood(50);
        System.out.println(farmer.taxFood(0.85));
        farmer.walletTransfer(1.0);
        System.out.println(farmer.getWallet().getWalletValues());
        Farmer farmer2 = new Farmer();
        farmer2.generateFood(150);
        System.out.println(farmer2.taxFood(0.1));
        farmer2.walletTransfer(1);
        System.out.println(farmer2.getWallet().getWalletValues());
        Miner miner = new Miner();
        miner.generateAlloy(210);
        System.out.println(miner.taxAlloy(0.45));
        miner.walletTransfer(1);
        System.out.println(miner.wallet.getWalletValues());
        Merchant merchant = new Merchant();
        merchant.generateGold(500);
        merchant.taxGold(0.69);
        Slave slave = new Slave();
        slave.generate(10,10,5);
        slave.tax(1);
        System.out.println(Food.getTotalFoodCount());
        System.out.println(Food.getTotalFoodCount());
        System.out.println(Gold.getTotalGoldCount());

        Property propertyy = new Property(PropertyConfig.FORTRESS, PropertyConfig.FORTRESS_Vault, "wjhatever");
        System.out.println("Testi: "+propertyy.getVault().getWalletValues());
        propertyy.maintenanceCost();
        System.out.println(propertyy.getVault().getWalletValues());

        propertyy.vault.addResources(50,50,50);
        System.out.println(propertyy.getVault().getWalletValues());
        propertyy.maintenanceCost();
        System.out.println(propertyy.getVault().getWalletValues());




        World world = new World("Medium World", Size.LARGE);

        Property property = PropertyCreation.createProperty("Iron Legion Conclave", "Province");
        King king = new King();
        Authority authority = new NationAuthority(property, king);
        System.out.println(authority.getCharacter().getClass().getSimpleName());

        System.out.println(property.getName());

        Time time = new Time();        String glock = time.getClock();
        int days = 5000;
        for (int i = 0; i < days; i++){
            time.incrementDay();
        }
        glock = time.getClock();
        System.out.println(glock);

        System.out.println(king.getClass().getSimpleName() +" "+ NameCreation.generateCharacterName());



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
