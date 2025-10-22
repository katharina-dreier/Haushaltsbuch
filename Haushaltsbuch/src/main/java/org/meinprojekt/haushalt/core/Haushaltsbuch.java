package org.meinprojekt.haushalt.core;


import java.util.Scanner;

public class Haushaltsbuch {
	// Hauptklasse für das Haushaltsbuch-Programm
	// Hier wird das Hauptmenü und die Auswahl der Optionen gesteuert
	
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		
		// Anlegen der Kontenübersicht
		Datenstroeme.kontenUebersichtAnlegen();
		Datenstroeme.ladeKontenAusDatei();
		Datenstroeme.ladeBuchungenFuerAlleKonten();

		while (MenueFuehrung.auswahl != 9) {

			MenueFuehrung.hauptMenue();
			switch (MenueFuehrung.auswahl) {
			case 1:
				MenueFuehrung.Untermenue1();
				switch (MenueFuehrung.auswahl) {
				case 1:
					KontoAktionen.kontoErstellen();
					break;
				case 2:
					KontoAktionen.kontenListeAnzeigen();
					break;
				case 3:
					KontoAktionen.gewaehltesKonto = KontoAktionen.kontoAuswaehlen();
					System.out.println("Der Kontostand beträgt: " + KontoAktionen.gewaehltesKonto.getKontostand() + " Euro.");
					break;
				case 4:
					System.out.println("Zurück zum Hauptmenü.");
					break;
				}
				break;
			case 2:
				MenueFuehrung.Untermenue2();
				switch (MenueFuehrung.auswahl) {
				case 1:
                    BuchungsAktionen.einnahmeTätigen();
                    break;
				case 2:
					BuchungsAktionen.ausgabeTätigen();
					break;
				case 3: 
					BuchungsAktionen.umbuchungTätigen();
					break;
				case 4:
					System.out.println("Zurück zum Hauptmenü.");
					break;
				}
				break;
			case 3:
				MenueFuehrung.Untermenue3();
				switch(MenueFuehrung.auswahl) {
				case 1:
					System.out.println("Die Gesamtsumme aller Konten beträgt: " + Konto.getGesamtSumme() + " Euro.");
					break;
				case 2:
					System.out.println("Nach Konto filtern noch nicht implementiert.");
					break;
				case 3:
					System.out.println("Nach Kategorie filtern noch nicht implementiert.");
					break;
				case 4: 
					System.out.println("Nach Zeitraum filtern noch nicht implementiert.");
					break;
				case 5: 
					System.out.println("Nach Ort/Geschäft filtern noch nicht implementiert.");
					break;
				case 6:
					System.out.println("Zurück zum Hauptmenü.");
					break;
				}
				break;
			case 9:
				System.out.println("Programm wurde erfolgreich beendet.");
				break;
			default:
				System.out.println("Bitte eine gültige Nummer wählen.");

			}

		}
		
		
	/*	while (MenueFuehrung.auswahl != 7) {

			MenueFuehrung.hauptMenue();
			switch (MenueFuehrung.auswahl) {
			case 1:
				KontoAktionen.kontoErstellen();
				break;
			case 2:
				BuchungsAktionen.einnahmeTätigen();
				break;
			case 3:
				BuchungsAktionen.ausgabeTätigen();
				break;
			case 4:
				BuchungsAktionen.umbuchungTätigen();
				break;
			case 5:
				KontoAktionen.gewaehltesKonto = KontoAktionen.kontoAuswaehlen();
				System.out.println("Der Kontostand beträgt: " + KontoAktionen.gewaehltesKonto.getKontostand() + " Euro.");
				break;
			case 6:
				System.out.println("Die Gesamtsumme aller Konten beträgt: " + Konto.getGesamtSumme() + " Euro.");
				break;
			case 7:
				System.out.println("Programm wurde erfolgreich beendet.");
				break;
			default:
				System.out.println("Bitte eine gültige Nummer wählen.");

			}

		}*/
	}
}
