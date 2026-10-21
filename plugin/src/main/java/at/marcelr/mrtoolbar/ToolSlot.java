package at.marcelr.mrtoolbar;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A hotbar slot that works as tool slot, with its belt. The belt holds the tools that are <b>not</b> in the hotbar;
 * the tool in the hotbar slot is a normal item. {@link #active()} remembers which belt position the tool in the
 * hotbar came from, so it goes back to the same place in the wheel.
 */
public final class ToolSlot {

    private final int hotbar;
    private final ItemStack[] belt = new ItemStack[Settings.MAX_BELT_SIZE];
    private int active = -1;

    public ToolSlot(int hotbar) {
        this.hotbar = hotbar;
    }

    /** Hotbar index 0-8. */
    public int hotbar() {
        return hotbar;
    }

    /** Belt position the tool in the hotbar came from, or -1. Only a hint: the player may have moved it away. */
    public int active() {
        return active;
    }

    public void active(int position) {
        this.active = position;
    }

    /** @return the tool at a belt position, or null */
    public ItemStack get(int position) {
        ItemStack item = belt[position];
        return item == null || item.isEmpty() ? null : item;
    }

    public void set(int position, ItemStack item) {
        belt[position] = item == null || item.isEmpty() ? null : item;
    }

    public boolean free(int position) {
        return get(position) == null;
    }

    /** Positions with a tool among the first {@code size}. */
    public boolean[] occupied(int size) {
        boolean[] result = new boolean[size];
        for (int i = 0; i < size; i++) {
            result[i] = get(i) != null;
        }
        return result;
    }

    public int count(int size) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (get(i) != null) {
                count++;
            }
        }
        return count;
    }

    /** Where a tool coming back from the hotbar goes: its old position if still free, otherwise the first free one, or -1. */
    public int returnPosition(int size) {
        if (active >= 0 && active < size && free(active)) {
            return active;
        }
        for (int i = 0; i < size; i++) {
            if (free(i)) {
                return i;
            }
        }
        return -1;
    }

    /** Removes and returns all tools from position {@code from} on (e.g. when the belt got smaller). */
    public List<ItemStack> takeFrom(int from) {
        List<ItemStack> taken = new ArrayList<>();
        for (int i = from; i < belt.length; i++) {
            if (get(i) != null) {
                taken.add(belt[i]);
            }
            belt[i] = null;
        }
        return taken;
    }

    /** Copy of all positions (empty ones as {@link ItemStack#empty()}), for saving. */
    ItemStack[] contents() {
        ItemStack[] copy = new ItemStack[belt.length];
        for (int i = 0; i < belt.length; i++) {
            copy[i] = belt[i] == null ? ItemStack.empty() : belt[i];
        }
        return copy;
    }

    /**
     * The next occupied position from {@code current} in direction {@code step} (+1 / -1), wrapping around.
     * {@code current} itself counts as occupied (the tool is in the hotbar). Returns -1 if there is no other tool.
     */
    public static int next(boolean[] occupied, int current, int step) {
        int size = occupied.length;
        if (size == 0) {
            return -1;
        }
        int start = current >= 0 && current < size ? current : (step > 0 ? -1 : size);
        for (int i = 1; i <= size; i++) {
            int position = Math.floorMod(start + i * step, size);
            if (position != current && occupied[position]) {
                return position;
            }
        }
        return -1;
    }
}
