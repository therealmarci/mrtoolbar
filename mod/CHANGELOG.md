# Changelog - Client-Mod

Änderungen am Fabric-Mod MRtoolbar. Versionen: `mod-mc<Minecraft>-v<Version>`, siehe
[docs/RELEASES.md](../docs/RELEASES.md). Das Plugin hat ein eigenes [CHANGELOG](../plugin/CHANGELOG.md) und eigene
Versionen - welche Plugin-Versionen passen, steht in [docs/COMPATIBILITY.md](../docs/COMPATIBILITY.md).

## [Unveröffentlicht]

## [0.2.2] - 2026-10-27

### Behoben & geändert
- **Rahmen in der Hotbar fehlte nach dem Einloggen:** Kam der Toolbar-Status vom Server vor der Antwort auf die Anmeldung an, verwarf der Mod ihn. Jetzt behält er ihn. Das Plugin behebt den Fehler ab 0.4.0 auch für ältere Mods; das Protokoll bleibt gleich.

## [0.2.1] - 2026-10-27

- **MRtechnology Lizenz 1.0:** Der Mod steht jetzt unter der einheitlichen Lizenz aller MRtechnology-Projekte (`LICENSE_MRtechnology` in der JAR, `LicenseRef-MRtechnology` in `fabric.mod.json`). Ausdrücklich erlaubt: den Mod aus der offiziellen Quelle nutzen ([mrtechnology.marcelr.at](https://mrtechnology.marcelr.at/download/mrtoolbar/)), Server-Betreiber dürfen dorthin verlinken. Sonst keine Änderungen, das Protokoll bleibt gleich.

## [0.2.0] - 2026-10-23

- **Taste „Inventar sortieren“** (Standard: Mausrad-Klick) in offenen Inventaren: Maus über dem eigenen Inventar
  sortiert das Inventar, über einer Kiste, einem Fass, Trichter usw. den Behälter (nicht beim Tippen in
  Suchfeldern, im Kreativmodus bleibt Mausrad-Klick „Item kopieren“). Sortiert wird auf dem Server, Ausnahmen in
  `/toolbar`. Braucht ein Plugin mit Sortieren - auf älteren Servern macht die Taste nichts, alles andere funktioniert
  weiter. Kein Konflikt mit „Block auswählen“, weil die Taste im Spiel selbst nichts tut.
- **Steuerung aufgeräumt:** feste Reihenfolge statt alphabetisch (Gürtel öffnen, Tools wechseln, nächstes / vorheriges
  Tool, Inventar sortieren, Hotbar-Design), klarere Namen. Eigene Belegungen bleiben erhalten.

## [0.1.0] - 2026-10-21

Erste Vorabversion für Minecraft 26.3 (Fabric). Braucht auf dem Server das MRtoolbar-Plugin (Protokoll 2).

- **Tasten** frei belegbar unter Optionen → Steuerung → MRtoolbar: Gürtel öffnen (B), nächstes / vorheriges Tool,
  Alt halten + Mausrad, Hotbar-Design öffnen.
- **Markierung** der Tool-Slots direkt in der Hotbar (unter Auswahlrahmen und Items).
- **Hotbar-Design** mit Vorschau im eigenen Resourcepack: Stil (Rahmen, Ecken, Unterstrich, Hintergrund, Textur aus
  dem Resourcepack), Farbe, Deckkraft, Dicke, Abstand, Verhalten auf dem ausgewählten Slot. Gespeichert pro PC.
- Texturepacks können die Markierung über `assets/mrtoolbar/textures/gui/sprites/hud/tool_slot.png` gestalten.
- „Konfigurieren“ in Mod Menu öffnet das Hotbar-Design (Mod Menu optional).
