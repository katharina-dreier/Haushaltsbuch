package org.meinprojekt.haushalt.core;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Datenstroeme {

	// Diese Klasse ist für die Datenströme verantwortlich
	// Hier werden die CSV-Dateien erstellt und verwaltet

//Hilfsmethoden für die Datenströme:
	public static String sep = File.separator;
	public static String headerBuchungen = "Datum;Buchungsart;Kategorie;Empfaenger;Sender;Betrag;Kontostand;Umbuchung;transferID";
	public static String headerKonten = "Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand_bei_Erstellung";
	public static String headerKategorien = "Kategorie";
	
	static String kontenlistePfad = sep + "Haushaltsbuch" + sep + "Kontoliste.csv";
	static String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten" + sep;
	
	public static String bildeDateiName (Konto konto) {
		return  konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName() + ".csv";
	}
	public static String bildeDateiName (Buchung buchung) {
		String dateiname = bildeDateiName(buchung.getKonto());
		return dateiname;
	}
	
	public static String bildeDateiPfad(String dateiname) {
		String verzeichnis = sep + "Haushaltsbuch";
		File ordner = ensureVerzeichnisVorhanden(verzeichnis);
		String ordnerName = ordner.getAbsolutePath();
		String dateipfad = ordnerName + sep + dateiname + ".csv";
        return dateipfad;
	}

	// Diese Methode formatiert eine Buchung in CSV-Format
	public static String buchungToCSV(String date, String buchungsart, String kategorie, String empfaenger,
			String sender, double betrag, double kontostand, boolean isUmbuchung, String transferID) {
		String betragCsv = String.format(Locale.ROOT, "%.2f", betrag);
		return date + ";" + buchungsart + ";" + kategorie + ";" + empfaenger + ";" + sender + ";" + betragCsv + ";"
				+ kontostand + ";" +  isUmbuchung + ";" + transferID;
	}
	
	public static String buchungToCSV(Buchung buchung) {
		return buchungToCSV(buchung.getFormatiertesDatum(), buchung.getBuchungsart(), buchung.getKategorie(),
				buchung.getEmpfaenger(), buchung.getSender(), buchung.getBetrag(), buchung.getKonto().getKontostand(),
				buchung.getIsUmbuchung(), buchung.getTransferID());
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
	
	public static File dateiMitHeaderNeuErstellen(String dateipfad, String headerZeile) {
		File datei = new File(dateipfad);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(datei))) {
			bw.write(headerZeile);
			bw.newLine();
			System.out.println("Datei wurde neu erstellt: " + dateipfad);
		} catch (IOException e) {
			System.out.println("Fehler beim Erstellen der Datei: " + e.getMessage());
		}
		return datei;
	}

	// Diese Methode fügt eine Zeile an eine bestehende Datei an
	public static void zeileInDateiAnhaengen(String dateipfad, String zeile) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(dateipfad, true))) {
			bw.write(zeile);
			bw.newLine();
			System.out.println("Zeile: " + zeile + " wurde angehängt an Datei: " + dateipfad);
		} catch (IOException e) {
			System.out.println("Fehler beim Schreiben in Datei: " + e.getMessage());
		}
	}
	
	public static void kontenNeuSpeichern() {
		String pfad = kontenUebersichtAnlegen(); // Pfad zur Kontenübersicht
		dateiMitHeaderNeuErstellen(pfad, headerKonten);
			for (Konto konto : Konto.konten.values()) {
				String kontoZeile = konto.toCSV();
				zeileInDateiAnhaengen(pfad, kontoZeile);
			}
	}


