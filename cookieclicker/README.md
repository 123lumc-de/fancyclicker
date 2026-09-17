# CookieClicker Plugin

Ein Cookie-Clicker-Plugin für Paper. Ein Block wird als "Cookie-Block" gebunden;
Rechtsklick darauf öffnet eine GUI zum Cookie-Sammeln, Leveln und Auszahlen.

## Bauen

Voraussetzungen: Java 21, Maven, Internetzugang (für Paper-API, Vault-API, PlaceholderAPI-Dependency).

```
mvn clean package
```

Die fertige JAR liegt danach unter `target/CookieClicker-1.0.0.jar`.

**Wichtig:** In `pom.xml` steht als Platzhalter die Paper-API-Version
`1.26.1-R0.1-SNAPSHOT`. Falls diese Version noch nicht im PaperMC-Repository
verfügbar ist (z. B. weil 1.26.1 erst kürzlich erschienen ist), trage dort
die exakte Version ein, die unter
https://repo.papermc.io/#browse/browse:maven-releases:io%2Fpapermc%2Fpaper%2Fpaper-api
gelistet ist.

## Abhängigkeiten (Soft-Dependencies)

- **Vault** – wird für das "Cash-Out" (Cookies gegen Geld) benötigt. Ohne Vault
  funktioniert der Rest des Plugins normal, nur Cash-Out ist deaktiviert.
- **PlaceholderAPI** – wird für die Placeholder benötigt. Ohne PAPI wird
  die Expansion einfach nicht registriert.

Für CoinsEngine / PlayerPoints (aus deiner Übersicht) ist aktuell kein
fertiger Adapter enthalten, da deren APIs nicht öffentlich zugänglich
gebaut werden konnten. `EconomyManager` ist so aufgebaut, dass du dort
leicht einen weiteren Provider ergänzen kannst.

## Befehle

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/cc bind` | Bindet den Block, auf den du schaust, als Cookie-Block | `cookieclicker.admin` |
| `/cc unbind` | Entfernt die aktuelle Bindung | `cookieclicker.admin` |
| `/cc reload` | Lädt die config.yml neu | `cookieclicker.admin` |
| `/cc reset <player>` | Setzt die Daten eines Spielers zurück | `cookieclicker.admin` |
| `/cc setcookie <player> <amount>` | Setzt die Cookie-Anzahl | `cookieclicker.admin` |
| `/cc addcookie <player> <amount>` | Fügt Cookies hinzu | `cookieclicker.admin` |
| `/cc setlevel <player> <level>` | Setzt das Level (= Cookies pro Klick) | `cookieclicker.admin` |

Spieler benötigen `cookieclicker.use` (Standard: jeder), um den gebundenen
Block anzuklicken und die GUI zu benutzen.

## Placeholder (PlaceholderAPI)

- `%cookieclicker_total%` – insgesamt jemals gesammelte Cookies
- `%cookieclicker_currently%` – aktueller Cookie-Bestand
- `%cookieclicker_level%` – aktuelles Level

## Ablauf

1. Admin stellt sich vor einen beliebigen Block (z. B. einen Cookie-Block
   oder Kuchen) und führt `/cc bind` aus.
2. Spieler rechtsklicken diesen Block → die Cookie-Clicker-GUI öffnet sich.
3. Im Cookie-Slot klicken → Cookies sammeln (Menge abhängig vom Level).
4. Im Level-Slot klicken → Level kaufen (kostet Cookies, erhöht Cookies/Klick).
5. Im Cash-Out-Slot klicken → Cookies werden gegen Geld eingelöst (Vault,
   Kurs einstellbar in `config.yml` unter `economy.cookies-per-money`).

Alle Texte, Sounds, GUI-Größe und Preise sind in `config.yml` anpassbar.
