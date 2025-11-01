package org.meinprojekt.haushalt.core;

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
		public static void einnahmeTätigen(Double betragEin, String kategorieEin, Konto gewaehltesKonto, String sender,  LocalDate date) {
			Einnahme einnahme = new Einnahme(betragEin, kategorieEin, gewaehltesKonto, sender,  date); //Einnahme erstellen
			gewaehltesKonto.einzahlen(einnahme);
			einnahme.setBuchungsart("Einnahme");
			Datenstroeme.buchungHinzufuegen(einnahme);
			System.out.println("Einnahme wurde getätigt: " + einnahme);
		}
		
		//Notwendige Daten einlesen und Ausgabe tätigen
		public static void ausgabeTätigen(Double betrag, String kat, Konto quell, String empfaenger, LocalDate datum) {
			Ausgabe ausgabe = new Ausgabe(betrag, kat, quell, empfaenger, datum); // Ausgabe erstellen
			quell.auszahlen(ausgabe);
			ausgabe.setBuchungsart("Ausgabe");
			Datenstroeme.buchungHinzufuegen(ausgabe);
			System.out.println("Ausgabe wurde getätigt: " + ausgabe);
		}
		
		//Umbuchung tätigen
		public static void umbuchungTätigen(Double betrag, Konto quell, Konto ziel, LocalDate datum) {
			Umbuchung umbuchung = new Umbuchung(betrag, quell, ziel,  datum);
			umbuchung.setBuchungsart("Umbuchung");
			quell.auszahlen(umbuchung);
			ziel.einzahlen(umbuchung);
			Datenstroeme.umbuchungHinzufuegen(umbuchung);
			System.out.println("Umbuchung wurde getätigt: " + umbuchung);
		}
		
		public static void loescheBuchung(Buchung b) {
		    if (b instanceof Umbuchung u) {
		        Konto von  = u.getKontoVon();
		        Konto nach = u.getKontoNach();
		        double betrag = u.getBetrag();

		        // 1) aus Listen entfernen
		        if (von != null)  von.getBuchungen().remove(u);
		        if (nach != null) nach.getBuchungen().remove(u);

		        // 2) Salden rückgängig
		        if (von != null)  von.setKontostand(von.getKontostand() + betrag);
		        if (nach != null) nach.setKontostand(nach.getKontostand() - betrag);

		        // 3) CSV neu schreiben (beide)
		        if (von != null)  Datenstroeme.kontoBuchungenNeuSpeichern(von);
		        if (nach != null) Datenstroeme.kontoBuchungenNeuSpeichern(nach);

		    } else {
		        Konto k = b.getKonto();
		        if (k == null) {
		           
		            System.out.println("Warnung: Buchung ohne Konto, Abbruch.");
		            return;
		        }

		        double betrag = b.getBetrag();
		        switch (String.valueOf(b.getBuchungsart())) {
		            case "Einnahme" -> k.setKontostand(k.getKontostand() - betrag);
		            case "Ausgabe"  -> k.setKontostand(k.getKontostand() + betrag);
		            default -> { /* optional: loggen */ }
		        }

		        k.getBuchungen().remove(b);
		        Datenstroeme.kontoBuchungenNeuSpeichern(k);
		    }

		    // 4) Kontenübersicht neu schreiben (neue Salden)
		    Datenstroeme.kontenNeuSpeichern();

		    System.out.println("Buchung gelöscht: " + b);
		}

		public static void buchungBearbeiten(Buchung original, double betrag, String kat, Konto konto, String beteiligter,
				LocalDate datum) {
			// Alte Buchung rückgängig machen
			double alterBetrag = original.getBetrag();
			Konto altesKonto = original.getKonto();
			switch (original.getBuchungsart()) {
			case "Einnahme" -> altesKonto.auszahlen(original);
			case "Ausgabe" -> altesKonto.einzahlen(original);
			default -> {
				/* optional: loggen */ }
			}

			// Neue Buchungsdaten setzen
			original.setBetrag(betrag);
			original.setKategorie(kat);
			original.setBuchungsDatum(datum);
			if (original instanceof Einnahme e) {
				e.setSender(beteiligter);
				e.setKonto(konto);
				konto.einzahlen(e);
			} else if (original instanceof Ausgabe a) {
				a.setEmpfaenger(beteiligter);
				a.setKonto(konto);
				konto.auszahlen(a);
			}
			
			// CSV-Datei aktualisieren
			Datenstroeme.kontoBuchungenNeuSpeichern(konto);
			if (altesKonto != konto) {
				Datenstroeme.kontoBuchungenNeuSpeichern(altesKonto);
			}
			Datenstroeme.kontenNeuSpeichern();

			System.out.println("Buchung bearbeitet: " + original);
			
		}

}
