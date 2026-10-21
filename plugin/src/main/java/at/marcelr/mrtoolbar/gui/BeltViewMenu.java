package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.ToolSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * Read-only view of another player's belts for the team (like MRadmin's inventory and ender chest view): one row per
 * tool slot, on the left the tool in the slot right now, then the belt. Refreshed live; nothing can be taken out.
 */
public final class BeltViewMenu extends Menu {

    /** Rows for tool slots - the last row is for navigation. */
    private static final int MAX_ROWS = 5;

    private final UUID targetId;
    private final Runnable back;

    public BeltViewMenu(MRToolbar plugin, Player viewer, Player target, Runnable back) {
        super(plugin, viewer, rows(plugin, target), plugin.messages().get("gui-title-belt-view",
                tag("player", target.getName())));
        this.targetId = target.getUniqueId();
        this.back = back;
    }

    private static int rows(MRToolbar plugin, Player target) {
        return Math.clamp(plugin.toolbars().get(target).slotCount(), 1, MAX_ROWS) + 1;
    }

    @Override
    protected void draw() {
        Messages messages = plugin.messages();
        Player target = Bukkit.getPlayer(targetId);
        int navRow = getInventory().getSize() / 9 - 1;
        if (target == null) {
            set(4, Items.of(Material.BARRIER).name(messages.item("gui-view-offline-name")).build());
        } else {
            PlayerToolbar toolbar = plugin.toolbars().get(target);
            int size = plugin.toolbars().beltSize(target);
            List<ToolSlot> slots = new ArrayList<>(toolbar.slots());
            if (slots.isEmpty()) {
                set(4, Items.of(Material.BARRIER).name(messages.item("gui-view-none-name")).build());
            }
            for (int row = 0; row < Math.min(slots.size(), MAX_ROWS); row++) {
                ToolSlot slot = slots.get(row);
                ItemStack inSlot = target.getInventory().getItem(slot.hotbar());
                boolean hasTool = inSlot != null && !inSlot.isEmpty();
                set(row * 9, (hasTool ? Items.copy(inSlot) : Items.of(Material.BARREL))
                        .name(messages.item(hasTool ? "gui-view-slot-name" : "gui-view-slot-empty-name",
                                tag("slot", slot.hotbar() + 1)))
                        .lore(messages.lore("gui-view-slot-lore", tag("tools", slot.count(size)), tag("size", size)))
                        .glint(hasTool)
                        .build());
                for (int position = 0; position < 8; position++) {
                    ItemStack tool = position < size ? slot.get(position) : null;
                    if (tool != null) {
                        set(row * 9 + 1 + position, Items.copy(tool).build());
                    } else if (position >= size) {
                        set(row * 9 + 1 + position, Items.of(Material.BLACK_STAINED_GLASS_PANE)
                                .name(messages.item("gui-belt-locked-name")).build());
                    }
                }
            }
        }
        if (back != null) {
            set(navRow * 9, Items.of(Material.ARROW).name(messages.item("gui-back-name")).build(), click -> {
                clickSound();
                back.run();
            });
        }
        set(navRow * 9 + 8, Items.of(Material.BARRIER).name(messages.item("gui-close-name")).build(),
                click -> viewer.closeInventory());
        fillRow(navRow);
    }
}
