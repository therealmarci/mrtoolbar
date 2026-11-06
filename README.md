# MRtoolbar

**Tool-Slots für die Hotbar:** Ein Hotbar-Slot wird zum Werkzeuggürtel mit bis zu 8 Tools, durch die du per Taste schaltest. Spitzhacke, Axt, Schaufel und Schwert brauchen nur noch **einen** Platz - der Rest der Hotbar bleibt frei für Blöcke und Essen. Dazu: Inventar und Kisten mit einem Klick sortieren.

**[⬇ Download](https://mrtechnology.marcelr.at/download/mrtoolbar/)** · **[📖 Wiki](https://mrtechnology.marcelr.at/wiki/mrtoolbar/)** · **[🐞 Fehler melden](https://mrtechnology.marcelr.at/report/mrtoolbar/)**

MRtoolbar besteht aus **zwei Teilen** - beide gibt es auf der [Download-Seite](https://mrtechnology.marcelr.at/download/mrtoolbar/):

| Teil | Wer braucht ihn | Wohin |
|---|---|---|
| **Plugin** (`MRtoolbar-mc26.3-v….jar`) | der Server | Paper 26.3, Java 25 → Ordner `plugins/` |
| **Client-Mod** (`MRtoolbar-mod-mc26.3-v….jar`) | jeder Spieler, der die Werkzeugleiste nutzen will | Fabric 26.3 mit [Fabric API](https://modrinth.com/mod/fabric-api) → Ordner `mods/` |

| Auf einen Blick | |
|---|---|
| Server | Paper 26.3 (oder ein Fork wie Purpur), Java 25 |
| Client | Fabric Loader + Fabric API für Minecraft 26.3 |
| Benötigt | nichts - LuckPerms empfohlen, MRadmin optional |
| Sprachen | Deutsch, Englisch - automatisch wie im Minecraft-Client |
| Status | stabil (1.x) - Plugin und Mod |

## Was kann MRtoolbar?
- **Tool-Slots:** bis zu 8 Werkzeuge in einem Hotbar-Slot, durchschalten mit `Alt` + Mausrad.
- **Mending im Gürtel**, **Inventar und Kisten sortieren** per Mausrad-Klick.
- **Hotbar-Design** für deine Tool-Slots.
- **Sicher:** Alle Items liegen auf dem Server, Spieler ohne Mod spielen ganz normal.
- **Für Admins:** Slots und Gürtelgröße per Permission, Übersicht über alle Gürtel - mit MRadmin auch im Browser.

## Schnellstart
**Server:** Plugin nach `plugins/`, starten, Rechte geben:
```text
/lp group default permission set mrtoolbar.player true
/lp group supporter permission set mrtoolbar.groups.support true
/lp group staff permission set mrtoolbar.groups.staff true
/lp group admin permission set mrtoolbar.groups.admin true
```

**Spieler:** Fabric Loader + Fabric API installieren, den Mod nach `mods/` legen, auf dem Server `/toolbar` öffnen und einen Tool-Slot einrichten, mit `B` den Gürtel füllen.

**Update:** alte JAR durch die neue ersetzen, Server neu starten - die Config passt sich selbst an. Plugin und Mod haben eigene Versionen.

## Ganze Anleitung im Wiki
| Für Spieler | Für Admins |
|---|---|
| [Mod einrichten & Tasten](https://mrtechnology.marcelr.at/wiki/mrtoolbar/einrichten/) · [Inventar sortieren](https://mrtechnology.marcelr.at/wiki/mrtoolbar/sortieren/) · [Hotbar-Design](https://mrtechnology.marcelr.at/wiki/mrtoolbar/design/) | [Befehle](https://mrtechnology.marcelr.at/wiki/mrtoolbar/befehle/) · [Permissions](https://mrtechnology.marcelr.at/wiki/mrtoolbar/permissions/) · [Konfiguration](https://mrtechnology.marcelr.at/wiki/mrtoolbar/konfiguration/) · [Plugin und Mod](https://mrtechnology.marcelr.at/wiki/mrtoolbar/plugin-und-mod/) · [Häufige Fragen](https://mrtechnology.marcelr.at/wiki/mrtoolbar/faq/) |

Die Seiten liegen als Markdown in [`docs/wiki/`](docs/wiki/) (Seitenbaum: [`docs/wiki/index.yml`](docs/wiki/index.yml)).

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
| `docs/` | [Wiki-Seiten](docs/wiki/), [Kompatibilität](docs/COMPATIBILITY.md), [Releases](docs/RELEASES.md), [Plan](docs/PLAN.md) | |
