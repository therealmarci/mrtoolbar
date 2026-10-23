package at.marcelr.mrtoolbar.sort;

import at.marcelr.mrtoolbar.MRToolbar;
import at.marcelr.mrtoolbar.ToolbarManager;
import at.marcelr.mrtoolbar.gui.BeltEditor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sorts the 36 slots of a player inventory (hotbar + the three rows above) or an open container. Stacks of the same
 * item are merged, then everything is put back in the player's {@link SortMode} - in the own inventory into the
 * inventory rows first, then the hotbar.
 * <p>
 * Never touched: slots the player locked in the sort menu, active tool slots, the selected tool of a tool slot
 * wherever it is, armour, offhand and the item on the cursor. Works without the mod too (button in the sort menu).
 */
public final class InventorySorter {

    public static final String PERM_SORT = "mrtoolbar.sort";
    public static final String PERM_SORT_CONTAINERS = "mrtoolbar.sort.containers";

    public enum Result {
        /** Items were moved. */
        SORTED,
        /** Everything was already in order. */
        ALREADY_SORTED,
        /** Sorting is switched off in the config or the player lacks the permission. */
        NOT_ALLOWED,
        /** The open window is no container that may be sorted (furnace, plugin menu, type not in the config ...). */
        NOT_SORTABLE,
        /** Not right now: spectator, a belt is open, or the cooldown is running. */
        NOT_NOW
    }

    private final MRToolbar plugin;
    private final Map<UUID, Integer> lastSort = new HashMap<>();
    /** Position of each item in Minecraft's item registry (built on first use). */
    private Map<NamespacedKey, Integer> registryOrder;

    public InventorySorter(MRToolbar plugin) {
        this.plugin = plugin;
    }

    /** Whether the player may sort at all (config + permission). */
    public boolean allowed(Player player) {
        return plugin.settings().sorting().enabled() && player.hasPermission(PERM_SORT);
    }

    public long locked(Player player) {
        return plugin.toolbars().get(player).sortLocked(plugin.settings().sorting().defaultLocked());
    }

    public SortMode mode(Player player) {
        return plugin.toolbars().get(player).sortMode(plugin.settings().sorting().defaultMode());
    }

    public void setLocked(Player player, long locked) {
        plugin.toolbars().get(player).setSortLocked(locked & SortSlots.ALL);
        plugin.toolbars().save(player);
    }

    public void setMode(Player player, SortMode mode) {
        plugin.toolbars().get(player).setSortMode(mode);
        plugin.toolbars().save(player);
    }

    /** Whether inventory index {@code slot} is an active tool slot (sorting never touches it). */
    public boolean toolSlot(Player player, int slot) {
        return plugin.toolbars().isToolSlot(player, slot);
    }

    /** Sort key of the mod over the own inventory: sorts and tells the player in the action bar. */
    public void sortByKey(Player player) {
        report(player, sort(player), "sort-done");
    }

    /** Sort key of the mod over an open container (chest, barrel, hopper ...). */
    public void sortContainerByKey(Player player) {
        report(player, sortContainer(player), "sort-container-done");
    }

    private void report(Player player, Result result, String doneKey) {
        switch (result) {
            case SORTED -> feedback(player, doneKey, Sound.ITEM_BUNDLE_DROP_CONTENTS);
            case ALREADY_SORTED -> feedback(player, "sort-already", null);
            case NOT_ALLOWED -> {
                if (plugin.settings().sorting().enabled()) {
                    plugin.messages().send(player, "no-permission");
                }
            }
            case NOT_SORTABLE -> feedback(player, "sort-container-unsupported", null);
            case NOT_NOW -> {
            }
        }
    }

    private void feedback(Player player, String key, Sound sound) {
        player.sendActionBar(plugin.messages().get(key));
        if (sound != null && plugin.settings().gui().sounds()) {
            player.playSound(player.getLocation(), sound, 0.6f, 1.2f);
        }
    }

    /** Sorts the player's own inventory (without locked slots and tool slots). */
    public Result sort(Player player) {
        if (!allowed(player)) {
            return Result.NOT_ALLOWED;
        }
        // While a belt is open its tool slot is empty and the belt is written from the window - stay out of it.
        InventoryHolder top = player.getOpenInventory().getTopInventory().getHolder(false);
        if (player.getGameMode() == GameMode.SPECTATOR || top instanceof BeltEditor || coolingDown(player)) {
            return Result.NOT_NOW;
        }

        ToolbarManager toolbars = plugin.toolbars();
        PlayerInventory inventory = player.getInventory();
        long locked = locked(player);
        List<Integer> slots = new ArrayList<>();
        for (int slot : SortSlots.fillOrder()) {
            if (!SortSlots.locked(locked, slot) && !toolbars.isToolSlot(player, slot)
                    && !toolbars.markedAny(inventory.getItem(slot))) {
                slots.add(slot);
            }
        }
        return arrange(player, inventory, slots);
    }

