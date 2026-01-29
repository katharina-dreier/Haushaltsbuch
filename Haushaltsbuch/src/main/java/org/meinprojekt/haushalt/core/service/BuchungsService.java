package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

import javafx.collections.transformation.FilteredList;

public final class BuchungsService {
	private static final Logger logger = Logger.getLogger(BuchungsService.class.getName());
	
	private BuchungsService() {
	    throw new IllegalStateException("Utility class");
	  }
	
	//Notwendige Daten einlesen und Einnahme tätigen
		
		public static void einnahmeTaetigen(Double betragEin, String kategorie, String beschreibung, Konto konto, String sender,  LocalDate datum, String transferID, boolean isUmbuchung) {

			logger.info("Einnahme buchen gestartet");
			BuchungsDaten daten = BuchungsDaten
				    .builder(betragEin, kategorie, datum, konto, Buchungstyp.EINNAHME)
				    .beschreibung(beschreibung)
				    .gegenpartei(sender)
				    .transfer(transferID, isUmbuchung)   // optional
				    .build();
			Buchung einnahme = new Buchung(daten);
			einnahme.kategorieHinzufuegen(kategorie);
			konto.addBuchung(einnahme);
			Datenstroeme.buchungHinzufuegen(einnahme);
			logger.info("Einnahme auf Konto " + konto.getKontoName() + " getaetigt.");
		}
		
		
		public static void ausgabeTaetigen(Double betrag, String kategorie, String beschreibung, Konto konto, String empfaenger, LocalDate datum, String transferID, boolean isUmbuchung) {
			logger.info("Ausgabe buchen gestartet");

			BuchungsDaten daten = BuchungsDaten
				    .builder(betrag, kategorie, datum, konto, Buchungstyp.AUSGABE)
				    .beschreibung(beschreibung)
				    .gegenpartei(empfaenger)
				    .transfer(transferID, isUmbuchung)   // optional
				    .build();
			Buchung ausgabe = new Buchung(daten);
			ausgabe.kategorieHinzufuegen(kategorie);
			konto.addBuchung(ausgabe);
			Datenstroeme.buchungHinzufuegen(ausgabe);
			logger.info("Ausgabe auf Konto " + konto.getKontoName() + " getaetigt.");

		}
		
		//Umbuchung tätigen
		
		
		public static void umbuchungTaetigen(Double betrag, String beschreibung, Konto quell, Konto ziel, LocalDate datum) {
			logger.info("Umbuchung buchen gestartet");

			String transferId = UUID.randomUUID().toString();

		    String quellLabel = quell.kontoLabel();
		    String zielLabel  = ziel.kontoLabel();

		    // 1) Ausgabe vom Quellkonto
		    BuchungsDaten ausgabeDaten = BuchungsDaten
		            .builder(betrag, "Umbuchung", datum, quell, Buchungstyp.AUSGABE)
		            .beschreibung(beschreibung)
		            .gegenpartei(zielLabel)
		            .transfer(transferId, true)
		            .build();

		    Buchung ausgabe = new Buchung(ausgabeDaten);

		    // 2) Einnahme im Zielkonto
		    BuchungsDaten einnahmeDaten = BuchungsDaten
		            .builder(betrag, "Umbuchung", datum, ziel, Buchungstyp.EINNAHME)
		            .beschreibung(beschreibung)
		            .gegenpartei(quellLabel)
		            .transfer(transferId, true)
		            .build();

		    Buchung einnahme = new Buchung(einnahmeDaten);

		    // 3) verbuchen/speichern 
		    Datenstroeme.buchungHinzufuegen(ausgabe);
		    Datenstroeme.buchungHinzufuegen(einnahme);

		    quell.addBuchung(ausgabe);
		    ziel.addBuchung(einnahme);

			logger.info("Umbuchung wurde getätigt.");
		}
		
		public static void loescheBuchung(Buchung buchung) {
			logger.info("Starte mit Löschen von Buchung");
		    if (buchung.getIsUmbuchung()) {
		    	umbuchungLoeschen(buchung);

		    } else {
		        Konto k = buchung.getKonto();
		        if (k == null) {
		           
		            logger.info("Warnung: Buchung ohne Konto, Abbruch.");
		            return;
		        }
		        k.getBuchungen().remove(buchung);
		        Datenstroeme.kontoBuchungenNeuSpeichern(k);
		      logger.info("Buchung gelöscht");
		    }
		    // Kontenübersicht neu schreiben
		    Datenstroeme.kontenNeuSpeichern();
		}


