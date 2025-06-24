import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class BuchungsAktionen {
	
	public static Konto gewaehltesKonto;
	public static Scanner scan = new Scanner(System.in);
	
	//Datum für Buchungen einlesen und prüfen
		public static LocalDate datumAuswaehlen() {
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		    while (true) {
		        System.out.print("Gib ein Datum ein (Format: dd.MM.yyyy): ");
		        String eingabe = scan.nextLine();
		        try {
		            LocalDate datum = LocalDate.parse(eingabe, formatter);
		            System.out.println("Eingegebenes Datum: " + datum);
		            return datum;
		        } catch (DateTimeParseException e) {
		            System.out.println("⚠️ Fehler: Ungültiges Datum!");
		            System.out.println("Beispiel für gültige Eingabe: 28.03.2025");
		    }
		        }
		    }
	
	//Notwendige Daten einlesen und Einnahme tätigen
		public static void einnahmeTätigen() {
			//Konto auswählen
			gewaehltesKonto = KontoAktionen.kontoAuswaehlen();
			System.out.println("Bitte geben Sie den Betrag ein: "); //Betrag eingeben
			double betragEin = scan.nextDouble();
			scan.nextLine();
			System.out.println("Bitte geben Sie den Sender ein: "); //Sender eingeben
			String sender = scan.nextLine();
			System.out.println("Bitte geben Sie die Kategorie ein: "); //Kategorie eingeben
			String kategorieEin = scan.nextLine();
			System.out.println("Bitte geben Sie das Buchungsdatum ein: "); //Buchungsdatum festlegen
			LocalDate date = datumAuswaehlen();
			Einnahme einnahme = new Einnahme(betragEin, kategorieEin, gewaehltesKonto, sender,  date); //Einnahme erstellen
			gewaehltesKonto.einzahlen(einnahme);
			Datenstroeme.einnahmeHinzufuegen(einnahme);
			System.out.println("Einnahme wurde getätigt: " + einnahme);
		}
		
		//Notwendige Daten einlesen und Ausgabe tätigen
		public static void ausgabeTätigen() {
			gewaehltesKonto = KontoAktionen.kontoAuswaehlen(); 		// Konto auswählen
			System.out.println("Bitte geben Sie den Betrag ein: "); // Betrag eingeben
			double betragAus = scan.nextDouble();
			scan.nextLine();
			System.out.println("Bitte geben Sie die Kategorie ein: "); // Kategorie eingeben
			String kategorieAus = scan.nextLine();
			System.out.println("Bitte geben Sie den Empfänger ein: ");
			String empfaenger = scan.nextLine();
			System.out.println("Bitte geben Sie das Buchungsdatum ein: "); // Buchungsdatum festlegen
			LocalDate date = datumAuswaehlen();
			Ausgabe ausgabe = new Ausgabe(betragAus, kategorieAus, gewaehltesKonto, empfaenger, date); // Ausgabe erstellen
			gewaehltesKonto.auszahlen(ausgabe);
			Datenstroeme.ausgabeHinzufuegen(ausgabe);
			System.out.println("Ausgabe wurde getätigt: " + ausgabe);
		}
		
		//Umbuchung tätigen
		public static void umbuchungTätigen() {
			System.out.println("Bitte wähle das Konto aus, von dem abgebucht werden soll:");
			Konto gewaehltesKontoVon = KontoAktionen.kontoAuswaehlen();
			System.out.println("Du hast \"" + gewaehltesKontoVon + "\" gewählt.");
			System.out.println("Bitte wähle das Konto aus, auf das eingezahlt werden soll:");
			Konto gewaehltesKontoNach = KontoAktionen.kontoAuswaehlen();
			System.out.println("Du hast \"" + gewaehltesKontoNach + "\" gewählt.");
			System.out.println("Bitte geben Sie den Betrag ein: ");
			double betragUmb = scan.nextDouble();
			scan.nextLine();
			System.out.println("Bitte geben Sie das Buchungsdatum ein: ");
			LocalDate date = datumAuswaehlen();
			Umbuchung umbuchung = new Umbuchung(betragUmb, gewaehltesKontoVon, gewaehltesKontoNach,
					date);
			gewaehltesKontoVon.auszahlen(umbuchung);
			gewaehltesKontoNach.einzahlen(umbuchung);
			Datenstroeme.umbuchungHinzufuegen(umbuchung);
		}
		

}
