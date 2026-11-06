# Inventar sortieren

Mit einem **Mausrad-Klick** in einem offenen Inventar wird sortiert: über deinem Inventar dein Inventar, über einer Kiste, einem Fass, einer Shulkerkiste, einem Trichter … der Behälter. Gleiche Items werden zusammengelegt und nach Kategorie, Name oder Menge geordnet.

## Was bleibt, wo es ist
In **`/toolbar` → Trichter** (oder `/toolbar sort`) legst du fest, welche Slots bleiben, wo sie sind: Ein Menü wie dein Inventar, jeder Slot ein Schalter - grün wird sortiert, rot bleibt. Standardmäßig bleibt die Hotbar, wie sie ist.

Nie angefasst werden: Tool-Slots, Rüstung, Zweithand und das Item am Mauszeiger.

## Ohne Mod
Das Menü unter `/toolbar sort` hat einen Knopf **Jetzt sortieren** - der funktioniert auch ohne Mod.

> [!NOTE]
> **Fürs Team:** Sortieren braucht `mrtoolbar.sort`, Behälter zusätzlich `mrtoolbar.sort.containers` (beides in `mrtoolbar.player`). Welche Behälter, welche Sortierung und welche Slots standardmäßig gesperrt sind, steht unter `sorting` in der [Konfiguration](konfiguration.md). Plugin-Menüs werden nie sortiert, auch wenn sie wie eine Kiste aussehen.
