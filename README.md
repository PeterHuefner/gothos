# Gothos
Gothos ist ein Programm zur Berechnung von Wettkampfergebnissen. Die Wertungen von Teilnehmern an verschiedenen Stationen werden zu einer Punktzahl zusammengerechnet und daraus folgend eine Platzierung gebildet.

Gothos wurde speziell für das Turnen entwickelt, kann aber auch für andere Sportarten und Einsatzgebiete verwendet werden.

## Funktionsumfang
* Wettkämpfe mit Teilnehmern und Mannschaften
* frei definierbare Altersklassen (mit jeweils eigenem Algorithmus zur Berechnung der Endwertung)
* frei definierbare Geräte (Stationen zu denen Wertungen erfasst werden)
* Erstellung von Protokollen für Altersklasse und Mannschaft
* Erstellung von Urkunden für Alterklasse und Mannschaft
* Erstellung von Riegenlisten für defnierte Geräte zum Eintragen von Wertungen durch Kampfgerichte
* Import und Export der Teilnehmer

## Systemvoraussetzungen und Installation
Gothos ist eine Java-Anwendung und wird als JAR-Datei bereitgestellt. Auf allen Java unterstützenden Betriebssystemen kann das Programm verwendet werden. Auf Linux, macOS und Windows kann Java in der Regel ohne Probleme installiert werden oder ist bereits vorinstalliert.

Java Version 8 muss installiert sein.

Eine JAR-Datei kann ohne Softwareinstallation ausgeführt werden. Es ist nur ein Download erforderlich, dann kann die Datei gestartet werden.

Die aktuelle Version kann [hier](https://raw.githubusercontent.com/PeterHuefner/gothos/master/build/current_release/gothos.jar) heruntergeladen werden.

Unter macOS und Windows kann die JAR-Datei direkt geöffnet werden. Unter Linux kann es sein, dass das Programm nur durch einen Kommandozeilenaufruf gestartet werden kann.
```
java -jar PFAD_ZUR_DATEI.jar
```

## Anleitung

Die folgende Anleitung soll einen Überblick über die wesentlichen Hautpfunktionen und Schritte liefern, die für einen Wettkampf notwendig sind.

Die Screenshots zeigen die Anwendung in Version 0.9. In zukünftigen Versionen können Änderungen und zusätzliche Funktionen die einzelnen Schritte verändern.

### Wettkampfdatenbank erstellen und auswählen

Die Anwendung speichert alle Daten in einer SQLite-Datenbank ab. Nach dem Start muss eine Datenbank erstellt oder ausgewählt werden. Die Datenbank ist eine einzelne Datei mit der Endung "sqlite" oder "sqlite3".<br>
Wenn Sie einmal eine Datenbank erstellt haben, wird diese beim nächsten Start der Anwendung automatisch geladen, sofern die Datei noch existiert.

![Startfenster](http://www.peterhuefner.de/gothos_anleitung/01_startfenster_1.png)

Klicken Sie auf "Neue Wettkampfdatenbank erstellen" oder "Wettkampfdatenbank auswählen", je nachdem ob Sie schon eine zuvor erstellt haben oder nicht.<br>
Anschließend wird der Pfad zur Datenbank angezeigt und es stehen weitere Funktionen zur Verfügung, was daran zu erkennen ist, dass weieter Buttons aktiviert wurden.

![Startfenster aktiviert](http://www.peterhuefner.de/gothos_anleitung/01_startfenster_2.png)

Wenn Sie eine vorhandene Datenbank ausgewählt haben werden bereits die Wettkämpfe im Feld "angelegte Wettkämpfe" aufgelistet. Haben Sie die Datenbank soeben erstellt, ist das Feld leer und es müssen zunächst Wettkämpfe angelegt werden.

*Hinweis*

Die Themen Altersklassen und Urkunden werden erst später in der Anleitung beschrieben. Zu diesem Zeitpunkt soll aber schon erwähnt werden, dass Sie Alterklassen und das aussehen der Urkunden einmal für die gesamte Datenbank definieren können. Jeder anschließend erstellte Wettkampf erhält dann die von Ihnen festgelegten Einstellungen für Alterklassen und Urkunden. In jedem Wettkampf können die Alterklassen und Urkunden weiterhin angepasst werden.<br>
Bearbeiten Sie Alterklassen und Urkunden vom Startfenster aus, bearbeiten Sie also lediglich Vorlagen für zukünftig angelegte Wettkämpfe.

### Wettkampf erstellen

Nachdem eine Datenbank erstellt oder ausgewählt wurde, können sie den Button "Wettkampf erstellen" zum Anlegen neuer Wettkämpfe verwenden.<br>
Es erscheint ein Fentser in dem die Grund-Einstellungen zum Wettkampf festgelegt werden.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/02_wettkampf_anlegen_1.png)

Tragen Sie alle Werte nach Ihren Wünschen und Anforderungen ein. Die Bedeutung der Felder ist teils direkt erkennbar. Dennoch ein paar Worte zu jedem Feld.

* Wettkampfkenner: Ist ein internes Kürzel für das nur Buchstaben und Zahlen verwendet werden dürfen. Es ist nicht änderbar und darf nur einmal pro Wettkampfdatenbank verwendet werden. Es wird nicht auf Protokolle oder Urkunden gedruckt.
* Wettkampfname: Ist der ausführliche Name des Wettkampfs. Er wird auf Protokolle und Riegenlisten gedruckt und kann ggf. auch auf Urkunden erscheinen.
* Beschreibung: Wird derzeit für als Platzhalter auf Urkunden zur Verwendung gespeichert
* Datum: Ist der Tag des Wettkampfs. Kann als Platzhalter auf Urkunden gedruckt werden.
* Mannschaftsberechnung: Sie können zwischen 2 Modi wählen:
   * Summe aller Wertungen: Alle Wertungen eines Turners werden addiert und anschließend alle Endwertungen aller Turner einer Mannschaft als Mannschaftswertung berechnet. Die festgelegte Berechnungsmethode der Alterklasse der Turner wird nicht verwendet. Wenn sie für eine Altersklasse festgelegt haben, dass Pilz und Pauschenpferd als MIttelwert in die Wertung kommen, wird das bei der Mannschaftswertung ignoriert.
   * Summe aller Ergebnisse: Zu jedem Turner wird die Endwertung nach der Berechnungsmethode der Alterklasse des Turners gebildet. Bspw. wird der Mittelwert aus Pilz und Pauschenpferd beachtet, sofern in der Altersklasse definiert. Alle Endwertungen der Turner bilden die Mannschaftswertung.
* Anzahl max. Mannschaftsmitglieder: Sie können festlegen, dass nur die besten X Wertungen in die Mannschaftsberechnung einfließen. Sie können also 6 Wertungen eingeben, aber festlegen, dass nur die 4 besten auch in das Mannschaftsergebnis einfließen. Je nach Mannschaftsmodi gibt es Unterschiede:
   * Summe aller Wertungen: Es werden nur die besten X Wertungen pro Gerät einer Mannschaft einbezogen.
   * Summe aller Ergebnisse: Es werden die X besten Endwertungen nach Alterklasse für jeden Turner in die Mannschaftswertung einberechnet.