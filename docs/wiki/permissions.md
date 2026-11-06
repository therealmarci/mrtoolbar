# Permissions

Wie die Gruppen in allen Plugins aufgebaut sind: [Rechte mit LuckPerms](https://mrtechnology.marcelr.at/wiki/allgemein/rechte/).

## Gruppen
Jede Gruppe enthält die darunter, die Team-Gruppen auch `mrtoolbar.player` - am besten `mrtoolbar.player` der Default-Gruppe geben:

<!-- auto:permission-groups -->

```text
/lp group default permission set mrtoolbar.player true
/lp group supporter permission set mrtoolbar.groups.support true
/lp group staff permission set mrtoolbar.groups.staff true
/lp group admin permission set mrtoolbar.groups.admin true
```

## Einzeln
„Enthalten in“ nennt die kleinste Gruppe, die die Permission mitbringt; „–“ heißt: in keiner Gruppe, nur einzeln vergeben.

<!-- auto:permissions -->

Dazu zwei Permissions mit Zahl, die du selbst wählst:

| Permission | Wofür |
|---|---|
| `mrtoolbar.slots.<n>` | Höchstens `<n>` Tool-Slots (sonst `toolbar.default-tool-slots`) |
| `mrtoolbar.belt.<n>` | `<n>` Plätze pro Gürtel, höchstens 8 (sonst `toolbar.default-belt-size`) |

## Beispiele
```text
/lp group default permission set mrtoolbar.player true             # alle Spieler
/lp group vip permission set mrtoolbar.slots.3 true                 # VIPs: 3 Tool-Slots …
/lp group vip permission set mrtoolbar.belt.8 true                  # … mit je 8 Plätzen
/lp group default permission set mrtoolbar.sort.containers false    # Kisten nicht sortieren
```
