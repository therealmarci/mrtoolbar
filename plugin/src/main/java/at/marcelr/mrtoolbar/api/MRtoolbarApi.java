package at.marcelr.mrtoolbar.api;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.PlayerToolbar;
import at.marcelr.mrtoolbar.Settings;
import at.marcelr.mrtoolbar.ToolSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Public interface for other plugins, called by reflection (no build dependency needed):
 * <pre>
 * Plugin p = Bukkit.getPluginManager().getPlugin("MRtoolbar");
 * Class&lt;?&gt; api = p.getClass().getClassLoader().loadClass("at.marcelr.mrtoolbar.api.MRtoolbarApi");
 * List&lt;Map&lt;String, Object&gt;&gt; belts = (List) api.getMethod("belts", UUID.class).invoke(null, uuid);
 * </pre>
 * MRpvp records the belts with the state it saves before a duel (for the admin view - it never changes them).
 * Only plain Java types and Paper item bytes leave this class. Keep the signatures stable; new methods raise
 * {@link #API_VERSION}. Main thread only.
 */
public final class MRtoolbarApi {

    public static final int API_VERSION = 1;

    private MRtoolbarApi() {
    }

    /** Whether the toolbar is paused for the online player (MRadmin admin mode, MRpvp). */
    public static boolean paused(UUID player) {
        MRToolbar plugin = JavaPlugin.getPlugin(MRToolbar.class);
        Player online = Bukkit.getPlayer(player);
        return plugin.isEnabled() && online != null && plugin.toolbars().paused(online);
    }

    /**
     * The belts of an online player, one entry per tool slot: "slot" (Integer, hotbar 0-8), "selected" (Integer,
     * belt position of the tool in the hotbar, -1 = none) and "items" (List of byte[] from
     * {@code ItemStack.serializeAsBytes}, null for an empty place). The tool in the hotbar itself is part of the
     * normal inventory, not of the belt. Empty list without tool slots or while MRtoolbar is disabled.
     */
    public static List<Map<String, Object>> belts(UUID player) {
        MRToolbar plugin = JavaPlugin.getPlugin(MRToolbar.class);
        Player online = Bukkit.getPlayer(player);
        if (!plugin.isEnabled() || online == null) {
            return List.of();
        }
        PlayerToolbar toolbar = plugin.toolbars().get(online);
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
}
