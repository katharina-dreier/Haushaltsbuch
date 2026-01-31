package org.meinprojekt.haushalt.core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.BuchungsDaten;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;

class WiederkehrendeZahlungenServiceTest {

	@Test
	void naechstesBuchungsdatumBerechnen_Monatlich() {
		
		LocalDate startDatum = LocalDate.of(2024, 1, 15);
		LocalDate erwartetesDatum = LocalDate.of(2024, 2, 15);

		LocalDate result = WiederkehrendeZahlungenService.naechstesBuchungsDatumBerechnen(startDatum,
				Haeufigkeit.MONATLICH);

		assertEquals(erwartetesDatum, result);
	}
	
	@Test
	void naechstesBuchungsdatumBerechnen_Quartalsweise() {

		LocalDate startDatum = LocalDate.of(2024, 1, 15);
		LocalDate erwartetesDatum = LocalDate.of(2024, 4, 15);

		LocalDate result = WiederkehrendeZahlungenService.naechstesBuchungsDatumBerechnen(startDatum,
				Haeufigkeit.QUARTALSWEISE);

		assertEquals(erwartetesDatum, result);
	}
	
	@Test
	void naechstesBuchungsdatumBerechnen_Jaehrlich() {

		LocalDate startDatum = LocalDate.of(2024, 1, 15);
		LocalDate erwartetesDatum = LocalDate.of(2025, 1, 15);

		LocalDate result = WiederkehrendeZahlungenService.naechstesBuchungsDatumBerechnen(startDatum,
				Haeufigkeit.JAEHRLICH);

		assertEquals(erwartetesDatum, result);
	}
	
