package at.marcelr.mrtoolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Order and arithmetic of Mending for the tools in the belts (without Bukkit, so it can be tested). Minecraft only
 * repairs what the player holds or wears; the XP left after that goes to the belts: first the belt of the tool slot
 * the player holds, then the others from left to right, each belt from its first position to its last.
 */
public final class BeltMending {

    /** Durability Mending repairs per XP point (vanilla value). */
    static final int DURABILITY_PER_XP = 2;

    private BeltMending() {
    }

    /** The tool slots in repair order: the held one ({@code held}, may be no tool slot) first, then by hotbar index. */
    public static List<Integer> order(int held, Collection<Integer> hotbars) {
        List<Integer> order = new ArrayList<>(hotbars.stream().sorted().toList());
        if (order.remove(Integer.valueOf(held))) {
            order.addFirst(held);
        }
        return order;
    }

    /** How much durability {@code xp} repairs on a tool with {@code damage}. */
    public static int repaired(int xp, int damage) {
        return Math.min(xp * DURABILITY_PER_XP, damage);
    }

    /** XP used for {@code repaired} durability - rounded down like Minecraft. */
    public static int xpUsed(int repaired) {
        return repaired / DURABILITY_PER_XP;
    }
}
