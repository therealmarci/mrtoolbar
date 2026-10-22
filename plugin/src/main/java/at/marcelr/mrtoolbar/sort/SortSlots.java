package at.marcelr.mrtoolbar.sort;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * The 36 slots of the player inventory that sorting can touch, as bits of a long: bit i = inventory index i
 * (0-8 hotbar, 9-35 the three rows above, top left first). A set bit means "locked" - sorting leaves the slot alone.
 */
public final class SortSlots {

    public static final int SIZE = 36;
    public static final long HOTBAR = 0x1FFL;
    public static final long INVENTORY = ((1L << SIZE) - 1) & ~HOTBAR;
    public static final long ALL = HOTBAR | INVENTORY;

    /** Slots in the order sorted items are put in: the three inventory rows from the top, then the hotbar. */
    private static final int[] FILL_ORDER = new int[SIZE];

    static {
        for (int i = 0; i < 27; i++) {
            FILL_ORDER[i] = 9 + i;
        }
        for (int i = 0; i < 9; i++) {
            FILL_ORDER[27 + i] = i;
        }
    }

    private SortSlots() {
    }

    public static boolean locked(long mask, int slot) {
        return slot >= 0 && slot < SIZE && (mask & (1L << slot)) != 0;
    }

    public static long toggle(long mask, int slot) {
        return slot >= 0 && slot < SIZE ? mask ^ (1L << slot) : mask;
    }

    /** Locks ({@code locked}) or frees all slots of {@code part} ({@link #HOTBAR} or {@link #INVENTORY}). */
    public static long set(long mask, long part, boolean locked) {
        return locked ? mask | part : mask & ~part;
    }

    /** Whether every slot of {@code part} is locked. */
    public static boolean allLocked(long mask, long part) {
        return (mask & part) == part;
    }

    public static int[] fillOrder() {
        return FILL_ORDER.clone();
    }

    /**
     * Reads sorting.default-locked: {@code hotbar}, {@code inventory}, {@code hotbar-<1-9>},
     * {@code inventory-<1-27>} (row by row from the top left).
     *
     * @param invalid gets every entry that could not be read
     */
    public static long parse(List<String> entries, Consumer<String> invalid) {
        long mask = 0;
        for (String entry : entries) {
            String value = entry == null ? "" : entry.trim().toLowerCase(Locale.ROOT);
            switch (value) {
                case "hotbar" -> mask |= HOTBAR;
                case "inventory" -> mask |= INVENTORY;
                default -> {
                    int slot = numbered(value, "hotbar-", 9, 0);
                    if (slot < 0) {
                        slot = numbered(value, "inventory-", 27, 9);
                    }
                    if (slot < 0) {
                        invalid.accept(entry);
                    } else {
                        mask |= 1L << slot;
                    }
                }
            }
        }
        return mask;
    }

    private static int numbered(String value, String prefix, int count, int offset) {
        if (!value.startsWith(prefix)) {
            return -1;
        }
        try {
            int number = Integer.parseInt(value.substring(prefix.length()));
            return number >= 1 && number <= count ? offset + number - 1 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
