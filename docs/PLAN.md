# MRtoolbar – Konzept & Plan

Name: **MRtoolbar** (Ordner `mrtoolbar`, bis 2026-10-21 `qol-tool`). Paper-Plugin für Minecraft 26.3, gleicher Aufbau wie
MRsleep / MRchunks / MRadmin (Gradle Kotlin DSL, Java 25, `mc<MC>-v<semver>`, beta/main, lang-Dateien,
eine config.yml, LuckPerms-Permission für jede Funktion).

## Idee

Die Hotbar bekommt zwei Arten von Slots:

- **Normale Slots** – verhalten sich wie immer.
- **Tool-Slots** – frei wählbare Hotbar-Slots (pro Spieler). Hinter jedem Tool-Slot hängt ein eigener
  **Werkzeuggürtel** (virtueller Speicher). Über ein **Rad** (wie das Waffenrad in GTA) wählt man,
  welches Tool aus dem Gürtel gerade im Slot liegt. Das gewählte Tool ist ein echtes Item und wird
  ganz normal benutzt (Haltbarkeit, Verzauberungen, Reparieren, … bleiben unverändert).

Komplett optional: Spieler ohne Tool-Slots merken nichts vom Plugin.

## Entscheidungen (2026-10-21)

| Thema | Entscheidung |
|---|---|
| Architektur | Reines Paper-Plugin, kein Client-Mod |
| Rad-Anzeige | Resourcepack mit eigener Schrift, Rad als Title-Overlay |
| Speicher | Virtueller Gürtel pro Tool-Slot |
| Steuerung | Pro Spieler konfigurierbar, Standard: Tool-Slot halten + **F** |
| Adminmodus (MRadmin) | Werkzeugleiste pausiert (Kreativ-Inventar darf nicht in den Gürtel), per `MRadminApi.adminMode` |
| Resourcepack | Vom Server, Pflicht (Kick mit Erklärung, Ausnahme `mrtoolbar.resourcepack.optional`), eingebauter Webserver Port 8163 / eigene URL / manuell; nur eigener Namespace → Spieler-Texturepacks bleiben aktiv |
| Markierung | Rahmen über Tool-Slots per Actionbar + Pack-Schrift (2026-10-21); Nachteil: überdeckt andere Actionbar-Meldungen (MRsleep, MRadmin-Freeze) |
| Konzept-Änderung (2026-10-21) | **Kein Rad mehr.** F (frei belegbar) öffnet den Gürtel wie eine Kiste, gewählt wird nur durch Durchschalten (nächstes/vorheriges Tool). Tool-Slots nehmen nur das gewählte Gürtel-Tool auf (Marker im Item-PDC, Wächter verschiebt Fremdes). Tasten: F/Q/Mausrad hoch/runter je ohne/Shift/Strg + 2× Shift, pro Spieler frei belegbar |
| Client-Mod (2026-10-21) | Optionaler **Fabric**-Mod in `mod/` (Plugin in `plugin/`): eigene Tasten unter Optionen → Steuerung → MRtoolbar (Gürtel öffnen = B, nächstes/vorheriges Tool, Alt halten + Mausrad), zeichnet die Markierung selbst. Protokoll `mrtoolbar:action` / `mrtoolbar:state` (siehe ModBridge). Für Mod-Spieler sind Server-Tasten und Actionbar-Markierung aus. Versionen von Plugin und Mod gleich halten (gemeinsames Release) |
| **Nur mit Mod** (2026-10-21, ersetzt Resourcepack/Actionbar/Server-Tasten) | Toolbar nur mit kompatiblem Fabric-Mod; ohne Mod: Tool-Slots pausiert, Tools sicher im Gürtel, `/toolbar takeback`. Mod: Tasten, Markierung (in der Hotbar unter Auswahlrahmen/Items gezeichnet), Hotbar-Design (pro PC, config/mrtoolbar.json, Stile Rahmen/Ecken/Unterstrich/Hintergrund/Textur aus Resourcepack). Plugin: alles mit Items. Handshake + Protokoll + `mod.minimum-version`, Regeln in docs/COMPATIBILITY.md |

## Bausteine

### 1. Tool-Slots & Gürtel
- Spieler legt per `/tools` (GUI) fest, welche Hotbar-Slots Tool-Slots sind (max. per Permission
  `mrtoolbar.toolslots.<n>`), und befüllt pro Tool-Slot den Gürtel (Plätze per `mrtoolbar.beltsize.<n>`,
  maximal 8 = ein Rad mit 8 Segmenten).
