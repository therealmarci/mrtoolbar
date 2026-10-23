package at.marcelr.mrtoolbar.controls;

import java.util.Locale;

/**
 * What a key of the mod does. The order is the code sent by the mod - new actions only at the end
 * (older plugins ignore codes they do not know).
 */
public enum ToolAction {
    /** Open the tool selection (wheel / menu). */
    OPEN("open"),
    /** Switch straight to the next tool in the belt. */
    NEXT("next"),
    /** Switch straight to the previous tool in the belt. */
    PREVIOUS("previous"),
    /** Sort the player inventory (works on any slot, not only on tool slots). */
    SORT("sort"),
    /** Sort the open container (chest, barrel, hopper ...) - the mouse was over it. */
    SORT_CONTAINER("sort-container");

    private final String id;

    ToolAction(String id) {
        this.id = id;
    }

    /** Name in config.yml, the player data and the language files ("action-<id>"). */
    public String id() {
        return id;
    }

    public static ToolAction parse(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (ToolAction action : values()) {
            if (action.id.equals(normalized)) {
                return action;
            }
        }
        return null;
    }
}
