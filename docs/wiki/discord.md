# Discord-Log

MRtoolbar meldet in einen Discord-Kanal, wer mit zu alter, zu neuer oder ganz ohne Mod spielt, wenn das Team einen Gürtel ansieht und wenn jemand neu lädt.

## Einrichten
1. In Discord: **Kanal bearbeiten → Integrationen → Webhooks → Neuer Webhook** und die **Webhook-URL kopieren**.
2. In der Config `discord.enabled: true` setzen und die URL bei `webhook-url` eintragen.
3. `/mrtoolbar reload`, dann mit **`/mrtoolbar discord test`** prüfen.

## Ereignisse
Unter `discord.events` legst du pro Ereignis fest, ob es gesendet wird und in welcher Farbe:

| Ereignis | Wann |
|---|---|
| `mod-too-old` · `mod-too-new` | Spieler kommt mit zu alter bzw. zu neuer Mod-Version |
| `mod-missing` | Spieler mit Tool-Slots kommt ohne Mod - **anfangs aus**, weil es bei jedem Login ohne Mod kommt |
| `belt-view` | Das Team sieht einen Gürtel an |
| `admin-reload` | Config neu geladen |

Die Texte stehen in der Sprachdatei unter `discord` (in `general.server-language`).

> [!TIP]
> **Kein eigener Webhook?** Mit MRadmin geht es auch über dessen Discord-Log: `discord.enabled: true`, `webhook-url` leer lassen und `discord.use-mradmin-webhook: true` setzen.
>
> Mit MRadmin steht jedes Ereignis außerdem im **Protokoll** der Weboberfläche (bis zu 14 Tage) - auch wenn Discord aus ist.
