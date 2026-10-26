package at.marcelr.mrtoolbar.api;

import at.marcelr.mrtoolbar.MRToolbar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * The optional connection to MRadmin. MRtoolbar works the same without it; while MRadmin runs, MRtoolbar is one of its
 * modules ({@link MRadminModule}) and pauses belts of players in admin mode. This class uses no MRadmin types,
 * so it is safe to load without MRadmin - only {@link MRadminModule} touches them, and only after MRadmin is known to
 * be enabled.
 */
public final class MRadminLink implements Listener {

    private static final String MRADMIN = "MRadmin";

    private final MRToolbar plugin;
    private boolean connected;

    public MRadminLink(MRToolbar plugin) {
        this.plugin = plugin;
    }

    /** Connects if MRadmin runs (on enable; MRadmin enables first, MRtoolbar lists it under softdepend). */
    public void connect() {
        connected = false;
        if (!Bukkit.getPluginManager().isPluginEnabled(MRADMIN)) {
            return;
        }
        try {
            connected = MRadminModule.register(plugin);
            if (!connected) {
                plugin.getLogger().info("MRadmin found, but it is too old for the module API - update MRadmin to connect.");
            }
        } catch (LinkageError | RuntimeException e) {
            plugin.getLogger().warning("Could not connect to MRadmin (versions do not fit?): " + e);
        }
    }

    /** Whether the player is in MRadmin's admin mode (false without MRadmin). */
    public boolean adminMode(Player player) {
        if (!connected) {
            return false;
        }
        try {
            return MRadminModule.adminMode(player);
        } catch (LinkageError | RuntimeException e) {
            plugin.getLogger().warning("MRadmin admin mode check failed, ignoring it from now on: " + e);
            connected = false;
            return false;
        }
    }

    /** Another plugin has the player busy, e.g. MRpvp during a duel (false without MRadmin). */
    public boolean busyElsewhere(Player player) {
        if (!connected) {
            return false;
        }
        try {
            return MRadminModule.busyElsewhere(player);
        } catch (LinkageError | RuntimeException e) {
            plugin.getLogger().warning("MRadmin busy check failed, ignoring it from now on: " + e);
            connected = false;
            return false;
        }
    }

    /** MRadmin enabled later (e.g. with a plugin manager): connect now. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals(MRADMIN)) {
            connect();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(MRADMIN)) {
            connected = false;
        }
    }
}
