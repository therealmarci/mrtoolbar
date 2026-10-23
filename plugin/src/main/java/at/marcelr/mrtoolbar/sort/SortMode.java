package at.marcelr.mrtoolbar.sort;

import java.util.Locale;

/** Order in which {@link InventorySorter} puts the items. */
public enum SortMode {
    /** Minecraft's own item order (blocks first, then items - similar items end up next to each other). */
    TYPE("type"),
    /** Alphabetically by item id. */
    NAME("name"),
    /** The item type you have most of first. */
    AMOUNT("amount");

    private final String id;

    SortMode(String id) {
        this.id = id;
    }

    /** Name in config.yml, the player data and the language files ("sort-mode-<id>"). */
    public String id() {
        return id;
    }

    public SortMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static SortMode parse(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (SortMode mode : values()) {
            if (mode.id.equals(normalized)) {
                return mode;
            }
        }
        return null;
    }
}
