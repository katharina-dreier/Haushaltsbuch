package org.meinprojekt.haushalt.core.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

public class KontoService {
	
	private static final Logger logger = Logger.getLogger(KontoService.class.getName());
	
	private KontoService() {
	    throw new IllegalStateException("Utility class");
	  }
	
	//Aus Dialogfenster erhaltene Daten verwenden um Konto zu erstellen
		public static void kontoErstellen(String kontoName, String inhaber, double kontostand, String kreditinstitut) {
			logger.info("Kontoerstellung gestartet");
			Konto konto = new Konto(kontoName, inhaber, kontostand, kreditinstitut);
			Konto.getKonten().put(konto.getKontonummer(), konto);
			BuchungsDaten daten = BuchungsDaten.builder(kontostand, "Kontoerstellung", LocalDate.now(), konto, Buchungstyp.EINNAHME)
					.beschreibung("Initiale Buchung zu diesem Konto").gegenpartei(inhaber)
					.build();
			BuchungsService.einnahmeTaetigen(daten);
			logger.log(Level.INFO, "Konto {} wurde erstellt", konto);
		}
		
		

		public static void loescheKonto(Konto k) throws IOException{
			logger.info("Starte mit Löschen von Konto");
			for (Buchung b : new ArrayList<>(k.getBuchungen())) {
				BuchungsService.loescheBuchung(b);
				}
			Datenstroeme.kontoLoeschen(k);
			Konto.getKonten().remove(k.getKontonummer());
			Datenstroeme.kontenNeuSpeichern();
			logger.info("Konto wurde gelöscht");
		}
		
		public static void kontoBearbeiten(Konto kontoAlt, double saldo, String inhaber) {
			logger.info("Konto wird bearbeitet");
			kontoAlt.setKontostandBeiErstellung(saldo);
			kontoAlt.setInhaber(inhaber);
			for (Buchung b : new ArrayList<>(kontoAlt.getBuchungen())) {
				if (b.getKategorie().contains("Kontoerstellung")) {
					b.setBetrag(saldo);
					logger.info("Kontoerstellungsbuchung angepasst");
				}
			}
			Datenstroeme.kontoBuchungenNeuSpeichern(kontoAlt);
			Datenstroeme.kontenNeuSpeichern();
			logger.info("Datenströme wurden aktualisiert.");
			logger.info("Konto wurde erfoglreich bearbeitet");
		}

		public static double getStartSaldoMonatsanfang(Konto konto) {
			double saldo = konto.getKontostand();
			LocalDate monatsAnfang = LocalDate.now().withDayOfMonth(1);
			for (Buchung b : konto.getBuchungen()) {
				LocalDate buchungsDatumMonat = b.getBuchungsDatum().withDayOfMonth(1);
				if (buchungsDatumMonat.equals(monatsAnfang)) {
					if (b.getBuchungsart().equals("Einnahme")) {
						saldo -= b.getBetrag();
					} else if (b.getBuchungsart().equals("Ausgabe")) {
						saldo += b.getBetrag();
					}
				}
			}
			return saldo;
		}
		
		public static double getStartSaldoMonatsanfang() {
			double saldo = 0.0;
			for (Konto konto : Konto.getAlleKonten()) {
				saldo += getStartSaldoMonatsanfang(konto);
			}
			return saldo;
		}

		}