# Quick start (server)

1. **[Download the plugin](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (row "Plugin (server)") and put it into the `plugins/` folder.
2. Start the server.
3. **Give players the permissions** - without them only OPs may use the tool bar:
   ```text
   /lp group default permission set mrtoolbar.player true
   /lp group supporter permission set mrtoolbar.groups.support true
   /lp group staff permission set mrtoolbar.groups.staff true
   /lp group admin permission set mrtoolbar.groups.admin true
   ```
4. Give your players the link to the mod: **https://mrtechnology.marcelr.at/download/mrtoolbar/** - or directly the page [Set up the mod & keys](einrichten.md). In game the link is also in `/toolbar`, and whoever joins with tool slots but without the mod gets a notice with the link.

## Update
Replace the old JAR with the new one and restart the server. The config is adapted automatically. Plugin and mod have their own versions - a plugin update does not force your players to update the mod (see [Plugin and mod](plugin-und-mod.md)).
