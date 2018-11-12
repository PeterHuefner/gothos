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
Anschließend wird der Pfad zur Datenbank angezeigt und es stehen weitere Funktionen zur Verfügung, was daran zu erkennen ist, dass weitere Buttons aktiviert wurden.

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

Wenn alle Einstellungen festgelegt sind, klicken Sie auf "speichern", anschließend können Sie das Fenster mit "abbrechen/zurück" schließen. Das Startfenster zeigt nun den soeben angelegten Wettkampf im Feld "angelegte Wettkämpfe" an.

![Startfesnter mit Wettkampf](http://www.peterhuefner.de/gothos_anleitung/02_wettkampf_anlegen_2.png)

Angelegte Wettkämpfe können nachträglich bearbeitet werden. Klicken Sie dazu den Wettkampf in der Liste an und klicken Sie auf "Wettkampf bearbeiten".

### mit einem Wettkampf arbeiten

Um mit einem Wettkampf zu arbeiten, also Turnerinnen und Turner anzulegen, Wertungen einzutragen etc., muss ein Wettkampf vom Startfenster aus geladen werden.<br>
Wählen Sie einen Wettkampf in der Liste aus und klicken Sie auf "Wettkampf laden" oder klicken Sie doppelt auf den Eintrag in der Liste.<br>
Es erscheint anschließend ein neues Fenster und das Startfenster schließt sich.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/03_mainform_01.png)

Nehmen wir uns kurz Zeit und betrachten das Wettkampffenster.

Der gerade geladene Wettkampf hat noch keine spezifizierten Daten, also Teilnehmer, Riegen oder Alterklassen.<br>
Im oberen Bereich des Fensters wird der Kenner des geladenen Wettkampfs angezeigt. Mit dem Button "zurück" schließen Sie den geladenen Wettkampf.

Im Bereich darunter werden Altersklassen und Riegen in Auswahlfeldern mit ihren Operationen aufgeführt aufgeführt. Ebenso sind Operationen für Mannschaften dargestellt.<br>
Zu Alterklassen und Mannschaften gibt es die Operationen ”ansehen”, "Protokoll" und "Urkunden" verfügbar. Die Operationen "Protokoll" und "Urkunden" erstellen jeweils Protokoll oder Urkunden der jeweiligen Altersklasse oder aller Mannschaften als PDF oder öffnen ein Druckdialog.<br>
"Riegenlisten" erstellt zu angegebenen Geräten eine Liste mit allen Teilnehmern der Riege und stellt diese per PDF oder Druckdialog bereit. "ansehen/eintragen" gibt eine Übersicht über die Riege und ermöglicht das Eintragen der Wertungen.

Im mittleren Bereich befindet sich die Teilnehmertabelle. In ihr werden alle Wettkampfteilnehmer aufgelistet.<br>
Sie können Teilnehmer suchen, löschen und hinzufügen, sowie IDs (Startnummern) vergeben.

Der untere Bereich ermöglicht das Importieren von mehreren Teilnehmern aus einer CSV-Datei, Exportieren in eine CSV-Datei, sowie das Bearbeiten des Urkunden Layouts, der Alterklassen und Geräte.

#### Geräte verwalten

Die Anwendung erstellt die Olympischen Geräte für Frauen und Männer (sowie Pilz und Minitramp) beim Anlegen eines Wettkampfs automatisch.<br>
Sie können die automatisch erstellten Geräte löschen und Ihre eigenen Geräte hinzufügen.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/04_geraete_verwalten_01.png)

Wie die Hinweise im Fenster "Geräte verwalten" aufzeigen, sind die Geräte für jeden Wettkampf exklusiv und werden nicht automatisch auf andere Wettkämpfe übertragen.<br>
In der Alterklassenverwaltung können Sie den Namen eines Gerätes für die Anzeige im Protokoll einer Altersklasse individuell festlegen und dort auch Sonderzeichen verwenden.

#### Alterklassen verwalten

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/05_ak_verwalten_01.png)

