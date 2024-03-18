package model.buildings.utilityBuilding;

import model.characters.Character;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//public class SlaveFacilityTest {
//
//    @Test
//    public void testIncreaseSlotAmount() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//        int initialSlots = slaveFacility.getSlots();
//        int initialSlaveAmount = slaveFacility.getSlaveAmount();
//
//        slaveFacility.increaseSlotAmount();
//
//        assertEquals(initialSlots * 2, slaveFacility.getSlots());
//        assertEquals(initialSlaveAmount * 2, slaveFacility.getSlaveAmount());
//    }
//
//    @Test
//    void testInitialProduction() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//
//        int[] expectedProduction = {10, 5 ,1};
//        assertArrayEquals(expectedProduction, slaveFacility.getProduction(), "Initial production should match Settings mineProduction");
//    }
//
//    @Test
//    void testUpgradeProduction() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//
//        int tests = 5;
//        for (int i = 0; i < tests; i++) {
//            slaveFacility.upgradeLevel();
//        }
//
//        int[] expectedProduction = {
//                10 * (int) Math.pow(2, tests),
//                5 * (int) Math.pow(2, tests),
//                (int) Math.pow(2, tests)
//        };
//
//        assertArrayEquals(expectedProduction, slaveFacility.getProduction(), "Production after upgrades should be as expected");
//    }
//
//    @Test
//    public void testIncreaseProduction() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//        int[] initialProduction = slaveFacility.getProduction().clone();
//
//        slaveFacility.increaseProduction();
//
//        int[] expectedProduction = new int[]{
//                initialProduction[0] * 2,
//                initialProduction[1] * 2,
//                initialProduction[2] * 2
//        };
//        assertArrayEquals(expectedProduction, slaveFacility.getProduction());
//    }
//
//    @Test
//    public void testUpgradeLevel() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//        int initialSlots = slaveFacility.getSlots();
//        int initialSlaveAmount = slaveFacility.getSlaveAmount();
//        int initialLevel = slaveFacility.getUpgradeLevel();
//
//        slaveFacility.upgradeLevel();
//
//        assertEquals(initialLevel + 1, slaveFacility.getUpgradeLevel());
//        assertEquals(initialSlots * 2, slaveFacility.getSlots());
//        assertEquals(initialSlaveAmount * 2, slaveFacility.getSlaveAmount());
//    }
//
//
//    @Test
//    public void testRemoveSlave() {
//        SlaveFacility slaveFacility = new SlaveFacility(30, new Character());
//        int initialSlaveAmount = slaveFacility.getSlaveAmount();
//
//        slaveFacility.removeSlave();
//
//        assertEquals(initialSlaveAmount - 1, slaveFacility.getSlaveAmount());
//    }
//}

