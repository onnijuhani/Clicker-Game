package model.shop;

import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import org.junit.jupiter.api.BeforeEach;

class ExchangeTest {

    private Exchange exchange;
    private Wallet testWallet;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        testWallet = new Wallet();
        TransferPackage transfer = new TransferPackage(100,100,100);
        testWallet.addResources(transfer);
        exchange = new Exchange(testWallet);

        testCharacter = new Character();
        testCharacter.getWallet().addResources(transfer);
    }

//    @Test
//    void testSellResource() {
//        // Assume sellResource deducts resources from the character's wallet
//        // and adds equivalent value in gold, considering marketFee.
//
//        // Initial setup - character sells 50 units of FOOD.
//        int foodToSell = 50;
//        Resource sellType = Resource.Food;
//
//        // Expected outcomes
//        double rate = exchange.getRates().getRate(sellType, Resource.Gold); // Need implementation for getRate()
//        double marketFee = exchange.getMarketFee();
//        int expectedGoldToAdd = (int)((foodToSell - foodToSell * marketFee) / rate);
//
//        // Record initial gold for comparison
//        int initialGold = testCharacter.getWallet().getGold();
//
//        // Perform the action
//        exchange.sellResource(foodToSell, sellType, testCharacter);
//
//        // Check outcomes
//        // Check if the gold in character's wallet has increased by the expected amount
//        assertEquals(initialGold + expectedGoldToAdd, testCharacter.getWallet().getGold(), "Gold amount after selling does not match expected value.");
//
//        // You may also want to check if the right amount of FOOD was deducted
//        int expectedFoodLeft = 100 - foodToSell; // Assuming the character initially had 100 units of FOOD, based on setup
//        assertEquals(expectedFoodLeft, testCharacter.getWallet().getFood(), "Food amount after selling does not match expected value.");
//
//        // If applicable, check for the expected event/message in the character's EventTracker
//        // This requires accessing the EventTracker and verifying the last event added matches expectations
//        // Example: assertTrue(testCharacter.getEventTracker().getLastEvent().contains("Exchanged FOOD for GOLD"), "Expected exchange event was not recorded.");
//    }

    // Add more test methods as needed
}
