package org.meinprojekt.haushalt.speicher;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.meinprojekt.haushalt.core.model.Ausgabe;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Einnahme;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;
import org.meinprojekt.haushalt.core.service.WiederkehrendeZahlungenService;

public class Datenstroeme {

	// Diese Klasse ist für die Datenströme verantwortlich
	// Hier werden die CSV-Dateien erstellt und verwaltet

//Hilfsmethoden für die Datenströme:
	public static String sep = File.separator;
	public static String headerBuchungen = "Datum;Buchungsart;Kategorie;Empfaenger;Sender;Betrag;Umbuchung;transferID;Beschreibung";
	public static String headerWiederkehrendeZahlungen = "NaechsteZahlungAm;Haeufigkeit;Buchungsart;Kategorie;Beschreibung;Empfaenger;Sender;Betrag;LetzteZahlungAm";
	public static String headerKonten = "Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand_bei_Erstellung";
	public static String headerKategorien = "Kategorie";
	
	static String kontenlistePfad = sep + "Haushaltsbuch" + sep + "Kontoliste.csv";
	static String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten" + sep;
	
	public static String bildeDateiNameBuchungsliste (Konto konto) {
		return  sep + "Konten" + sep + konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName() + "_Buchungen";
	}
	public static String bildeDateiNameBuchungsliste (Buchung buchung) {
		String dateiname = bildeDateiNameBuchungsliste(buchung.getKonto());
		return dateiname;
	}
	
	public static String bildeDateiNameWiederkehrendeZahlungen(Konto konto) {
		return sep + "Konten" + sep + konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName()
				+ "_WiederkehrendeZahlungen";
	}
	
	public static String bildeDateiNameWiederkehrendeZahlungen(WiederkehrendeZahlung zahlung) {
		String dateiname = bildeDateiNameWiederkehrendeZahlungen(zahlung.getKonto());
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
			String sender, double betrag, boolean isUmbuchung, String transferID, String beschreibung) {
		String betragCsv = String.format(Locale.ROOT, "%.2f", betrag);
		return date + ";" + buchungsart + ";" + kategorie + ";" + empfaenger + ";" + sender + ";" + betragCsv + ";"
				 +  isUmbuchung + ";" + transferID + ";" + beschreibung;
	}
	
	public static String buchungToCSV(Buchung buchung) {
		return buchungToCSV(buchung.getFormatiertesDatum(), buchung.getBuchungsart(), buchung.getKategorie(),
				buchung.getEmpfaenger(), buchung.getSender(), buchung.getBetrag(),
				buchung.getIsUmbuchung(), buchung.getTransferID(), buchung.getBeschreibung());
	}
	
	public static String wiederkehrendeBuchungToCSV(String naechsteZahlungAm, Haeufigkeit haeufigkeit, String buchungsart, String kategorie, String beschreibung,
			String empfaenger, String sender, double betrag, String letzteZahlungAm) {
		String betragCsv = String.format(Locale.ROOT, "%.2f", betrag);
		return naechsteZahlungAm + ";" + haeufigkeit + ";" + buchungsart + ";" + kategorie + ";" + beschreibung + ";"
				+ empfaenger + ";" + sender + ";" + betragCsv + ";" + letzteZahlungAm;
	}
	
