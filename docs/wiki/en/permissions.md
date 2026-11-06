# Permissions

How the groups are built in all plugins: [Permissions with LuckPerms](https://mrtechnology.marcelr.at/wiki/allgemein/rechte/).

## Groups
Every group contains the one below, the team groups also contain `mrtoolbar.player` - best give `mrtoolbar.player` to the default group:

<!-- auto:permission-groups -->

```text
/lp group default permission set mrtoolbar.player true
/lp group supporter permission set mrtoolbar.groups.support true
/lp group staff permission set mrtoolbar.groups.staff true
/lp group admin permission set mrtoolbar.groups.admin true
```

## Single permissions
"Included in" names the smallest group that brings the permission; "–" means: in no group, only given on its own.

<!-- auto:permissions -->

Plus two permissions with a number you choose yourself:

| Permission | What for |
|---|---|
| `mrtoolbar.slots.<n>` | At most `<n>` tool slots (otherwise `toolbar.default-tool-slots`) |
| `mrtoolbar.belt.<n>` | `<n>` places per belt, at most 8 (otherwise `toolbar.default-belt-size`) |

## Examples
```text
/lp group default permission set mrtoolbar.player true             # all players
/lp group vip permission set mrtoolbar.slots.3 true                 # VIPs: 3 tool slots …
/lp group vip permission set mrtoolbar.belt.8 true                  # … with 8 places each
/lp group default permission set mrtoolbar.sort.containers false    # do not sort chests
```
