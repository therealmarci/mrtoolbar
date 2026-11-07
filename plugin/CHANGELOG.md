# Changelog - Plugin

Änderungen am Paper-Plugin MRtoolbar. Versionen: `plugin-mc<Minecraft>-v<Version>`, siehe
[docs/RELEASES.md](../docs/RELEASES.md). Der Client-Mod hat ein eigenes [CHANGELOG](../mod/CHANGELOG.md) und eigene
Versionen - welche Mod-Versionen passen, steht in [docs/COMPATIBILITY.md](../docs/COMPATIBILITY.md).

## [Unveröffentlicht]

### Behoben & geändert
- **Geprüfte Releases:** Jedes Release muss jetzt vorher eine automatische Prüfrunde auf GitHub bestehen - Bauen und Tests, Code-Analyse auf Sicherheitsprobleme (CodeQL) und alle mitgelieferten Bibliotheken gegen bekannte Sicherheitslücken; ab Schweregrad hoch entsteht kein Release. Die veröffentlichte JAR ist genau die geprüfte und hat einen Herkunftsnachweis von GitHub (prüfen mit `gh attestation verify <jar> -R therealmarci/mrtoolbar`).
- **Bereit für das neue Versionsschema:** Die Update-Prüfung versteht neben den bisherigen Versionen (`mc26.3-v1.3.1`) auch das künftige Format `mc26.3-STA-1.4.0-bu001` (stabil) bzw. `mc26.3-BET-…` (Beta) und vergleicht beide richtig miteinander. Betas bekommt ein Server nur noch, wenn `updates.include-prereleases` an ist oder das Plugin noch eine 0.x-Version ist - wer Betas wieder ausschaltet, bleibt auf seiner Beta, bis eine neuere stabile Version erscheint (es wird nie zurückgestuft). Dieses Update vor dem ersten Release im neuen Schema einspielen, sonst sieht der Server neue Versionen nicht.

## [1.3.1] - 2026-11-06

