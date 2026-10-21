package at.marcelr.mrtoolbar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Order of sneak + scroll switching. */
class ToolSlotTest {

    @Test
    void nextSkipsEmptyPositionsAndWraps() {
        boolean[] occupied = {true, false, true, false, true};
        assertEquals(2, ToolSlot.next(occupied, 0, 1));
        assertEquals(4, ToolSlot.next(occupied, 2, 1));
        assertEquals(0, ToolSlot.next(occupied, 4, 1));
        assertEquals(4, ToolSlot.next(occupied, 0, -1));
        assertEquals(0, ToolSlot.next(occupied, 2, -1));
    }

    @Test
    void withoutCurrentToolStartsAtTheEnds() {
        boolean[] occupied = {false, true, false, true};
        assertEquals(1, ToolSlot.next(occupied, -1, 1));
        assertEquals(3, ToolSlot.next(occupied, -1, -1));
    }

    @Test
    void currentPositionCountsAsTheToolInHand() {
        // The tool in hand came from position 1, which is free in the belt now.
        boolean[] occupied = {false, false, true};
        assertEquals(2, ToolSlot.next(occupied, 1, 1));
        assertEquals(2, ToolSlot.next(occupied, 1, -1));
    }

    @Test
    void noOtherToolGivesMinusOne() {
        assertEquals(-1, ToolSlot.next(new boolean[]{false, false}, -1, 1));
        assertEquals(-1, ToolSlot.next(new boolean[]{true, false}, 0, 1));
        assertEquals(-1, ToolSlot.next(new boolean[0], -1, 1));
    }
}
