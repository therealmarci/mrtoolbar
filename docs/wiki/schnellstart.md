# Schnellstart (Server)

1. **[Plugin herunterladen](https://mrtechnology.marcelr.at/download/mrtoolbar/)** (Zeile „Plugin (Server)“) und in den Ordner `plugins/` legen.
2. Den Server starten.
3. **Spielern die Rechte geben** - ohne sie dürfen nur OPs die Werkzeugleiste nutzen:
   ```text
   /lp group default permission set mrtoolbar.player true
   /lp group supporter permission set mrtoolbar.groups.support true
   /lp group staff permission set mrtoolbar.groups.staff true
   /lp group admin permission set mrtoolbar.groups.admin true
   ```
4. Deinen Spielern den Link zum Mod geben: **https://mrtechnology.marcelr.at/download/mrtoolbar/** - oder gleich die Seite [Mod einrichten & Tasten](einrichten.md). Im Spiel steht der Link auch in `/toolbar`, und wer mit Tool-Slots, aber ohne Mod beitritt, bekommt einen Hinweis mit Link.

## Update
Die alte JAR durch die neue ersetzen und den Server neu starten. Die Config wird automatisch angepasst. Plugin und Mod haben eigene Versionen - ein Plugin-Update zwingt deine Spieler nicht zu einem Mod-Update (siehe [Plugin und Mod](plugin-und-mod.md)).