Im Fenster "Altersklassen verwalten" können Sie die Eigenschaften einer Altersklasse festlegen.<br>
Welchen Name die Alterklasse auf dem Protokoll und den Urkunden hat, welche Geräte im Protokoll angezeigt werden sollen und wie das Ergebnis für die Alterklasse berechnet werden soll.

Die Eigenschaften einer Alterklasse die Sie hier definieren, wirken sich nur aus, wenn Sie Teilnehmer haben die auch dieser Alterklasse zugeordnet sind. Dabei ist darauf zu achten, dass die Schreibweise der Alterklasse in der Teilnehmertabelle und im Fenster "Altersklassen verwalten" identisch ist.<br>
Altersklassen die nicht im Fenster "Altersklassen verwalten" angelegt sind, aber bei Teilnehmern angelegt sind (also in der Teilnehmertabelle in der Spalte "Alterklasse" aufgeführt sind) werden automatisch verarbeitet.<br>
Das heißt, dass die Endwertung für Teilnehmer der Altersklasse die Summe aller Gerätewertungen ist und die angezeigten Geräte im Protokoll nach Geschlecht erfolgt.<br>
Das Geschlecht wird durch ein "m" oder "w" am Ende des Namens der Alterklasse ermittelt. Ist am Ende kein "w" oder "m", wird die Alterklasse als männliche Leistungsklasse eingestuft und die olympischen männlichen Geräte auf das Protokoll gedruckt.

Um von der automatischen Verarbeitung abzuweichen, können Sie im Fenster "Altersklassen verwalten" für alle in der Teilnehmertabelle verwendeteten Altersklassen angeben wie verfahren werden soll.

Ist ein "Anzeigename" für die Altersklasse angegeben, wird dieser Name auf das Protokoll und die Urkunde gedruckt. Fehler der Anzeigename wird der Kenner "Altersklasse" verwendet.

"Berechnung" ist ein mathematischer Ausdruck der das Endergbnis eines Teilnehmers der Alterklasse ausdrückt. Die angelegten Geräte können dabei wie Variablen verwendet werden.<br>
Achten Sie hier auf die Schreibweise der Geräte. Verwenden Sie hier andere Geräte als angelegt sind, kann unter Umständen kein Endergebnis ermittelt werden.<br>
Hier ein paar Beispiele für die "Berechnung" (welche keinen turnerischen Sinn haben müssen, nur die Möglichkeiten aufzeigen sollen):
* Boden + Sprung + Balken + Reck
* (Boden * 2) + Sprung
* (Boden / 2) - Pilz
* Boden + ((Pauschenpferd+Pilz) / 2) + Ringe + Sprung + Barren + Reck

*Hinweis*<br>
 Für jene die von Datenbanken und SQL Ahnung haben: "Berechnung" ist ein Ausdruck der als virtuelle Spalte einer SQL-Abfrage erzeugt wird. Die Geräte sind physische Spalten, weshalb mit ihnen gerechnet werden kann. Zusätzlich zu den mathematischen Operationen +-*/ können auch SQLite-Funktionen wie SQRT oder ABS verwendet werden. 

"Geräte" ist eine mit Komma separierte Liste der Geräte die auf dem Protokoll angezeigt werden sollen.
 

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/05_ak_verwalten_02.png)

#### Teilnehmer eintragen

Der Button "Teilnehmer hinzufügen" im Wettkampffenster fügt einen neuen Eintrag in der Teilnehmertabelle hinzu. In der neuen Zeile können dann alle Angaben zum Teilnehmer gemacht werden.
Sobald eine Zelle ausgefüllt wurde und man aus dieser herausklickt oder die nächste Zelle fokussiert, wird der eingetragene Wert gespeichert. 

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/06_turner_eintragen_01.png)

Teilnehmer können auch über den Button "Importieren" aus einer CSV-Datei importiert werden.

#### IDs vergeben

