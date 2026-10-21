package at.marcelr.mrtoolbar.controls;

import java.util.Locale;

/** What a hotkey does on a tool slot. */
public enum ToolAction {
    /** Open the tool selection (wheel / menu). */
    OPEN("open"),
    /** Switch straight to the next tool in the belt. */
    NEXT("next"),
    /** Switch straight to the previous tool in the belt. */
    PREVIOUS("previous");

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
