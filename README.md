# FancyClicker

Ein moderner Cookie-Clicker für Paper. Beliebig viele Blöcke können als
Cookie-Clicker gebunden werden; ein Rechtsklick oder Linksklick (je nach
Spieler-Einstellung) farmt Cookies, die andere Maustaste öffnet die GUI
zum Leveln und Auszahlen.

## Bauen

Voraussetzungen: Java 25, Maven, Internetzugang (für Paper-API, Vault-API,
PlaceholderAPI, bStats).

```
mvn clean package
```


Die fertige JAR liegt danach unter `target/FancyClicker-1.0.0.jar`.

**Wichtig:** In `pom.xml` steht die Paper-API-Version als Bereich
`[26.1.2.build,)`. Falls diese Version nicht verfügbar ist, kannst du dort
eine exakte Version eintragen, z. B. `26.1.2.build.74-stable`. Verfügbare
Versionen:
https://repo.papermc.io/#browse/browse:maven-releases:io%2Fpapermc%2Fpaper%2Fpaper-api

## Abhängigkeiten (Soft-Dependencies)

- **Vault** – wird für das "Cash-Out" (Cookies gegen Geld) benötigt. Ohne
  Vault funktioniert der Rest des Plugins normal, nur Cash-Out ist deaktiviert.
- **PlaceholderAPI** – wird für die Placeholder benötigt. Ohne PAPI wird die
  Expansion einfach nicht registriert.

## Befehle

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/fc bind` | Bindet den Block, auf den du schaust, als Cookie-Block | `fancyclicker.admin` |
| `/fc unbind` | Entfernt die Bindung des Blocks, auf den du schaust. Schaut der Spieler auf keinen gebundenen Block, werden **alle** Bindungen entfernt. | `fancyclicker.admin` |
| `/fc reload` | Lädt `config.yml` und `gui.yml` neu | `fancyclicker.admin` |
| `/fc reset <player>` | Setzt die Daten eines Spielers zurück | `fancyclicker.admin` |
| `/fc setcookie <player> <amount>` | Setzt die Cookie-Anzahl | `fancyclicker.admin` |
| `/fc addcookie <player> <amount>` | Fügt Cookies hinzu | `fancyclicker.admin` |
| `/fc setlevel <player> <level>` | Setzt das Level (= Cookies pro Klick) | `fancyclicker.admin` |

Spieler benötigen `fancyclicker.use` (Standard: jeder), um einen gebundenen
Block zu farmen oder die GUI zu öffnen.

## Placeholder (PlaceholderAPI)

Alle Placeholder funktionieren **pro Spieler** und werden über den
Identifier `fancyclicker` angesprochen. Beispiel: `%fancyclicker_total%`.

| Placeholder | Beschreibung |
|---|---|
| `%fancyclicker_currently%` | Aktueller Cookie-Bestand |
| `%fancyclicker_total%` | Insgesamt jemals gesammelte Cookies |
| `%fancyclicker_level%` | Aktuelles Level |
| `%fancyclicker_perclick%` | Cookies pro Klick (= Level) |
| `%fancyclicker_money%` | Cash-Out-Wert in Geld (exakt, 2 Nachkommastellen) |
| `%fancyclicker_money_rounded%` | Cash-Out-Wert gerundet |
| `%fancyclicker_nextlevel%` | Nächstes Level |
| `%fancyclicker_nextcost%` | Preis für das nächste Level |
| `%fancyclicker_preference%` | Aktueller Farm-Modus (`LEFT` oder `RIGHT`) |
| `%fancyclicker_preference_friendly%` | Farm-Modus lesbar (`Linksklick` oder `Rechtsklick`) |

**Beispiele:**

- `&7Deine Cookies: &f%fancyclicker_currently%`
- `&7Pro Klick: &f%fancyclicker_perclick%`
- `&7Level: &f%fancyclicker_level%`
- `&7Cash-Out: &f%fancyclicker_money%`
- `&7Nächstes Level kostet: &f%fancyclicker_nextcost%`

## GUI

Die GUI wird über einen **Rechtsklick** oder **Linksklick** auf einen
gebundenen Block geöffnet (je nach Spieler-Einstellung). Die Items und ihre
Beschreibungen sind in `gui.yml` anpassbar.

| Slot | Item | Aktion |
|---|---|---|
| 11 | Paper | Linksklick: Cash-Out · Rechtsklick: Info |
| 13 | Comparator | Klick: Farm-Modus wechseln (Links-/Rechtsklick) |
| 15 | Chest | Klick: Level-Up kaufen |

**Farben in `gui.yml`:** `&f` (weiß), `&7` (grau), `&8` (dunkelgrau), `#25FF95` (Hex).
Fette Schrift (`&l`) wird nicht verwendet.

## Ablauf

1. Admin stellt sich vor einen beliebigen Block und führt `/fc bind` aus.
   Das kann beliebig oft wiederholt werden – **jeder Block** wird zu einem
   Cookie-Clicker.
2. Spieler klickt den Block mit der eingestellten Maustaste → Cookies farmen.
   Die andere Maustaste öffnet die GUI.
3. In der GUI kann per Comparator zwischen Links-/Rechtsklick-Farm-Modus
   gewechselt werden. Die Einstellung wird **pro Spieler** gespeichert.
4. Im Chest-Slot klickt der Spieler, um ein Level zu kaufen (kostet Cookies,
   erhöht Cookies/Klick).
5. Im Paper-Slot (Linksklick) löst der Spieler Cash-Out aus (Vault,
   Kurs einstellbar in `config.yml` unter `economy.cookies-per-money`).

Alle Texte, Sounds, GUI-Größe und Preise sind in `config.yml` und
`gui.yml` anpassbar.

## bStats

FancyClicker nutzt bStats (ID **34103**), um anonyme Statistiken zu sammeln.
Das kann in `plugins/bStats/config.yml` oder per JVM-Argument
`-Dbstats.disabled=true` deaktiviert werden.