IDs oder auch Startnummern sind fortlaufende Nummern für jeden Teilnehmer eines Wettkampfs.<br>
Die ID kann, wie jede andere Spalte der Teilnehmertabelle auch, durch einfaches anklicken bearbeitet werden.<br>
Die IDs können so händisch vergeben werden.

Über den Button "IDs vergeben" können die Startnummern aber auch automatisiert jedem Teilnehmer vergeben werden.<br>
Im anschließenden Fenster können Sortierungskritierien vergeben werden, nach denen die IDs automatisch erstellt werden sollen.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/07_IDs_vergeben.png)


#### Riegenlisten

Jedem Teilnehmer kann eine Riege zugeordnet werden. Das geschieht einfach durch die Angabe eines Wertes in der Spalte "Riege" der Teilnehmertabelle.
Zu jedem einzigartig vergebenen Wert wird automatisch eine Riege erstellt, welche im Wettkampffenster im Auswahlfeld "Riegen" aufgelistet sind.

Zu jeder Riege kann eine Liste mit allen Teilnehmern zu gewünschten Geräten erstellt werden.

Der Button "Riegenlisten" öffnet ein neues Fenster zur Erstellung von Riegenlisten. Zuvor muss im Auswahlfeld "Riegen" die jeweilige Riege ausgewählt werden.
Das Fenster "Regenlisten" bietet eine Auswahl aller möglichen Geräte an und auch eine olympische Schnellauswahl für weiblich und männlich. Damit diese Schnellauswahl funktioniert, müssen die Geräte wie im Bild benannt sein.<br>
Die Riegenliste kann dann als PDF-Datei erstellt oder direkt gedruckt werden.

Auf jede Liste kann noch ein Feld gedruckt werden, welches es dem Kampfgericht ermöglicht die Berücksichtigung der Wertung für die Mannschaftswertung anzugeben.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/08_Riegenlisten_01.png)

Die erstellte Riegenliste kann wie folgt aussehen:

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/08_Riegenlisten_02.png)

#### Wertungen eintragen

Im Wettkampffentser muss zunächst die Riege ausgewählt sein zu der eine mit Wertungen ausgefüllte Riegenliste vorliegt.<br>
Mit dem Button "ansehen/eintragen" wird ein Fenster geöffnet in dem alle Teilnehmer der Riege in einer Tabelle dargestellt werden. Ebenfalls ist ein Auswahlfeld für alle möglichen Geräte vorhanden.

Im Auswahlfeld muss nun das Gerät ausgewählt werden zu dem die Wertungen eingetragen werden sollen.<br>
Nach der Auswahl wird der Tabelle eine Spalte für die Wertung des Geräts und eine Spalte "Wertung für Mannschaft" hinzugefügt.<br>
Die Wertung wird nun in der Spalte mit dem Namen des Gerätes eingetragen und die Checkbox der anderen Spalte kann zur Berücksichting dieser Wertung in der Mannschaftswertung verwendet werden.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/09_Wertungen_eintragen_01.png)
![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/09_Wertungen_eintragen_02.png)

Sind alle Wertungen eingetragen ist die zuletzt eingetragene Wertung mit ENTER zu bestätigen.

#### Protokoll einer Alterklasse erstellen

Sind zu einer Alterklasse alle Wertungen eingetragen kann das Protokoll erstellt werden.<br>
Dazu muss im Auswahlfeld "Alterklassen" im Wettkampffenster die gewünschte Alterklasse ausgewählt werden.

Über den Button "Protokoll" kann nun direkt das Protokoll als PDF erstellt oder direkt gedruckt werden.<br>
Der Button "ansehen" öffnet zunächst ein weiteres Fenster und zeigt die Teilnehmer der Alterklasse mit ihren Wertungen und der Platzierung an.

![Wettkampf erstellen](http://www.peterhuefner.de/gothos_anleitung/10_AK_ansehen.png)

Von diesem Fenster aus kann ebenfalls ein Protokoll als PDF erstellt oder direkt gedruckt werden.