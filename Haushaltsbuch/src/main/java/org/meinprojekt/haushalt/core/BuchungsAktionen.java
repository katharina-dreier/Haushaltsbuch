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
			Datenstroeme.einnahmeHinzufuegen(einnahme);
			System.out.println("Einnahme wurde getätigt: " + einnahme);
		}
		
		//Notwendige Daten einlesen und Ausgabe tätigen
		public static void ausgabeTätigen(Double betrag, String kat, Konto quell, String empfaenger, LocalDate datum) {
			Ausgabe ausgabe = new Ausgabe(betrag, kat, quell, empfaenger, datum); // Ausgabe erstellen
			quell.auszahlen(ausgabe);
			ausgabe.setBuchungsart("Ausgabe");
			Datenstroeme.ausgabeHinzufuegen(ausgabe);
			System.out.println("Ausgabe wurde getätigt: " + ausgabe);
		}
		
		//Umbuchung tätigen
		public static void umbuchungTätigen(Double betrag, Konto quell, Konto ziel, LocalDate datum) {
			Umbuchung umbuchung = new Umbuchung(betrag, quell, ziel,  datum);
			umbuchung.setBuchungsart("Umbuchung");
			quell.auszahlen(umbuchung);
			ziel.einzahlen(umbuchung);
			Datenstroeme.umbuchungHinzufuegen(umbuchung);
		}
		

}
