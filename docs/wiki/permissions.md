# Permissions

Wie die Gruppen in allen Plugins aufgebaut sind: [Rechte mit LuckPerms](https://mrtechnology.marcelr.at/wiki/allgemein/rechte/).

## Spieler
| Permission | Standard | Wofür |
|---|---|---|
| `mrtoolbar.player` | OP | **Alle Spieler-Rechte** (`use`, `mending`, `sort`, `sort.containers`, `version`) - am besten der Default-Gruppe geben |
| `mrtoolbar.use` | OP | Werkzeugleiste nutzen, `/toolbar` |
| `mrtoolbar.mending` | OP | Mending repariert auch die Tools im Gürtel |
| `mrtoolbar.sort` | OP | Inventar sortieren, eigene Ausnahmen festlegen |
| `mrtoolbar.sort.containers` | OP | Offene Kisten, Fässer, Shulker, Trichter … sortieren |
| `mrtoolbar.version` | OP | `/mrtoolbar version` |
| `mrtoolbar.slots.<n>` | – | Höchstens `<n>` Tool-Slots (sonst `toolbar.default-tool-slots`) |
| `mrtoolbar.belt.<n>` | – | `<n>` Plätze pro Gürtel, höchstens 8 (sonst `toolbar.default-belt-size`) |

## Team-Gruppen
Jede enthält die darunter (und `mrtoolbar.player`):

| Gruppe | Standard | Enthält |
|---|---|---|
| `mrtoolbar.groups.support` | OP | Admin-Menü, Gürtel anderer ansehen |
| `mrtoolbar.groups.staff` | OP | Support + `/mrtoolbar debug` |
| `mrtoolbar.groups.admin` | OP | Alles: Staff + Reload, Discord-Log, Update-Hinweis |

## Einzelne Team-Rechte
| Permission | Standard | Wofür |
|---|---|---|
| `mrtoolbar.admin.gui` · `view` | OP | Admin-Menü · Gürtel anderer ansehen (auch über MRadmin) |
| `mrtoolbar.admin.debug` · `reload` · `discord` | OP | `/mrtoolbar debug` · `/mrtoolbar reload` · `/mrtoolbar discord` |
| `mrtoolbar.admin.update` | OP | Update-Hinweis beim Betreten |

## Beispiele
```text
/lp group default permission set mrtoolbar.player true             # alle Spieler
/lp group vip permission set mrtoolbar.slots.3 true                 # VIPs: 3 Tool-Slots …
/lp group vip permission set mrtoolbar.belt.8 true                  # … mit je 8 Plätzen
/lp group default permission set mrtoolbar.sort.containers false    # Kisten nicht sortieren
```
