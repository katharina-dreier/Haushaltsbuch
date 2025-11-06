package org.meinprojekt.haushalt.core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
		public static void einnahmeTätigen(Double betragEin, String kategorieEin, Konto gewaehltesKonto, String sender,  LocalDate datum, String transferID, boolean isUmbuchung) {
			Einnahme einnahme = new Einnahme(betragEin, kategorieEin, gewaehltesKonto, sender,  datum, transferID, isUmbuchung); //Einnahme erstellen
			gewaehltesKonto.einzahlen(einnahme);
			einnahme.setBuchungsart("Einnahme");
			Datenstroeme.buchungHinzufuegen(einnahme);
			System.out.println("Einnahme wurde getätigt: " + einnahme);
		}
		
		//Notwendige Daten einlesen und Ausgabe tätigen
		public static void ausgabeTätigen(Double betrag, String kat, Konto quell, String empfaenger, LocalDate datum, String transferID, boolean isUmbuchung) {
			Ausgabe ausgabe = new Ausgabe(betrag, kat, quell, empfaenger, datum, transferID, isUmbuchung); // Ausgabe erstellen
			quell.auszahlen(ausgabe);
			ausgabe.setBuchungsart("Ausgabe");
			Datenstroeme.buchungHinzufuegen(ausgabe);
			System.out.println("Ausgabe wurde getätigt: " + ausgabe);
		}
		
		//Umbuchung tätigen
		public static void umbuchungTätigen(Double betrag, Konto quell, Konto ziel, LocalDate datum) {
			Umbuchung umbuchung = new Umbuchung(betrag, quell, ziel,  datum);
			String ID = umbuchung.getTransferID();
			ausgabeTätigen(betrag, "Umbuchung", quell, umbuchung.getEmpfaenger(), datum, ID, true);
			einnahmeTätigen(betrag, "Umbuchung", ziel, umbuchung.getSender(), datum, ID, true) ;
			System.out.println("Umbuchung wurde getätigt: " + umbuchung);
		}
		
		public static void loescheBuchung(Buchung b) {
		    if (b.getIsUmbuchung()) {
		    	String transferId = b.getTransferID();
		    	List<Buchung> buchungen = findeBuchungenZuTransferID(transferId);
				if (buchungen.size() != 2) {
					System.out.println("Fehler: Umbuchung nicht gefunden oder unvollständig.");
					return;
				}
				Buchung buchung1 = buchungen.get(0);
				Buchung buchung2 = buchungen.get(1);
		    	
		        // Beide Buchungen der Umbuchung rückgängig machen
				Konto konto1 = buchung1.getKonto();
				Konto konto2 = buchung2.getKonto();

		        // aus Listen entfernen
		        if (konto1 != null)  konto1.getBuchungen().remove(buchung1);
		        if (konto2 != null) konto2.getBuchungen().remove(buchung2);

		        // Salden rückgängig
		        double betrag1 = buchung1.getBetrag();
		        double betrag2 = buchung2.getBetrag();
				if (konto1 != null) {
					switch (String.valueOf(buchung1.getBuchungsart())) {
					case "Einnahme" -> konto1.setKontostand(konto1.getKontostand() - betrag1);
					case "Ausgabe" -> konto1.setKontostand(konto1.getKontostand() + betrag1);
					}
					}
				if (konto2 != null) {
					switch (String.valueOf(buchung2.getBuchungsart())) {
					case "Einnahme" -> konto2.setKontostand(konto2.getKontostand() - betrag2);
					case "Ausgabe" -> konto2.setKontostand(konto2.getKontostand() + betrag2);
					}
				}
		        // CSV neu schreiben (beide)
		        if (konto1 != null)  Datenstroeme.kontoBuchungenNeuSpeichern(konto1);
		        if (konto2 != null) Datenstroeme.kontoBuchungenNeuSpeichern(konto2);
		        
		        System.out.println("Umbuchung gelöscht: " + buchung1 + " und " + buchung2);

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
		        System.out.println("Buchung gelöscht: " + b);
		    }

		    // Kontenübersicht neu schreiben (neue Salden)
		    Datenstroeme.kontenNeuSpeichern();
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
		
		
		public static void umbuchungBearbeiten(Buchung original, double betrag, LocalDate datum) {
			
			List<Buchung> buchungen = findeBuchungenZuTransferID(original.getTransferID());
			if (buchungen.size() != 2) {
				System.out.println("Fehler: Umbuchung nicht gefunden oder unvollständig.");
				return;
			}
			Buchung buchung1 = buchungen.get(0);
			Buchung buchung2 = buchungen.get(1);
			String beteiligter1 = buchung1 instanceof Einnahme ? buchung1.getSender() : buchung1.getEmpfaenger();
			String beteiligter2 = buchung2 instanceof Einnahme ? buchung2.getSender() : buchung2.getEmpfaenger();
			
			buchungBearbeiten(buchung1, betrag, buchung1.getKategorie(), buchung1.getKonto(), beteiligter1, datum);
			buchungBearbeiten(buchung2, betrag, buchung2.getKategorie(), buchung2.getKonto(), beteiligter2, datum);
			
		}
		
		// Alle Buchungen mit einer bestimmten transferId finden 
		public static List<Buchung> findeBuchungenZuTransferID(String transferId) {
			return Konto.getAlleBuchungen().stream().filter(b -> transferId.equals(b.getTransferID())).toList();
		}


}
