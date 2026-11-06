# Plugin and mod

The **server** does everything that has to do with items: saving belts, switching tools, protecting tool slots, permissions, death. The **mod** only takes care of keys and display and asks the server - it cannot create or move items.

## Do the versions match?
Plugin and mod have **their own versions and releases**. Whether they fit together is not decided by the version number but by the shared **protocol** - so a plugin update does not force your players to update the mod. When joining, both check whether they fit together:

| Result | What happens |
|---|---|
| fits | The tool bar works |
| mod too old | Tool bar off, notice in chat: update the mod (with link) |
| mod too new | Tool bar off, notice in chat: tell an admin or use a matching mod version - the plugin on the server is outdated |
| no mod (after `mod.handshake-seconds`) | The player plays normally, tool bar off, notice with download link; tools stay safe in the belt, `/toolbar takeback` gets them back |

With `mod.minimum-version` you can additionally lock out older mod versions, e.g. because of a bug in the mod. Whether a player is connected and with which version is shown by `/mrtoolbar debug <player>` or `/mrtoolbar gui`.

Technical details (handshake, messages, protocol rules): [Plugin ↔ mod compatibility](/docs/COMPATIBILITY.md).
