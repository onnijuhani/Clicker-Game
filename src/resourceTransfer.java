public class resourceTransfer {

    public static void walletDeposit(Character character, Property property) {
        double food = property.vault.getFood();
        double alloy = property.vault.getAlloy();
        double gold = property.vault.getGold();

        property.vault.subtractResources(food, alloy, gold);

        character.wallet.addResources(food, alloy, gold);
    }


}
