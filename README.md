# MRtoolbar

**Tool-Slots für die Hotbar.** Einzelne Hotbar-Slots werden zu Tool-Slots: Jeder hat einen eigenen Gürtel mit bis
zu 8 Tools, durch die man per Taste durchschaltet - die Hotbar bleibt frei für Blöcke, Essen und Co.

MRtoolbar besteht aus zwei Teilen, die beide nötig sind:

| Teil | Für | Download |
|---|---|---|
| **Plugin** `MRtoolbar-mc26.3-v….jar` | Paper-Server 26.3 | [Releases](https://github.com/therealmarci/mrtoolbar/releases) („MRtoolbar Plugin …“) |
| **Client-Mod** `MRtoolbar-mod-mc26.3-v….jar` | Fabric 26.3 (Fabric Loader + Fabric API) | [Releases](https://github.com/therealmarci/mrtoolbar/releases) („MRtoolbar Mod …“) |

Plugin und Mod haben eigene Versionen: Ein Plugin-Update heißt nicht, dass Spieler ihren Mod aktualisieren müssen.
Ob ein Mod-Update ansteht, zeigt `/toolbar` im Buch oben links.
Spieler ohne Mod spielen ganz normal auf dem Server, nur ohne Werkzeugleiste.

## Für Spieler

1. Fabric Loader und [Fabric API](https://modrinth.com/mod/fabric-api) für Minecraft 26.3 installieren,
   `MRtoolbar-mod-….jar` in den `mods`-Ordner legen.
2. Auf dem Server `/toolbar` öffnen und einen Hotbar-Slot zum **Tool-Slot** machen.
3. Auf dem Tool-Slot **B** drücken: Der Gürtel öffnet sich wie eine Kiste - Tools hineinlegen.
4. **Alt halten + Mausrad** schaltet die Tools durch.

| Taste (Standard) | Funktion |
|---|---|
| `B` | Gürtel öffnen |
| `Alt` halten + Mausrad | nächstes / vorheriges Tool |
| Mausrad-Klick (im Inventar) | sortieren: Maus über dem eigenen Inventar → Inventar, über Kiste, Fass, Trichter usw. → der Behälter. Im Spiel bleibt es „Block auswählen“ |
| nicht belegt | Nächstes Tool, Vorheriges Tool, Hotbar-Design öffnen |

Alle Tasten lassen sich unter **Optionen → Steuerung → MRtoolbar** ändern.

**Inventar sortieren:** Gleiche Items werden zusammengelegt und nach Kategorie, Name oder Menge geordnet. Welche Slots
dabei bleiben, wo sie sind, legst du in `/toolbar` → Trichter (oder `/toolbar sort`) fest: ein Menü wie dein Inventar,
jeder Slot ein Schalter (grün = wird sortiert, rot = gesperrt). Standardmäßig ist die Hotbar gesperrt. Tool-Slots,
Rüstung und Zweithand werden nie angefasst.

**Hotbar-Design** (Button in `/toolbar`, eigene Taste oder „Konfigurieren“ in Mod Menu): wie die Tool-Slots
markiert werden - Rahmen, Ecken, Unterstrich, Hintergrund oder eine Textur aus dem Resourcepack, dazu Farbe,
Deckkraft, Dicke und Abstand. Die Vorschau nutzt die Hotbar deines Resourcepacks; gespeichert wird pro PC in
`config/mrtoolbar.json`.

**Texturepack-Ersteller:** Der Stil „Textur“ zeichnet `assets/mrtoolbar/textures/gui/sprites/hud/tool_slot.png`
(20×20, Nine-Slice mit 3 px Rand, wird mit der gewählten Farbe eingefärbt - Weiß = Originalfarben).

## Für Server-Admins

1. `MRtoolbar-….jar` in `plugins/`, Server starten.
2. `plugins/MRtoolbar/config.yml` anpassen, `/mrtoolbar reload`.
3. In LuckPerms der Default-Gruppe `mrtoolbar.player` geben (standardmäßig nur für OPs).

### Befehle

| Befehl | Permission | Funktion |
|---|---|---|
| `/toolbar` (`/tb`, `/tools`) | `mrtoolbar.use` | Menü: Tool-Slots, Gürtel, Hotbar-Design, an/aus, Versionen |
| `/toolbar on` / `off` | `mrtoolbar.use` | Werkzeugleiste ein-/ausschalten |
| `/toolbar takeback` | `mrtoolbar.use` | alle Tools aus den Gürteln zurück ins Inventar (geht auch ohne Mod) |
| `/toolbar sort` | `mrtoolbar.sort` | Ausnahmen und Reihenfolge fürs Sortieren, „Jetzt sortieren“ (geht auch ohne Mod) |
| `/mrtoolbar version` | – | Version und Update-Status |
| `/mrtoolbar reload` | `mrtoolbar.admin.reload` | Config und Sprachdateien neu laden |
| `/mrtoolbar debug [spieler]` | `mrtoolbar.admin.debug` | Mod-Verbindung, Status und Tool-Slots eines Spielers |
| `/mrtoolbar gui` | `mrtoolbar.admin.gui` | alle Online-Spieler mit Mod-Version und Tool-Slots, Klick zeigt ihre Gürtel |
| `/mrtoolbar view <spieler>` | `mrtoolbar.admin.view` | Gürtel eines Spielers ansehen (nur lesen) |

### Permissions

| Permission | Standard | Bedeutung |
|---|---|---|
| `mrtoolbar.player` | OP | alle Spieler-Rechte (enthält `mrtoolbar.use` und `mrtoolbar.sort`) |
| `mrtoolbar.use` | OP | Werkzeugleiste nutzen |
| `mrtoolbar.sort` | OP | Inventar sortieren und eigene Ausnahmen festlegen |
| `mrtoolbar.sort.containers` | OP | offene Kisten, Fässer, Shulker, Trichter usw. sortieren |
| `mrtoolbar.slots.<n>` | – | höchstens `<n>` Tool-Slots (sonst `toolbar.default-tool-slots`) |
| `mrtoolbar.belt.<n>` | – | `<n>` Plätze pro Gürtel, max. 8 (sonst `toolbar.default-belt-size`) |
| `mrtoolbar.admin` | OP | alle Admin-Rechte |
| `mrtoolbar.admin.update` | OP | Update-Hinweis beim Betreten |
| `mrtoolbar.admin.view` | OP | Gürtel anderer Spieler ansehen (auch über MRadmin) |

**MRadmin:** Ist MRadmin installiert, erscheint MRtoolbar dort automatisch im Dashboard und im Spieler-Menü (Mod-Version,
Tool-Slots, Klick zeigt die Gürtel - nur ansehen, wie Inventar und Enderchest).

### Config (Auszug)

| Schlüssel | Standard | Bedeutung |
|---|---|---|
| `language` | `auto` | `auto` = Sprache, die der Spieler in Minecraft eingestellt hat (wie der Mod), oder fest `de` / `en` |
| `fallback-language` | `en` | für Spieler, deren Minecraft-Sprache keine Datei hat |
| `toolbar.default-tool-slots` | `2` | Tool-Slots pro Spieler |
| `toolbar.default-belt-size` | `6` | Plätze pro Gürtel (1-8) |
| `toolbar.allowed-items` | Werkzeuge, Waffen, … | was in einen Gürtel darf (Item-Namen, `#Tags`, `*`) |
| `toolbar.disabled-worlds` | `[]` | Welten ohne Werkzeugleiste |
| `toolbar.on-death` | `drop` | Gürtel-Inhalt beim Tod: `drop` oder `keep` |
| `toolbar.pause-in-admin-mode` | `true` | im MRadmin-Adminmodus pausieren |
| `sorting.enabled` | `true` | Inventar-Sortieren an/aus |
| `sorting.default-mode` | `type` | Reihenfolge, bis ein Spieler selbst wählt: `type`, `name`, `amount` |
| `sorting.default-locked` | `[hotbar]` | gesperrte Slots für Spieler ohne eigene Ausnahmen (`hotbar`, `hotbar-3`, `inventory`, `inventory-5`) |
| `sorting.cooldown-ticks` | `10` | Mindestabstand zwischen zwei Sortierungen |
| `sorting.containers` | Kiste, Fass, Shulker, Endertruhe, Trichter, Werfer, Spender | welche Behälter sortiert werden können (Plugin-Menüs nie) |
| `mod.minimum-version` | leer | ältere Mod-Versionen aussperren, z.B. `0.2.1` |
| `mod.download-url` | GitHub Releases | Link in den Hinweisen |
| `mod.handshake-seconds` | `5` | Wartezeit auf den Mod nach dem Betreten |

Alle Texte stehen in `plugins/MRtoolbar/lang/de.yml` und `en.yml` (MiniMessage) und können angepasst werden.
Plugin und Mod richten sich beide nach der Minecraft-Sprache des Spielers (Deutsch inkl. Österreich/Schweiz → Deutsch,
alles andere → Englisch) - so gibt es keine gemischten Sprachen. Eine eigene `fr.yml` bekommen französische Spieler
automatisch. Die Konsole ist immer Englisch.

## Wie Plugin und Mod zusammenarbeiten

Der **Server** macht alles, was mit Items zu tun hat (Gürtel speichern, Tools wechseln, Tool-Slots schützen,
Rechte, Tod, Adminmodus). Der **Mod** kümmert sich um Tasten und Darstellung und fragt beim Server nur an - er
kann keine Items erzeugen oder verschieben. Beim Betreten prüfen beide, ob ihre Versionen zusammenpassen; Details
und Regeln für Änderungen: [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md).

## Entwicklung

| Ordner | Inhalt | Bauen |
|---|---|---|
| `plugin/` | Paper-Plugin (Java 25, Gradle Kotlin DSL) - [CHANGELOG](plugin/CHANGELOG.md) | `cd plugin && ./gradlew build` |
| `mod/` | Fabric-Mod (Java 25, Fabric Loom) - [CHANGELOG](mod/CHANGELOG.md) | `cd mod && ./gradlew build` |
| `docs/` | [Kompatibilität](docs/COMPATIBILITY.md), [Releases](docs/RELEASES.md), [Plan](docs/PLAN.md) | |

## Lizenz

Nutzung und eigene Anpassungen erlaubt, Weiterveröffentlichung nicht - siehe [LICENSE](LICENSE).
