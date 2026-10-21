package at.marcelr.mrtoolbar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/** Decides which items may be put into a tool belt (toolbar.allowed-items in config.yml). */
public final class ToolFilter {

    private final Set<Material> materials;
    private final boolean anyItem;

    private ToolFilter(Set<Material> materials, boolean anyItem) {
        this.materials = materials;
        this.anyItem = anyItem;
    }

    /**
     * Entries are item names ("bow", "minecraft:shears"), item tags with "#" ("#minecraft:pickaxes") or "*" for
     * every item. Unknown entries are reported and skipped.
     */
    public static ToolFilter parse(List<String> entries, Logger log) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        boolean any = false;
        for (String raw : entries) {
            String entry = raw.trim();
            if (entry.equals("*")) {
                any = true;
            } else if (entry.startsWith("#")) {
                NamespacedKey key = NamespacedKey.fromString(entry.substring(1).toLowerCase());
                Tag<Material> tag = key == null ? null : Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
                if (tag == null) {
                    log.warning("Unknown item tag in toolbar.allowed-items: " + entry);
                } else {
                    materials.addAll(tag.getValues());
                }
            } else {
                Material material = Material.matchMaterial(entry);
                if (material == null || !material.isItem() || material.isAir()) {
                    log.warning("Unknown item in toolbar.allowed-items: " + entry);
                } else {
                    materials.add(material);
                }
            }
        }
        return new ToolFilter(materials, any);
    }

    public boolean allows(ItemStack item) {
        return item != null && !item.isEmpty() && (anyItem || materials.contains(item.getType()));
    }
}
