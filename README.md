# SMPTools

Ein All-in-One Plugin für deinen SMP Server. Status, Gruppenchat, Regeln, Sitzen und mehr – alles in einem Paket.

## Was kann das Plugin?

- **Status**: Zeig im Chat und Tab an, ob du gerade `LIVE` oder `REC`.
- **Gruppen**: Erstelle Teams und chatte privat mit `/g <nachricht>`.
- **Sitzen**: Setz dich überall hin mit `/sit` (oder `/sit` nochmal zum Aufstehen).
- **Chat & Tab**: Schöne Formatierung mit MiniMessage Support.
- **Regeln & MOTD**: Custom Join-Nachrichten und `/rules` Befehl.
- **Ping**: Sieh deinen Ping mit `/ping`.

## Commands

Hier sind alle Befehle im Überblick:

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/status <status>` | Setze deinen Status (z.B. live). | `smptools.status.<status>` |
| `/status clear` | Lösche deinen Status. | `smptools.use` |
| `/group join <name>` | Tritt einer Gruppe bei. | `smptools.use` |
| `/group leave` | Verlässt deine aktuelle Gruppe. | `smptools.use` |
| `/groupchat <msg>` | Schreibt in den Gruppenchat (Alias: `/g`, `/gc`). | `smptools.use` |
| `/sit` | Hinsetzen oder Aufstehen. | `smptools.use` |
| `/ping` | Zeigt deinen Ping. | `smptools.use` |
| `/rules` | Zeigt die Regeln. | `smptools.use` |
| `/group create <name> <display>` | Erstellt eine neue Gruppe. | `smptools.admin` |
| `/group delete <name>` | Löscht eine Gruppe. | `smptools.admin` |
| `/group add <player> <group>` | Fügt Spieler manuell hinzu. | `smptools.admin` |
| `/group remove <player>` | Entfernt Spieler manuell. | `smptools.admin` |

## Config & Style

Alles ist in der `config.yml` anpassbar. Du kannst Farben, Nachrichten und Formate ändern.
Wir nutzen [MiniMessage](https://docs.adventure.kyori.net/minimessage/format.html) für alle Texte – also volle Kontrolle über Farben und Gradients!

---

Build mit `./gradlew build`. Getestet auf Paper 1.21.
