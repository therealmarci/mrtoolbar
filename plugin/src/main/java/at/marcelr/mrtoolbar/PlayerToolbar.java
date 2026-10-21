package at.marcelr.mrtoolbar;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Toolbar data of one player. Stored in the player's PersistentDataContainer, i.e. in the same player file as the
 * inventory - belt and inventory are always saved together, so a crash cannot duplicate or lose a tool.
 */
public final class PlayerToolbar {

    /** 2: key bindings and highlight setting moved into the mod. */
    private static final int DATA_VERSION = 2;

    /** Tool slots by hotbar index. */
    private final Map<Integer, ToolSlot> slots = new TreeMap<>();
    private boolean enabled = true;

    public Collection<ToolSlot> slots() {
        return slots.values();
    }

    /** @return the tool slot on this hotbar index, or null */
    public ToolSlot slot(int hotbar) {
        return slots.get(hotbar);
    }

    public ToolSlot addSlot(int hotbar) {
        return slots.computeIfAbsent(hotbar, ToolSlot::new);
    }

    public ToolSlot removeSlot(int hotbar) {
        return slots.remove(hotbar);
    }

    public int slotCount() {
        return slots.size();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ------------------------------------------------------------------ storage

    private record Keys(NamespacedKey data, NamespacedKey version, NamespacedKey enabled, NamespacedKey slots,
                        NamespacedKey hotbar, NamespacedKey active, NamespacedKey items) {
    }

    private static Keys keys(Plugin plugin) {
        return new Keys(new NamespacedKey(plugin, "toolbar"), new NamespacedKey(plugin, "version"),
                new NamespacedKey(plugin, "enabled"), new NamespacedKey(plugin, "slots"),
                new NamespacedKey(plugin, "hotbar"),
                new NamespacedKey(plugin, "active"), new NamespacedKey(plugin, "items"));
    }

    public static PlayerToolbar read(Plugin plugin, PersistentDataContainer pdc) {
        Keys k = keys(plugin);
        PlayerToolbar toolbar = new PlayerToolbar();
        PersistentDataContainer data = pdc.get(k.data, PersistentDataType.TAG_CONTAINER);
        if (data == null) {
            return toolbar;
        }
        toolbar.enabled = data.getOrDefault(k.enabled, PersistentDataType.BOOLEAN, true);
        List<PersistentDataContainer> slots = data.getOrDefault(k.slots,
                PersistentDataType.LIST.dataContainers(), List.of());
        for (PersistentDataContainer entry : slots) {
            Integer hotbar = entry.get(k.hotbar, PersistentDataType.INTEGER);
            if (hotbar == null || hotbar < 0 || hotbar > 8) {
                continue;
            }
            ToolSlot slot = toolbar.addSlot(hotbar);
            slot.active(entry.getOrDefault(k.active, PersistentDataType.INTEGER, -1));
            byte[] bytes = entry.get(k.items, PersistentDataType.BYTE_ARRAY);
            if (bytes != null) {
                ItemStack[] items = ItemStack.deserializeItemsFromBytes(bytes);
                for (int i = 0; i < Math.min(items.length, Settings.MAX_BELT_SIZE); i++) {
                    slot.set(i, items[i]);
                }
            }
        }
        return toolbar;
    }

    public void write(Plugin plugin, PersistentDataContainer pdc) {
        Keys k = keys(plugin);
        PersistentDataAdapterContext context = pdc.getAdapterContext();
        PersistentDataContainer data = context.newPersistentDataContainer();
        data.set(k.version, PersistentDataType.INTEGER, DATA_VERSION);
        data.set(k.enabled, PersistentDataType.BOOLEAN, enabled);
        List<PersistentDataContainer> entries = new ArrayList<>();
        for (ToolSlot slot : slots.values()) {
            PersistentDataContainer entry = context.newPersistentDataContainer();
            entry.set(k.hotbar, PersistentDataType.INTEGER, slot.hotbar());
            entry.set(k.active, PersistentDataType.INTEGER, slot.active());
            entry.set(k.items, PersistentDataType.BYTE_ARRAY, ItemStack.serializeItemsAsBytes(slot.contents()));
            entries.add(entry);
        }
        data.set(k.slots, PersistentDataType.LIST.dataContainers(), entries);
        pdc.set(k.data, PersistentDataType.TAG_CONTAINER, data);
    }
}
