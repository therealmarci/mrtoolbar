package at.marcelr.mrtoolbar.api;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.gui.AdminMenu;
import at.marcelr.mrtoolbar.gui.BeltViewMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * Connection point for MRadmin (dashboard button and player menu, next to its inventory and ender chest view).
 * MRadmin finds this class at runtime - no dependency in either direction, MRtoolbar works the same without
 * MRadmin. Keep the signatures stable.
 * <p>
 * Version 1: summary, playerSummary, openPlayerMenu (read-only belt view).
 */
public final class MRadminHook {

    public static final int API_VERSION = 1;

    private MRadminHook() {
    }

    private static MRToolbar plugin() {
        return JavaPlugin.getPlugin(MRToolbar.class);
    }

    /** Lines for the MRtoolbar button in MRadmin's dashboard: players with mod, players with tool slots. */
    public static List<Component> summary(Player viewer) {
        MRToolbar plugin = plugin();
        return plugin.messages().as(viewer, () -> {
            var online = Bukkit.getOnlinePlayers();
            long modded = online.stream().filter(plugin.modBridge()::compatible).count();
            long active = online.stream().filter(plugin.toolbars()::active)
                    .filter(player -> plugin.toolbars().get(player).slotCount() > 0).count();
            return plugin.messages().lore("mradmin-summary", tag("modded", modded), tag("online", online.size()),
                    tag("active", active));
        });
    }

    /** Mod version, state and tool slots of the player (online only). */
    public static List<Component> playerSummary(Player viewer, UUID target) {
        MRToolbar plugin = plugin();
        Player player = Bukkit.getPlayer(target);
        return plugin.messages().as(viewer, () -> player == null
                ? plugin.messages().lore("mradmin-player-offline")
                : AdminMenu.playerLines(plugin, player));
    }

    /** Opens the player's belts (read-only). @return false without permission or if the player is offline */
    public static boolean openPlayerMenu(Player viewer, UUID target, Runnable back) {
        MRToolbar plugin = plugin();
        Player player = Bukkit.getPlayer(target);
        if (player == null || !viewer.hasPermission(AdminMenu.PERM_VIEW)) {
            return false;
        }
        plugin.messages().run(viewer, () -> new BeltViewMenu(plugin, viewer, player, back).open());
        return true;
    }
}
