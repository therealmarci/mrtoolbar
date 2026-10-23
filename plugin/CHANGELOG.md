# Changelog - Plugin

Änderungen am Paper-Plugin MRtoolbar. Versionen: `plugin-mc<Minecraft>-v<Version>`, siehe
[docs/RELEASES.md](../docs/RELEASES.md). Der Client-Mod hat ein eigenes [CHANGELOG](../mod/CHANGELOG.md) und eigene
Versionen - welche Mod-Versionen passen, steht in [docs/COMPATIBILITY.md](../docs/COMPATIBILITY.md).

## [Unveröffentlicht]

## [0.2.0] - 2026-10-23

- **Inventar sortieren:** Taste im Mod (Standard: Mausrad-Klick in einem offenen Inventar) oder Knopf
  „Jetzt sortieren“.
  Gleiche Items werden zusammengelegt, Reihenfolge pro Spieler wählbar: Kategorie, Name, Menge.
- **Ausnahmen** in `/toolbar` → Trichter bzw. `/toolbar sort`: Menü wie das Inventar (oben die drei Reihen, unten die
  Hotbar), jeder Slot ein Schalter - grün wird sortiert, rot bleibt. Klick ins echte Inventar darunter schaltet
  denselben Slot, dazu „ganzes Inventar / ganze Hotbar“. Gespeichert im Spielerdatensatz.
- Nie sortiert: Tool-Slots, das gewählte Tool, Rüstung, Zweithand, Item am Mauszeiger.
- **Behälter sortieren:** Sortier-Taste über einer offenen Kiste, Doppelkiste, Fass, Shulker-Kiste, Endertruhe,
  Trichter, Werfer, Spender (auch Kisten-/Trichter-Loren und Kistenboote). Welche Typen, steht in
  `sorting.containers`, Permission `mrtoolbar.sort.containers`. Menüs von Plugins werden nie sortiert.
- Neue Permission `mrtoolbar.sort` (in `mrtoolbar.player` enthalten), Config-Abschnitt `sorting`
  (an/aus, Standard-Reihenfolge, Standard-Sperren - Standard: Hotbar gesperrt -, Cooldown, Behälter).
  config-version 8.
- Sortieren über das Menü geht auch ohne Mod. Kein neues Protokoll - neue Mods funktionieren weiter mit Plugin 0.1.x.
- **MRpvp:** In Duellen, im Arena-Editor und beim Zuschauen pausiert die Werkzeugleiste (`toolbar.pause-in-pvp`,
  Standard an) - MRpvp sichert das ganze Inventar samt gewähltem Tool und gibt es danach genau so zurück.
  config-version 9.
- Neue Schnittstelle `MRtoolbarApi` (`paused`, `belts`) für andere Plugins; MRpvp zeigt damit die Gürtel im
  gesicherten Inventar an.

### Behoben
- Während einer Pause (auch im MRadmin-Adminmodus) wird nichts mehr zwischen Gürtel und Inventar verschoben:
  `/toolbar on|off|takeback` sind gesperrt, gekürzte Limits warten bis nach der Pause. Vorher konnten Tools im
  Kreativ-Inventar des Adminmodus landen und mit ihm verschwinden.

## [0.1.0] - 2026-10-21

Erste Vorabversion für Minecraft 26.3. Braucht auf dem Client den MRtoolbar-Mod (Protokoll 2).

- **Tool-Slots:** Hotbar-Slots lassen sich in `/toolbar` zu Tool-Slots machen. Jeder Tool-Slot hat einen eigenen
  Gürtel mit bis zu 8 Tools; im Slot liegt immer nur das gewählte Tool aus seinem Gürtel.
- **Gürtel** wie eine Kiste: Tools hineinlegen und herausnehmen, das gewählte Tool kommt danach zurück in den Slot.
- **Schutz:** In einen Tool-Slot kommt nichts per Hand hinein oder hinaus (Klicken, Ziehen, Zifferntasten, F, Q,
  Aufheben). Bricht ein Tool, rückt das nächste aus dem Gürtel nach.
- Gürtel und Inventar werden zusammen im Spielerdatensatz gespeichert - kein Verdoppeln oder Verlieren bei Abstürzen.
- **Tod:** Gürtel-Inhalt fällt wie das Inventar (`on-death: drop`) oder bleibt erhalten (`keep`); keepInventory wird
  beachtet.
- **Limits per LuckPerms:** `mrtoolbar.slots.<n>` (Anzahl Tool-Slots), `mrtoolbar.belt.<n>` (Gürtelgröße), erlaubte
  Items als Liste mit Item-Tags.
- **MRadmin:** Im Adminmodus ist die Werkzeugleiste pausiert (kein Kreativ-Item im Gürtel).
- **Ohne Mod:** Werkzeugleiste pausiert, Tools sicher im Gürtel, Hinweis mit Download-Link, `/toolbar takeback`
  holt alle Tools zurück.
- **Versionsprüfung** beim Betreten (Protokoll + optional `mod.minimum-version`), klare Meldungen bei zu alter oder
  zu neuer Mod-Version.
- `/toolbar` zeigt Server- und Client-Version samt Update-Status, `/mrtoolbar debug [spieler]` die Mod-Verbindung.
- **Team:** `/mrtoolbar gui` listet alle Online-Spieler mit Mod-Version und Tool-Slots, `/mrtoolbar view <spieler>`
  zeigt ihre Gürtel (nur ansehen). **MRadmin** zeigt MRtoolbar im Dashboard und im Spieler-Menü, dort öffnet ein Klick
  die Gürtel - wie Inventar und Enderchest.
- **Sprache pro Spieler:** Plugin und Mod richten sich nach der Minecraft-Sprache des Spielers (Deutsch inkl.
  Österreich/Schweiz → Deutsch, sonst Englisch bzw. eigene Sprachdateien) - keine gemischten Sprachen.
  Feste Sprache für alle weiterhin möglich (`language: de`). Konsole immer Englisch.
- Update-Check über GitHub Releases.

