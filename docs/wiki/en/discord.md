# Discord log

MRtoolbar reports to a Discord channel who plays with a mod that is too old, too new or missing, when the team looks at a belt and when someone reloads.

## Setup
1. In Discord: **Edit channel → Integrations → Webhooks → New webhook** and **copy the webhook URL**.
2. In the config set `discord.enabled: true` and enter the URL at `webhook-url`.
3. `/mrtoolbar reload`, then check with **`/mrtoolbar discord test`**.

## Events
Under `discord.events` you set per event whether it is sent and in which color:

| Event | When |
|---|---|
| `mod-too-old` · `mod-too-new` | A player joins with a mod version that is too old or too new |
| `mod-missing` | A player with tool slots joins without the mod - **off at first**, because it comes on every login without the mod |
| `belt-view` | The team looks at a belt |
| `admin-reload` | Config reloaded |

The texts are in the language file under `discord` (in `general.server-language`).

> [!TIP]
> **No own webhook?** With MRadmin it also works through its Discord log: `discord.enabled: true`, leave `webhook-url` empty and set `discord.use-mradmin-webhook: true`.
>
> With MRadmin every event is also in the **log** of the web interface (up to 14 days) - even when Discord is off.
