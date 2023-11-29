public class SimulationTest {

    public static void main(String[] args){

        long seed = 42;



        World world = new World("Medium World", Size.LARGE);


        Time time = new Time();

        for (int i = 0; i < 500; i++) {
            time.incrementDay();

            System.out.println("Captains Wealth: "+world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).
                   getContents().get(0).getAuthority().getProperty().getVault().getWalletValues());

            System.out.println("Captains wallet Wealth: "+world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).
                    getContents().get(0).getAuthority().getCharacter().getWallet().getWalletValues());

            System.out.println("Mayors Wealth: "+world.getContents().get(0).getContents().get(0).getContents().get(0).
                    getContents().get(0).getAuthority().getProperty().getVault().getWalletValues());

            System.out.println("Governors vault Wealth: "+world.getContents().get(0).getContents().get(0).
                    getContents().get(0).getAuthority().getCharacter().getWallet().getWalletValues());

            System.out.println("Governors wallet Wealth: "+world.getContents().get(0).getContents().get(0).
                    getContents().get(0).getAuthority().getCharacter().getWallet().getWalletValues());


            System.out.println("Kings Wealth: "+world.getContents().get(0).
                    getContents().get(0).getAuthority().getCharacter().getWallet().getWalletValues());
            System.out.println("Kings wallet Wealth: "+world.getContents().get(0).
                    getContents().get(0).getAuthority().getCharacter().getWallet().getWalletValues());

            try {
                // Sleep for 1000 milliseconds (1 second)
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // Handle the exception if needed
                e.printStackTrace();
            }
        }
        System.out.println("Farmers " + Farmer.totalAmount);
        System.out.println("Kings " + King.totalAmount);
        System.out.println("Miners " + Miner.totalAmount);
        System.out.println("Mercenearies " + Mercenary.totalAmount);
        System.out.println("Vanguards " + Vanguard.totalAmount);
        System.out.println(Captain.totalAmount);
        System.out.println("Food "+Food.getTotalFoodCount());


    }
}
