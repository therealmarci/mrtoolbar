package at.marcelr.mrtoolbar.api;

import at.marcelr.mradmin.api.AdminModule;
import at.marcelr.mradmin.api.MRadminService;
import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.Settings;
import at.marcelr.mrtoolbar.ToolSlot;
import at.marcelr.mrtoolbar.gui.AdminMenu;
import at.marcelr.mrtoolbar.gui.BeltViewMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * MRtoolbar as a module of MRadmin (module API, see MRadmin's docs/MODULE-API.md): mod and tool belt counts on the
 * dashboard button, the player's belts in the player menu (next to MRadmin's inventory and ender chest view).
 * <b>Only loaded while MRadmin runs</b> - everything else in MRtoolbar goes through {@link MRadminLink}, which has no
 * MRadmin types.
 */
final class MRadminModule implements AdminModule {

    static final String ID = "mrtoolbar";

    private final MRToolbar plugin;

    private MRadminModule(MRToolbar plugin) {
        this.plugin = plugin;
    }

    /** Registers the module with the running MRadmin. @return false if MRadmin offers no module API */
    static boolean register(MRToolbar plugin) {
        MRadminService service = MRadminService.get();
        if (service == null) {
            return false;
        }
        service.register(new MRadminModule(plugin));
        return true;
    }

    static boolean adminMode(Player player) {
        MRadminService service = MRadminService.get();
        return service != null && service.adminMode(player);
    }

    /** Another module has the player busy (e.g. MRpvp during a duel). */
    static boolean busyElsewhere(Player player) {
        MRadminService service = MRadminService.get();
        String module = service == null ? null : service.busy(player);
        return module != null && !module.equals(ID);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public String viewPermission() {
        return AdminMenu.PERM_GUI;
    }

    @Override
    public Material icon() {
        return Material.IRON_PICKAXE;
    }

    /**
     * The belts, one entry per tool slot: "slot" (hotbar 0-8), "selected" (belt position of the tool in the hotbar,
     * -1 = none) and "items" (byte[] per place, null if empty). The tool in the hotbar itself is part of the normal
     * inventory. MRpvp records them with the state it saves before a duel (for its admin view - it never changes them).
     */
    @Override
    public List<Map<String, Object>> storedItems(Player player) {
        PlayerToolbar toolbar = plugin.toolbars().get(player);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ToolSlot slot : toolbar.slots()) {
            List<byte[]> items = new ArrayList<>();
            for (int i = 0; i < Settings.MAX_BELT_SIZE; i++) {
                ItemStack item = slot.get(i);
                items.add(item == null ? null : item.serializeAsBytes());
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("slot", slot.hotbar());
            entry.put("selected", slot.active());
            entry.put("items", items);
            result.add(entry);
        }
        return result;
    }

    /** Players with the mod, players online (without NPCs such as MRchunks' dummies), players with tool slots. */
    @Override
    public List<Component> summary(Player viewer) {
        return plugin.messages().as(viewer, () -> {
            List<? extends Player> online = Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasMetadata("NPC")).toList();
            long modded = online.stream().filter(plugin.modBridge()::compatible).count();
            long active = online.stream().filter(plugin.toolbars()::active)
                    .filter(player -> plugin.toolbars().get(player).slotCount() > 0).count();
            return plugin.messages().lore("mradmin-summary", tag("modded", modded), tag("online", online.size()),
                    tag("active", active));
        });
    }

    /** Mod version, state and tool slots of the player (online only). */
    @Override
    public List<Component> playerSummary(Player viewer, UUID player) {
        Player online = Bukkit.getPlayer(player);
        return plugin.messages().as(viewer, () -> online == null
                ? plugin.messages().lore("mradmin-player-offline")
                : AdminMenu.playerLines(plugin, online));
    }

    @Override
    public boolean openMenu(Player viewer, Runnable back) {
        if (!viewer.hasPermission(AdminMenu.PERM_GUI)) {
            plugin.messages().send(viewer, "no-permission");
            return false;
        }
        plugin.messages().run(viewer, () -> new AdminMenu(plugin, viewer).open());
        return true;
    }

    /** The player's belts (read-only). */
    @Override
    public boolean openPlayerMenu(Player viewer, UUID player, Runnable back) {
        Player online = Bukkit.getPlayer(player);
        if (online == null || !viewer.hasPermission(AdminMenu.PERM_VIEW)) {
            return false;
        }
        plugin.messages().run(viewer, () -> new BeltViewMenu(plugin, viewer, online, back).open());
        return true;
    }
}