		private static void umbuchungLoeschen(Buchung buchung) {
			String transferId = buchung.getTransferID();
			List<Buchung> buchungen = findeBuchungenZuTransferID(transferId);
			if (buchungen.size() != 2) {
				logger.info("Fehler: Umbuchung nicht gefunden oder unvollständig.");
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
				logger.info("Fehler: Originalbuchung nicht in der Umbuchung gefunden.");
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
			String beteiligterGegenpart = buchungGegenpart.getBuchungstyp()== Buchungstyp.EINNAHME ? "gelöschtes Konto: " + buchungGegenpart.getSender() : "gelöschtes Konto: " + buchungGegenpart.getEmpfaenger();
			if (buchungGegenpart.getBuchungstyp()== Buchungstyp.EINNAHME) {
				buchungGegenpart.setSender(beteiligterGegenpart);
			} else if (buchungGegenpart.getBuchungstyp()== Buchungstyp.AUSGABE) {
				buchungGegenpart.setEmpfaenger(beteiligterGegenpart);
			             }
			// CSV neu schreiben (beide)
			if (konto2 != null) Datenstroeme.kontoBuchungenNeuSpeichern(konto2);
			
			logger.info("Buchung gelöscht und Gegenbuchung geändert");
		}

		public static void buchungBearbeiten(Buchung original, double betrag, String kat, String beschreibung, Konto konto, String beteiligter,
				LocalDate datum) {
			logger.info("Starte mit Bearbeiten der Buchung");
			Konto altesKonto = original.getKonto();

			// Neue Buchungsdaten setzen
			original.setBetrag(betrag);
			original.setKategorie(kat);
			original.setBeschreibung(beschreibung);
			original.setBuchungsDatum(datum);
			
			if (original.getBuchungstyp()== Buchungstyp.EINNAHME) {
				original.setSender(beteiligter);
				original.setKonto(konto);
			} else if (original.getBuchungstyp()== Buchungstyp.AUSGABE) {
				original.setEmpfaenger(beteiligter);
				original.setKonto(konto);	
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
			logger.info("Buchung bearbeitet");
		}
		
		
		public static void umbuchungBearbeiten(Buchung original,String beschreibung, Konto konto, double betrag, LocalDate datum) {
			logger.info("Starte mit Bearbeiten der Umbuchung");
			// Beide Buchungen der Umbuchung finden
			List<Buchung> buchungen = findeBuchungenZuTransferID(original.getTransferID());
			if (buchungen.size() != 2) {
				logger.info("Fehler: Umbuchung nicht gefunden oder unvollständig.");
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
				logger.info("Fehler: Originalbuchung nicht in der Umbuchung gefunden.");
				return;
			}
			
			String beteiligter1;
			String beteiligter2;
			
			// Beteiligte aktualisieren
			beteiligter1 = buchungOriginal.getBuchungstyp()== Buchungstyp.EINNAHME ? buchungOriginal.getSender() : buchungOriginal.getEmpfaenger();
			buchungBearbeiten(buchungOriginal, betrag, buchungOriginal.getKategorie(), beschreibung, konto, beteiligter1, datum);
			// Wenn das Konto geändert wurde, Beteiligten der Gegenpart-Buchung anpassen
			Konto kontoAlt = buchungOriginal.getKonto();
			if (kontoAlt != konto) {
					beteiligter2 = konto.getKontoName() + "(" + konto.getKreditinstitut() + ")";
			}
			else  beteiligter2 = buchungGegenpart.getBuchungstyp()== Buchungstyp.EINNAHME ? buchungGegenpart.getSender() : buchungGegenpart.getEmpfaenger();
			
			buchungBearbeiten(buchungGegenpart, betrag, buchungGegenpart.getKategorie(), beschreibung, buchungGegenpart.getKonto(), beteiligter2, datum);
			logger.info("Umbuchung bearbeitet");
		}
		
		// Alle Buchungen mit einer bestimmten transferId finden 
		public static List<Buchung> findeBuchungenZuTransferID(String transferId) {
			return Konto.getAlleBuchungen().stream().filter(b -> transferId.equals(b.getTransferID())).toList();
		}

		public static double berechneSummeEinnahmen(FilteredList<Buchung> gefilterteBuchungsListe) {
			return gefilterteBuchungsListe.stream().filter(buchung -> buchung.getBuchungstyp()== Buchungstyp.EINNAHME)
					.mapToDouble(Buchung::getBetrag).sum();
		}
		
		public static double berechneSummeAusgaben(FilteredList<Buchung> gefilterteBuchungsListe) {
			return gefilterteBuchungsListe.stream().filter(buchung -> buchung.getBuchungstyp()== Buchungstyp.AUSGABE)
					.mapToDouble(Buchung::getBetrag).sum();
		}

		public static List<Map.Entry<String, Double>> bestimmeAusgabenNachKategorien(FilteredList<Buchung> gefilterteBuchungsListe) {
			Map<String, Double> kategorienSumme = new java.util.HashMap<>();
			for (Buchung buchung : gefilterteBuchungsListe) {
				if (buchung.getBuchungstyp()== Buchungstyp.AUSGABE) {
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