- Erlaubte Items: konfigurierbare Liste (Standard: Werkzeuge, Waffen, Speer, Streitkolben, Bogen,
  Armbrust, Dreizack, Angel, Schere, Feuerzeug, Pinsel, Schild …).
- Speicherung: Gürtel-Inhalt serialisiert im PersistentDataContainer des Spielers (überlebt Neustarts,
  keine eigene Datenbank).
- Das aktive Tool trägt ein PDC-Tag (Gürtel + Platz). Wird es aus dem Slot genommen / gedroppt,
  verlässt es den Gürtel und ist wieder ein normales Item → keine Dupes, kein „verlorenes“ Item.
- Tod: konfigurierbar (`drop` = Gürtel-Inhalt droppt wie Inventar [Standard], `keep`, oder
  `keepInventory` der Welt übernehmen).

### 2. Rad öffnen (konfigurierbar, pro Spieler)
Auslöser, jeweils nur wenn ein Tool-Slot ausgewählt ist:
- **F** (Hand tauschen) – Standard; Event wird abgebrochen, auf normalen Slots bleibt F normal
- **Schleichen + F**
- **Q** (Droppen) auf dem Tool-Slot
- **Schleichen + Scrollen** – Schnellwechsel ohne Rad (Anzeige nur in der Actionbar)

Admin legt in der config.yml fest, welche Auslöser erlaubt sind und welcher Standard gilt.

### 3. Auswahl im Rad
- **Mausbewegung** (GTA-Gefühl): beim Öffnen wird die Blickrichtung gemerkt; die Abweichung
  (Yaw/Pitch) ergibt einen Winkel → Segment. Totzone in der Mitte = „nichts ändern“.
  Beim Schließen wird die Blickrichtung zurückgesetzt.
- **Mausrad** als Alternative (Scroll wird abgefangen, springt ein Segment weiter).
- **Bestätigen**: Klick, nochmal F, oder „Halten“-Modus (Schleichen halten → loslassen bestätigt,
  über Paper `PlayerInputEvent`). Timeout schließt ohne Änderung.

### 4. Anzeige (Resourcepack)
- Plugin baut das Pack selbst (Font mit Rad-Hintergrund, 8 Segment-Highlights, Abstands-Glyphen über
  den `space`-Provider) und liefert es über einen eingebauten Mini-HTTP-Server oder eine externe URL
  aus. Wird **zusätzlich** zum Server-Pack gesendet (mehrere Packs seit 1.20.3), stört also nichts.
- Item-Icons im Rad über Text-Objekte (Atlas-Sprites, seit 1.21.9) → keine eigene Grafik pro Item.
- Mitte des Rads: Name + Haltbarkeit des markierten Tools.
- Fallback, wenn ein Spieler das Pack ablehnt: kleines Inventar-Menü oder Actionbar-Liste
  (konfigurierbar).

### 5. Befehle & Rechte
- `/tools` – GUI: Tool-Slots wählen, Gürtel befüllen, Steuerung einstellen, an/aus
- `/mrtoolbar reload | info` – Admin
- Permissions: `mrtoolbar.use`, `mrtoolbar.toolslots.<n>`, `mrtoolbar.beltsize.<n>`,
  `mrtoolbar.trigger.<art>`, `mrtoolbar.admin` …

## Phasen

1. ✅ **Grundgerüst** (Build, Messages, ConfigUpdater, UpdateChecker, Menüs, Workflows, Lizenz)
2. ✅ **Gürtel-Logik**: Tool-Slots, Gürtel als Kiste, Speichern im Spielerdatensatz, Schutz der Tool-Slots, Tod
3. ~~Rad~~ entfällt, ~~Resourcepack + Actionbar~~ ersetzt durch den Client-Mod (2026-10-21)
4. ✅ **Client-Mod** (Fabric): Tasten, Markierung in der Hotbar, Hotbar-Design, Mod-Menu-Anbindung
5. ✅ **Kompatibilität**: Handshake, Protokoll, Mindestversion - [COMPATIBILITY.md](COMPATIBILITY.md)
6. ✅ **Release-fertig** `mc26.3-v0.1.0` (README, CHANGELOG, RELEASES.md, Workflows) - Veröffentlichung nur auf Ansage

## Offene Punkte
- ~~Name~~ → MRtoolbar (2026-10-21)
- GitHub-Repo `therealmarci/mrtoolbar` anlegen (main + beta) und erstes Pre-Release taggen - auf Ansage
