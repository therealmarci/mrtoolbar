package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.ToolbarManager;
import at.marcelr.mrtoolbar.sort.InventorySorter;
import at.marcelr.mrtoolbar.sort.SortMode;
import at.marcelr.mrtoolbar.sort.SortSlots;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * Exceptions for sorting, laid out like the player inventory: the three inventory rows on top, a gap, the hotbar
 * below. Each slot is a toggle - green is sorted, red stays where it is. Clicking the real inventory below the menu
 * toggles the same slot.
 */
public final class SortMenu extends Menu {

    /** First menu slot of the hotbar row (row 4, below a gap like in the real inventory). */
    private static final int HOTBAR_ROW = 36;
    private static final int ALL_INVENTORY = 45;
    private static final int ALL_HOTBAR = 46;
    private static final int BACK = 48;
    private static final int HELP = 50;
    private static final int SORT = 52;
    private static final int MODE = 53;

    public SortMenu(MRToolbar plugin, Player viewer) {
        super(plugin, viewer, 6, plugin.messages().get("gui-title-sort"));
    }

    @Override
    protected void draw() {
        Messages messages = plugin.messages();
        InventorySorter sorter = plugin.sorter();
        long locked = sorter.locked(viewer);

        for (int slot = 0; slot < SortSlots.SIZE; slot++) {
            int index = slot;
            set(menuSlot(slot), slotIcon(slot, locked), click -> toggle(index));
        }

        if (viewer.hasPermission(ToolbarManager.PERM_USE) && plugin.modBridge().compatible(viewer)) {
            set(BACK, Items.of(Material.ARROW).name(messages.item("gui-back-name")).build(), click -> {
                clickSound();
                ToolbarMenu.open(plugin, viewer);
            });
        } else {
            set(BACK, Items.of(Material.BARRIER).name(messages.item("gui-close-name")).build(), click -> {
                clickSound();
                viewer.closeInventory();
            });
        }

        set(ALL_INVENTORY, partIcon(Material.CHEST, "gui-sort-all-inventory-name", locked, SortSlots.INVENTORY),
                click -> setPart(SortSlots.INVENTORY, click));
        set(ALL_HOTBAR, partIcon(Material.ITEM_FRAME, "gui-sort-all-hotbar-name", locked, SortSlots.HOTBAR),
                click -> setPart(SortSlots.HOTBAR, click));

        SortMode mode = sorter.mode(viewer);
        List<Component> modes = new ArrayList<>();
        for (SortMode option : SortMode.values()) {
            modes.add(messages.item(option == mode ? "gui-sort-mode-selected" : "gui-sort-mode-other",
                    tag("mode", messages.get("sort-mode-" + option.id()))));
        }
        set(MODE, Items.of(Material.COMPARATOR)
                .name(messages.item("gui-sort-mode-name", tag("mode", messages.get("sort-mode-" + mode.id()))))
                .lore(modes).lore(messages.lore("gui-sort-mode-lore")).build(), click -> {
            sorter.setMode(viewer, click.isRightClick() ? previous(mode) : mode.next());
            clickSound();
            render();
        });

        set(SORT, Items.of(Material.HOPPER).name(messages.item("gui-sort-now-name"))
                .lore(messages.lore("gui-sort-now-lore")).build(), click -> {
            switch (sorter.sort(viewer)) {
                case SORTED -> {
                    viewer.sendActionBar(messages.get("sort-done"));
                    successSound();
                }
                case ALREADY_SORTED -> {
                    viewer.sendActionBar(messages.get("sort-already"));
                    clickSound();
                }
                default -> sound(Sound.ENTITY_VILLAGER_NO);
            }
        });

        set(HELP, Items.of(Material.BOOK).name(messages.item("gui-sort-help-name"))
                .lore(messages.lore("gui-sort-help-lore")).build());

        fillRow(3);
        fillRow(5);
    }

    /** Menu slot of inventory index {@code slot}: rows 0-2 = inventory 9-35, row 4 = hotbar 0-8. */
    private static int menuSlot(int slot) {
        return slot < 9 ? HOTBAR_ROW + slot : slot - 9;
    }

    private ItemStack slotIcon(int slot, long locked) {
        Messages messages = plugin.messages();
        Component name = slot < 9
                ? messages.get("gui-sort-slot-hotbar", tag("slot", slot + 1))
                : messages.get("gui-sort-slot-inventory", tag("slot", slot - 8));
        ItemStack content = viewer.getInventory().getItem(slot);
        Component current = content == null || content.isEmpty()
                ? messages.get("gui-sort-slot-empty") : content.effectiveName();

        if (plugin.sorter().toolSlot(viewer, slot)) {
            return Items.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .name(messages.item("gui-sort-slot-tool-name", tag("slot", name)))
                    .lore(messages.lore("gui-sort-slot-tool-lore", tag("current", current)))
                    .build();
        }
        boolean isLocked = SortSlots.locked(locked, slot);
        return Items.of(isLocked ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE)
                .name(messages.item(isLocked ? "gui-sort-slot-locked-name" : "gui-sort-slot-free-name",
                        tag("slot", name)))
                .lore(messages.lore(isLocked ? "gui-sort-slot-locked-lore" : "gui-sort-slot-free-lore",
                        tag("current", current)))
                .build();
    }

    private ItemStack partIcon(Material material, String nameKey, long locked, long part) {
        Messages messages = plugin.messages();
        boolean all = SortSlots.allLocked(locked, part);
        boolean none = (locked & part) == 0;
        String state = all ? "gui-sort-part-locked" : none ? "gui-sort-part-free" : "gui-sort-part-mixed";
        return Items.of(material).name(messages.item(nameKey, tag("state", messages.get(state))))
                .lore(messages.lore("gui-sort-part-lore")).build();
    }

    private void toggle(int slot) {
        if (plugin.sorter().toolSlot(viewer, slot)) {
            sound(Sound.ENTITY_VILLAGER_NO);
            return;
        }
        plugin.sorter().setLocked(viewer, SortSlots.toggle(plugin.sorter().locked(viewer), slot));
        clickSound();
        render();
    }

    /** Left click frees all slots of the part (they get sorted), right click locks them all. */
    private void setPart(long part, ClickType click) {
        plugin.sorter().setLocked(viewer, SortSlots.set(plugin.sorter().locked(viewer), part, click.isRightClick()));
        clickSound();
        render();
    }

    private static SortMode previous(SortMode mode) {
        SortMode[] modes = SortMode.values();
        return modes[(mode.ordinal() + modes.length - 1) % modes.length];
    }

    @Override
    protected void clickBottom(int slot, ClickType type) {
        // The real inventory below: 0-8 hotbar, 9-35 inventory, everything else (armour, offhand) is never sorted.
        if (slot >= 0 && slot < SortSlots.SIZE) {
            toggle(slot);
        }
    }

    /** Opens the menu if sorting is switched on and the player may use it - no mod needed. */
    public static void open(MRToolbar plugin, Player player) {
        if (!plugin.settings().sorting().enabled()) {
            plugin.messages().send(player, "sort-disabled");
            return;
        }
        if (!player.hasPermission(InventorySorter.PERM_SORT)) {
            plugin.messages().send(player, "no-permission");
            return;
        }
        new SortMenu(plugin, player).open();
    }
}
