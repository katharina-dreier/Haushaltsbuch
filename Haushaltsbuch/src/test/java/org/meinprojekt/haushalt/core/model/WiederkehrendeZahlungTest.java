package org.meinprojekt.haushalt.core.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;

class WiederkehrendeZahlungTest {

	@Test 
	void haeufigkeitFromString_valid() {
		assertEquals(Haeufigkeit.MONATLICH, Haeufigkeit.haeufigkeitFromString("Monatlich"));
		assertEquals(Haeufigkeit.QUARTALSWEISE, Haeufigkeit.haeufigkeitFromString("Quartalsweise"));
		assertEquals(Haeufigkeit.JAEHRLICH, Haeufigkeit.haeufigkeitFromString("Jährlich"));
	}
	
	@Test
	void haeufigkeitFromString_invalid() {
		assertThrows(IllegalArgumentException.class, () -> {
			Haeufigkeit.haeufigkeitFromString("Wöchentlich");
		});
	}
	
	@Test
	void toString_liefert_Korrekte_Strings() {
		assertEquals("Monatlich", Haeufigkeit.MONATLICH.toString());
		assertEquals("Quartalsweise", Haeufigkeit.QUARTALSWEISE.toString());
		assertEquals("Jährlich", Haeufigkeit.JAEHRLICH.toString());
	}
	
	@Test
	void getKontoanzeige_nullKonto_liefert_leeren_String() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setKonto(null);
		assertEquals("", wz.getKontoAnzeige());
	}
	
	@Test
	void getKontoanzeige_validKonto_liefert_Kontoname() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		Konto konto = new Konto();
		konto.setKontoName("Girokonto");
		konto.setKreditinstitut("Meine Bank");
		wz.setKonto(konto);
		assertEquals("Girokonto (Meine Bank)", wz.getKontoAnzeige());
	}
	
	@Test
	void get_und_set_Haeufigkeit() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setHaeufigkeit(Haeufigkeit.JAEHRLICH);
		assertEquals(Haeufigkeit.JAEHRLICH, wz.getHaeufigkeit());
	}
	
	@Test
	void get_und_set_Kategorie() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setKategorie("Miete");
		assertEquals("Miete", wz.getKategorie());
	}
	
	@Test
	void get_und_set_Betrag() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setBetrag(1500.75);
		assertEquals(1500.75, wz.getBetrag());
	}
	
	@Test
	void get_und_set_Beschreibung() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setBeschreibung("Monatliche Miete für Wohnung");
		assertEquals("Monatliche Miete für Wohnung", wz.getBeschreibung());
	}
	
	@Test
	void get_und_set_Empfaenger() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setEmpfaenger("Vermieter GmbH");
		assertEquals("Vermieter GmbH", wz.getEmpfaenger());
	}
	
	@Test
	void get_und_set_Sender() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		wz.setSender("Max Mustermann");
		assertEquals("Max Mustermann", wz.getSender());
	}
	
	@Test
	void get_und_set_Konto() {
		WiederkehrendeZahlung wz = new WiederkehrendeZahlung();
		Konto konto = new Konto();
		konto.setKontoName("Sparkonto");
		wz.setKonto(konto);
		assertEquals(konto, wz.getKonto());
	}
	
	@Test
	void konstruktor_mit_Parametern_setzt_Attribute() {
		Konto konto = new Konto();
		konto.setKontoName("Girokonto");
		LocalDate datum = LocalDate.of(2024, 1, 15);
		LocalDate letzteZahlung = LocalDate.of(2023, 12, 15);

		WiederkehrendeZahlung wz = new WiederkehrendeZahlung(datum, Haeufigkeit.MONATLICH, "Ausgabe", "Miete", "Monatliche Miete", "Vermieter GmbH", "Max Mustermann", 500.0, konto, letzteZahlung);

		assertEquals(Haeufigkeit.MONATLICH, wz.getHaeufigkeit());
		assertEquals(500.0, wz.getBetrag());
		assertEquals("Miete", wz.getKategorie());
		assertEquals("Monatliche Miete", wz.getBeschreibung());
		assertEquals(konto, wz.getKonto());
		assertEquals("Vermieter GmbH", wz.getEmpfaenger());
		assertEquals("Max Mustermann", wz.getSender());
	}
}
