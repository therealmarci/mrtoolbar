# Set up the mod & keys

## Setup
1. Install **Fabric Loader** and **[Fabric API](https://modrinth.com/mod/fabric-api)** for Minecraft 26.3.
2. **[Download the MRtoolbar mod](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (row "Fabric mod (client)") and put it into the `mods/` folder.
3. Join the server and open **`/toolbar`**: turn a hotbar slot into a **tool slot**.
4. Press **`B`** on the tool slot: the belt opens like a chest - put tools into it.
5. **Hold `Alt` + mouse wheel** to cycle through the tools.

The server decides how many tool slots and how many places per belt you have (default 2 tool slots with 6 places each).

## Keys
| Key (default) | Function |
|---|---|
| `B` | Open the belt of the tool slot |
| Hold `Alt` + mouse wheel | Next / previous tool |
| Middle click in an inventory | **Sort** - over your inventory your inventory, over a chest, barrel, hopper … the container |
| not bound | Next tool, previous tool, open hotbar design |

You change all keys under **Options → Controls → MRtoolbar**.

## Commands
| Command | What it does |
|---|---|
| `/toolbar` (`/tb`, `/tools`) | Menu: tool slots, belts, hotbar design, sorting, on/off |
| `/toolbar on` · `off` | Turn the tool bar on or off |
| `/toolbar takeback` | All tools from the belts back into your inventory (also works without the mod) |
| `/toolbar sort` | Set sorting exceptions and sort now (also works without the mod) |

Whether there is a mod update is shown by `/toolbar` in the book at the top left.

## Good to know
- **Mending** also repairs the tools in the belt: first the belt you have in your hand, then the others from left to right. Only when everything is repaired do you get the XP.
- **When you die** the tools drop like normal items or stay in the belt, depending on the server.
- In some worlds or situations the tool bar is paused (e.g. in a duel) - your tools stay safe in the belt.

> [!TIP]
> `B` does nothing? Check in Mod Menu whether the mod is loaded, and in `/toolbar` whether the slot really is a tool slot.
