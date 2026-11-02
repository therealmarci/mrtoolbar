package at.marcelr.mrtoolbar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mvplugins.multiverse.inventories.event.GameModeChangeShareHandlingEvent;
import org.mvplugins.multiverse.inventories.event.ShareHandlingEvent;
import org.mvplugins.multiverse.inventories.event.WorldChangeShareHandlingEvent;
import org.mvplugins.multiverse.inventories.handleshare.PersistingProfile;
import org.mvplugins.multiverse.inventories.profile.key.ContainerType;
import org.mvplugins.multiverse.inventories.profile.key.ProfileKey;
import org.mvplugins.multiverse.inventories.share.Sharables;

import java.util.List;

/**
 * Multiverse-Inventories (optional, only registered if it is installed) gives every world group its own inventory. The
 * belts belong to the inventory - shared across groups they would carry tools past that separation. So the belts are
 * swapped exactly when Multiverse-Inventories swaps the inventory, which it announces right before doing so.
 */
final class InventoriesHook implements Listener {

    private final ToolbarManager toolbars;

    InventoriesHook(ToolbarManager toolbars) {
        this.toolbars = toolbars;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldChange(WorldChangeShareHandlingEvent event) {
        swap(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(GameModeChangeShareHandlingEvent event) {
        swap(event);
    }

    private void swap(ShareHandlingEvent event) {
        String from = inventoryOf(event.getWriteProfiles());
        String to = inventoryOf(event.getReadProfiles());
        if (from != null && to != null && !from.equals(to)) {
            toolbars.switchInventory(event.getPlayer(), from, to);
        }
    }

    /** Names the profile that holds the inventory; null if the inventory is not part of this change. */
    private static String inventoryOf(List<PersistingProfile> profiles) {
        ProfileKey world = null;
        for (PersistingProfile profile : profiles) {
            if (profile.getShares().contains(Sharables.INVENTORY)) {
                ProfileKey key = profile.getProfileKey();
                // Multiverse-Inventories may write the world profile as well - the group is the one that counts.
                if (key.getContainerType() == ContainerType.GROUP) {
                    return name(key);
                }
                world = key;
            }
        }
        return world == null ? null : name(world);
    }

    private static String name(ProfileKey key) {
        return key.getContainerType() + ":" + key.getDataName() + ":" + key.getProfileType().getName();
    }
}
