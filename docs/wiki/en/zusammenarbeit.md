# Working with other plugins

MRtoolbar runs **on its own**. Together with other MRtechnology plugins it can do more - the plugins talk to each other through **MRadmin** ([how that works](https://mrtechnology.marcelr.at/wiki/allgemein/zusammenarbeit/)).

| Feature | Needs |
|---|---|
| MRtoolbar in MRadmin's dashboard and player menu (mod version, tool slots, view belts) | MRadmin |
| Configure MRtoolbar in the browser (Plugins → MRtoolbar → Configuration) | MRadmin |
| Pause in admin mode (`toolbar.pause-in-admin-mode`) | MRadmin |
| Pause in duels, in the arena editor and while spectating (`toolbar.pause-in-pvp`), belts are saved along | MRadmin + MRpvp |
| Events in the **log** of the MRadmin web interface, Discord log via MRadmin's webhook (`discord.use-mradmin-webhook`) | MRadmin |

## Multiverse-Inventories
Every inventory group has its own tool slots and belts. When Multiverse-Inventories switches the inventory, the belts switch along, so no tools move between separate groups. On your first visit to a group there are no tool slots there yet, you set them up as usual with `/toolbar`. On/off and the sorting settings apply everywhere.
