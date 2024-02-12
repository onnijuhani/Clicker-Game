package model.characters.decisions;

import model.characters.Character;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.resourceManagement.Resource;

public class MarketActions {



    public static void decideMarketActions(Character npc){
        Exchange exchange = npc.getNation().getShop().getExchange();
        double foodRatio = exchange.getRatioBalance()[0];
        double alloyRatio = exchange.getRatioBalance()[1];
        System.out.println(foodRatio);

        if (foodRatio < 10) {
            sellFood(npc, exchange);
            System.out.println("sold food for gold if foodratio < 10. foodratio = " + foodRatio);
        }


    }

    private static void sellFood(Character npc, Exchange exchange) {
        int amountToSell = calculateAmount(npc.getWallet(), Resource.Food);
        exchange.sellResource(amountToSell,Resource.Food,npc);
    }

    private static int calculateAmount(Wallet wallet, Resource type) {
        return wallet.getResource(type) / 2;
    }
}
