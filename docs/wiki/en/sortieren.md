# Sorting your inventory

A **middle click** in an open inventory sorts it: over your inventory your inventory, over a chest, barrel, shulker box, hopper … the container. Equal items are stacked and ordered by category, name or amount.

## What stays where it is
In **`/toolbar` → hopper** (or `/toolbar sort`) you set which slots stay where they are: a menu like your inventory, every slot a switch - green is sorted, red stays. By default the hotbar stays as it is.

Never touched: tool slots, armor, off hand and the item on the cursor.

## Without the mod
The menu under `/toolbar sort` has a button **Sort now** - it also works without the mod.

> [!NOTE]
> **For the team:** sorting needs `mrtoolbar.sort`, containers additionally `mrtoolbar.sort.containers` (both in `mrtoolbar.player`). Which containers, which sorting and which slots are locked by default is under `sorting` in the [configuration](konfiguration.md). Plugin menus are never sorted, even if they look like a chest.
