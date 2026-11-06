# Häufige Fragen

### Ich drücke `B`, aber nichts passiert.
Prüf, ob der Mod geladen ist (Mod Menu) und ob der Slot ein Tool-Slot ist (`/toolbar`). Admins sehen mit `/mrtoolbar debug <spieler>`, ob die Verbindung zum Mod steht.

### Muss jeder Spieler den Mod installieren?
Nein. Spieler ohne Mod spielen ganz normal, nur ohne Werkzeugleiste. `/toolbar takeback` und das Sortieren über `/toolbar sort` gehen auch ohne Mod.

### Was passiert mit den Tools beim Tod?
Standardmäßig fallen sie wie normale Items (`toolbar.on-death: drop`). Mit `keep` bleiben sie im Gürtel.

### Repariert Mending auch die Tools im Gürtel?
Ja. Was Minecraft nicht für das Item in der Hand und die Rüstung braucht, geht an die Gürtel: zuerst an den Gürtel des Tool-Slots, den du gerade in der Hand hast, dann an die anderen Gürtel von links nach rechts - in jedem Gürtel ein Tool nach dem anderen, in der Reihenfolge, in der sie im Gürtel liegen. Erst wenn alles heil ist, bekommst du die XP. Abschalten: `toolbar.mending: false` bzw. Permission `mrtoolbar.mending` entziehen.

### Muss ich nach einem Plugin-Update auch den Mod aktualisieren?
Meist nicht - entscheidend ist das gemeinsame Protokoll, nicht die Version. Passt es nicht, sagt der Server beim Betreten Bescheid (siehe [Plugin und Mod](plugin-und-mod.md)).

### Ich habe einen Fehler gefunden oder einen Wunsch.
Gern über das **[Formular auf der Website](https://mrtechnology.marcelr.at/report/mrtoolbar/)** - am besten mit der Ausgabe von `/mrtoolbar version` und deiner Mod-Version.
