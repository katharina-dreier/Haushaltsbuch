package org.meinprojekt.haushalt.core;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Datenstroeme {

	// Diese Klasse ist für die Datenströme verantwortlich
	// Hier werden die CSV-Dateien erstellt und verwaltet

//Hilfsmethoden für die Datenströme:
	public static String sep = File.separator;
	public static String headerBuchungen = "Datum;Buchungsart;Kategorie;Empfaenger;Sender;Betrag;Kontostand;Umbuchung;transferID";
	public static String headerKonten = "Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand";
	public static String headerKategorien = "Kategorie";

	// Diese Methode formatiert eine Buchung in CSV-Format
	public static String buchungToCSV(String date, String buchungsart, String kategorie, String empfaenger,
			String sender, double betrag, double kontostand, boolean isUmbuchung, String transferID) {
		return date + ";" + buchungsart + ";" + kategorie + ";" + empfaenger + ";" + sender + ";" + betrag + ";"
				+ kontostand + ";" +  isUmbuchung + ";" + transferID;
	}

	// Diese Methode stellt sicher, dass ein Verzeichnis vorhanden ist
	public static File ensureVerzeichnisVorhanden(String pfad) {
		File dir = new File(pfad);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("Verzeichnis wurde erstellt: " + dir.getAbsolutePath());
		}
		return dir;
	}

	// Diese Methode stellt sicher, dass eine Datei mit einem Header vorhanden ist
	public static File ensureDateiMitHeader(String dateipfad, String headerZeile) {
		File datei = new File(dateipfad);
		if (!datei.exists()) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(datei))) {
				bw.write(headerZeile);
				bw.newLine();
				System.out.println("Datei wurde erstellt: " + dateipfad);
			} catch (IOException e) {
				System.out.println("Fehler beim Erstellen der Datei: " + e.getMessage());
			}
		}
		return datei;
	}

	// Diese Methode fügt eine Zeile an eine bestehende Datei an
	public static void zeileInDateiAnhaengen(String dateipfad, String zeile) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(dateipfad, true))) {
			bw.write(zeile);
			bw.newLine();
		} catch (IOException e) {
			System.out.println("Fehler beim Schreiben in Datei: " + e.getMessage());
		}
	}

