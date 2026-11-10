# Zusammenarbeit mit anderen Plugins

MRtoolbar läuft **allein**. Zusammen mit anderen MRtechnology-Plugins kann es mehr - die Plugins sprechen dafür über **MRadmin** miteinander ([wie das funktioniert](https://mrtechnology.marcelr.at/wiki/allgemein/zusammenarbeit/)).

| Funktion | Braucht |
|---|---|
| MRtoolbar im Dashboard und Spieler-Menü von MRadmin (Mod-Version, Tool-Slots, Gürtel ansehen) | MRadmin |
| MRtoolbar im Browser einstellen (Plugins → MRtoolbar → Konfiguration) | MRadmin |
| Pause im Admin-Modus (`toolbar.pause-in-admin-mode`) | MRadmin |
| Pause in Duellen, im Arena-Editor und beim Zuschauen (`toolbar.pause-in-pvp`), Gürtel werden mitgesichert | MRadmin + MRpvp |
| Ereignisse im **Protokoll** der MRadmin-Weboberfläche, Discord-Log über MRadmins Webhook (`discord.use-mradmin-webhook`) | MRadmin |
| Hinweise ans Team im Spiel (z.B. Update verfügbar) lassen sich unter Konto → Benachrichtigungen ein- und ausschalten und mit `/mradmin mute` stummschalten | MRadmin |

## Multiverse-Inventories
Jede Inventar-Gruppe hat eigene Tool-Slots und Gürtel. Wechselt Multiverse-Inventories das Inventar, wechseln die Gürtel mit, damit keine Werkzeuge zwischen getrennten Gruppen wandern. Bei deinem ersten Besuch in einer Gruppe gibt es dort noch keine Tool-Slots, du richtest sie wie gewohnt mit `/toolbar` ein. An/Aus und die Sortier-Einstellungen gelten überall.
