package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import org.meinprojekt.haushalt.core.model.BuchungsDaten;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

public class WiederkehrendeZahlungenService {

	private static final Logger logger = Logger.getLogger(WiederkehrendeZahlungenService.class.getName());
	
	private WiederkehrendeZahlungenService() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static void wiederkehrendeZahlungAnlegen(LocalDate datum, Haeufigkeit haeufigkeit, Buchungstyp typ,
			String kategorie, String beschreibung, String empfaenger, String sender, double betrag, Konto konto) {
		String gegenpartei = "";
		switch (typ) {
		case Buchungstyp.EINNAHME:  gegenpartei = sender; break;
		case Buchungstyp.AUSGABE: gegenpartei = empfaenger; break;
		case Buchungstyp.UMBUCHUNG: gegenpartei = "nicht bekannt"; break;
		}
		BuchungsDaten daten = BuchungsDaten
			    .builder(betrag, kategorie, datum, konto, typ)
			    .beschreibung(beschreibung)
			    .gegenpartei(gegenpartei)
			    .build();
		WiederkehrendeZahlung wkz = new WiederkehrendeZahlung(daten, haeufigkeit);
		konto.addWiederkehrendeZahlung(wkz);
		Datenstroeme.wiederkehrendeBuchungHinzufuegen(wkz);

	}
	
	public static void wiederkehrendeZahlungAnlegen(BuchungsDaten daten, Haeufigkeit haeufigkeit) {
		
		WiederkehrendeZahlung wkz = new WiederkehrendeZahlung(daten, haeufigkeit);
		daten.getKonto().addWiederkehrendeZahlung(wkz);
		Datenstroeme.wiederkehrendeBuchungHinzufuegen(wkz);

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
	
	public static void wiederkehrendeZahlungBearbeiten(WiederkehrendeZahlung zahlung, BuchungsDaten daten, Haeufigkeit haeufigkeit) {

		zahlung.setNaechsteZahlungAm(daten.getBuchungsdatum());
		zahlung.setHaeufigkeit(haeufigkeit);
		zahlung.setBuchungstyp(daten.getTyp());
		zahlung.setKategorie(daten.getKategorie());
		zahlung.setBeschreibung(daten.getBeschreibung());
		switch (daten.getTyp()) {
		case Buchungstyp.EINNAHME:  zahlung.setEmpfaenger(daten.getKonto().getInhaber()); zahlung.setSender(daten.getGegenpartei()); break;
		case Buchungstyp.AUSGABE: zahlung.setEmpfaenger(daten.getGegenpartei()); zahlung.setSender(daten.getKonto().getInhaber()); break;
		case Buchungstyp.UMBUCHUNG: System.out.println("nicht bekannt"); break;
		}
		zahlung.setBetrag(daten.getBetrag());

		Datenstroeme.kontoWiederkehrendeZahlungenNeuSpeichern(zahlung.getKonto());

	}

	public static void naechstesZahlDatumAktualisieren(WiederkehrendeZahlung zahlung) {
		LocalDate aktuellesDatum = zahlung.getNaechsteZahlungAm();
		Haeufigkeit haeufigkeit = zahlung.getHaeufigkeit();
		LocalDate naechstesDatum = naechstesBuchungsDatumBerechnen(aktuellesDatum, haeufigkeit);
		zahlung.setLetzteZahlungAm(aktuellesDatum);
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
		Buchungstyp typ = zahlung.getBuchungstyp();
		switch (typ) {
		case EINNAHME:
			BuchungsService.einnahmeTaetigen(zahlung.getBetrag(), zahlung.getKategorie(), zahlung.getBeschreibung(),
					zahlung.getKonto(), zahlung.getSender(), zahlung.getNaechsteZahlungAm(), "", false); break;

		case AUSGABE:
			BuchungsService.ausgabeTaetigen(zahlung.getBetrag(), zahlung.getKategorie(), zahlung.getBeschreibung(),
					zahlung.getKonto(), zahlung.getEmpfaenger(), zahlung.getNaechsteZahlungAm(), "", false); break;

		default:
			logger.info("Fehler: Unbekannte Buchungsart bei wiederkehrender Zahlung");

		}
		naechstesZahlDatumAktualisieren(zahlung);
	}

	public static String bestimmeStatusSysmbolWKZ(WiederkehrendeZahlung zahlung) {

		if (isNochFaellig(zahlung)) {
			if (zahlung.getNaechsteZahlungAm().isBefore(LocalDate.now())) return"\u26A0"; // Warn-Symbol
			else return "\u23F3"; // Sanduhr-Symbol
		} else {
			return "\u2705"; // Haken-Symbol
		}
	}

	public static boolean isNochFaellig(WiederkehrendeZahlung zahlung) {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
		return !zahlungsMonat.isAfter(aktuellerMonat);
		

	}

	public static List<WiederkehrendeZahlung> getAlleWiederkehrendeZahlungen() {
		List<WiederkehrendeZahlung> alleZahlungen = new java.util.ArrayList<>();
		for (Konto konto : Konto.getAlleKonten()) {
			for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
				alleZahlungen.add(zahlung);
			}
		}
		return alleZahlungen;
	}
	
