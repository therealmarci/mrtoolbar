package at.marcelr.mrtoolbar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Which belt Mending repairs first, and how much XP it costs. */
class BeltMendingTest {

    @Test
    void heldToolSlotComesFirstThenLeftToRight() {
        assertEquals(List.of(4, 0, 2, 7), BeltMending.order(4, Set.of(7, 2, 4, 0)));
    }

    @Test
    void withoutHeldToolSlotLeftToRight() {
        assertEquals(List.of(1, 3), BeltMending.order(5, Set.of(3, 1)));
    }

    @Test
    void repairsLikeMinecraft() {
        // 2 durability per XP point, never more than the damage.
        assertEquals(10, BeltMending.repaired(5, 100));
        assertEquals(3, BeltMending.repaired(5, 3));
        assertEquals(5, BeltMending.xpUsed(10));
        // Odd remainder rounds down (Minecraft does the same).
        assertEquals(1, BeltMending.xpUsed(3));
    }
}
