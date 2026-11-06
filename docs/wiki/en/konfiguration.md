# Configuration

Easiest in the browser: with **MRadmin** under **Plugins → MRtoolbar → Configuration** - every setting with an explanation, saving reloads MRtoolbar right away (permission `mradmin.config`). Everything is also commented in `plugins/MRtoolbar/config.yml`; you apply changes by hand with `/mrtoolbar reload`.

| Section | What for | More |
|---|---|---|
| `general` | Language of the players (`auto` = like the Minecraft client), fallback and server language | [Languages & messages](https://mrtechnology.marcelr.at/wiki/allgemein/sprachen/) |
| `toolbar` | Tool slots and belt size, allowed items, worlds without the tool bar, belts on death, pauses in admin mode and in duels, Mending | [Set up & use](einrichten.md) |
| `sorting` | Sorting on/off, default order, locked slots, containers | [Sorting](sortieren.md) |
| `mod` | Minimum version of the mod, download link, waiting time for the mod | [Plugin & mod](plugin-und-mod.md) |
| `discord` | Discord log | [Discord log](discord.md) |
| `updates` · `gui` | Notice about new versions, menus | |

Every single setting with its default is listed below under [All settings](#all-settings).

## Messages
All messages are in `plugins/MRtoolbar/lang/de.yml` and `en.yml` ([MiniMessage](https://docs.advntr.dev/minimessage/format.html)). Plugin and mod follow the player's Minecraft language - so there are no mixed languages. French players automatically get your own `fr.yml`. More under [Languages & messages](https://mrtechnology.marcelr.at/wiki/allgemein/sprachen/).

## All settings
Every setting of `config.yml` with its default - the same fields as in MRadmin's browser form. The list is generated automatically from the plugin and therefore always matches this version.

<!-- auto:config -->
