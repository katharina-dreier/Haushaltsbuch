package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;

import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

public class WiederkehrendeZahlungenService {
	
	public static void wiederkehrendeZahlungAnlegen(LocalDate datum, Haeufigkeit haeufigkeit, String buchungsart, String kategorie, 
			String beschreibung, String empfaenger, String sender, double betrag, Konto konto) {
		
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung(datum, haeufigkeit, buchungsart, kategorie, beschreibung, empfaenger, sender, betrag, konto);
		konto.wiederkehrendeZahlungen.add(zahlung);
		Datenstroeme.wiederkehrendeBuchungHinzufuegen(zahlung);
		
	}
	
	public static void wiederkehrendeZahlungBearbeiten(WiederkehrendeZahlung zahlung, LocalDate datum,
			Haeufigkeit haeufigkeit, String buchungsart, String kategorie, String beschreibung, String empfaenger,
			String sender, double betrag) {

		zahlung.setNaechsteZahlungAm(datum);
		zahlung.setHaeufigkeit(haeufigkeit);
		zahlung.setBuchungsart(buchungsart);
		zahlung.setKategorie(kategorie);
		zahlung.setBeschreibung(beschreibung);
		zahlung.setEmpfaenger(empfaenger);
		zahlung.setSender(sender);
		zahlung.setBetrag(betrag);

		Datenstroeme.kontoWiederkehrendeZahlungenNeuSpeichern(zahlung.getKonto());

	}
	
	public static void naechstesZahlDatumAktualisieren(WiederkehrendeZahlung zahlung) {
		LocalDate aktuellesDatum = zahlung.getNaechsteZahlungAm();
		Haeufigkeit haeufigkeit = zahlung.getHaeufigkeit();
		LocalDate naechstesDatum = naechstesBuchungsDatumBerechnen(aktuellesDatum, haeufigkeit);
		zahlung.setNaechsteZahlungAm(naechstesDatum);
		Datenstroeme.kontoWiederkehrendeZahlungenNeuSpeichern(zahlung.getKonto());
		
	}

	public static LocalDate naechstesBuchungsDatumBerechnen(LocalDate datum, Haeufigkeit haeufigkeit) {
		LocalDate naechsteZahlungAm = datum;
		
		switch (haeufigkeit) {
		case MONATLICH:
			naechsteZahlungAm = datum.plusMonths(1);
			break;
		case QUARTALSWEISE:
			naechsteZahlungAm = datum.plusMonths(3);
			break;
		case JAEHRLICH:
			naechsteZahlungAm = datum.plusYears(1);
			break;
		}
		return naechsteZahlungAm;
	}
	
	public static void wiederkehrendeZahlungAusfuehren(WiederkehrendeZahlung zahlung) {
		String buchungsart = zahlung.getBuchungsart();
		switch (buchungsart) {
		case "Einnahme":
			BuchungsService.einnahmeTätigen(zahlung.getBetrag(), zahlung.getKategorie(), zahlung.getBeschreibung(), zahlung.getKonto(), zahlung.getSender(), zahlung.getNaechsteZahlungAm(), "", false);
			
		case "Ausgabe":
			BuchungsService.ausgabeTätigen(zahlung.getBetrag(), zahlung.getKategorie(), zahlung.getBeschreibung(),
					zahlung.getKonto(), zahlung.getEmpfaenger(), zahlung.getNaechsteZahlungAm(), "", false);
			
		default: 
			System.out.println("Fehler: Unbekannte Buchungsart bei wiederkehrender Zahlung: " + buchungsart);
			
		}
		naechstesZahlDatumAktualisieren(zahlung);
	}
	
	public static String bestimmeStatusSysmbolWKZ(WiederkehrendeZahlung zahlung) {
		
		if (isNochFaellig(zahlung)) {
			return "\u23F3"; // Sanduhr-Symbol
		} else {
			return "\u2705"; // Haken-Symbol
		}
	}
	
	public static boolean isNochFaellig(WiederkehrendeZahlung zahlung) {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
		boolean nochFaellig = !zahlungsMonat.isAfter(aktuellerMonat);
		return nochFaellig;
		
	}
	
	public static void getAlleWiederkehrendeZahlungen() {
		for (Konto konto : Konto.getAlleKonten()) {
			for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
				System.out.println(zahlung);
			}
		}
	}

	public static double berechneNochOffeneWKZImAktuellenMonat(Konto konto) {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		double summe = 0.0;
		for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
			LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			String buchungsart = zahlung.getBuchungsart();
			if (!zahlungsMonat.isAfter(aktuellerMonat)) {
				switch (buchungsart) {
				case "Einnahme":
					summe -= zahlung.getBetrag();
					break;
				case "Ausgabe":
                    summe += zahlung.getBetrag();
                    break;
                }
			}
		}
		return summe;
	}

}
