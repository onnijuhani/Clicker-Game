package model.characters.decisions;

import model.shop.Exchange;
import model.characters.Character;

public interface MarketBehavior {
    void decideMarketActions(Character npc, Exchange exchange);
}
