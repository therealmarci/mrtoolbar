package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.ToolbarManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps foreign items out of tool slots (clicks, number keys, dragging, picking up) and runs the belt window.
 * After every inventory change the tool slots are checked once more a tick later ({@link ToolbarManager#guard}).
 */
public final class ProtectionListener implements Listener {

    private final MRToolbar plugin;
    private final Set<UUID> pending = new HashSet<>();

    public ProtectionListener(MRToolbar plugin) {
        this.plugin = plugin;
    }

    /** Checks the player's tool slots on the next tick (once, however many events come in). */
    public void scheduleGuard(Player player) {
        if (pending.add(player.getUniqueId())) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                pending.remove(player.getUniqueId());
                if (player.isOnline()) {
                    plugin.toolbars().guard(player);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ToolbarManager toolbars = plugin.toolbars();
        Inventory top = event.getView().getTopInventory();
        BeltEditor editor = top.getHolder(false) instanceof BeltEditor belt ? belt : null;

        // Nothing goes into or out of a tool slot by hand - only through its belt.
        if (event.getClickedInventory() instanceof PlayerInventory && toolbars.isToolSlot(player, event.getSlot())) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction() == InventoryAction.HOTBAR_SWAP && toolbars.isToolSlot(player, event.getHotbarButton())) {
            event.setCancelled(true);
            return;
        }
        if (editor != null && !editorClick(event, editor, player)) {
            return;
        }
        if (editor != null) {
            Bukkit.getScheduler().runTask(plugin, editor::sync);
        }
        scheduleGuard(player);
    }

    /** Rules inside the belt window. @return false if the click was cancelled */
    private boolean editorClick(InventoryClickEvent event, BeltEditor editor, Player player) {
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            // Double click could pull the decoration out of the row; never needed for tools.
            event.setCancelled(true);
            return false;
        }
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            if (event.getSlot() >= editor.size()) {
                event.setCancelled(true);
                return false;
            }
            ItemStack incoming = switch (event.getAction()) {
                case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> event.getCursor();
                case HOTBAR_SWAP -> event.getHotbarButton() >= 0
                        ? player.getInventory().getItem(event.getHotbarButton())
                        : player.getInventory().getItemInOffHand();
                default -> null;
            };
            if (!allowed(editor, incoming)) {
                deny(event, editor);
                return false;
            }
        } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && !allowed(editor, event.getCurrentItem())) {
            deny(event, editor);
            return false;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryView view = event.getView();
        int topSize = view.getTopInventory().getSize();
        BeltEditor editor = view.getTopInventory().getHolder(false) instanceof BeltEditor belt ? belt : null;
        boolean intoBelt = false;
        for (int raw : event.getRawSlots()) {
            if (raw >= topSize) {
                if (view.getBottomInventory() instanceof PlayerInventory
                        && plugin.toolbars().isToolSlot(player, view.convertSlot(raw))) {
                    event.setCancelled(true);
                    return;
                }
            } else if (editor != null) {
                if (raw >= editor.size()) {
                    event.setCancelled(true);
                    return;
                }
                intoBelt = true;
            }
        }
        if (intoBelt && !allowed(editor, event.getOldCursor())) {
            deny(event, editor);
            return;
        }
        if (editor != null) {
            Bukkit.getScheduler().runTask(plugin, editor::sync);
        }
        scheduleGuard(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder(false) instanceof BeltEditor editor) {
            editor.close();
        }
        if (event.getPlayer() instanceof Player player) {
            scheduleGuard(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || !plugin.toolbars().active(player)) {
            return;
        }
        // Minecraft would put the item into an empty tool slot if there is no other room - then leave it lying.
        if (!plugin.toolbars().roomOutsideToolSlots(player, event.getItem().getItemStack())) {
            event.setCancelled(true);
            return;
        }
        scheduleGuard(player);
    }

    /** A broken tool leaves its slot empty - the next one of the belt takes its place. */
    @EventHandler
    public void onBreak(PlayerItemBreakEvent event) {
        scheduleGuard(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        scheduleGuard(event.getPlayer());
    }

    private static boolean allowed(BeltEditor editor, ItemStack item) {
        return item == null || item.isEmpty() || editor.plugin().settings().toolbar().allowed().allows(item);
    }

    private static void deny(Cancellable event, BeltEditor editor) {
        event.setCancelled(true);
        Player player = editor.viewer();
        editor.plugin().messages().send(player, "belt-not-allowed");
        if (editor.plugin().settings().gui().sounds()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.6f, 1.2f);
        }
    }
}
