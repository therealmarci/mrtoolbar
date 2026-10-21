package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.ToolSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * The belt of one tool slot as a chest row: tools are put in and taken out like in any chest. While it is open,
 * the selected tool lies in the belt too (the tool slot is empty), so the row shows every tool of the belt.
 * Every change is written into the player data a tick later (see {@code ProtectionListener}) and once more when
 * the window closes; then the selected tool goes back into the tool slot.
 */
public final class BeltEditor implements InventoryHolder {

    /** Last slot of the row: information. */
    static final int INFO = 8;

    private final MRToolbar plugin;
    private final Player viewer;
    private final ToolSlot slot;
    private final int size;
    private final Inventory inventory;
    private boolean closed;

    private BeltEditor(MRToolbar plugin, Player viewer, ToolSlot slot) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.slot = slot;
        this.size = plugin.toolbars().beltSize(viewer);
        this.inventory = Bukkit.createInventory(this, 9,
                Menu.fit(plugin.messages().get("gui-title-belt", tag("slot", slot.hotbar() + 1))));
    }

    /** Opens the belt of a tool slot. */
    public static void open(MRToolbar plugin, Player player, ToolSlot slot) {
        plugin.toolbars().prepareEditor(player, slot);
        BeltEditor editor = new BeltEditor(plugin, player, slot);
        editor.fill();
        player.openInventory(editor.inventory);
    }

    private void fill() {
        Messages messages = plugin.messages();
        for (int position = 0; position < size; position++) {
            ItemStack tool = slot.get(position);
            inventory.setItem(position, tool == null ? null : tool.clone());
        }
        for (int position = size; position < INFO; position++) {
            inventory.setItem(position, Items.of(Material.BLACK_STAINED_GLASS_PANE)
                    .name(messages.item("gui-belt-locked-name")).lore(messages.lore("gui-belt-locked-lore")).build());
        }
        inventory.setItem(INFO, Items.of(Material.BOOK).name(messages.item("gui-belt-info-name"))
                .lore(messages.lore("gui-belt-info-lore", tag("size", size))).build());
    }

    public int size() {
        return size;
    }

    public ToolSlot slot() {
        return slot;
    }

    Player viewer() {
        return viewer;
    }

    MRToolbar plugin() {
        return plugin;
    }

    /** Copies the window into the belt. Items that may not be in a belt go back to the player. */
    public void sync() {
        if (closed) {
            return;
        }
        boolean exists = plugin.toolbars().get(viewer).slot(slot.hotbar()) == slot;
        List<ItemStack> rejected = new ArrayList<>();
        for (int position = 0; position < size; position++) {
            ItemStack item = inventory.getItem(position);
            if (item == null || item.isEmpty()) {
                slot.set(position, null);
            } else if (exists && plugin.settings().toolbar().allowed().allows(item)) {
                slot.set(position, plugin.toolbars().unmark(item.clone()));
            } else {
                rejected.add(item.clone());
                inventory.setItem(position, null);
                slot.set(position, null);
            }
        }
        if (!rejected.isEmpty()) {
            plugin.toolbars().giveBack(viewer, rejected);
        }
        plugin.toolbars().save(viewer);
    }

    /** Final sync when the window closes; the window is emptied so nothing can be taken out twice. */
    public void close() {
        sync();
        closed = true;
        inventory.clear();
        plugin.toolbars().finishEditor(viewer, slot);
    }

    /** Whether the player has the belt of this tool slot open right now. */
    public static boolean isOpen(Player player, ToolSlot slot) {
        return player.getOpenInventory().getTopInventory().getHolder(false) instanceof BeltEditor editor
                && editor.slot == slot && !editor.closed;
    }

    /** Closes the player's belt if it is open (writes it back first). */
    public static void closeFor(Player player) {
        if (player.getOpenInventory().getTopInventory().getHolder(false) instanceof BeltEditor) {
            player.closeInventory();
        }
    }

    public static void closeAll() {
        Bukkit.getOnlinePlayers().forEach(BeltEditor::closeFor);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }
}