//Konten und Buchungsmethoden 
	// Diese Methode erstellt eine neue Datei für die Kontenübersicht
	public static String kontenUebersichtAnlegen() {
		String verzeichnis = sep + "Haushaltsbuch";
		File dir = ensureVerzeichnisVorhanden(verzeichnis);
		String dirName = dir.getAbsolutePath();
		String dateiName = dirName + sep + "Kontoliste" + ".csv";
		ensureDateiMitHeader(dateiName, headerKonten);
		return dateiName;
	}

	// Diese Methode erstellt eine neue Datei für die Kategorienübersicht
	public static String kategorieUebersichtAnlegen() {
		String verzeichnis = sep + "Haushaltsbuch";
		File dir = ensureVerzeichnisVorhanden(verzeichnis);
		String dirName = dir.getAbsolutePath();
		String dateiName = dirName + sep + "Kategorienliste" + ".csv";
		ensureDateiMitHeader(dateiName, headerKategorien);
		return dateiName;
	}

	// Diese Methode erstellt eine neue Datei für ein Konto
	public static void KontoDateiAnlegen(Konto konto) {

		String verzeichnis = sep + "Haushaltsbuch" + sep + "Konten";
		File newDir = ensureVerzeichnisVorhanden(verzeichnis);
		String newDirName = newDir.getAbsolutePath();
		String dateiName = newDirName + sep + konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_"
				+ konto.getKontoName() + ".csv";
		ensureDateiMitHeader(dateiName, headerBuchungen);
	}

	// Diese Methode fügt ein Konto zur Kontenübersicht hinzu
	public static void kontoHinzufuegen(Konto konto) {
		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
		ensureVerzeichnisVorhanden(ordnerpfad);
		String dateiname = konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName()
				+ ".csv";
		String kontopfad = ordnerpfad + sep + dateiname;
		// Header für Buchungsdatei
		ensureDateiMitHeader(kontopfad, headerBuchungen);
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		String ersteZeile = date + ";Erstellung;" + "" + ";" + "" + ";" + "" + ";" + konto.getKontostand() + ";"
				+ konto.getKontostand() + ";" + "false" + ";" + "" ; // "Datum;Buchungsart;Kategorie;Empfänger;Sender;Betrag;Kontostand;Umbuchung;transferID
		zeileInDateiAnhaengen(kontopfad, ersteZeile);
		// Pfad zur zentralen Kontenliste
		String kontenlistePfad = sep + "Haushaltsbuch" + sep + "Kontoliste.csv";
		String kontoZeile = konto.toCSV();
		zeileInDateiAnhaengen(kontenlistePfad, kontoZeile);
	}
	// Diese Methoden fügen eine Buchung (Einnahme, Ausgabe, Umbuchung) zur
	// entsprechenden Datei hinzu

	public static void buchungHinzufuegen(Buchung buchung) {

		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
			ensureVerzeichnisVorhanden(ordnerpfad);
			String dateiname = buchung.getKonto().getKontonummer() + "_" + buchung.getKonto().getKreditinstitut() + "_"
					+ buchung.getKonto().getKontoName() + ".csv";
			String kontopfad = ordnerpfad + sep + dateiname;
			ensureDateiMitHeader(kontopfad, headerBuchungen);
			String buchungsZeile = buchungToCSV(buchung.getFormatiertesDatum(), buchung.getBuchungsart(),
					buchung.getKategorie(), buchung.getEmpfaenger(), buchung.getSender(), buchung.getBetrag(),
					buchung.getKonto().getKontostand(),buchung.getIsUmbuchung(), buchung.getTransferID());
			zeileInDateiAnhaengen(kontopfad, buchungsZeile);
			kontenNeuSpeichern();
			kategorieZurDateiHinzufuegen(buchung.getKategorie());
		}
	


	// Diese Methode lädt die Konten aus der Datei in die zentrale Map
	public static void ladeKontenAusDatei() {
		String pfad = kontenUebersichtAnlegen(); // Pfad zur Kontenübersicht
		try (BufferedReader br = new BufferedReader(new FileReader(pfad))) {
			String zeile;
			boolean ersteZeile = true;

			while ((zeile = br.readLine()) != null) {
				if (ersteZeile) {
					ersteZeile = false; // Header überspringen
					continue;
				}
				// Header Konten: Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand
				String[] teile = zeile.split(";");
				int kontonummer = Integer.parseInt(teile[0]);
				String kreditInstitut = teile[1];
				String kontoName = teile[2];
				String kontoinhaber = teile[3];
				double kontostand = Double.parseDouble(teile[4]);

				Konto konto = new Konto(kontoName, kontoinhaber, kontostand, kreditInstitut); // Konstruktor Konto:
																								// String kontoName,
																								// String inhaber,
																								// double kontostand,
																								// String
																								// Kreditinstitut)
				Konto.konten.put(kontonummer, konto); // in die zentrale Map einfügen
			}

		} catch (IOException e) {
			System.out.println("Fehler beim Laden der Konten: " + e.getMessage());
		}
	}

	public static void kontenNeuSpeichern() {
		String pfad = kontenUebersichtAnlegen(); // Pfad zur Kontenübersicht
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(pfad))) {
			bw.write(headerKonten);
			bw.newLine();
			for (Konto konto : Konto.konten.values()) {
				String kontoZeile = konto.toCSV();
				bw.write(kontoZeile);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Speichern der Konten: " + e.getMessage());
		}
	}

	public static Buchung buchungAusCSV(Konto konto, String csvZeile) {
		String[] teile = csvZeile.split(";", -1); // -1 um leere Felder zu behalten
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate datum = LocalDate.parse(teile[0].trim(), formatter);
		String art = teile.length > 1 ? teile[1].trim() : "";
		String kategorie = teile.length > 2 ? teile[2].trim() : "";
		String empfaenger = teile.length > 3 ? teile[3].trim() : "";
		String sender = teile.length > 4 ? teile[4].trim() : "";
		double betrag        = teile.length > 5 ? Double.parseDouble(teile[5].trim().replace(",", ".")) : 0.0;
		double kontostand    = teile.length > 6 ? Double.parseDouble(teile[6].trim().replace(",", ".")) : 0.0;
		boolean isUmbuchung = teile.length >7 ? Boolean.parseBoolean(teile[7].trim()) : false;
		String transferID =  teile.length > 8 && !teile[8].isBlank() ? teile[8].trim() : null;;
		
		if (art.equalsIgnoreCase("Einnahme")) {
			return new Einnahme(konto, datum, art, kategorie, empfaenger, sender, betrag, kontostand, transferID, isUmbuchung);
		} else if (art.equalsIgnoreCase("Ausgabe")) {
			return new Ausgabe(konto, datum, art, kategorie, empfaenger, sender, betrag, kontostand, transferID, isUmbuchung);
		} 
		else if (art.equalsIgnoreCase("Erstellung")) {
			return new Buchung(konto, datum, art, kategorie, empfaenger, sender, betrag, kontostand, transferID, isUmbuchung);
		}
	else
	{
		System.out.println("⚠️ Unbekannte Buchungsart: " + art);
		return null;
	}}

	// Diese Methode lädt die Buchungen aus der Datei in die entsprechenden Konten
	public static void ladeBuchungenFuerAlleKonten() {
		String basisPfad = sep + "Haushaltsbuch" + sep + "Konten" + sep;

		for (Konto konto : Konto.konten.values()) {
			// Dateiname zusammensetzen
			String dateiname = konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName()
					+ ".csv";
			String pfad = basisPfad + dateiname;

			File buchungsDatei = new File(pfad);
			if (!buchungsDatei.exists()) {
				System.out.println("⚠️ Buchungsdatei für Konto " + konto.getKontoName() + " nicht gefunden.");
				continue;
			}

			try (BufferedReader br = new BufferedReader(new FileReader(buchungsDatei))) {
				String zeile;
				boolean ersteZeile = true;

				while ((zeile = br.readLine()) != null) {
					if (ersteZeile) {
						ersteZeile = false; // Header überspringen
						continue;
					}

					System.out.println("Lade Buchung: " + zeile);
					Buchung buchung = buchungAusCSV(konto, zeile);
					konto.getBuchungen().add(buchung);
				}

				System.out.println("✅ Buchungen für Konto " + konto.getKontoName() + " geladen.");

			} catch (IOException e) {
				System.out.println("Fehler beim Laden der Buchungen für Konto " + konto.getKontoName());
				e.printStackTrace();
			}
		}
	}

	public static void ladeKategorienAusDatei() {
		String dateiName = kategorieUebersichtAnlegen(); // Pfad zur Kategorienübersicht
		File kategorienDatei = new File(dateiName);
		if (!kategorienDatei.exists()) {
			System.out.println("⚠️ Kategorien-Datei nicht gefunden.");
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(kategorienDatei))) {
			String zeile;
			boolean ersteZeile = true;

			while ((zeile = br.readLine()) != null) {
				if (ersteZeile) {
					ersteZeile = false; // Header überspringen
					continue;
				}
				String kategorie = zeile.trim();
				if (!kategorie.isEmpty()) {
					Buchung.listeMitKategorien.add(kategorie);
				}
			}

			System.out.println("✅ Kategorien geladen.");

		} catch (IOException e) {
			System.out.println("Fehler beim Laden der Kategorien: " + e.getMessage());
		}

	}

	private static void kategorieZurDateiHinzufuegen(String kategorie) {
		String pfad = kategorieUebersichtAnlegen();
		zeileInDateiAnhaengen(pfad, kategorie);
	}

	public static void kontoBuchungenNeuSpeichern(Konto konto) {

		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
		ensureVerzeichnisVorhanden(ordnerpfad);
		String dateiname = konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName()
				+ ".csv";
		String kontopfad = ordnerpfad + sep + dateiname;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(kontopfad))) {
			bw.write(headerBuchungen);
			bw.newLine();
			for (Buchung buchung : konto.getBuchungen()) {
				String buchungsZeile = buchungToCSV(buchung.getFormatiertesDatum(), buchung.getBuchungsart(),
						buchung.getKategorie(), buchung.getEmpfaenger(), buchung.getSender(), buchung.getBetrag(),
						konto.getKontostand(), buchung.getIsUmbuchung() ,buchung.getTransferID());
				bw.write(buchungsZeile);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println(
					"Fehler beim Speichern der Buchungen für Konto " + konto.getKontoName() + ": " + e.getMessage());
		}
	}

}