	@Test
	void bestimmeStatusSymbol_nochFällig() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now();
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		String symbol = WiederkehrendeZahlungenService.bestimmeStatusSysmbolWKZ(zahlung);
		assertEquals("\u23F3", symbol);
	}
	
	@Test
	void bestimmeStatusSymbol_überfällig() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now().minusDays(1);
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		String symbol = WiederkehrendeZahlungenService.bestimmeStatusSysmbolWKZ(zahlung);
		assertEquals("\u26A0", symbol);
	}
	
	@Test
	void bestimmeStatusSymbol_noch_nicht_faellig() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now().plusMonths(1);
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		String symbol = WiederkehrendeZahlungenService.bestimmeStatusSysmbolWKZ(zahlung);
		assertEquals("\u2705", symbol);
	}
	
	@Test
	void isNochFaellig_true() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now();
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		assertTrue(WiederkehrendeZahlungenService.isNochFaellig(zahlung));
	}
	
	@Test
	void isNochFaellig_false() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now().plusMonths(1);
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		assertFalse(WiederkehrendeZahlungenService.isNochFaellig(zahlung));
	}
	
	@Test
	void isNochFaellig_überfällig() {
		WiederkehrendeZahlung zahlung = new WiederkehrendeZahlung();
		LocalDate naechsteZahlungAm = LocalDate.now().minusDays(1);
		zahlung.setNaechsteZahlungAm(naechsteZahlungAm);
		assertTrue(WiederkehrendeZahlungenService.isNochFaellig(zahlung));
	}
	
	@Test
	void getFormatiertesDatum_formatiert_korrekt() {
		LocalDate datum = LocalDate.of(2024, 6, 5);
		String formatiertesDatum = WiederkehrendeZahlungenService.getFormatiertesDatum(datum);
		assertEquals("05.06.2024", formatiertesDatum);
	}
	
	@Test
	void getFormatiertesDatum_null() {
		String formatiertesDatum = WiederkehrendeZahlungenService.getFormatiertesDatum(null);
		assertEquals("", formatiertesDatum);
	}

	@Test
	void berechnung_Einnahmen_und_Ausgaben_korrekt() {
        Konto konto = new Konto();
        Konto konto2 = new Konto();
        
        BuchungsDaten daten1 = BuchungsDaten.builder(100.0, "Test", LocalDate.now(), konto, Buchungstyp.EINNAHME)
			    .build();
        WiederkehrendeZahlung zahlung1 = new WiederkehrendeZahlung(daten1, Haeufigkeit.MONATLICH);
        
        BuchungsDaten daten2 = BuchungsDaten.builder(200.0, "Test", LocalDate.now().plusMonths(1), konto, Buchungstyp.EINNAHME)
			    .build();
        WiederkehrendeZahlung zahlung2 = new WiederkehrendeZahlung(daten2, Haeufigkeit.MONATLICH);
       
        BuchungsDaten daten3 = BuchungsDaten.builder(150.0, "Test", LocalDate.now().plusMonths(1), konto, Buchungstyp.AUSGABE)
			    .build();
        WiederkehrendeZahlung zahlung3 = new WiederkehrendeZahlung(daten3, Haeufigkeit.MONATLICH);
     
        BuchungsDaten daten4 = BuchungsDaten.builder(50.0, "Test", LocalDate.now(), konto, Buchungstyp.AUSGABE)
			    .build();
        WiederkehrendeZahlung zahlung4 = new WiederkehrendeZahlung(daten4, Haeufigkeit.MONATLICH);
       
        
       List<WiederkehrendeZahlung> wkzListe = new ArrayList<>();
       wkzListe.add(zahlung1);
       wkzListe.add(zahlung2);
       wkzListe.add(zahlung3);
       wkzListe.add(zahlung4);
       konto.setWiederkehrendeZahlungen(wkzListe);
       konto2.setWiederkehrendeZahlungen(wkzListe);
       Map<Integer, Konto> kontenMap = new HashMap<>();
       kontenMap.put(1, konto);
       kontenMap.put(2, konto2);
       Konto.setKonten(kontenMap);
        double summeNochOffeneEinnahmenImAktuellenMonatKonto1 = WiederkehrendeZahlungenService.berechneNochOffeneEinnahmenImAktuellenMonat(konto);
        assertEquals(100.0, summeNochOffeneEinnahmenImAktuellenMonatKonto1);
        double summeNochOffeneFixkostenImAktuellenMonatKonto = WiederkehrendeZahlungenService.berechneNochOffeneFixkostenImAktuellenMonat(konto);
        assertEquals(-50.0, summeNochOffeneFixkostenImAktuellenMonatKonto);
        double summeNochOffeneEinnahmenImAktuellenMonat = WiederkehrendeZahlungenService.berechneNochOffeneEinnahmenImAktuellenMonat();
        assertEquals(200.0, summeNochOffeneEinnahmenImAktuellenMonat);
        double summeNochOffeneFixkostenImAktuellenMonat = WiederkehrendeZahlungenService.berechneNochOffeneFixkostenImAktuellenMonat();
        assertEquals(-100.0, summeNochOffeneFixkostenImAktuellenMonat);
        double summeNochOffeneWKZImAktuellenMonatKonto = WiederkehrendeZahlungenService.berechneNochOffeneWKZImAktuellenMonat(konto);
        assertEquals(-50.0, summeNochOffeneWKZImAktuellenMonatKonto);
        double summeNochOffeneWKZImAktuellenMonat = WiederkehrendeZahlungenService.berechneNochOffeneWKZImAktuellenMonat();
        assertEquals(-100.0, summeNochOffeneWKZImAktuellenMonat);
        double summeFixkostenImAktuellenMonatKonto = WiederkehrendeZahlungenService.berechneFixkostenImAktuellenMonat(konto);
        assertEquals(-150.0, summeFixkostenImAktuellenMonatKonto);
        double summeFixkostenImAktuellenMonat = WiederkehrendeZahlungenService.berechneFixkostenImAktuellenMonat();
        assertEquals(-300.0, summeFixkostenImAktuellenMonat);
	}
	
	

}
