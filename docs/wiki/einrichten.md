# Mod einrichten & Tasten

## Einrichten
1. **Fabric Loader** und **[Fabric API](https://modrinth.com/mod/fabric-api)** für Minecraft 26.3 installieren.
2. Den **[MRtoolbar-Mod herunterladen](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (Zeile „Fabric-Mod (Client)“) und in den Ordner `mods/` legen.
3. Auf den Server gehen und **`/toolbar`** öffnen: Einen Hotbar-Slot zum **Tool-Slot** machen.
4. Auf dem Tool-Slot **`B`** drücken: Der Gürtel öffnet sich wie eine Kiste - Tools hineinlegen.
5. **`Alt` halten + Mausrad** schaltet durch die Tools.

Wie viele Tool-Slots und wie viele Plätze pro Gürtel du hast, legt der Server fest (Standard 2 Tool-Slots mit je 6 Plätzen).

## Tasten
| Taste (Standard) | Funktion |
|---|---|
| `B` | Gürtel des Tool-Slots öffnen |
| `Alt` halten + Mausrad | Nächstes / vorheriges Tool |
| Mausrad-Klick im Inventar | **Sortieren** - über deinem Inventar das Inventar, über einer Kiste, einem Fass, Trichter … den Behälter |
| nicht belegt | Nächstes Tool, Vorheriges Tool, Hotbar-Design öffnen |

Alle Tasten änderst du unter **Optionen → Steuerung → MRtoolbar**.

## Befehle
| Befehl | Was er tut |
|---|---|
| `/toolbar` (`/tb`, `/tools`) | Menü: Tool-Slots, Gürtel, Hotbar-Design, Sortieren, an/aus |
| `/toolbar on` · `off` | Werkzeugleiste ein- oder ausschalten |
| `/toolbar takeback` | Alle Tools aus den Gürteln zurück ins Inventar (geht auch ohne Mod) |
| `/toolbar sort` | Sortier-Ausnahmen festlegen und jetzt sortieren (geht auch ohne Mod) |

Ob es ein Mod-Update gibt, zeigt `/toolbar` im Buch oben links.

## Gut zu wissen
- **Mending** repariert auch die Tools im Gürtel: zuerst den Gürtel, den du gerade in der Hand hast, dann die anderen von links nach rechts. Erst wenn alles heil ist, bekommst du die XP.
- **Beim Tod** fallen die Tools je nach Server wie normale Items oder bleiben im Gürtel.
- In manchen Welten oder Situationen ist die Werkzeugleiste pausiert (z.B. im Duell) - deine Tools bleiben sicher im Gürtel.

> [!TIP]
> `B` tut nichts? Prüf in Mod Menu, ob der Mod geladen ist, und in `/toolbar`, ob der Slot wirklich ein Tool-Slot ist.