	public static double berechneNochOffeneWKZImAktuellenMonat() {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		double summe = 0.0;
		for (WiederkehrendeZahlung zahlung : getAlleWiederkehrendeZahlungen()) {
			LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			Buchungstyp typ = zahlung.getBuchungstyp();
			if (!zahlungsMonat.isAfter(aktuellerMonat)) {
				switch (typ) {
				case EINNAHME:
					summe -= zahlung.getBetrag();
					break;
				case AUSGABE:
					summe += zahlung.getBetrag();
					break;
				default: logger.info("fehlerhafte Buchungsart");
				}
			}
		}
		return summe;
	}
	
	

	public static double berechneNochOffeneWKZImAktuellenMonat(Konto konto) {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		double summe = 0.0;
		for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
			LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			Buchungstyp typ = zahlung.getBuchungstyp();
			if (!zahlungsMonat.isAfter(aktuellerMonat)) {
				switch (typ) {
				case EINNAHME:
					summe -= zahlung.getBetrag();
					break;
				case AUSGABE:
					summe += zahlung.getBetrag();
					break;
				default: logger.info("fehlerhafte Buchungsart");
				}
			}
		}
		return summe;
	}

	public static double berechneFixkostenImAktuellenMonat() {
		double gesamtFixkosten = 0.0;
		for (Konto konto : Konto.getAlleKonten()) {
			double summeKonto = berechneFixkostenImAktuellenMonat(konto);
			gesamtFixkosten += summeKonto;
		}
		return gesamtFixkosten;
	}
	
	public static double berechneFixkostenImAktuellenMonat(Konto konto) {
		double gesamtFixkosten = 0.0;
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
			LocalDate monatNaechsteZahlung = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			if (zahlung.getLetzteZahlungAm() == null) {
				zahlung.setLetzteZahlungAm(monatNaechsteZahlung);
			}
			LocalDate monatLetzteZahlung = zahlung.getLetzteZahlungAm().withDayOfMonth(1);
			Buchungstyp typ = zahlung.getBuchungstyp();
			if (!monatNaechsteZahlung.equals(aktuellerMonat) || !monatLetzteZahlung.equals(aktuellerMonat) && typ == Buchungstyp.AUSGABE) {
				
					gesamtFixkosten += zahlung.getBetrag();
				}
			
		}
		return gesamtFixkosten;
	}

	public static double berechneNochOffeneFixkostenImAktuellenMonat() {
		double gesamtFixkosten = 0.0;		
		for (Konto konto : Konto.getAlleKonten()) {
			double summeKonto = berechneNochOffeneFixkostenImAktuellenMonat(konto);
			gesamtFixkosten += summeKonto;
				}
		return gesamtFixkosten;
	}
	
	public static double berechneNochOffeneFixkostenImAktuellenMonat(Konto konto) {
		double gesamtFixkosten = 0.0;
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		List<WiederkehrendeZahlung> alleZahlungenKonto = konto.getWiederkehrendeZahlungen();
		for (WiederkehrendeZahlung zahlung : alleZahlungenKonto) {
			LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			Buchungstyp typ = zahlung.getBuchungstyp();
			if (!zahlungsMonat.isAfter(aktuellerMonat) && typ == Buchungstyp.AUSGABE) {
				
					gesamtFixkosten += zahlung.getBetrag();
				
			}
		}
		return gesamtFixkosten;
	}

	public static String getFormatiertesDatum(LocalDate datum) {
	    if (datum != null) {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    
	    return datum.format(formatter);
	} else {
		return "";
	}
	}

	public static double berechneNochOffeneEinnahmenImAktuellenMonat() {
		double summe = 0.0;
		for (Konto konto : Konto.getAlleKonten()) {
			double summeKonto = berechneNochOffeneEinnahmenImAktuellenMonat(konto);
			summe += summeKonto;
				}
		return summe;
	}
	
	public static double berechneNochOffeneEinnahmenImAktuellenMonat(Konto konto) {
		LocalDate aktuellerMonat = LocalDate.now().withDayOfMonth(1);
		double summe = 0.0;
		for (WiederkehrendeZahlung zahlung : konto.getWiederkehrendeZahlungen()) {
			LocalDate zahlungsMonat = zahlung.getNaechsteZahlungAm().withDayOfMonth(1);
			Buchungstyp typ = zahlung.getBuchungstyp();
			if (!zahlungsMonat.isAfter(aktuellerMonat) && typ == Buchungstyp.EINNAHME) {
				
					summe += zahlung.getBetrag();
				
			}
		}
		return summe;
	}


}
