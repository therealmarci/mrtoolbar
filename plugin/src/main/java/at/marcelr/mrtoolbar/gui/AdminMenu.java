package at.marcelr.mrtoolbar.gui;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.Messages;
import at.marcelr.mrtoolbar.ModBridge;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.ToolSlot;
import at.marcelr.mrtoolbar.util.Versions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static at.marcelr.mrtoolbar.Messages.tag;

/** /mrtoolbar gui: all online players with mod version and tool slots; a click shows their belts (read-only). */
public final class AdminMenu extends Menu {

    public static final String PERM_GUI = "mrtoolbar.admin.gui";
    public static final String PERM_VIEW = "mrtoolbar.admin.view";
    private static final int PLAYER_SLOTS = 45;

    public AdminMenu(MRToolbar plugin, Player viewer) {
        super(plugin, viewer, 6, plugin.messages().get("gui-title-admin"));
    }

    @Override
    protected void draw() {
        Messages messages = plugin.messages();
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        for (int i = 0; i < Math.min(players.size(), PLAYER_SLOTS); i++) {
            Player target = players.get(i);
            set(i, Items.head(target).name(messages.item("gui-admin-player-name", tag("player", target.getName())))
                    .lore(playerLines(plugin, target))
                    .lore(messages.lore("gui-admin-player-click"))
                    .build(), click -> {
                if (!allowed(PERM_VIEW)) {
                    return;
                }
                if (!target.isOnline()) {
                    sound(Sound.ENTITY_VILLAGER_NO);
                    return;
                }
                clickSound();
                new BeltViewMenu(plugin, viewer, target, () -> new AdminMenu(plugin, viewer).open()).open();
            });
        }
        set(53, Items.of(Material.BARRIER).name(messages.item("gui-close-name")).build(), click -> viewer.closeInventory());
        fillRow(5);
    }

    /** Mod version and tool slots of a player - also shown in MRadmin's player menu. */
    public static List<Component> playerLines(MRToolbar plugin, Player target) {
        Messages messages = plugin.messages();
        ModBridge.Client client = plugin.modBridge().client(target);
        String mod = client == null
                ? messages.plain(plugin.modBridge().outdated(target) ? "debug-mod-outdated" : "debug-mod-none")
                : Versions.plugin(client.modVersion());
        PlayerToolbar toolbar = plugin.toolbars().get(target);
        int size = plugin.toolbars().beltSize(target);
        List<String> slots = new ArrayList<>();
        for (ToolSlot slot : toolbar.slots()) {
            slots.add((slot.hotbar() + 1) + " (" + slot.count(size) + "/" + size + ")");
        }
        return messages.lore("gui-admin-player-lore", tag("mod", mod),
                tag("active", messages.get(plugin.toolbars().active(target) ? "state-on" : "state-off")),
                tag("slots", slots.isEmpty() ? "-" : String.join(", ", slots)));
    }
}
