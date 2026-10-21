package at.marcelr.mrtoolbar;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Typed, validated view of config.yml. */
public record Settings(
        ToolbarSettings toolbar,
        ModSettings mod,
        UpdateSettings update,
        GuiSettings gui) {

    /** Most tools a belt can hold. */
    public static final int MAX_BELT_SIZE = 8;

    public enum DeathMode { DROP, KEEP }

    public record ToolbarSettings(int defaultToolSlots, int defaultBeltSize, ToolFilter allowed,
                                  Set<String> disabledWorlds, DeathMode death, boolean pauseInAdminMode) {

        public boolean disabledIn(String world) {
            return disabledWorlds.contains(world.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * The MRtoolbar mod, without which the toolbar cannot be used.
     *
     * @param minimumVersion   oldest mod version accepted ("" = every version with a compatible protocol)
     * @param downloadUrl      where players get the mod
     * @param handshakeSeconds how long to wait for the mod after joining before treating the player as without mod
     */
    public record ModSettings(String minimumVersion, String downloadUrl, int handshakeSeconds) {
    }

    public record UpdateSettings(boolean enabled, String source, boolean includePrereleases,
                                 int intervalHours, boolean notifyOnJoin) {
    }

    public record GuiSettings(int refreshTicks, boolean sounds) {
    }

    public static Settings from(FileConfiguration c, Logger log) {
        DeathMode death = switch (c.getString("toolbar.on-death", "drop").toLowerCase(Locale.ROOT)) {
            case "keep" -> DeathMode.KEEP;
            case "drop" -> DeathMode.DROP;
            default -> {
                log.warning("Invalid toolbar.on-death '" + c.getString("toolbar.on-death") + "', using drop.");
                yield DeathMode.DROP;
            }
        };
        ToolbarSettings toolbar = new ToolbarSettings(
                Math.clamp(c.getInt("toolbar.default-tool-slots", 1), 0, 9),
                Math.clamp(c.getInt("toolbar.default-belt-size", 6), 1, MAX_BELT_SIZE),
                ToolFilter.parse(c.getStringList("toolbar.allowed-items"), log),
                c.getStringList("toolbar.disabled-worlds").stream()
                        .map(world -> world.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toUnmodifiableSet()),
                death,
                c.getBoolean("toolbar.pause-in-admin-mode", true));

        String minimum = c.getString("mod.minimum-version", "").trim();
        if (!minimum.isEmpty() && !minimum.matches("[vV]?\\d+(\\.\\d+)*(-[0-9A-Za-z.]+)?")) {
            log.warning("Invalid mod.minimum-version '" + minimum + "' (expected e.g. 0.2.0), ignoring it.");
            minimum = "";
        }
        ModSettings mod = new ModSettings(minimum,
                c.getString("mod.download-url", "https://github.com/therealmarci/mrtoolbar/releases").trim(),
                Math.clamp(c.getInt("mod.handshake-seconds", 5), 1, 60));

        String source = c.getString("update-checker.source", "github").toLowerCase(Locale.ROOT);
        if (!source.equals("modrinth") && !source.equals("github")) {
            log.warning("Invalid update-checker.source '" + source + "', using github.");
            source = "github";
        }
        UpdateSettings update = new UpdateSettings(
                c.getBoolean("update-checker.enabled", true),
                source,
                c.getBoolean("update-checker.include-prereleases", false),
                Math.max(1, c.getInt("update-checker.interval-hours", 6)),
                c.getBoolean("update-checker.notify-on-join", true));

        GuiSettings gui = new GuiSettings(
                Math.max(5, c.getInt("gui.refresh-ticks", 20)),
                c.getBoolean("gui.sounds", true));

        return new Settings(toolbar, mod, update, gui);
    }
}
