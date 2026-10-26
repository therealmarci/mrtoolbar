package at.marcelr.mrtoolbar;

import at.marcelr.mrtoolbar.controls.ToolAction;
import at.marcelr.mrtoolbar.gui.BeltEditor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static at.marcelr.mrtoolbar.Messages.tag;

/**
 * Toolbar data of the online players and everything that moves tools between belt and hotbar.
 * <p>
 * A tool slot only ever holds the selected tool of its belt. That tool carries a marker (PDC) with the slot it
 * belongs to; everything else that ends up in the slot is moved out again by {@link #guard}. The other tools of
 * the belt are stored in the player data ({@link ToolSlot}).
 */
public final class ToolbarManager {

    public static final String PERM_USE = "mrtoolbar.use";
    private static final String PERM_SLOTS = "mrtoolbar.slots.";
    private static final String PERM_BELT = "mrtoolbar.belt.";

    private final MRToolbar plugin;
    private final NamespacedKey marker;
    private final Map<UUID, PlayerToolbar> online = new HashMap<>();

    public ToolbarManager(MRToolbar plugin) {
        this.plugin = plugin;
        this.marker = new NamespacedKey(plugin, "tool-slot");
    }

    public PlayerToolbar get(Player player) {
        return online.computeIfAbsent(player.getUniqueId(),
                id -> PlayerToolbar.read(plugin, player.getPersistentDataContainer()));
    }

    /** Writes the data into the player (saved by the server together with the inventory). */
    public void save(Player player) {
        PlayerToolbar toolbar = online.get(player.getUniqueId());
        if (toolbar != null) {
            toolbar.write(plugin, player.getPersistentDataContainer());
            plugin.modBridge().update(player);
        }
    }

    public void unload(Player player) {
        save(player);
        online.remove(player.getUniqueId());
    }

    // ------------------------------------------------------------------ limits

    /** How many tool slots the player may have: mrtoolbar.slots.<n> (highest wins) or the config default. */
    public int maxToolSlots(Player player) {
        return Math.min(9, permissionNumber(player, PERM_SLOTS, plugin.settings().toolbar().defaultToolSlots()));
    }

    /** How many tools fit into each belt: mrtoolbar.belt.<n> (highest wins) or the config default, at most 8. */
    public int beltSize(Player player) {
        return Math.clamp(permissionNumber(player, PERM_BELT, plugin.settings().toolbar().defaultBeltSize()),
                1, Settings.MAX_BELT_SIZE);
    }

