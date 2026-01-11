package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

public class KontoService {
	
	//Aus Dialogfenster erhaltene Daten verwenden um Konto zu erstellen
		public static void kontoErstellen(String kontoName, String inhaber, double kontostand, String kreditinstitut) {
			System.out.println("Kontoerstellung gestartet mit folgenden Daten: " + kontoName + ", " + inhaber + ", " + kontostand + ", " + kreditinstitut);
			Konto konto = new Konto(kontoName, inhaber, kontostand, kreditinstitut);
			Konto.getKonten().put(konto.getKontonummer(), konto);
			BuchungsService.einnahmeTätigen(kontostand, "Kontoerstellung", "Initiale Buchung zu diesem Konto", konto, inhaber, LocalDate.now() , "", false);
			System.out.println("Konto wurde erstellt: " + konto);
		}
		
		

		public static void loescheKonto(Konto k) {
			System.out.println("Starte mit Löschen von Konto: " + k);
			for (Buchung b : new ArrayList<>(k.buchungen)) {
				BuchungsService.loescheBuchung(b);
				}
			Datenstroeme.kontoLoeschen(k);
			Konto.getKonten().remove(k.getKontonummer());
			Datenstroeme.kontenNeuSpeichern();
			System.out.println("Konto wurde gelöscht: " + k);
		}
		
		public static void kontoBearbeiten(Konto kontoAlt, double saldo, String inhaber) {
			System.out.println("Konto wird bearbeitet: " + kontoAlt + "Saldo: " + saldo + ", Inhaber: " + inhaber);
			kontoAlt.setKontostandBeiErstellung(saldo);
			System.out.println("Kontostand bei Erstellung gesetzt auf: " + kontoAlt.getKontostandBeiErstellung());
			kontoAlt.setInhaber(inhaber);
			for (Buchung b : new ArrayList<>(kontoAlt.getBuchungen())) {
				if (b.getKategorie().contains("Kontoerstellung")) {
					b.setBetrag(saldo);
					System.out.println("Kontoerstellungsbuchung angepasst: " + b);
				}
			}
			System.out.println("Konto nach Bearbeitung: " + kontoAlt);
			Datenstroeme.kontoBuchungenNeuSpeichern(kontoAlt);
			Datenstroeme.kontenNeuSpeichern();
			System.out.println("Datenströme wurden aktualisiert. Konto: " + kontoAlt);
			System.out.println("Konto wurde erfoglreich bearbeitet: " + kontoAlt);
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