    /**
     * Sorts the container the player has open - only real ones from the world (see {@link #sortable}), never the
     * menus of plugins, which are chests too.
     */
    public Result sortContainer(Player player) {
        if (!containersAllowed(player)) {
            return Result.NOT_ALLOWED;
        }
        if (player.getGameMode() == GameMode.SPECTATOR || coolingDown(player)) {
            return Result.NOT_NOW;
        }
        Inventory top = player.getOpenInventory().getTopInventory();
        if (!sortable(player, top)) {
            return Result.NOT_SORTABLE;
        }
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < top.getSize(); slot++) {
            // Never move a selected tool out of a tool slot, wherever it ended up.
            if (!plugin.toolbars().markedAny(top.getItem(slot))) {
                slots.add(slot);
            }
        }
        return arrange(player, top, slots);
    }

    /** Whether the player may sort containers at all (config + permission). */
    public boolean containersAllowed(Player player) {
        return allowed(player) && !plugin.settings().sorting().containers().isEmpty()
                && player.hasPermission(PERM_SORT_CONTAINERS);
    }

    /**
     * A container of the world whose type is allowed in sorting.containers: a block (chest, barrel, shulker box,
     * hopper ...), a double chest, a minecart or boat with a chest / hopper, or the player's own ender chest.
     * Plugin menus are chests with a holder of their own (or none) and are never sorted.
     */
    private boolean sortable(Player player, Inventory top) {
        if (!plugin.settings().sorting().containers().contains(top.getType())) {
            return false;
        }
        if (top.getType() == InventoryType.ENDER_CHEST) {
            return top.equals(player.getEnderChest());
        }
        InventoryHolder holder = top.getHolder(false);
        return holder instanceof BlockInventoryHolder || holder instanceof DoubleChest
                || holder instanceof StorageMinecart || holder instanceof HopperMinecart || holder instanceof ChestBoat;
    }

    /** Merges and sorts the items of {@code slots} and puts them back into these slots in order. */
    private Result arrange(Player player, Inventory inventory, List<Integer> slots) {
        List<ItemStack> items = new ArrayList<>();
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.isEmpty()) {
                items.add(item.clone());
            }
        }
        List<ItemStack> sorted = merge(items);
        sorted.sort(comparator(mode(player), sorted));

        boolean changed = false;
        for (int i = 0; i < slots.size(); i++) {
            ItemStack target = i < sorted.size() ? sorted.get(i) : null;
            ItemStack current = inventory.getItem(slots.get(i));
            if (!same(current, target)) {
                inventory.setItem(slots.get(i), target);
                changed = true;
            }
        }
        if (changed) {
            lastSort.put(player.getUniqueId(), Bukkit.getCurrentTick());
        }
        return changed ? Result.SORTED : Result.ALREADY_SORTED;
    }

    public void forget(Player player) {
        lastSort.remove(player.getUniqueId());
    }

    private boolean coolingDown(Player player) {
        Integer last = lastSort.get(player.getUniqueId());
        return last != null && Bukkit.getCurrentTick() - last < plugin.settings().sorting().cooldownTicks();
    }

    /** Fills up stacks of the same item (in the order found), so no stack is split over more slots than needed. */
    private static List<ItemStack> merge(List<ItemStack> items) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack item : items) {
            for (ItemStack stack : merged) {
                int room = stack.getMaxStackSize() - stack.getAmount();
                if (room > 0 && stack.isSimilar(item)) {
                    int moved = Math.min(room, item.getAmount());
                    stack.setAmount(stack.getAmount() + moved);
                    item.setAmount(item.getAmount() - moved);
                    if (item.isEmpty()) {
                        break;
                    }
                }
            }
            if (!item.isEmpty()) {
                merged.add(item);
            }
        }
        return merged;
    }

    private Comparator<ItemStack> comparator(SortMode mode, List<ItemStack> items) {
        Comparator<ItemStack> byType = Comparator.comparingInt(this::registryIndex);
        // Same item: full stacks first, then the least damaged one.
        Comparator<ItemStack> within = Comparator.<ItemStack>comparingInt(ItemStack::getAmount).reversed()
                .thenComparingInt(InventorySorter::damage);
        return switch (mode) {
            case TYPE -> byType.thenComparing(within);
            case NAME -> Comparator.<ItemStack, String>comparing(item -> item.getType().getKey().getKey())
                    .thenComparing(byType).thenComparing(within);
            case AMOUNT -> {
                Map<NamespacedKey, Integer> totals = new HashMap<>();
                for (ItemStack item : items) {
                    totals.merge(item.getType().getKey(), item.getAmount(), Integer::sum);
                }
                yield Comparator.<ItemStack>comparingInt(item -> totals.get(item.getType().getKey())).reversed()
                        .thenComparing(byType).thenComparing(within);
            }
        };
    }

    private int registryIndex(ItemStack item) {
        if (registryOrder == null) {
            Map<NamespacedKey, Integer> order = new HashMap<>();
            int index = 0;
            for (ItemType type : Registry.ITEM) {
                order.put(type.getKey(), index++);
            }
            registryOrder = order;
        }
        return registryOrder.getOrDefault(item.getType().getKey(), Integer.MAX_VALUE);
    }

    private static int damage(ItemStack item) {
        return item.getItemMeta() instanceof Damageable damageable ? damageable.getDamage() : 0;
    }

    private static boolean same(ItemStack a, ItemStack b) {
        boolean emptyA = a == null || a.isEmpty();
        boolean emptyB = b == null || b.isEmpty();
        if (emptyA || emptyB) {
            return emptyA == emptyB;
        }
        return a.getAmount() == b.getAmount() && a.isSimilar(b);
    }
}