//Konten und Buchungsmethoden 
	// Diese Methode erstellt eine neue Datei für die Kontenübersicht
	public static String kontenUebersichtAnlegen() {
		String dateipfad = bildeDateiPfad("Kontoliste");
		ensureDateiMitHeader(dateipfad, headerKonten);
		return dateipfad;
	}

	// Diese Methode erstellt eine neue Datei für die Kategorienübersicht
	public static String kategorieUebersichtAnlegen() {
		String dateiPfad = bildeDateiPfad("Kategorienliste");
		ensureDateiMitHeader(dateiPfad, headerKategorien);
		return dateiPfad;
	}

	// Diese Methode erstellt eine neue Datei für ein Konto
	public static void KontoDateiAnlegen(Konto konto) {
		String dateiPfad = bildeDateiPfad("Konten" + sep + konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_"
				+ konto.getKontoName());
		ensureDateiMitHeader(dateiPfad, headerBuchungen);
		System.out.println("Konto-Datei wurde angelegt: " + dateiPfad);
	}

	// Diese Methoden fügen eine Buchung (Einnahme, Ausgabe, Umbuchung) zur
	// entsprechenden Datei hinzu

	public static void buchungHinzufuegen(Buchung buchung) {
			ensureVerzeichnisVorhanden(ordnerpfad);
			String dateiname = bildeDateiName(buchung);
			String kontopfad = ordnerpfad  + dateiname;
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
				double kontostandErstellung = Double.parseDouble(teile[4]);

				Konto konto = new Konto(kontoName, kontoinhaber, kontostandErstellung, kreditInstitut); 
				Konto.konten.put(kontonummer, konto); // in die zentrale Map einfügen
			}

		} catch (IOException e) {
			System.out.println("Fehler beim Laden der Konten: " + e.getMessage());
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
		System.out.println("RAW CSV: " + teile[5]);
		System.out.println("PARSED: " + betrag);

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
		for (Konto konto : Konto.konten.values()) {
			File buchungsDatei = new File(ordnerpfad + bildeDateiName(konto));
			if (!buchungsDatei.exists()) {
				System.out.println("⚠️ Buchungsdatei für Konto " + ordnerpfad + bildeDateiName(konto) + " nicht gefunden.");
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
		File kategorienDatei = new File(kategorieUebersichtAnlegen());
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
				if (!kategorie.isEmpty() && !Buchung.listeMitKategorien.contains(kategorie)) {
					Buchung.listeMitKategorien.add(kategorie);
				}
			}

			System.out.println("✅ Kategorien geladen.");

		} catch (IOException e) {
			System.out.println("Fehler beim Laden der Kategorien: " + e.getMessage());
		}
	}

	private static void kategorieZurDateiHinzufuegen(String kategorie) {
		zeileInDateiAnhaengen(kategorieUebersichtAnlegen(), kategorie);
	}

	public static void kontoBuchungenNeuSpeichern(Konto konto) {
		ensureVerzeichnisVorhanden(ordnerpfad);
		String kontopfad = ordnerpfad + bildeDateiName(konto);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(kontopfad))) {
			bw.write(headerBuchungen);
			bw.newLine();
			for (Buchung buchung : konto.getBuchungen()) {
				bw.write(buchungToCSV(buchung));
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println(
					"Fehler beim Speichern der Buchungen für Konto " + konto.getKontoName() + ": " + e.getMessage());
		}
	}

	public static void kontoLoeschen(Konto k) {
		String kontopfad = ordnerpfad + bildeDateiName(k);
		File datei = new File(kontopfad);
		if (datei.delete()) {
			System.out.println("Datei gelöscht: " + kontopfad);
		} else {
			System.out.println("Fehler beim Löschen der Datei: " + kontopfad);
		}
		kontenNeuSpeichern();
	}
	
	/*public static void kontoAendern(Konto altesKonto, Konto neuesKonto) {
		String altesKontopfad = ordnerpfad + bildeDateiName(altesKonto);
		String neuesKontopfad = ordnerpfad  + bildeDateiName(neuesKonto);
		File altesDatei = new File(altesKontopfad);
		File neuesDatei = new File(neuesKontopfad);
		if (altesDatei.renameTo(neuesDatei)) {
			System.out.println("Datei umbenannt: " + neuesKontopfad);
		} else {
			System.out.println("Fehler beim Umbenennen der Datei: " + altesKontopfad);
		}
}*/
}
