package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.ModBridge;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.ToolSlot;
import at.marcelr.mrtoolbar.ToolbarManager;
import at.marcelr.mrtoolbar.UpdateChecker;
import at.marcelr.mrtoolbar.util.Versions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * /toolbar: choose tool slots, open their belts, open the hotbar design of the mod, sorting exceptions,
 * switch on/off.
 */
public final class ToolbarMenu extends Menu {

    private static final int HOTBAR_ROW = 9;
    private static final int HELP = 4;
    private static final int DESIGN = 29;
    private static final int SORT = 31;
    private static final int ENABLED = 33;

    public ToolbarMenu(MRToolbar plugin, Player viewer) {
        super(plugin, viewer, 4, plugin.messages().get("gui-title-main"));
    }

    @Override
    protected void draw() {
        Messages messages = plugin.messages();
        ToolbarManager toolbars = plugin.toolbars();
        PlayerToolbar toolbar = toolbars.get(viewer);

        set(HELP, Items.of(Material.BOOK).name(messages.item("gui-help-name"))
                .lore(messages.lore("gui-help-lore")).lore(versionLore()).build(), click -> {
            clickSound();
            UpdateChecker updates = plugin.updateChecker();
            messages.send(viewer, "help-links", tag("download", plugin.modBridge().downloadLink()),
                    tag("plugin", messages.get("help-plugin-label").clickEvent(ClickEvent.openUrl(updates.githubPage()))));
        });

        int used = toolbar.slotCount();
        int max = toolbars.maxToolSlots(viewer);
        int size = toolbars.beltSize(viewer);
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            ToolSlot slot = toolbar.slot(hotbar);
            int index = hotbar;
            if (slot != null) {
                set(HOTBAR_ROW + hotbar, toolSlotIcon(slot, size), click -> {
                    if (click.isRightClick()) {
                        int returned = toolbars.removeSlot(viewer, index);
                        messages.send(viewer, "slot-removed", tag("slot", index + 1), tag("count", returned));
                        clickSound();
                        render();
                    } else {
                        clickSound();
                        // Not inside the click event: opening the belt changes the inventory.
                        Bukkit.getScheduler().runTask(plugin, () -> plugin.openBelt(viewer, slot));
                    }
                });
            } else {
                boolean full = used >= max;
                set(HOTBAR_ROW + hotbar, Items.of(full ? Material.GRAY_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE)
                        .name(messages.item("gui-slot-normal-name", tag("slot", hotbar + 1)))
                        .lore(messages.lore(full ? "gui-slot-limit-lore" : "gui-slot-normal-lore",
                                tag("used", used), tag("max", max)))
                        .build(), click -> {
                    switch (toolbars.addSlot(viewer, index)) {
                        case ADDED -> {
                            messages.send(viewer, "slot-added", tag("slot", index + 1));
                            successSound();
                        }
                        case LIMIT -> {
                            messages.send(viewer, "slot-limit", tag("max", max));
                            sound(Sound.ENTITY_VILLAGER_NO);
                        }
                        case NO_ROOM -> {
                            messages.send(viewer, "slot-no-room", tag("slot", index + 1));
                            sound(Sound.ENTITY_VILLAGER_NO);
                        }
                    }
                    render();
                });
            }
        }

        set(DESIGN, Items.of(Material.PAINTING).name(messages.item("gui-design-name"))
                .lore(messages.lore("gui-design-lore")).build(), click -> {
            clickSound();
            viewer.closeInventory();
            plugin.modBridge().openDesign(viewer);
        });

        if (plugin.sorter().allowed(viewer)) {
            set(SORT, Items.of(Material.HOPPER).name(messages.item("gui-sort-name"))
                    .lore(messages.lore("gui-sort-lore")).build(), click -> {
                clickSound();
                new SortMenu(plugin, viewer).open();
            });
        }

        boolean enabled = toolbar.enabled();
        set(ENABLED, Items.of(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                .name(messages.item("gui-enabled-name", tag("state", messages.get(enabled ? "state-on" : "state-off"))))
                .lore(messages.lore("gui-enabled-lore"))
                .build(), click -> {
            plugin.toolbars().setEnabled(viewer, !enabled);
            clickSound();
            render();
        });

        fillRow(0);
        fillRow(2);
        fillRow(3);
    }

    /**
     * Server plugin and client mod version, each with its update status. Plugin and mod are released separately -
     * the mod only needs an update when there is a newer mod release.
     */
    private List<Component> versionLore() {
        Messages messages = plugin.messages();
        UpdateChecker updates = plugin.updateChecker();
        ModBridge.Client client = plugin.modBridge().client(viewer);
        String clientVersion = client == null ? "-" : Versions.plugin(client.modVersion());
        boolean known = updates.state() != UpdateChecker.State.DISABLED && updates.state() != UpdateChecker.State.FAILED;
        return messages.lore("gui-help-versions",
                tag("server", updates.currentVersion()),
                tag("server-status", status(updates.currentVersion(), known ? updates.latestVersion() : null)),
                tag("client", clientVersion),
                tag("client-status", client == null ? Component.empty() : status(clientVersion, updates.latestModVersion())));
    }

    private Component status(String version, String latest) {
        Messages messages = plugin.messages();
        if (latest == null) {
            return messages.get("version-status-unknown");
        }
        return Versions.compare(latest, version) > 0
                ? messages.get("version-status-update", tag("latest", latest))
                : messages.get("version-status-latest");
    }

    private ItemStack toolSlotIcon(ToolSlot slot, int size) {
        Messages messages = plugin.messages();
        ItemStack inHand = viewer.getInventory().getItem(slot.hotbar());
        boolean hasTool = inHand != null && !inHand.isEmpty();
        Items icon = hasTool ? Items.copy(inHand) : Items.of(Material.BARREL);
        Component current = hasTool ? inHand.effectiveName() : messages.get("gui-slot-tool-nothing");
        return icon.name(messages.item("gui-slot-tool-name", tag("slot", slot.hotbar() + 1)))
                .lore(messages.lore("gui-slot-tool-lore", tag("current", current),
                        tag("tools", slot.count(size)), tag("size", size)))
                .glint(true)
                .build();
    }

    /**
     * Opens the menu if the player may use the toolbar. Without (compatible) mod there is only a hint with the
     * download link - and a way to get tools back that are still in a belt.
     */
    public static void open(MRToolbar plugin, Player player) {
        Messages messages = plugin.messages();
        if (!player.hasPermission(ToolbarManager.PERM_USE)) {
            messages.send(player, "no-permission");
            return;
        }
        if (!plugin.modBridge().compatible(player)) {
            if (plugin.modBridge().outdated(player)) {
                ModBridge.Client client = plugin.modBridge().client(player);
                messages.send(player, "mod-too-old", tag("version", client == null ? "?" : client.modVersion()),
                        tag("download", plugin.modBridge().downloadLink()));
            } else {
                messages.send(player, "mod-required", tag("download", plugin.modBridge().downloadLink()));
            }
            if (plugin.toolbars().get(player).slotCount() > 0) {
                messages.send(player, "mod-required-take-back");
            }
            return;
        }
        if (plugin.toolbars().paused(player)) {
            messages.send(player, "toolbar-paused");
            return;
        }
        plugin.toolbars().enforceLimits(player);
        new ToolbarMenu(plugin, player).open();
    }
}
