package at.marcelr.mrtoolbar.sort;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortSlotsTest {

    @Test
    void fillOrderIsInventoryRowsThenHotbar() {
        int[] order = SortSlots.fillOrder();
        assertEquals(36, order.length);
        assertEquals(9, order[0]);
        assertEquals(35, order[26]);
        assertEquals(0, order[27]);
        assertEquals(8, order[35]);
        assertArrayEquals(java.util.stream.IntStream.range(0, 36).toArray(), Arrays.stream(order).sorted().toArray());
    }

    @Test
    void partsCoverAllSlotsOnce() {
        assertEquals(0, SortSlots.HOTBAR & SortSlots.INVENTORY);
        assertEquals(36, Long.bitCount(SortSlots.ALL));
        assertEquals(9, Long.bitCount(SortSlots.HOTBAR));
    }

    @Test
    void toggleAndSet() {
        long mask = SortSlots.toggle(0, 12);
        assertTrue(SortSlots.locked(mask, 12));
        assertFalse(SortSlots.locked(SortSlots.toggle(mask, 12), 12));
        assertEquals(mask, SortSlots.toggle(mask, 36), "outside the 36 slots nothing changes");

        long hotbar = SortSlots.set(mask, SortSlots.HOTBAR, true);
        assertTrue(SortSlots.allLocked(hotbar, SortSlots.HOTBAR));
        assertTrue(SortSlots.locked(hotbar, 12), "other slots are kept");
        assertEquals(mask, SortSlots.set(hotbar, SortSlots.HOTBAR, false));
    }

    @Test
    void parsesConfigEntries() {
        List<String> invalid = new ArrayList<>();
        long mask = SortSlots.parse(List.of("hotbar", "inventory-1", "Inventory-27", "hotbar-10", "chest"), invalid::add);
        assertTrue(SortSlots.allLocked(mask, SortSlots.HOTBAR));
        assertTrue(SortSlots.locked(mask, 9));
        assertTrue(SortSlots.locked(mask, 35));
        assertFalse(SortSlots.locked(mask, 10));
        assertEquals(List.of("hotbar-10", "chest"), invalid);

        assertEquals(1L << 2, SortSlots.parse(List.of("hotbar-3"), invalid::add));
        assertEquals(SortSlots.INVENTORY, SortSlots.parse(List.of("inventory"), invalid::add));
    }

    @Test
    void modesCycleAndParse() {
        assertEquals(SortMode.NAME, SortMode.TYPE.next());
        assertEquals(SortMode.TYPE, SortMode.AMOUNT.next());
        assertEquals(SortMode.AMOUNT, SortMode.parse(" Amount "));
        assertEquals(null, SortMode.parse("random"));
    }
}
