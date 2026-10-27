# MRtoolbar

**Tool-Slots für die Hotbar:** Ein Hotbar-Slot wird zum Werkzeuggürtel mit bis zu 8 Tools, durch die du per Taste schaltest. Spitzhacke, Axt, Schaufel und Schwert brauchen nur noch **einen** Platz - der Rest der Hotbar bleibt frei für Blöcke und Essen. Dazu: Inventar und Kisten mit einem Klick sortieren.

**[⬇ Download](https://mrtechnology.marcelr.at/download/mrtoolbar/)** · **[📖 Anleitung online](https://mrtechnology.marcelr.at/docs/mrtoolbar/)** · **[🐞 Fehler melden](https://mrtechnology.marcelr.at/report/mrtoolbar/)**

MRtoolbar besteht aus **zwei Teilen** - beide gibt es auf der [Download-Seite](https://mrtechnology.marcelr.at/download/mrtoolbar/):

| Teil | Wer braucht ihn | Wohin |
|---|---|---|
| **Plugin** (`MRtoolbar-mc26.3-v….jar`) | der Server | Paper 26.3, Java 25 → Ordner `plugins/` |
| **Client-Mod** (`MRtoolbar-mod-mc26.3-v….jar`) | jeder Spieler, der die Werkzeugleiste nutzen will | Fabric 26.3 mit [Fabric API](https://modrinth.com/mod/fabric-api) → Ordner `mods/` |

Spieler **ohne** Mod spielen ganz normal auf dem Server, nur ohne Werkzeugleiste. Plugin und Mod haben eigene Versionen: Nicht jedes Plugin-Update braucht ein Mod-Update.

| Auf einen Blick | |
|---|---|
| Server | Paper 26.3 (oder ein Fork wie Purpur), Java 25 |
| Client | Fabric Loader + Fabric API für Minecraft 26.3 |
| Benötigt | nichts - LuckPerms empfohlen, MRadmin optional |
| Sprachen | Deutsch, Englisch - automatisch wie im Minecraft-Client |
| Status | Vorabversion (0.x) - Bedienung und Config können sich bis 1.0.0 noch ändern |

---

## Für Spieler

### Einrichten
1. **Fabric Loader** und **[Fabric API](https://modrinth.com/mod/fabric-api)** für Minecraft 26.3 installieren.
2. Den **[MRtoolbar-Mod herunterladen](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (Zeile „Fabric-Mod (Client)“) und in den Ordner `mods/` legen.
3. Auf den Server gehen und **`/toolbar`** öffnen: Einen Hotbar-Slot zum **Tool-Slot** machen.
4. Auf dem Tool-Slot **`B`** drücken: Der Gürtel öffnet sich wie eine Kiste - Tools hineinlegen.
5. **`Alt` halten + Mausrad** schaltet durch die Tools.

### Tasten
| Taste (Standard) | Funktion |
|---|---|
| `B` | Gürtel des Tool-Slots öffnen |
| `Alt` halten + Mausrad | Nächstes / vorheriges Tool |
| Mausrad-Klick im Inventar | **Sortieren** - über deinem Inventar das Inventar, über einer Kiste, einem Fass, Trichter … den Behälter |
| nicht belegt | Nächstes Tool, Vorheriges Tool, Hotbar-Design öffnen |

Alle Tasten änderst du unter **Optionen → Steuerung → MRtoolbar**.

### Inventar sortieren
Gleiche Items werden zusammengelegt und nach Kategorie, Name oder Menge geordnet. In **`/toolbar` → Trichter** (oder `/toolbar sort`) legst du fest, welche Slots bleiben, wo sie sind: Ein Menü wie dein Inventar, jeder Slot ein Schalter - grün wird sortiert, rot bleibt. Standardmäßig bleibt die Hotbar, wie sie ist. Tool-Slots, Rüstung und Zweithand werden nie angefasst.

### Hotbar-Design
Wie deine Tool-Slots in der Hotbar markiert werden: Rahmen, Ecken, Unterstrich, Hintergrund oder eine Textur aus deinem Resourcepack - dazu Farbe, Deckkraft, Dicke und Abstand. Öffnen über den Knopf in `/toolbar`, eine eigene Taste oder „Konfigurieren“ in Mod Menu. Gespeichert wird auf deinem PC in `config/mrtoolbar.json`.

<details>
<summary>Für Resourcepack-Ersteller</summary>

Der Stil „Textur“ zeichnet `assets/mrtoolbar/textures/gui/sprites/hud/tool_slot.png` (20×20, Nine-Slice mit 3 px Rand). Sie wird mit der gewählten Farbe eingefärbt - Weiß behält die Originalfarben.
</details>

### Befehle für Spieler
| Befehl | Was er tut |
|---|---|
| `/toolbar` (`/tb`, `/tools`) | Menü: Tool-Slots, Gürtel, Hotbar-Design, Sortieren, an/aus |
| `/toolbar on` · `off` | Werkzeugleiste ein- oder ausschalten |
| `/toolbar takeback` | Alle Tools aus den Gürteln zurück ins Inventar (geht auch ohne Mod) |
| `/toolbar sort` | Sortier-Ausnahmen festlegen und jetzt sortieren (geht auch ohne Mod) |

Ob es ein Mod-Update gibt, zeigt `/toolbar` im Buch oben links.

---

## Für Server-Admins

### Schnellstart
1. **[Plugin herunterladen](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (Zeile „Plugin (Server)“) und in den Ordner `plugins/` legen.
2. Den Server starten.
3. **Spielern die Rechte geben** - ohne sie dürfen nur OPs die Werkzeugleiste nutzen:
   ```text
   /lp group default permission set mrtoolbar.player true
   ```
4. Deinen Spielern den Link zum Mod geben: **https://mrtechnology.marcelr.at/download/mrtoolbar/**. Im Spiel steht der Link auch in `/toolbar`, und wer mit Tool-Slots, aber ohne Mod beitritt, bekommt einen Hinweis mit Link.

**Update:** Die alte JAR durch die neue ersetzen und den Server neu starten. Die Config wird automatisch angepasst.

### Befehle
| Befehl | Was er tut | Permission |
|---|---|---|
| `/mrtoolbar gui` | Alle Online-Spieler mit Mod-Version und Tool-Slots, Klick zeigt ihre Gürtel | `mrtoolbar.admin.gui` |
| `/mrtoolbar view <spieler>` | Gürtel eines Spielers ansehen (nur lesen) | `mrtoolbar.admin.view` |
| `/mrtoolbar debug [spieler]` | Mod-Verbindung, Status und Tool-Slots eines Spielers | `mrtoolbar.admin.debug` |
| `/mrtoolbar reload` | Config und Sprachdateien neu laden | `mrtoolbar.admin.reload` |
| `/mrtoolbar version` | Version und Update-Status | alle |

### Permissions
| Permission | Standard | Wofür |
|---|---|---|
| `mrtoolbar.player` | OP | **Alle Spieler-Rechte** (`use`, `sort`, `sort.containers`) - am besten der Default-Gruppe geben |
| `mrtoolbar.use` | OP | Werkzeugleiste nutzen, `/toolbar` |
| `mrtoolbar.sort` | OP | Inventar sortieren, eigene Ausnahmen festlegen |
| `mrtoolbar.sort.containers` | OP | Offene Kisten, Fässer, Shulker, Trichter … sortieren |
| `mrtoolbar.slots.<n>` | – | Höchstens `<n>` Tool-Slots (sonst `toolbar.default-tool-slots`) |
| `mrtoolbar.belt.<n>` | – | `<n>` Plätze pro Gürtel, höchstens 8 (sonst `toolbar.default-belt-size`) |
| `mrtoolbar.admin` | OP | Alle folgenden Admin-Rechte |
| `mrtoolbar.admin.gui` · `view` | OP | Admin-Menü · Gürtel anderer ansehen (auch über MRadmin) |
| `mrtoolbar.admin.debug` · `reload` | OP | `/mrtoolbar debug` · `/mrtoolbar reload` |
| `mrtoolbar.admin.update` | OP | Update-Hinweis beim Betreten |

<details>
<summary>LuckPerms-Beispiele</summary>

```text
/lp group default permission set mrtoolbar.player true      # alle Spieler
/lp group vip permission set mrtoolbar.slots.3 true          # VIPs: 3 Tool-Slots …
/lp group vip permission set mrtoolbar.belt.8 true           # … mit je 8 Plätzen
/lp group default permission set mrtoolbar.sort.containers false   # Kisten nicht sortieren
```
</details>

### Konfiguration
Alles steht kommentiert in `plugins/MRtoolbar/config.yml`. Änderungen übernimmst du mit `/mrtoolbar reload`.

| Einstellung | Standard | Bedeutung |
|---|---|---|
| `language` | `auto` | `auto` = wie im Minecraft-Client des Spielers (wie der Mod), sonst `de` oder `en` für alle |
| `fallback-language` | `en` | Für Spieler, deren Minecraft-Sprache keine eigene Datei hat |
| `server-language` | `en` | Texte ohne einzelnen Empfänger (z.B. in der Weboberfläche von MRadmin) |
| `toolbar.default-tool-slots` | `2` | Tool-Slots pro Spieler |
| `toolbar.default-belt-size` | `6` | Plätze pro Gürtel (1-8) |
| `toolbar.allowed-items` | Werkzeuge, Waffen … | Was in einen Gürtel darf (Item-Namen, `#Tags`, `*` für alles) |
| `toolbar.disabled-worlds` | `[]` | Welten ohne Werkzeugleiste |
| `toolbar.on-death` | `drop` | Gürtel-Inhalt beim Tod: `drop` oder `keep` |
| `sorting.enabled` | `true` | Sortieren an/aus |
| `sorting.default-locked` | `[hotbar]` | Gesperrte Slots für Spieler ohne eigene Ausnahmen |
| `sorting.containers` | Kiste, Fass, Shulker … | Welche Behälter sortiert werden können (Plugin-Menüs nie) |
| `mod.minimum-version` | leer | Ältere Mod-Versionen aussperren, z.B. `0.2.1` |
| `mod.download-url` | Download-Seite | Link zum Mod in den Hinweisen |

Alle Texte stehen in `plugins/MRtoolbar/lang/de.yml` und `en.yml` ([MiniMessage](https://docs.advntr.dev/minimessage/format.html)). Plugin und Mod richten sich nach der Minecraft-Sprache des Spielers - so gibt es keine gemischten Sprachen. Eine eigene `fr.yml` bekommen französische Spieler automatisch.

### Wie Plugin und Mod zusammenarbeiten
Der **Server** macht alles, was mit Items zu tun hat: Gürtel speichern, Tools wechseln, Tool-Slots schützen, Rechte, Tod. Der **Mod** kümmert sich nur um Tasten und Darstellung und fragt beim Server an - er kann keine Items erzeugen oder verschieben. Beim Betreten prüfen beide, ob ihre Versionen zusammenpassen. Details: [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md).

---

## Zusammenarbeit mit anderen Plugins
MRtoolbar läuft **allein**. Zusammen mit anderen MRtechnology-Plugins kann es mehr - die Plugins sprechen dafür über **MRadmin** miteinander:

| Funktion | Braucht |
|---|---|
| MRtoolbar im Dashboard und Spieler-Menü von MRadmin (Mod-Version, Tool-Slots, Gürtel ansehen) | MRadmin |
| Pause im Admin-Modus (`toolbar.pause-in-admin-mode`) | MRadmin |
| Pause in Duellen, im Arena-Editor und beim Zuschauen (`toolbar.pause-in-pvp`), Gürtel werden mitgesichert | MRadmin + MRpvp |

## Häufige Fragen
**Ich drücke `B`, aber nichts passiert.**
Prüf, ob der Mod geladen ist (Mod Menu) und ob der Slot ein Tool-Slot ist (`/toolbar`). Admins sehen mit `/mrtoolbar debug <spieler>`, ob die Verbindung zum Mod steht.

**Muss jeder Spieler den Mod installieren?**
Nein. Spieler ohne Mod spielen ganz normal, nur ohne Werkzeugleiste. `/toolbar takeback` und das Sortieren über `/toolbar sort` gehen auch ohne Mod.

**Was passiert mit den Tools beim Tod?**
Standardmäßig fallen sie wie normale Items (`toolbar.on-death: drop`). Mit `keep` bleiben sie im Gürtel.

**Ich habe einen Fehler gefunden oder einen Wunsch.**
Gern über das **[Formular auf der Website](https://mrtechnology.marcelr.at/report/mrtoolbar/)** - am besten mit der Ausgabe von `/mrtoolbar version` und deiner Mod-Version.

---

## Lizenz
MRtoolbar (Plugin und Mod) steht unter der **[MRtechnology Lizenz](LICENSE)** (source-available, nicht Open Source). Kurz gesagt:

| ✅ Erlaubt | ❌ Nicht erlaubt |
|---|---|
| Nutzen - privat und gewerblich, auch auf Servern mit Spenden, Rängen oder Shops | Weiterveröffentlichen (auch verändert), z.B. auf Modrinth, CurseForge, in Modpacks |
| Den Mod aus der offiziellen Quelle auf deinem PC nutzen, Spielern den Link geben | Verkaufen oder gegen Geld anbieten |
| Verändern und neu kompilieren, an dein Team weitergeben | Als eigenes Werk ausgeben, Lizenzhinweis entfernen |
| Verbesserungen per Pull Request einreichen | |

Die Nutzung erfolgt auf eigenes Risiko - leg vor Updates eine Sicherung an. Maßgeblich ist der vollständige Text in [LICENSE](LICENSE).

## Für Entwickler
| Ordner | Inhalt | Bauen |
|---|---|---|
| `plugin/` | Paper-Plugin (Java 25, Gradle) - [CHANGELOG](plugin/CHANGELOG.md) | `cd plugin && ./gradlew build` |
| `mod/` | Fabric-Mod (Java 25, Fabric Loom) - [CHANGELOG](mod/CHANGELOG.md) | `cd mod && ./gradlew build` |
| `docs/` | [Kompatibilität](docs/COMPATIBILITY.md), [Releases](docs/RELEASES.md), [Plan](docs/PLAN.md) | |
