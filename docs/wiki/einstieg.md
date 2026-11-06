# Überblick

**Tool-Slots für die Hotbar:** Ein Hotbar-Slot wird zum Werkzeuggürtel mit bis zu 8 Tools, durch die du per Taste schaltest. Spitzhacke, Axt, Schaufel und Schwert brauchen nur noch **einen** Platz - der Rest der Hotbar bleibt frei für Blöcke und Essen. Dazu: Inventar und Kisten mit einem Klick sortieren.

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
| Status | stabil (1.x) - Plugin und Mod |

## Was kann MRtoolbar?
- **Tool-Slots:** Ein Hotbar-Slot wird zum Gürtel mit bis zu 8 Werkzeugen. Du schaltest per Taste oder `Alt` + Mausrad durch. → [Mod einrichten & Tasten](einrichten.md)
- **Mending im Gürtel:** Werkzeuge mit Mending werden auch im Gürtel repariert - erst der Gürtel in der Hand, dann die anderen.
- **Inventar und Kisten sortieren** mit einem Mausrad-Klick. Welche Slots dabei bleiben, legst du selbst fest. → [Inventar sortieren](sortieren.md)
- **Hotbar-Design:** Rahmen, Ecken, Unterstrich, Hintergrund oder eine eigene Textur für deine Tool-Slots. → [Hotbar-Design](design.md)
- **Sicher:** Alle Items liegen auf dem Server, der Mod zeigt nur an. Spieler ohne Mod holen ihre Tools mit `/toolbar takeback` zurück. → [Plugin und Mod](plugin-und-mod.md)
- **Für Admins:** Anzahl der Slots und Gürtelgröße per Permission, Übersicht über alle Gürtel, alles in der `config.yml` einstellbar.

## Wo anfangen?
| Du bist … | Lies zuerst |
|---|---|
| **Spieler** | [Mod einrichten & Tasten](einrichten.md) |
| **Server-Betreiber** | [Schnellstart](schnellstart.md), dann [Permissions](permissions.md) und [Konfiguration](konfiguration.md) |
