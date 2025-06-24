import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Datenstroeme {
	
	// Diese Klasse ist für die Datenströme verantwortlich
	// Hier werden die CSV-Dateien erstellt und verwaltet
	
	
//Hilfsmethoden für die Datenströme:
	
	public static String headerBuchungen = "Datum;Buchungsart;Kategorie;Empfaenger;Sender;Betrag;Kontostand";
	public static String headerKonten = "Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand";

	// Diese Methode formatiert eine Buchung in CSV-Format
	public static String buchungToCSV(String date, String buchungsart, String kategorie, String empfaenger,
			String sender, double betrag, double kontostand) {
		return date + ";" + buchungsart + ";" + kategorie + ";" + empfaenger + ";" + sender + ";" + betrag + ";"
				+ kontostand;
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
		String sep = File.separator;
	    String verzeichnis = sep + "Haushaltsbuch";
		File dir = ensureVerzeichnisVorhanden(verzeichnis);
		String dirName = dir.getAbsolutePath();
		String dateiName = dirName + sep + "Kontoliste" + ".csv";
		ensureDateiMitHeader(dateiName, headerKonten);
		return dateiName;
	}

	// Diese Methode erstellt eine neue Datei für ein Konto
	public static void KontoDateiAnlegen(Konto konto) {

		String sep = File.separator;
		String verzeichnis = sep + "Haushaltsbuch" + sep + "Konten";
		File newDir = ensureVerzeichnisVorhanden(verzeichnis);
		String newDirName = newDir.getAbsolutePath();
		String dateiName = newDirName + sep + konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" 
							+ konto.getKontoName() + ".csv";
		ensureDateiMitHeader(dateiName, headerBuchungen);
	}
	
	// Diese Methode fügt ein Konto zur Kontenübersicht hinzu
	public static void kontoHinzufuegen(Konto konto) {
	    String sep = File.separator;
	    String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
	    ensureVerzeichnisVorhanden(ordnerpfad);
	    String dateiname = konto.getKontonummer() + "_" + konto.getKreditinstitut() + "_" + konto.getKontoName() + ".csv";
	    String kontopfad = ordnerpfad + sep + dateiname;
	    // Header für Buchungsdatei
	    ensureDateiMitHeader(kontopfad, headerBuchungen);
	    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	    String ersteZeile = date + ";Erstellung;" + "nicht vorhanden" + ";" + "nicht vorhanden" + ";" + "nicht vorhanden" + ";" + "0.0" + ";" +  konto.getKontostand(); //"Datum;Buchungsart;Kategorie;Empfänger;Sender;Betrag;Kontostand
	    zeileInDateiAnhaengen(kontopfad, ersteZeile);
	    // Pfad zur zentralen Kontenliste
	    String kontenlistePfad = sep + "Haushaltsbuch" + sep + "Kontoliste.csv";
	    String kontoZeile = konto.toCSV();
	    zeileInDateiAnhaengen(kontenlistePfad, kontoZeile);
	}
	// Diese Methoden fügen eine Buchung (Einnahme, Ausgabe, Umbuchung) zur entsprechenden Datei hinzu
	public static void einnahmeHinzufuegen(Einnahme einnahme) {
		String sep = File.separator;
		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
		ensureVerzeichnisVorhanden(ordnerpfad);
		String dateiname = einnahme.getKonto().getKontonummer() + "_" + einnahme.getKonto().getKreditinstitut() + "_"
				+ einnahme.getKonto().getKontoName() + ".csv";
		String kontopfad = ordnerpfad + sep + dateiname;
		ensureDateiMitHeader(kontopfad, headerBuchungen);
		String buchungsZeile = buchungToCSV(einnahme.getFormatiertesDatum(), "Einnahme", einnahme.getKategorie(),
				einnahme.getKonto().getInhaber(), einnahme.getSender(), einnahme.getBetrag(),
				einnahme.getKonto().getKontostand());
		zeileInDateiAnhaengen(kontopfad, buchungsZeile);	
	}
	
	public static void ausgabeHinzufuegen(Ausgabe ausgabe) {
		String sep = File.separator;
		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
		ensureVerzeichnisVorhanden(ordnerpfad);
		String dateiname = ausgabe.getKonto().getKontonummer() + "_" + ausgabe.getKonto().getKreditinstitut() + "_"
				+ ausgabe.getKonto().getKontoName() + ".csv";
		String kontopfad = ordnerpfad + sep + dateiname;
		ensureDateiMitHeader(kontopfad, headerBuchungen);
		String buchungsZeile = buchungToCSV(ausgabe.getFormatiertesDatum(), "Ausgabe", ausgabe.getKategorie(),
				ausgabe.getKonto().getInhaber(), ausgabe.getEmpfaenger(), ausgabe.getBetrag(),
				ausgabe.getKonto().getKontostand());
		zeileInDateiAnhaengen(kontopfad, buchungsZeile);
	}
	
	public static void umbuchungHinzufuegen(Umbuchung umbuchung) {
		String sep = File.separator;
		String ordnerpfad = sep + "Haushaltsbuch" + sep + "Konten";
		ensureVerzeichnisVorhanden(ordnerpfad);
		String dateiname = umbuchung.getKontoVon().getKontonummer() + "_" + umbuchung.getKontoVon().getKreditinstitut()
				+ "_" + umbuchung.getKontoVon().getKontoName() + ".csv";
		String kontopfad = ordnerpfad + sep + dateiname;
		ensureDateiMitHeader(kontopfad, headerBuchungen);
		String buchungsZeile = buchungToCSV(umbuchung.getFormatiertesDatum(), "Ausgabe", umbuchung.getKategorie(),
				umbuchung.getKontoVon().getInhaber(), umbuchung.getKontoNach().getKontoName(), umbuchung.getBetrag(),
				umbuchung.getKontoVon().getKontostand());
		zeileInDateiAnhaengen(kontopfad, buchungsZeile);
		String dateiname2 = umbuchung.getKontoNach().getKontonummer() + "_"
				+ umbuchung.getKontoNach().getKreditinstitut() + "_" + umbuchung.getKontoNach().getKontoName() + ".csv";
		String kontopfad2 = ordnerpfad + sep + dateiname2;
		ensureDateiMitHeader(kontopfad2, headerBuchungen);
		String buchungsZeile2 = buchungToCSV(umbuchung.getFormatiertesDatum(), "Einnahme", umbuchung.getKategorie(),
				umbuchung.getKontoNach().getInhaber(), umbuchung.getKontoVon().getKontoName(), umbuchung.getBetrag(),
				umbuchung.getKontoNach().getKontostand());
		zeileInDateiAnhaengen(kontopfad2, buchungsZeile2);

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
	            //Header Konten: Kontonummer;Kreditinstitut;Kontoname;Kontoinhaber;Kontostand
	            String[] teile = zeile.split(";");
	            int kontonummer = Integer.parseInt(teile[0]);
	            String kreditInstitut = teile[1];
	            String kontoName = teile[2];
	            String kontoinhaber = teile[3];
	            double kontostand = Double.parseDouble(teile[4]);

	            Konto konto = new Konto(kontoName, kontoinhaber, kontostand, kreditInstitut);   //Konstruktor Konto: String kontoName, String inhaber, double kontostand, String Kreditinstitut)
	            Konto.konten.put(kontonummer, konto); // in die zentrale Map einfügen
	        }

	    } catch (IOException e) {
	        System.out.println("Fehler beim Laden der Konten: " + e.getMessage());
	    }
	}
	
	public static Buchung buchungAusCSV(String csvZeile) {
	    String[] teile = csvZeile.split(";");
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    LocalDate datum = LocalDate.parse(teile[0], formatter);
	    String art = teile[1];
	    String kategorie = teile[2];
	    String empfaenger = teile[3];
	    String sender = teile[4];
	    double betrag = Double.parseDouble(teile[5]);
	    double kontostand = Double.parseDouble(teile[6]);

	    return new Buchung(datum, art, kategorie, empfaenger, sender, betrag, kontostand);
	}

	
	// Diese Methode lädt die Buchungen aus der Datei in die entsprechenden Konten
	public static void ladeBuchungenFuerAlleKonten() {
	    String sep = File.separator;
	    String basisPfad = sep + "Haushaltsbuch" + sep + "Konten" + sep;

	    for (Konto konto : Konto.konten.values()) {
	        // Dateiname zusammensetzen
	        String dateiname = konto.getKontonummer() + "_" +
	                           konto.getKreditinstitut() + "_" +
	                           konto.getKontoName() + ".csv";
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
	                Buchung buchung = buchungAusCSV(zeile);
	                konto.getBuchungen().add(buchung);
	            }

	            System.out.println("✅ Buchungen für Konto " + konto.getKontoName() + " geladen.");

	        } catch (IOException e) {
	            System.out.println("Fehler beim Laden der Buchungen für Konto " + konto.getKontoName());
	            e.printStackTrace();
	        }
	    }
	}

}