    private static int permissionNumber(Player player, String prefix, int fallback) {
        int best = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission();
            if (info.getValue() && permission.startsWith(prefix)) {
                try {
                    best = Math.max(best, Integer.parseInt(permission.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // e.g. mrtoolbar.slots.* - not a number
                }
            }
        }
        return best >= 0 ? best : fallback;
    }

    /**
     * Whether tool slots work for this player right now: compatible mod, permission, switched on, world, game mode,
     * admin mode.
     */
    public boolean active(Player player) {
        return plugin.modBridge().compatible(player)
                && player.hasPermission(PERM_USE)
                && get(player).enabled()
                && player.getGameMode() != GameMode.SPECTATOR
                && !plugin.settings().toolbar().disabledIn(player.getWorld().getName())
                && !paused(player);
    }

    /**
     * Whether the toolbar pauses for the player right now:
     * <ul>
     *   <li>MRadmin's admin mode - the admin inventory is a creative one, nothing of it may end up in a belt;</li>
     *   <li>MRpvp (duel, arena editor, watching) - MRpvp saves, clears and gives back the whole inventory, the
     *       selected tool goes with it and comes back exactly as it was.</li>
     * </ul>
     * While paused nothing moves between belts and inventory (no guard, no take back, no limits).
     */
    public boolean paused(Player player) {
        var s = plugin.settings().toolbar();
        return (s.pauseInAdminMode() && plugin.mradmin().adminMode(player))
                || (s.pauseInPvp() && plugin.mradmin().busyElsewhere(player));
    }

    /** The tool slot the player is holding, or null. */
    public ToolSlot held(Player player) {
        return get(player).slot(player.getInventory().getHeldItemSlot());
    }

    /** Whether hotbar index {@code index} is one of the player's tool slots and they are active. */
    public boolean isToolSlot(Player player, int index) {
        return index >= 0 && index < 9 && get(player).slot(index) != null && active(player);
    }

    /** Gives back tools that no longer fit after a permission or config change (fewer slots or a smaller belt). */
    public void enforceLimits(Player player) {
        if (paused(player)) {
            // Tools handed out now would land in a temporary inventory (admin mode, duel) and be lost.
            return;
        }
        PlayerToolbar toolbar = get(player);
        int returned = 0;
        int max = maxToolSlots(player);
        while (toolbar.slotCount() > max) {
            int last = toolbar.slots().stream().mapToInt(ToolSlot::hotbar).max().orElseThrow();
            returned += removeSlot(player, last);
        }
        int size = beltSize(player);
        for (ToolSlot slot : toolbar.slots()) {
            returned += giveBack(player, slot.takeFrom(size));
        }
        if (returned > 0) {
            save(player);
            plugin.messages().send(player, "limits-reduced", tag("count", returned));
        }
    }

    /** Puts items into the inventory (never into a tool slot), drops what does not fit. @return number of items */
    public int giveBack(Player player, Collection<ItemStack> items) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack item : items) {
            ItemStack clean = unmark(item);
            int free = freeNormalSlot(player);
            if (free >= 0) {
                inventory.setItem(free, clean);
            } else {
                player.getWorld().dropItem(player.getLocation(), clean, drop -> drop.setOwner(player.getUniqueId()));
            }
        }
        return items.size();
    }

    // ------------------------------------------------------------------ marker

    /** Whether {@code item} is the selected tool of the tool slot on {@code hotbar}. */
    public boolean marked(ItemStack item, int hotbar) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        Integer value = item.getPersistentDataContainer().get(marker, PersistentDataType.INTEGER);
        return value != null && value == hotbar;
    }

    /** Whether {@code item} is the selected tool of any tool slot. */
    public boolean markedAny(ItemStack item) {
        return item != null && !item.isEmpty() && item.getPersistentDataContainer().has(marker, PersistentDataType.INTEGER);
    }

    private ItemStack mark(ItemStack item, int hotbar) {
        ItemStack copy = item.clone();
        copy.editPersistentDataContainer(pdc -> pdc.set(marker, PersistentDataType.INTEGER, hotbar));
        return copy;
    }

    /** The item without marker (for the belt storage, drops and everything that leaves the tool slot). */
    public ItemStack unmark(ItemStack item) {
        if (!markedAny(item)) {
            return item;
        }
        ItemStack copy = item.clone();
        copy.editPersistentDataContainer(pdc -> pdc.remove(marker));
        return copy;
    }

    // ------------------------------------------------------------------ tool slots

    public enum AddResult { ADDED, LIMIT, NO_ROOM }

    /**
     * Makes a hotbar slot a tool slot. A tool lying there becomes its first tool, anything else is moved to another
     * slot of the inventory.
     */
    public AddResult addSlot(Player player, int hotbar) {
        PlayerToolbar toolbar = get(player);
        if (toolbar.slot(hotbar) != null) {
            return AddResult.ADDED;
        }
        if (toolbar.slotCount() >= maxToolSlots(player)) {
            return AddResult.LIMIT;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack existing = inventory.getItem(hotbar);
        boolean tool = plugin.settings().toolbar().allowed().allows(existing);
        if (existing != null && !existing.isEmpty() && !tool) {
            int free = freeNormalSlot(player, hotbar);
            if (free < 0) {
                return AddResult.NO_ROOM;
            }
            inventory.setItem(free, existing);
            inventory.setItem(hotbar, null);
        }
        ToolSlot slot = toolbar.addSlot(hotbar);
        if (tool) {
            inventory.setItem(hotbar, mark(existing, hotbar));
            slot.active(0);
        }
        save(player);
        return AddResult.ADDED;
    }

    /** Turns a tool slot back into a normal slot; all its tools go into the inventory. @return returned tools */
    public int removeSlot(Player player, int hotbar) {
        ToolSlot slot = get(player).removeSlot(hotbar);
        if (slot == null) {
            return 0;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack inHand = inventory.getItem(hotbar);
        int returned = 0;
        if (marked(inHand, hotbar)) {
            inventory.setItem(hotbar, unmark(inHand));
            returned++;
        }
        returned += giveBack(player, slot.takeFrom(0));
        save(player);
        return returned;
    }

    // ------------------------------------------------------------------ switching

    /**
     * Runs an action (from a key binding or the mod) on the tool slot the player holds.
     *
     * @return false if the player holds no active tool slot
     */
    public boolean perform(Player player, ToolAction action) {
        ToolSlot slot = held(player);
        if (slot == null || !active(player)) {
            return false;
        }
        switch (action) {
            case OPEN -> plugin.openBelt(player, slot);
            case NEXT -> cycle(player, slot, 1);
            case PREVIOUS -> cycle(player, slot, -1);
            // Not tied to a tool slot - ModBridge hands them to the InventorySorter.
            case SORT, SORT_CONTAINER -> {
                return false;
            }
        }
        return true;
    }

    /** Next / previous tool (cycling is the only way to choose). @return whether something changed */
    public boolean cycle(Player player, ToolSlot slot, int step) {
        if (BeltEditor.isOpen(player, slot)) {
            return false;
        }
        guardSlot(player, slot);
        PlayerInventory inventory = player.getInventory();
        int size = beltSize(player);
        ItemStack current = inventory.getItem(slot.hotbar());
        boolean holdsTool = marked(current, slot.hotbar());
        int next = ToolSlot.next(slot.occupied(size), holdsTool ? slot.active() : -1, step);
        if (next < 0) {
            player.sendActionBar(plugin.messages().get(holdsTool ? "belt-no-other" : "belt-empty"));
            return false;
        }
        ItemStack target = slot.get(next);
        slot.set(next, null);
        if (holdsTool) {
            int back = slot.returnPosition(size);
            if (back < 0) {
                // Cannot happen (the target's place just became free) - but never lose a tool.
                back = next;
            }
            slot.set(back, unmark(current));
        }
        inventory.setItem(slot.hotbar(), mark(target, slot.hotbar()));
        slot.active(next);
        save(player);
        // Minecraft shows the name of the new item above the hotbar by itself.
        if (plugin.settings().gui().sounds()) {
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.5f, 1.5f);
        }
        return true;
    }

    /** Puts the tool of {@code position} into the (empty) tool slot. */
    private void equip(Player player, ToolSlot slot, int position) {
        ItemStack tool = slot.get(position);
        if (tool == null) {
            return;
        }
        slot.set(position, null);
        player.getInventory().setItem(slot.hotbar(), mark(tool, slot.hotbar()));
        slot.active(position);
        save(player);
    }

    /** The place a tool should be equipped from: the last selected one if still there, otherwise the first. */
    private static int preferred(ToolSlot slot, int size) {
        if (slot.active() >= 0 && slot.active() < size && !slot.free(slot.active())) {
            return slot.active();
        }
        for (int i = 0; i < size; i++) {
            if (!slot.free(i)) {
                return i;
            }
        }
        return -1;
    }

    /** Without (compatible) mod: the selected tools go back into their belts, the hotbar is a normal one. */
    public void suspend(Player player) {
        for (ToolSlot slot : get(player).slots()) {
            prepareEditor(player, slot);
        }
    }

    /** Turns every tool slot back into a normal one and gives all tools back. @return number of tools */
    public int takeBackAll(Player player) {
        int returned = 0;
        for (int hotbar : get(player).slots().stream().map(ToolSlot::hotbar).toList()) {
            returned += removeSlot(player, hotbar);
        }
        return returned;
    }

    /** Switches the toolbar on or off for the player. Off: the selected tools go back into their belts. */
    public void setEnabled(Player player, boolean enabled) {
        PlayerToolbar toolbar = get(player);
        if (!enabled && toolbar.enabled()) {
            for (ToolSlot slot : toolbar.slots()) {
                prepareEditor(player, slot);
            }
        }
        toolbar.enabled(enabled);
        save(player);
        if (enabled) {
            guard(player);
        }
    }

    // ------------------------------------------------------------------ belt editor

    /** Before the belt opens: the selected tool goes back into the belt, so the window shows all tools. */
    public void prepareEditor(Player player, ToolSlot slot) {
        PlayerInventory inventory = player.getInventory();
        ItemStack inHand = inventory.getItem(slot.hotbar());
        if (marked(inHand, slot.hotbar())) {
            int back = slot.returnPosition(beltSize(player));
            if (back >= 0) {
                slot.set(back, unmark(inHand));
                slot.active(back);
                inventory.setItem(slot.hotbar(), null);
            }
        }
        save(player);
    }

    /** After the belt closed: the last selected tool (or the first one) goes into the tool slot again. */
    public void finishEditor(Player player, ToolSlot slot) {
        if (get(player).slot(slot.hotbar()) != slot) {
            return;
        }
        guardSlot(player, slot);
    }

    // ------------------------------------------------------------------ guard

    /** Checks all tool slots of the player (see {@link #guardSlot}). */
    public void guard(Player player) {
        if (!active(player)) {
            return;
        }
        for (ToolSlot slot : get(player).slots()) {
            if (!BeltEditor.isOpen(player, slot)) {
                guardSlot(player, slot);
            }
        }
    }

    /**
     * Makes sure the tool slot holds its selected tool and nothing else: a foreign item is moved to a free slot
     * (or dropped if there is none), a selected tool that ended up elsewhere comes back, and an empty slot gets the
     * next tool of the belt (e.g. after the tool broke).
     */
    private void guardSlot(Player player, ToolSlot slot) {
        PlayerInventory inventory = player.getInventory();
        int hotbar = slot.hotbar();
        ItemStack inSlot = inventory.getItem(hotbar);
        if (inSlot != null && !inSlot.isEmpty() && !marked(inSlot, hotbar)) {
            moveOut(player, hotbar, inSlot);
            inSlot = null;
        }
        // The selected tool somewhere else in the inventory (e.g. moved by another plugin): back into its slot.
        ItemStack[] storage = inventory.getStorageContents();
        for (int i = 0; i < storage.length; i++) {
            if (i != hotbar && marked(storage[i], hotbar)) {
                if (inSlot == null || inSlot.isEmpty()) {
                    inventory.setItem(hotbar, storage[i]);
                    inventory.setItem(i, null);
                    inSlot = storage[i];
                } else {
                    inventory.setItem(i, unmark(storage[i]));
                }
            }
        }
        if (inSlot == null || inSlot.isEmpty()) {
            int position = preferred(slot, beltSize(player));
            if (position >= 0) {
                equip(player, slot, position);
            }
        }
    }

    private void moveOut(Player player, int hotbar, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(hotbar, null);
        int free = freeNormalSlot(player);
        if (free >= 0) {
            inventory.setItem(free, unmark(item));
        } else {
            player.getWorld().dropItem(player.getLocation(), unmark(item), drop -> drop.setOwner(player.getUniqueId()));
            plugin.messages().send(player, "slot-cleared-dropped");
        }
    }

    /** First empty inventory slot that is not a tool slot (and not {@code except}), or -1. */
    private int freeNormalSlot(Player player, int... except) {
        ItemStack[] storage = player.getInventory().getStorageContents();
        PlayerToolbar toolbar = get(player);
        outer:
        for (int i = 0; i < storage.length; i++) {
            if (storage[i] != null && !storage[i].isEmpty() || (i < 9 && toolbar.slot(i) != null)) {
                continue;
            }
            for (int skip : except) {
                if (skip == i) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Whether an item on the ground can be picked up without landing in a tool slot: Minecraft puts it into the
     * first empty slot, which may be an empty tool slot - the guard moves it out again, but only if there is room.
     */
    public boolean roomOutsideToolSlots(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] storage = inventory.getStorageContents();
        PlayerToolbar toolbar = get(player);
        boolean emptyToolSlot = false;
        for (ToolSlot slot : toolbar.slots()) {
            ItemStack inSlot = storage[slot.hotbar()];
            emptyToolSlot |= inSlot == null || inSlot.isEmpty();
        }
        if (!emptyToolSlot) {
            return true;
        }
        for (int i = 0; i < storage.length; i++) {
            if (i < 9 && toolbar.slot(i) != null) {
                continue;
            }
            ItemStack stack = storage[i];
            if (stack == null || stack.isEmpty()
                    || (stack.isSimilar(item) && stack.getAmount() < stack.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ death

    /**
     * Handles the drops of a dying player. {@code keep}: the selected tools stay in their belts (they come back into
     * the tool slots after respawning); otherwise all tools of the belts drop like the inventory.
     */
    public void onDeath(Player player, List<ItemStack> drops, boolean keep) {
        PlayerToolbar toolbar = get(player);
        int size = beltSize(player);
        for (Iterator<ItemStack> it = drops.iterator(); it.hasNext(); ) {
            ItemStack drop = it.next();
            if (!markedAny(drop)) {
                continue;
            }
            ToolSlot slot = null;
            for (ToolSlot candidate : toolbar.slots()) {
                if (marked(drop, candidate.hotbar())) {
                    slot = candidate;
                }
            }
            int back = keep && slot != null ? slot.returnPosition(size) : -1;
            if (back >= 0) {
                slot.set(back, unmark(drop));
                slot.active(back);
                it.remove();
            }
        }
        // Remaining markers must not leave the player.
        drops.replaceAll(this::unmark);
        if (!keep) {
            List<ItemStack> belts = new ArrayList<>();
            for (ToolSlot slot : toolbar.slots()) {
                belts.addAll(slot.takeFrom(0));
                slot.active(-1);
            }
            drops.addAll(belts);
        }
        save(player);
    }
}
