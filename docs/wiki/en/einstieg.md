# Overview

**Tool slots for the hotbar:** a hotbar slot becomes a tool belt with up to 8 tools that you cycle through with a key. Pickaxe, axe, shovel and sword need only **one** slot - the rest of the hotbar stays free for blocks and food. Plus: sort your inventory and chests with one click.

MRtoolbar consists of **two parts** - both are on the [download page](https://mrtechnology.marcelr.at/download/mrtoolbar/):

| Part | Who needs it | Where |
|---|---|---|
| **Plugin** (`MRtoolbar-mc26.3-v….jar`) | the server | Paper 26.3, Java 25 → folder `plugins/` |
| **Client mod** (`MRtoolbar-mod-mc26.3-v….jar`) | every player who wants to use the tool bar | Fabric 26.3 with [Fabric API](https://modrinth.com/mod/fabric-api) → folder `mods/` |

Players **without** the mod play normally on the server, just without the tool bar. Plugin and mod have their own versions: not every plugin update needs a mod update.

| At a glance | |
|---|---|
| Server | Paper 26.3 (or a fork like Purpur), Java 25 |
| Client | Fabric Loader + Fabric API for Minecraft 26.3 |
| Requires | nothing - LuckPerms recommended, MRadmin optional |
| Languages | German, English - automatically like the Minecraft client |
| Status | stable (1.x) - plugin and mod |

## What can MRtoolbar do?
- **Tool slots:** a hotbar slot becomes a belt with up to 8 tools. You cycle through them with a key or `Alt` + mouse wheel. → [Set up the mod & keys](einrichten.md)
- **Mending in the belt:** tools with Mending are repaired in the belt as well - first the belt in your hand, then the others.
- **Sort your inventory and chests** with a middle click. You decide which slots stay where they are. → [Sorting your inventory](sortieren.md)
- **Hotbar design:** frame, corners, underline, background or your own texture for your tool slots. → [Hotbar design](design.md)
- **Safe:** all items are on the server, the mod only displays them. Players without the mod get their tools back with `/toolbar takeback`. → [Plugin and mod](plugin-und-mod.md)
- **For admins:** number of slots and belt size per permission, an overview of all belts, everything configurable in `config.yml`.

## Where to start?
| You are … | Read first |
|---|---|
| **a player** | [Set up the mod & keys](einrichten.md) |
| **a server owner** | [Quick start](schnellstart.md), then [Permissions](permissions.md) and [Configuration](konfiguration.md) |
