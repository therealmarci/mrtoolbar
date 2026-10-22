# Kompatibilität Plugin ↔ Mod

MRtoolbar besteht aus zwei Teilen, die zusammenpassen müssen:

| Teil | Ordner | Läuft auf | Aufgabe |
|---|---|---|---|
| **Plugin** (Paper) | `plugin/` | Server | Alles mit Items: Gürtel speichern, Tools wechseln, Tool-Slots schützen, Rechte, Limits, Tod, Adminmodus, Menüs |
| **Mod** (Fabric) | `mod/` | Client | Tasten (Optionen → Steuerung → MRtoolbar), Markierung in der Hotbar, Hotbar-Design. Fragt nur an, verschiebt nie selbst Items |

Ohne passenden Mod ist die Werkzeugleiste für einen Spieler aus (Tool-Slots pausiert, Tools sicher im Gürtel,
`/toolbar takeback` holt sie zurück).

## Zwei Nummern: Version und Protokoll

- **Version** (`plugin-mc26.3-v0.2.0`, `mod-mc26.3-v0.1.0`): Release-Nummer, SemVer. Plugin und Mod haben **eigene**
  Versionen und eigene Releases ([RELEASES.md](RELEASES.md)) - ändert sich nur ein Teil, wird nur er veröffentlicht.
- **Protokoll** (ganze Zahl): Format der Nachrichten zwischen Mod und Plugin.
  - Mod: `Protocol.VERSION` (`mod/.../Protocol.java`)
  - Plugin: `Compatibility.MIN_PROTOCOL` .. `MAX_PROTOCOL` (`plugin/.../Compatibility.java`)

Kompatibel ist, was ein gemeinsames Protokoll spricht - nicht, was dieselbe Version hat. Ein Mod 0.2.3 funktioniert
also mit Plugin 0.2.0, solange sich das Protokoll nicht geändert hat.

## Handshake

1. Spieler betritt den Server, der Mod schickt `mrtoolbar:hello` (Protokoll, Mod-Version, Minecraft-Version).
2. Das Plugin prüft (`Compatibility.check`):
   - Protokoll < `MIN_PROTOCOL` → **Mod zu alt**
   - Protokoll > `MAX_PROTOCOL` → **Mod zu neu** (Plugin auf dem Server ist veraltet)
   - Mod-Version < `mod.minimum-version` aus der config.yml → **Mod zu alt** (z.B. um einen Mod mit Fehler auszusperren)
3. Das Plugin antwortet mit `mrtoolbar:welcome` (Status, eigener Protokollbereich, Plugin-Version, Mindestversion,
   Download-Link) und schreibt bei „zu alt / zu neu“ eine Chat-Nachricht mit Link.
4. Nur bei **OK** ist die Werkzeugleiste aktiv. Kommt innerhalb von `mod.handshake-seconds` kein hello, gilt der
   Spieler als „ohne Mod“.

| Situation | Ergebnis |
|---|---|
| Kein Mod | Werkzeugleiste aus, Hinweis mit Download-Link, Tools bleiben sicher im Gürtel |
| Mod passt | Alles aktiv |
| Mod zu alt | Werkzeugleiste aus, Chat: „bitte Mod aktualisieren“ + Link |
| Mod ohne Handshake (Test-Version mit Protokoll 1) | Wird an den angemeldeten `mrtoolbar:`-Kanälen erkannt und wie „Mod zu alt“ behandelt |
| Mod zu neu (Plugin veraltet) | Werkzeugleiste aus, Chat: „Admin informieren / passende Mod-Version nutzen“ |
| Mod auf Server ohne MRtoolbar | Mod macht nichts (kein hello-Kanal) |
| Plugin antwortet mit unbekanntem Status (sehr neues Plugin) | Mod schaltet sich ab und meldet „passt nicht“ |

## Regeln für Änderungen

**Eingefroren - nie ändern, nur hinten Felder anhängen:**

- `hello`: VarInt Protokoll, String Mod-Version, String Minecraft-Version
- `welcome`: VarInt Status, VarInt min. Protokoll, VarInt max. Protokoll, String Plugin-Version, String Mindest-Modversion, String Download-Link
- Status-Codes: 0 = OK, 1 = Mod zu alt, 2 = Mod zu neu (neue Codes nur hinten anfügen)

Damit kann jede Mod-Version mit jeder Plugin-Version zumindest feststellen, ob sie zusammenpassen.

**Protokoll erhöhen**, sobald sich `state`, `action`, `design` ändern oder eine neue Nachricht dazukommt:

1. `Protocol.VERSION` im Mod +1
2. `Compatibility.MAX_PROTOCOL` im Plugin +1
3. Wenn das Plugin die alte Variante weiter bedienen kann: `MIN_PROTOCOL` lassen und im Plugin je nach
   Protokoll des Spielers (`ModBridge.client(player).protocol()`) unterschiedlich senden.
   Sonst `MIN_PROTOCOL` mit anheben - dann müssen alle Spieler den Mod aktualisieren.
4. Eintrag in der Tabelle unten.

**Kein Protokollwechsel nötig** bei reinen Mod-Änderungen (Design, Tasten, Darstellung) oder Plugin-Änderungen,
die keine Nachricht betreffen.

**Neue `action`-Codes** (hinten angehängt) brauchen ebenfalls keinen Protokollwechsel: Jedes Plugin ab 0.1.0
ignoriert Codes, die es nicht kennt (`ModBridge.action`). Ein neuer Mod auf einem älteren Server bleibt also
kompatibel, die neue Taste macht dort nur nichts. Bestehende Codes nie umnummerieren.

| Code | Aktion | Plugin ab | Mod ab |
|---|---|---|---|
| 0 | Gürtel öffnen | 0.1.0 | 0.1.0 |
| 1 | nächstes Tool | 0.1.0 | 0.1.0 |
| 2 | vorheriges Tool | 0.1.0 | 0.1.0 |
| 3 | Inventar sortieren | 0.2.0 | 0.2.0 |
| 4 | offenen Behälter sortieren (Kiste, Fass, Trichter ...) | 0.2.0 | 0.2.0 |

## Protokoll-Historie

| Protokoll | Plugin ab | Mod ab | Änderung |
|---|---|---|---|
| 1 | (nur Test) | (nur Test) | erster Testaufbau ohne Handshake - wird nicht mehr unterstützt |
| 2 | 0.1.0 | 0.1.0 | Handshake (hello/welcome), state (aktiv + Tool-Slots), action, design |

## Welche Versionen passen zusammen?

| Plugin | versteht Protokoll | passende Mods |
|---|---|---|
| 0.1.x | 2 | 0.1.x |

Neue Zeilen kommen dazu, sobald ein Teil ein neues Protokoll spricht. Solange sich das Protokoll nicht ändert,
passen alle neueren Versionen des einen Teils zu allen des anderen.
