package model.buildings.utilityBuilding;

import model.characters.Character;
import model.characters.player.EventTracker;
import model.resourceManagement.wallets.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AlloyMineTest {

    private AlloyMine alloyMine;
    private Character owner;

    @BeforeEach
    void setUp() {
        owner = mock(Character.class);
        alloyMine = new AlloyMine(10, owner);
    }

    @Test
    void testInitialProduction() {
        assertEquals(1, alloyMine.getProduction(), "Initial production should match Settings mineProduction");
    }

    @Test
    void testUpgradeProduction() {
        alloyMine.upgradeProduction();
        assertEquals(2, alloyMine.getProduction(), "Production should double after upgrade");
    }

    @Test
    void testUpgradeLevelIncreasesProduction() {
        alloyMine.upgradeLevel();
        assertEquals(2, alloyMine.getUpgradeLevel(), "Upgrade level should increase");
        assertEquals(2, alloyMine.getProduction(), "Production should double with level upgrade");
    }

    @Test
    void testGenerateActionAddsResources() {
        Wallet wallet = new Wallet();
        Character owner = Mockito.mock(Character.class);
        EventTracker eventTracker = Mockito.mock(EventTracker.class);

        when(owner.getWallet()).thenReturn(wallet);
        when(owner.getEventTracker()).thenReturn(eventTracker);

        AlloyMine alloyMine = new AlloyMine(100, owner);

        int tests = 20;
        for (int i = 0 ; i < tests; i++) {
            alloyMine.upgradeProduction();
        }
        alloyMine.generateAction();

        int correctAmount = (int) (1 * Math.pow(2, tests));

        assertEquals(correctAmount, wallet.getAlloy(), "AlloyMine should generate and add alloys to the owner's wallet");
        verify(eventTracker).addEvent(anyString());
    }


}
