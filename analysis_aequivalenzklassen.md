# Äquivalenzklassen- und Grenzwertanalyse für Shop#accept

## Regeln laut Aufgabenstellung

Ein Auftrag wird **nur angenommen**, wenn:
- Es sich **nicht** um ein E-Bike handelt
- Es sich **nicht** um ein Gravel-Bike handelt
- Der Kunde **nicht** bereits einen offenen Auftrag hat
- Insgesamt **maximal 5 offene Aufträge** im System sind

---

## Äquivalenzklassen

| ID  | Bedingung                                             | Gültig/Ungültig | Beschreibung                         |
|-----|--------------------------------------------------------|-----------------|--------------------------------------|
| EK1 | Bike ist **nicht** E-Bike                              | Gültig          | Zulässiger Fahrradtyp                |
| EK2 | Bike ist E-Bike                                        | Ungültig        | Verstoß gegen Regel                  |
| EK3 | Bike ist **nicht** Gravel-Bike                         | Gültig          | Zulässiger Fahrradtyp                |
| EK4 | Bike ist Gravel-Bike                                   | Ungültig        | Verstoß gegen Regel                  |
| EK5 | Kunde hat **keinen** offenen Auftrag                   | Gültig          | Auftrag erlaubt                      |
| EK6 | Kunde hat **bereits** einen offenen Auftrag            | Ungültig        | Verstoß gegen Regel                  |
| EK7 | Es sind **max. 4 andere** Aufträge im System vorhanden | Gültig          | Auftrag erlaubt                      |
| EK8 | Es sind **mehr als 5** Aufträge im System              | Ungültig        | Verstoß gegen Systemgrenze          |

---

## Grenzwertanalyse

| ID  | Bedingung                      | Testwert(e)       | Erwartung       |
|-----|--------------------------------|-------------------|------------------|
| GW1 | Anzahl Aufträge im System      | 4 → 5             | Gültig           |
| GW2 | Anzahl Aufträge im System      | 5 → 6             | Ungültig         |
| GW3 | Offene Aufträge pro Kunde      | 0 → 1             | Gültig           |
| GW4 | Offene Aufträge pro Kunde      | 1 → 2             | Ungültig         |

---

## Konkrete Testfälle

| Test-ID | Bike-Typ       | Kunde hat offenen Auftrag | Offene Gesamtaufträge | Erwartung | Begründung                       |
|---------|----------------|---------------------------|------------------------|-----------|----------------------------------|
| T1      | Standard-Bike  | Nein                      | 0                      | `true`    | Alle Bedingungen erfüllt         |
| T2      | E-Bike         | Nein                      | 0                      | `false`   | Verstoß gegen EK2                |
| T3      | Gravel-Bike    | Nein                      | 0                      | `false`   | Verstoß gegen EK4                |
| T4      | Standard-Bike  | Ja                        | 0                      | `false`   | Verstoß gegen EK6                |
| T5      | Standard-Bike  | Nein                      | 5                      | `true`    | GW1 erlaubt noch genau 5         |
| T6      | Standard-Bike  | Nein                      | 6                      | `false`   | GW2: Grenze überschritten        |
| T7      | Gravel-Bike    | Ja                        | 6                      | `false`   | Alle Bedingungen verletzt        |

