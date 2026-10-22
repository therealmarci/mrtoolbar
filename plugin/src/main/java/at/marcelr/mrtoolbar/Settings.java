package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.sort.SortMode;
import at.marcelr.mrtoolbar.sort.SortSlots;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Typed, validated view of config.yml. */
public record Settings(
        ToolbarSettings toolbar,
        SortSettings sorting,
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
     * Sorting the player inventory (key of the mod or button in /toolbar).
     *
     * @param defaultLocked slots that stay where they are for players who never changed their exceptions
     *                      (bits, see {@link SortSlots})
     * @param cooldownTicks shortest time between two sorts of a player
     * @param containers    container types that can be sorted (empty = only the own inventory)
     */
    public record SortSettings(boolean enabled, SortMode defaultMode, long defaultLocked, int cooldownTicks,
                               Set<InventoryType> containers) {
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

    /** Window types whose order does not matter - crafters, furnaces etc. would break if their slots were mixed. */
    private static final Set<InventoryType> SORTABLE_CONTAINERS = EnumSet.of(InventoryType.CHEST,
            InventoryType.BARREL, InventoryType.SHULKER_BOX, InventoryType.ENDER_CHEST, InventoryType.HOPPER,
            InventoryType.DISPENSER, InventoryType.DROPPER);

    private static Set<InventoryType> containers(List<String> entries, Logger log) {
        Set<InventoryType> types = EnumSet.noneOf(InventoryType.class);
        for (String entry : entries) {
            InventoryType type = null;
            try {
                type = InventoryType.valueOf(entry.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                // reported below
            }
            if (type != null && SORTABLE_CONTAINERS.contains(type)) {
                types.add(type);
            } else {
                log.warning("Invalid entry '" + entry + "' in sorting.containers (possible: chest, barrel, shulker_box, "
                        + "ender_chest, hopper, dispenser, dropper), ignoring it.");
            }
        }
        return Collections.unmodifiableSet(types);
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

        SortMode mode = SortMode.parse(c.getString("sorting.default-mode", "type"));
        if (mode == null) {
            log.warning("Invalid sorting.default-mode '" + c.getString("sorting.default-mode") + "', using type.");
            mode = SortMode.TYPE;
        }
        SortSettings sorting = new SortSettings(
                c.getBoolean("sorting.enabled", true),
                mode,
                SortSlots.parse(c.getStringList("sorting.default-locked"),
                        entry -> log.warning("Invalid entry '" + entry + "' in sorting.default-locked, ignoring it.")),
                Math.clamp(c.getInt("sorting.cooldown-ticks", 10), 0, 200),
                containers(c.getStringList("sorting.containers"), log));

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

        return new Settings(toolbar, sorting, mod, update, gui);
    }
}
