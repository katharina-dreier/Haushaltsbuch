package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.meinprojekt.haushalt.core.model.Ausgabe;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Einnahme;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.Umbuchung;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class BuchungsService {
	
	
	//Notwendige Daten einlesen und Einnahme tätigen
		public static void einnahmeTätigen(Double betragEin, String kategorieEin, String beschreibung, Konto gewaehltesKonto, String sender,  LocalDate datum, String transferID, boolean isUmbuchung) {
			System.out.println("Einnahme wird getätigt mit folgenden Daten: Betrag: " + betragEin + ", Kategorie: " + kategorieEin + ", Konto: " + gewaehltesKonto.getKontoName() + ", Sender: " + sender + ", Datum: " + datum);
			Einnahme einnahme = new Einnahme(betragEin, kategorieEin, beschreibung, gewaehltesKonto, sender,  datum, transferID, isUmbuchung); //Einnahme erstellen
			Datenstroeme.buchungHinzufuegen(einnahme);
			System.out.println("Einnahme wurde getätigt: " + einnahme);
		}
		
		//Notwendige Daten einlesen und Ausgabe tätigen
		public static void ausgabeTätigen(Double betrag, String kat, String beschreibung, Konto quell, String empfaenger, LocalDate datum, String transferID, boolean isUmbuchung) {
			System.out.println("Ausgabe wird getätigt mit folgenden Daten: Betrag: " + betrag + ", Kategorie: " + kat + ", Konto: " + quell.getKontoName() + ", Empfänger: " + empfaenger + ", Datum: " + datum);
			Ausgabe ausgabe = new Ausgabe(betrag, kat, beschreibung, quell, empfaenger, datum, transferID, isUmbuchung); // Ausgabe erstellen
			ausgabe.setBuchungsart("Ausgabe");
			Datenstroeme.buchungHinzufuegen(ausgabe);
			System.out.println("Ausgabe wurde getätigt: " + ausgabe);
		}
		
		//Umbuchung tätigen
		public static void umbuchungTätigen(Double betrag, String beschreibung, Konto quell, Konto ziel, LocalDate datum) {
			System.out.println("Umbuchung wird getätigt mit folgenden Daten: Betrag: " + betrag + ", Von Konto: " + quell.getKontoName() + ", Zu Konto: " + ziel.getKontoName() + ", Datum: " + datum);
			Umbuchung umbuchung = new Umbuchung(betrag, quell, ziel,  datum);
			String ID = umbuchung.getTransferID();
			ausgabeTätigen(betrag, "Umbuchung", beschreibung, quell, umbuchung.getEmpfaenger(), datum, ID, true);
			einnahmeTätigen(betrag, "Umbuchung", beschreibung, ziel, umbuchung.getSender(), datum, ID, true) ;
			System.out.println("Umbuchung wurde getätigt: " + umbuchung);
		}
		
		public static void loescheBuchung(Buchung buchung) {
			System.out.println("Starte mit Löschen von Buchung: " + buchung);
		    if (buchung.getIsUmbuchung()) {
		    	String transferId = buchung.getTransferID();
		    	List<Buchung> buchungen = findeBuchungenZuTransferID(transferId);
				if (buchungen.size() != 2) {
					System.out.println("Fehler: Umbuchung nicht gefunden oder unvollständig.");
					return;
				}
				Buchung buchung1 = buchungen.get(0);
				Buchung buchung2 = buchungen.get(1);
				
				Buchung buchungOriginal;
				Buchung buchungGegenpart;
				
				// Originalbuchung und Gegenpart identifizieren
				if (buchung1 == buchung) {
					buchungOriginal = buchung1;
					buchungGegenpart = buchung2;
				} else if (buchung2 == buchung) {
					buchungOriginal = buchung2;
					buchungGegenpart = buchung1;
				} else {
					System.out.println("Fehler: Originalbuchung nicht in der Umbuchung gefunden.");
					return;
				}
		    	
		        // Beide Buchungen der Umbuchung rückgängig machen
				Konto konto1 = buchungOriginal.getKonto();
				Konto konto2 = buchungGegenpart.getKonto();

		        // Buchung aus zu löschendem Konto entfernen
		        if (konto1 != null)  konto1.getBuchungen().remove(buchungOriginal);
		        //Gegenbuchung ändern
		        buchungGegenpart.setIsUmbuchung(false);
		        buchungGegenpart.setTransferID("");
		        String beteiligterGegenpart = buchungGegenpart instanceof Einnahme ? "gelöschtes Konto: " + buchungGegenpart.getSender() : "gelöschtes Konto: " + buchungGegenpart.getEmpfaenger();
		        if (buchungGegenpart instanceof Einnahme e) {
					e.setSender(beteiligterGegenpart);
				} else if (buchungGegenpart instanceof Ausgabe a) {
					a.setEmpfaenger(beteiligterGegenpart);
		                     }
		        // CSV neu schreiben (beide)
		        if (konto2 != null) Datenstroeme.kontoBuchungenNeuSpeichern(konto2);
		        
		        System.out.println("Umbuchung gelöscht: " + buchungOriginal + " und geändert" + buchungGegenpart);

		    } else {
		        Konto k = buchung.getKonto();
		        if (k == null) {
		           
		            System.out.println("Warnung: Buchung ohne Konto, Abbruch.");
		            return;
		        }
		        k.getBuchungen().remove(buchung);
		        Datenstroeme.kontoBuchungenNeuSpeichern(k);
		        System.out.println("Buchung gelöscht: " + buchung);
		    }
		    // Kontenübersicht neu schreiben
		    Datenstroeme.kontenNeuSpeichern();
		}

		public static void buchungBearbeiten(Buchung original, double betrag, String kat, String beschreibung, Konto konto, String beteiligter,
				LocalDate datum) {
			System.out.println("Starte mit Bearbeiten der Buchung: " + original);
			Konto altesKonto = original.getKonto();

			// Neue Buchungsdaten setzen
			original.setBetrag(betrag);
			original.setKategorie(kat);
			original.setBeschreibung(beschreibung);
			original.setBuchungsDatum(datum);
			
			if (original instanceof Einnahme e) {
				e.setSender(beteiligter);
				e.setKonto(konto);
			} else if (original instanceof Ausgabe a) {
				a.setEmpfaenger(beteiligter);
				a.setKonto(konto);	
			}
			
			if (altesKonto != konto) {
				altesKonto.getBuchungen().remove(original);
				konto.getBuchungen().add(original);
			}
			// CSV-Datei aktualisieren
			Datenstroeme.kontoBuchungenNeuSpeichern(konto);
			if (altesKonto != konto) {
				Datenstroeme.kontoBuchungenNeuSpeichern(altesKonto);
			}
			Datenstroeme.kontenNeuSpeichern();
			System.out.println("Buchung bearbeitet: " + original);
		}
		
		
		public static void umbuchungBearbeiten(Buchung original,String beschreibung, Konto konto, double betrag, LocalDate datum) {
			System.out.println("Starte mit Bearbeiten der Umbuchung, Originalbuchung: " + original);
			// Beide Buchungen der Umbuchung finden
			List<Buchung> buchungen = findeBuchungenZuTransferID(original.getTransferID());
			if (buchungen.size() != 2) {
				System.out.println("Fehler: Umbuchung nicht gefunden oder unvollständig.");
				return;
			}
			Buchung buchung1 = buchungen.get(0);
			Buchung buchung2 = buchungen.get(1);
			Buchung buchungOriginal;
			Buchung buchungGegenpart;
			
			// Originalbuchung und Gegenpart identifizieren
			if (buchung1 == original) {
				buchungOriginal = buchung1;
				buchungGegenpart = buchung2;
			} else if (buchung2 == original) {
				buchungOriginal = buchung2;
				buchungGegenpart = buchung1;
			} else {
				System.out.println("Fehler: Originalbuchung nicht in der Umbuchung gefunden.");
				return;
			}
			
			String beteiligter1;
			String beteiligter2;
			
			// Beteiligte aktualisieren
			beteiligter1 = buchungOriginal instanceof Einnahme ? buchungOriginal.getSender() : buchungOriginal.getEmpfaenger();
			buchungBearbeiten(buchungOriginal, betrag, buchungOriginal.getKategorie(), beschreibung, konto, beteiligter1, datum);
			// Wenn das Konto geändert wurde, Beteiligten der Gegenpart-Buchung anpassen
			Konto kontoAlt = buchungOriginal.getKonto();
			if (kontoAlt != konto) {
					beteiligter2 = konto.getKontoName() + "(" + konto.getKreditinstitut() + ")";
			}
			else  beteiligter2 = buchungGegenpart instanceof Einnahme ? buchungGegenpart.getSender() : buchungGegenpart.getEmpfaenger();
			
			buchungBearbeiten(buchungGegenpart, betrag, buchungGegenpart.getKategorie(), beschreibung, buchungGegenpart.getKonto(), beteiligter2, datum);
			System.out.println("Umbuchung bearbeitet: " + buchungOriginal + " und " + buchungGegenpart);
		}
		
		// Alle Buchungen mit einer bestimmten transferId finden 
		public static List<Buchung> findeBuchungenZuTransferID(String transferId) {
			return Konto.getAlleBuchungen().stream().filter(b -> transferId.equals(b.getTransferID())).toList();
		}

		public static double berechneSummeEinnahmen(FilteredList<Buchung> gefilterteBuchungsListe) {
			return gefilterteBuchungsListe.stream().filter(buchung -> buchung instanceof Einnahme)
					.mapToDouble(Buchung::getBetrag).sum();
		}
		
		public static double berechneSummeAusgaben(FilteredList<Buchung> gefilterteBuchungsListe) {
			return gefilterteBuchungsListe.stream().filter(buchung -> buchung instanceof Ausgabe)
					.mapToDouble(Buchung::getBetrag).sum();
		}

		public static List<Map.Entry<String, Double>> bestimmeAusgabenNachKategorien(FilteredList<Buchung> gefilterteBuchungsListe) {
			Map<String, Double> kategorienSumme = new java.util.HashMap<>();
			for (Buchung buchung : gefilterteBuchungsListe) {
				if (buchung instanceof Ausgabe) {
					String kategorie = buchung.getKategorie();
					double betrag = buchung.getBetrag();
					kategorienSumme.put(kategorie, kategorienSumme.getOrDefault(kategorie, 0.0) + betrag);
				}
			}
			List<Map.Entry<String, Double>> kategorienListe = new ArrayList<>(kategorienSumme.entrySet());
			kategorienListe.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
			
			return kategorienListe;
		}
		
		
		


}
