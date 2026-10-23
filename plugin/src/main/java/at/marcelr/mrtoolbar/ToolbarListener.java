package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.gui.BeltEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/** Joins, quits, deaths and keeping the tool of a tool slot in place (no off hand, no dropping). */
public final class ToolbarListener implements Listener {

    private final MRToolbar plugin;

    public ToolbarListener(MRToolbar plugin) {
        this.plugin = plugin;
    }

    private ToolbarManager toolbars() {
        return plugin.toolbars();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        toolbars().get(player);
        // LuckPerms may still be setting up permissions during the join event.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                toolbars().enforceLimits(player);
            }
        }, 20L);
        plugin.updateChecker().notifyIfOutdated(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BeltEditor.closeFor(player);
        toolbars().unload(player);
        plugin.sorter().forget(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        // An open belt window puts its tools back first (and the selected one into the tool slot).
        BeltEditor.closeFor(player);
        // In admin mode MRadmin keeps the inventory and clears the drops - the belts must not be emptied then.
        if (event.getKeepInventory() || toolbars().paused(player)) {
            return;
        }
        toolbars().onDeath(player, event.getDrops(), plugin.settings().toolbar().death() == Settings.DeathMode.KEEP);
    }

    // ------------------------------------------------------------------ protection

    /** Whether the player holds an active tool slot - its tool must never go to the off hand or be dropped. */
    private boolean onToolSlot(Player player) {
        return toolbars().isToolSlot(player, player.getInventory().getHeldItemSlot());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        if (onToolSlot(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        // Only the tool of the selected tool slot (Q in the hotbar) - not something dropped from an open inventory.
        if (onToolSlot(player) && toolbars().marked(event.getItemDrop().getItemStack(), player.getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
        }
    }
}
