# Plugin und Mod

Der **Server** macht alles, was mit Items zu tun hat: Gürtel speichern, Tools wechseln, Tool-Slots schützen, Rechte, Tod. Der **Mod** kümmert sich nur um Tasten und Darstellung und fragt beim Server an - er kann keine Items erzeugen oder verschieben.

## Passen die Versionen?
Plugin und Mod haben **eigene Versionen und Releases**. Ob sie zusammenpassen, entscheidet nicht die Versionsnummer, sondern das gemeinsame **Protokoll** - ein Plugin-Update zwingt deine Spieler also nicht zu einem Mod-Update. Beim Betreten prüfen beide, ob sie zusammenpassen:

| Ergebnis | Was passiert |
|---|---|
| passt | Werkzeugleiste läuft |
| Mod zu alt | Werkzeugleiste aus, Hinweis im Chat: Mod aktualisieren (mit Link) |
| Mod zu neu | Werkzeugleiste aus, Hinweis im Chat: Admin informieren bzw. passende Mod-Version nutzen - das Plugin auf dem Server ist veraltet |
| kein Mod (nach `mod.handshake-seconds`) | Spieler spielt normal, Werkzeugleiste aus, Hinweis mit Download-Link; Tools bleiben sicher im Gürtel, `/toolbar takeback` holt sie zurück |

Mit `mod.minimum-version` sperrst du zusätzlich ältere Mod-Versionen aus, z.B. wegen eines Fehlers im Mod. Ob ein Spieler verbunden ist und mit welcher Version, zeigt `/mrtoolbar debug <spieler>` bzw. `/mrtoolbar gui`.

Technische Einzelheiten (Handshake, Nachrichten, Protokoll-Regeln): [Kompatibilität Plugin ↔ Mod](/docs/COMPATIBILITY.md).