	public static String wiederkehrendeBuchungToCSV(WiederkehrendeZahlung zahlung) {
		return wiederkehrendeBuchungToCSV(WiederkehrendeZahlungenService.getFormatiertesDatum(zahlung.getNaechsteZahlungAm()), zahlung.getHaeufigkeit(), zahlung.getBuchungsart(), zahlung.getKategorie(), zahlung.getBeschreibung(),
				zahlung.getEmpfaenger(), zahlung.getSender(), zahlung.getBetrag(), WiederkehrendeZahlungenService.getFormatiertesDatum(zahlung.getLetzteZahlungAm()));
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
			for (Konto konto : Konto.getKonten().values()) {
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
	public static void kontoBuchungenDateiAnlegen(Konto konto) {
		String dateiPfad = bildeDateiPfad(bildeDateiNameBuchungsliste(konto));
		ensureDateiMitHeader(dateiPfad, headerBuchungen);
		System.out.println("Konto-Datei wurde angelegt: " + dateiPfad);
	}
	
	public static void kontoWiederkehrendeZahlungenDateiAnlegen(Konto konto) {
		String dateiPfad = bildeDateiPfad(bildeDateiNameWiederkehrendeZahlungen(konto));
		ensureDateiMitHeader(dateiPfad, headerWiederkehrendeZahlungen);
		System.out.println("Konto-Datei für wiederkehrende Zahlungen wurde angelegt: " + dateiPfad);
	}

	// Diese Methoden fügen eine Buchung (Einnahme, Ausgabe, Umbuchung) zur
	// entsprechenden Datei hinzu

	public static void buchungHinzufuegen(Buchung buchung) {
			ensureVerzeichnisVorhanden(ordnerpfad);
			String kontopfad = bildeDateiPfad(bildeDateiNameBuchungsliste(buchung));
			ensureDateiMitHeader(kontopfad, headerBuchungen);
			String buchungsZeile = buchungToCSV(buchung.getFormatiertesDatum(), buchung.getBuchungsart(),
					buchung.getKategorie(), buchung.getEmpfaenger(), buchung.getSender(), buchung.getBetrag(), buchung.getIsUmbuchung(), buchung.getTransferID(), buchung.getBeschreibung());
			zeileInDateiAnhaengen(kontopfad, buchungsZeile);
			kontenNeuSpeichern();
			kategorieZurDateiHinzufuegen(buchung.getKategorie());
		}
	
	public static void wiederkehrendeBuchungHinzufuegen(WiederkehrendeZahlung zahlung) {
		ensureVerzeichnisVorhanden(ordnerpfad);
		String kontopfad = bildeDateiPfad(bildeDateiNameWiederkehrendeZahlungen(zahlung));
		ensureDateiMitHeader(kontopfad, headerWiederkehrendeZahlungen);
		String buchungsZeile = wiederkehrendeBuchungToCSV(WiederkehrendeZahlungenService.getFormatiertesDatum(zahlung.getNaechsteZahlungAm()), zahlung.getHaeufigkeit(),
				zahlung.getBuchungsart(), zahlung.getKategorie(), zahlung.getBeschreibung(), zahlung.getEmpfaenger(),
				zahlung.getSender(), zahlung.getBetrag(), WiederkehrendeZahlungenService.getFormatiertesDatum(zahlung.getLetzteZahlungAm()));
		zeileInDateiAnhaengen(kontopfad, buchungsZeile);
		kategorieZurDateiHinzufuegen(zahlung.getKategorie());
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
				Konto.getKonten().put(kontonummer, konto); // in die zentrale Map einfügen
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
		boolean isUmbuchung = teile.length >6 ? Boolean.parseBoolean(teile[6].trim()) : false;
		String transferID =  teile.length > 7 && !teile[7].isBlank() ? teile[7].trim() : null;;
		String beschreibung = teile.length > 8 ? teile[8].trim() : "";

		if (art.equalsIgnoreCase("Einnahme")) {
			return new Einnahme(konto, datum, art, kategorie, beschreibung, empfaenger, sender, betrag, transferID, isUmbuchung);
		} else if (art.equalsIgnoreCase("Ausgabe")) {
			return new Ausgabe(konto, datum, art, kategorie, beschreibung, empfaenger, sender, betrag, transferID, isUmbuchung);
		} 
		else if (art.equalsIgnoreCase("Erstellung")) {
			return new Buchung(konto, datum, art, kategorie,beschreibung, empfaenger, sender,  betrag, transferID, isUmbuchung);
		}
	else
	{
		System.out.println("⚠️ Unbekannte Buchungsart: " + art);
		return null;
	}}
	
public static WiederkehrendeZahlung wiederkehrendeBuchungAusCSV(Konto konto, String csvZeile) {
	String[] teile = csvZeile.split(";", -1); // -1 um leere Felder zu behalten
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	LocalDate naechsteZahlungAm = LocalDate.parse(teile[0].trim(), formatter);
	System.out.println("Parsed date: " + naechsteZahlungAm);
	Haeufigkeit häufigkeit = teile.length > 1 ? Haeufigkeit.haeufigkeitFromString(teile[1].trim()) : Haeufigkeit.MONATLICH;
	String art = teile.length > 2 ? teile[2].trim() : "";
	String kategorie = teile.length > 3 ? teile[3].trim() : "";
	String beschreibung = teile.length > 4 ? teile[4].trim() : "";
	String empfaenger = teile.length > 5 ? teile[5].trim() : "";
	String sender = teile.length > 6 ? teile[6].trim() : "";
	double betrag = teile.length > 7 ? Double.parseDouble(teile[7].trim().replace(",", ".")) : 0.0;
	LocalDate letzteZahlungAm = teile.length > 8 && !teile[8].isBlank() ? LocalDate.parse(teile[8].trim(), formatter) : naechsteZahlungAm;
	
	return new WiederkehrendeZahlung(naechsteZahlungAm, häufigkeit, art, kategorie, beschreibung, empfaenger, sender, betrag, konto, letzteZahlungAm); 
	}

	// Diese Methode lädt die Buchungen aus der Datei in die entsprechenden Konten
	public static void ladeBuchungenFuerAlleKonten() {
		for (Konto konto : Konto.getKonten().values()) {
			File buchungsDatei = new File(bildeDateiPfad(bildeDateiNameBuchungsliste(konto)));
			if (!buchungsDatei.exists()) {
				System.out.println("⚠️ Buchungsdatei für Konto " + bildeDateiPfad(bildeDateiNameBuchungsliste(konto)) + " nicht gefunden.");
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
	
	public static void ladeWiederkehrendeZahlungenFuerAlleKonten() {
		for (Konto konto : Konto.getKonten().values()) {
			File dateiWiederkehrendeBuchungen = new File(bildeDateiPfad(bildeDateiNameWiederkehrendeZahlungen(konto)));
			if (!dateiWiederkehrendeBuchungen.exists()) {
				System.out.println("⚠️ Buchungsdatei für Konto " + bildeDateiPfad(bildeDateiNameWiederkehrendeZahlungen(konto)) + " nicht gefunden.");
				continue;
			}
			try (BufferedReader br = new BufferedReader(new FileReader(dateiWiederkehrendeBuchungen))) {
				String zeile;
				boolean ersteZeile = true;

				while ((zeile = br.readLine()) != null) {
					if (ersteZeile) {
						ersteZeile = false; // Header überspringen
						continue;
					}
					System.out.println("Lade Wiederkehrende Buchung: " + zeile);
					WiederkehrendeZahlung zahlung = wiederkehrendeBuchungAusCSV(konto, zeile);
					konto.getWiederkehrendeZahlungen().add(zahlung);
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
		String kontopfad = bildeDateiPfad(bildeDateiNameBuchungsliste(konto));
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

	public static void kontoLoeschen(Konto konto) {
		String kontopfad = bildeDateiPfad(bildeDateiNameBuchungsliste(konto));
		File datei = new File(kontopfad);
		if (datei.delete()) {
			System.out.println("Datei gelöscht: " + kontopfad);
		} else {
			System.out.println("Fehler beim Löschen der Datei: " + kontopfad);
		}
		kontenNeuSpeichern();
	}
	public static void kontoWiederkehrendeZahlungenNeuSpeichern(Konto konto) {
		ensureVerzeichnisVorhanden(ordnerpfad);
		String kontopfad = bildeDateiPfad(bildeDateiNameWiederkehrendeZahlungen(konto));
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(kontopfad))) {
			bw.write(headerWiederkehrendeZahlungen);
			bw.newLine();
			for (WiederkehrendeZahlung wz : konto.getWiederkehrendeZahlungen()) {
				bw.write(wiederkehrendeBuchungToCSV(wz));
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Speichern der wiederkehrenden Zahlungen für Konto "
					+ konto.getKontoName() + ": " + e.getMessage());
		}
	}
	
		
}