### Behoben & geändert
- **Anleitung als Wiki:** Die Anleitung (Plugin und Mod) steht jetzt im Wiki auf der Website (https://mrtechnology.marcelr.at/wiki/mrtoolbar/) - aufgeteilt in Seiten für Spieler (Mod einrichten & Tasten, Inventar sortieren, Hotbar-Design) und für Admins (Befehle, Permissions, Konfiguration, Discord-Log, Plugin und Mod, Häufige Fragen), mit Suche über alle Plugins. Die README ist nur noch eine kurze Übersicht. Alte Links auf `/docs/mrtoolbar/` führen automatisch ins Wiki. Die Seiten **Permissions** und **Konfiguration** listen jede Permission bzw. Einstellung mit Standardwert automatisch aus dem Plugin - sie sind also immer auf dem Stand der Version.

## [1.3.0] - 2026-11-05

### Neu
- **Mending repariert den Gürtel:** Werkzeuge mit Mending werden jetzt auch im Gürtel repariert. Die XP, die Minecraft nicht für das Item in der Hand und die Rüstung braucht, geht zuerst an den Gürtel des Tool-Slots in der Hand (ein Tool nach dem anderen, in der Reihenfolge im Gürtel), ist dort alles heil, an die anderen Gürtel von links nach rechts. Erst danach bekommt der Spieler die XP. Neue Einstellung `toolbar.mending` (Standard an, `config-version` 14, auch im Browser) und Permission `mrtoolbar.mending` (in `mrtoolbar.player`).

### Behoben & geändert
- **Gürtel-Fenster zeigt keine fremden Tools mehr:** Solange ein Gürtel offen ist, liegen die ausgewählten Tools aller Tool-Slots in ihren Gürteln - in der Hotbar unter dem Fenster tauchen keine Tools aus anderen Gürteln mehr auf (das führte zu Fehlern beim Verschieben). Beim Schließen kommen sie wieder in ihre Slots.

## [1.2.0] - 2026-11-04

### Neu
- **Discord-Log:** MRtoolbar meldet in einen Discord-Kanal, wer mit zu alter (`mod-too-old`), zu neuer (`mod-too-new`) oder ohne Mod (`mod-missing`, anfangs aus) spielt, wenn das Team einen Gürtel ansieht (`belt-view`) und wenn jemand neu lädt (`admin-reload`). Einrichten unter `discord` in der `config.yml` (`config-version` 13, auch im Browser), prüfen mit **`/mrtoolbar discord [test]`** - neue Permission `mrtoolbar.admin.discord` (in `mrtoolbar.groups.admin`). Ohne eigenen Webhook geht es mit `discord.use-mradmin-webhook: true` über MRadmins Discord-Log.
- **Ereignisse im MRadmin-Protokoll:** Jedes dieser Ereignisse steht mit MRadmin (Modul-API 13) zusätzlich im Protokoll der Weboberfläche (bis zu 14 Tage) - auch wenn Discord aus ist.
- **Einstellen im Browser:** Mit MRadmin (ab der Version mit „Konfiguration im Browser“) lässt sich die ganze `config.yml` unter **Plugins → MRtoolbar → Konfiguration** als Formular einstellen - jede Einstellung mit Erklärung auf Deutsch und Englisch, Speichern lädt MRtoolbar sofort neu (wie `/mrtoolbar reload`). Die Datei bleibt von Hand bearbeitbar.

### Behoben & geändert
- **Config neu gegliedert** (gleicher Aufbau wie alle MRtechnology-Plugins; `config-version` 12): `language`, `fallback-language`, `server-language` → `general.*`, `update-checker` → `updates`. Bestehende Einstellungen werden beim Update automatisch übernommen (Sicherung `config.v11.backup.yml`).

## [1.1.0] - 2026-11-02

### Neu
- **Eigene Gürtel pro Inventar-Gruppe:** Mit Multiverse-Inventories hat jede Gruppe eigene Tool-Slots und Gürtel, die beim Inventarwechsel mitwechseln. Bisher trugen Gürtel Werkzeuge an der Trennung der Gruppen vorbei. An/Aus und Sortier-Einstellungen gelten überall. Ohne Multiverse-Inventories ändert sich nichts.

## [1.0.1] - 2026-10-30

### Behoben & geändert
- **Kurze Chat- und Menütexte:** Chatnachrichten sind jetzt eine knappe Zeile; Einzelheiten erscheinen, wenn man mit der Maus darüberfährt, in der Konsole in Klammern dahinter. Auch die Hilfetexte im Menü (Hilfe, Sortieren, Gürtel) sind kürzer (`lang-version` 10 - eigene Änderungen an diesen Texten in `lang/de.yml` und `lang/en.yml` werden dabei ersetzt). Nur das Plugin, der Mod bleibt unverändert.

## [1.0.0] - 2026-10-29

### Behoben & geändert
- **Stabile Version 1.0.0:** Funktionen, Config und Permissions gelten ab jetzt als fest; Änderungen daran gibt es nur noch mit einer neuen Hauptversion.
- **Aufgeräumt:** ein nie benutzter Text (`switched`) ist aus den Sprachdateien entfernt, die Anleitung hat einen Überblick „Was kann MRtoolbar?“.

## [0.5.0] - 2026-10-28

> [!WARNING]
> **Neue Permission-Gruppen:** Das Paket `mrtoolbar.admin` gibt es nicht mehr. Stattdessen gibt es in allen MRtechnology-Plugins dieselben Gruppen `<plugin>.player` und `<plugin>.groups.support|staff|admin`. Admin-Gruppen umstellen, z.B. `/lp group admin permission unset mrtoolbar.admin` und `/lp group admin permission set mrtoolbar.groups.admin true`.

### Neu
- **Permission-Gruppen:** `mrtoolbar.groups.support` (Admin-Menü, Gürtel anderer ansehen), `mrtoolbar.groups.staff` (+ `/mrtoolbar debug`) und `mrtoolbar.groups.admin` (alles). Jede Gruppe enthält die darunter und `mrtoolbar.player`.
- **Knopf im MRadmin-Dashboard:** öffnet das Admin-Menü jetzt direkt über die Modul-API.

### Entfernt
- **Paket `mrtoolbar.admin`:** ersetzt durch `mrtoolbar.groups.admin`.

### Behoben & geändert
- **`/mrtoolbar version` nur noch mit Permission:** `mrtoolbar.version` (in `mrtoolbar.player`) - bisher durfte das jeder.

## [0.4.0] - 2026-10-27

### Neu
- **Alle Server auf die Sprache des Spielers:** Bestehende Configs mit fest eingestellter Sprache (`language: de`/`en`) werden auf `language: auto` umgestellt - jeder Spieler bekommt die Sprache seines Minecraft-Clients, wie bei MRsleep, MRchunks und MRadmin.
- **Server-Sprache (`server-language`):** Texte ohne einzelnen Empfänger kommen in dieser Sprache (Standard `en`).
- **Automatische Umstellung:** Bestehende Configs bekommen `language: auto`; eine bisher fest eingestellte Sprache wird zur `server-language`, damit z.B. die Discord-Logs eines deutschen Servers deutsch bleiben.

### Behoben & geändert
- **Rahmen in der Hotbar fehlte, Werkzeuge ließen sich nicht wechseln** (z.B. mit Inventory Essentials oder anderen Mods): Meldete der Mod sich an, bevor der Client seine Kanäle angekündigt hatte, konnte der Toolbar-Status vor der Antwort auf die Anmeldung ankommen - der Mod verwarf ihn, und weil der Status nur bei Änderungen kam, blieb es so, bis man die Toolbar aus- und wieder einschaltete. Jetzt kommen die Nachrichten immer in der richtigen Reihenfolge an, und der Status wird zusätzlich alle 10 Sekunden erneut geschickt. Behebt den Fehler auch mit älteren Mod-Versionen; das Protokoll bleibt gleich.
- **Update-Hinweis bei `/mrtoolbar version` doppelt:** Fand die Abfrage eine neue Version, kam zuerst der Admin-Hinweis „Für MRtoolbar gibt es ein Update“ und direkt danach „Neue Version verfügbar“. Jetzt kommt nur noch die Antwort auf den Befehl. Die Download-Links führen auf die Website (https://mrtechnology.marcelr.at/download/mrtoolbar/).
- **Sprache wurde bei Config-Updates zurückgesetzt:** Bei jedem Config-Update - zuletzt mit 0.3.0 - wurde `language` auf `auto` gestellt. Jetzt passiert das nur noch beim Update von sehr alten Testversionen (vor config-version 6). Wer `language` fest auf `de` oder `en` eingestellt hatte, sollte die Einstellung nach dem Update auf 0.3.0 prüfen.
- **Config und plugin.yml auf Englisch:** Alle Kommentare in der `config.yml` und die Beschreibungen der Befehle und Permissions in der `plugin.yml` sind jetzt englisch. Bestehende Configs bekommen die neuen Kommentare beim nächsten Start automatisch (config-version 11, Sicherung als `config.v<alt>.backup.yml`), alle Einstellungen bleiben erhalten.

## [0.3.0] - 2026-10-27

### Geändert – Lizenz, Doku, Website
- **MRtechnology Lizenz 1.0:** Eine einheitliche Lizenz für alle MRtechnology-Projekte. Ausdrücklich erlaubt ist jetzt auch die gewerbliche Nutzung, Weiterveröffentlichen und Verkaufen bleiben verboten, die Nutzung erfolgt auf eigenes Risiko.
- **README überarbeitet:** Schnellstart, Auf einen Blick, Häufige Fragen, Kurzlinks zur Website (Download, Anleitung, Fehler melden).
- **Links zur Website** [mrtechnology.marcelr.at](https://mrtechnology.marcelr.at): Update-Hinweise in Konsole und Spiel führen zur Download-Seite, `/mrtoolbar version` zeigt „Website | GitHub“ (statt des Modrinth-Links), `website` in der plugin.yml zeigt auf die Anleitung. Der Text `version-links` wird in den mitgelieferten Sprachdateien automatisch erneuert.
- **Mod-Download über die Website:** `mod.download-url` zeigt jetzt auf https://mrtechnology.marcelr.at/download/mrtoolbar/ (config-version 10). Wer noch den alten GitHub-Link stehen hat, bekommt den neuen automatisch, ein eigener Link bleibt erhalten. Der Plugin-Link im Hilfe-Buch von `/toolbar` führt zur Anleitung.

### Geändert
- **MRadmin über die Modul-API:** Das Plugin meldet sich als Modul bei MRadmin an (statt der alten
  `MRadminHook`-Klasse). Die Pause im Admin-Modus fragt MRadmin jetzt ebenfalls über die Modul-API. Braucht MRadmin mit Modul-API; ohne MRadmin ändert sich nichts.
- **Verbindung zu anderen MR-Plugins nur noch über MRadmin:** Die Pause in Duellen (`toolbar.pause-in-pvp`) und das
  Mitsichern der Gürtel durch MRpvp funktionieren jetzt nur mit MRpvp **und** MRadmin (vorher eigene
  Schnittstellen, `MRtoolbarApi` gibt es nicht mehr).

### Behoben
- Menüs mit Spielerköpfen fragten jede Sekunde erneut bei Mojang nach dem Skin (HTTP 429 im Log, besonders im
  Offline-Mode). Köpfe werden jetzt einmal nachgeschlagen und 10 Minuten zwischengespeichert.

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

