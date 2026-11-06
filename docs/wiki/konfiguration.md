# Konfiguration

Am einfachsten im Browser: Mit **MRadmin** unter **Plugins → MRtoolbar → Konfiguration** - jede Einstellung mit Erklärung, Speichern lädt MRtoolbar sofort neu (Permission `mradmin.config`). Alles steht außerdem kommentiert in `plugins/MRtoolbar/config.yml`; Änderungen von Hand übernimmst du mit `/mrtoolbar reload`.

| Abschnitt | Wofür | Mehr dazu |
|---|---|---|
| `general` | Sprache der Spieler (`auto` = wie der Minecraft-Client), Ersatz- und Server-Sprache | [Sprachen & Texte](https://mrtechnology.marcelr.at/wiki/allgemein/sprachen/) |
| `toolbar` | Tool-Slots und Gürtelgröße, erlaubte Items, Welten ohne Werkzeugleiste, Gürtel beim Tod, Pausen im Admin-Modus und in Duellen, Mending | [Einrichten & benutzen](einrichten.md) |
| `sorting` | Sortieren an/aus, Standard-Reihenfolge, gesperrte Slots, Behälter | [Sortieren](sortieren.md) |
| `mod` | Mindestversion des Mods, Download-Link, Wartezeit auf den Mod | [Plugin & Mod](plugin-und-mod.md) |
| `discord` | Discord-Log | [Discord-Log](discord.md) |
| `updates` · `gui` | Hinweis auf neue Versionen, Menüs | |

Jede einzelne Einstellung mit Standardwert steht unten unter [Alle Einstellungen](#alle-einstellungen).

## Texte
Alle Texte stehen in `plugins/MRtoolbar/lang/de.yml` und `en.yml` ([MiniMessage](https://docs.advntr.dev/minimessage/format.html)). Plugin und Mod richten sich nach der Minecraft-Sprache des Spielers - so gibt es keine gemischten Sprachen. Eine eigene `fr.yml` bekommen französische Spieler automatisch. Mehr unter [Sprachen & Texte](https://mrtechnology.marcelr.at/wiki/allgemein/sprachen/).

## Alle Einstellungen
Jede Einstellung der `config.yml` mit Standardwert - dieselben Felder wie im Browser-Formular von MRadmin. Die Liste entsteht automatisch aus dem Plugin und ist damit immer auf dem Stand dieser Version.

<!-- auto:config -->
