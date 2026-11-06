# Konfiguration

Am einfachsten im Browser: Mit **MRadmin** unter **Plugins → MRtoolbar → Konfiguration** - jede Einstellung mit Erklärung, Speichern lädt MRtoolbar sofort neu (Permission `mradmin.config`). Alles steht außerdem kommentiert in `plugins/MRtoolbar/config.yml`; Änderungen von Hand übernimmst du mit `/mrtoolbar reload`.

| Einstellung | Standard | Bedeutung |
|---|---|---|
| `general.language` | `auto` | `auto` = wie im Minecraft-Client des Spielers (wie der Mod), sonst `de` oder `en` für alle |
| `general.fallback-language` | `en` | Für Spieler, deren Minecraft-Sprache keine eigene Datei hat |
| `general.server-language` | `en` | Texte ohne einzelnen Empfänger (z.B. Discord, Weboberfläche von MRadmin) |
| `toolbar.default-tool-slots` | `2` | Tool-Slots pro Spieler |
| `toolbar.default-belt-size` | `6` | Plätze pro Gürtel (1-8) |
| `toolbar.allowed-items` | Werkzeuge, Waffen … | Was in einen Gürtel darf (Item-Namen, `#Tags`, `*` für alles) |
| `toolbar.disabled-worlds` | `[]` | Welten ohne Werkzeugleiste |
| `toolbar.on-death` | `drop` | Gürtel-Inhalt beim Tod: `drop` oder `keep` |
| `toolbar.pause-in-admin-mode` | `true` | Pause im Admin-Modus von MRadmin (sonst könnten Kreativ-Items in den Gürtel geraten) |
| `toolbar.pause-in-pvp` | `true` | Pause in MRpvp-Duellen, im Arena-Editor und beim Zuschauen |
| `toolbar.mending` | `true` | Mending repariert auch die Tools im Gürtel (übrige XP: zuerst der Gürtel in der Hand, dann von links nach rechts) |
| `sorting.enabled` | `true` | Sortieren an/aus |
| `sorting.default-mode` | `type` | Reihenfolge, bis ein Spieler selbst eine wählt: `type` (wie Minecraft), `name`, `amount` |
| `sorting.default-locked` | `[hotbar]` | Gesperrte Slots für Spieler ohne eigene Ausnahmen (`hotbar`, `hotbar-<1-9>`, `inventory`, `inventory-<1-27>`) |
| `sorting.cooldown-ticks` | `10` | Mindestabstand zwischen zwei Sortierungen eines Spielers (20 = 1 Sekunde) |
| `sorting.containers` | Kiste, Fass, Shulker … | Welche Behälter sortiert werden können (Plugin-Menüs nie) |
| `mod.minimum-version` | leer | Ältere Mod-Versionen aussperren, z.B. `0.2.1` |
| `mod.download-url` | Download-Seite | Link zum Mod in den Hinweisen |
| `mod.handshake-seconds` | `5` | So lange wartet der Server nach dem Betreten auf den Mod, dann gilt der Spieler als „ohne Mod“ |
| `discord.*` | aus | Discord-Log - siehe [Discord-Log](discord.md) |
| `gui.refresh-ticks` · `gui.sounds` | `20` · `true` | Wie oft offene Menüs aktualisiert werden (Ticks) · Klick-Töne in Menüs |
| `updates.*` | an, `github`, alle 6 h | Hinweis auf neue Versionen (Quelle, Vorabversionen, Prüfintervall) |

## Texte
Alle Texte stehen in `plugins/MRtoolbar/lang/de.yml` und `en.yml` ([MiniMessage](https://docs.advntr.dev/minimessage/format.html)). Plugin und Mod richten sich nach der Minecraft-Sprache des Spielers - so gibt es keine gemischten Sprachen. Eine eigene `fr.yml` bekommen französische Spieler automatisch. Mehr unter [Sprachen & Texte](https://mrtechnology.marcelr.at/wiki/allgemein/sprachen/).
