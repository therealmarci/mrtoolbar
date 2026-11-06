# FAQ

### I press `B`, but nothing happens.
Check whether the mod is loaded (Mod Menu) and whether the slot is a tool slot (`/toolbar`). Admins see with `/mrtoolbar debug <player>` whether the connection to the mod is up.

### Does every player have to install the mod?
No. Players without the mod play normally, just without the tool bar. `/toolbar takeback` and sorting via `/toolbar sort` also work without the mod.

### What happens to the tools on death?
By default they drop like normal items (`toolbar.on-death: drop`). With `keep` they stay in the belt.

### Does Mending also repair the tools in the belt?
Yes. What Minecraft does not need for the item in your hand and the armor goes to the belts: first to the belt of the tool slot you have in your hand, then to the other belts from left to right - in every belt one tool after the other, in the order they are in the belt. Only when everything is repaired do you get the XP. Turn off: `toolbar.mending: false` or remove the permission `mrtoolbar.mending`.

### Do I have to update the mod after a plugin update?
Usually not - what matters is the shared protocol, not the version. If it does not fit, the server tells you when joining (see [Plugin and mod](plugin-und-mod.md)).

### I found a bug or have a wish.
Gladly via the **[form on the website](https://mrtechnology.marcelr.at/report/mrtoolbar/)** - ideally with the output of `/mrtoolbar version` and your mod version.
