package org.meinprojekt.haushalt.core.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ZeitraumTest {

	@Test
	void monatAusDatum_soll_ersten_und_letzten_tag_liefern() {
		
        LocalDate datum = LocalDate.of(2025, 2, 15);
        LocalDate erwarteterVon = datum.withDayOfMonth(1);
        LocalDate erwarteterBis = datum.withDayOfMonth(datum.lengthOfMonth());
        Zeitraum zeitraum = Zeitraum.monatAusDatum(datum);
        assertEquals(erwarteterVon, zeitraum.getVon());
        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void JahrAusDatum_soll_ersten_und_letzten_tag_liefern() {
		
        LocalDate datum = LocalDate.of(2025, 2, 15);
        LocalDate erwarteterVon = datum.withDayOfYear(1);
        LocalDate erwarteterBis = datum.withDayOfYear(datum.lengthOfYear());
        Zeitraum zeitraum = Zeitraum.jahrAusDatum(datum);
        assertEquals(erwarteterVon, zeitraum.getVon());
        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void aktuellerMonat_soll_ersten_und_letzten_tag_liefern() {
		
		LocalDate jetzt = LocalDate.now();
		LocalDate erwarteterVon = jetzt.withDayOfMonth(1);
		 LocalDate erwarteterBis = jetzt.withDayOfMonth(jetzt.lengthOfMonth());
	        Zeitraum zeitraum = Zeitraum.aktuellerMonat();
	        assertEquals(erwarteterVon, zeitraum.getVon());
	        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void vorigerMonat_soll_ersten_und_letzten_tag_des_vorigen_Monats_liefern() {
		LocalDate vorherigerMonat = LocalDate.now().minusMonths(1);
		LocalDate erwarteterVon = vorherigerMonat.withDayOfMonth(1);
		 LocalDate erwarteterBis = vorherigerMonat.withDayOfMonth(vorherigerMonat.lengthOfMonth());
	        Zeitraum zeitraum = Zeitraum.vorherigerMonat();
	        assertEquals(erwarteterVon, zeitraum.getVon());
	        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void aktuellesJahr_soll_ersten_und_letzten_tag_des_Jahres_liefern() {
		LocalDate jetzt = LocalDate.now();
		LocalDate erwarteterVon = jetzt.withDayOfYear(1);
		 LocalDate erwarteterBis = jetzt.withDayOfYear(jetzt.lengthOfYear());
	        Zeitraum zeitraum = Zeitraum.aktuellesJahr();
	        assertEquals(erwarteterVon, zeitraum.getVon());
	        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void vorherigesJahr_soll_ersten_und_letzten_tag_des_Jahres_liefern() {
		LocalDate vorherigesJahr = LocalDate.now().minusYears(1);
		LocalDate erwarteterVon = vorherigesJahr.withDayOfYear(1);
		 LocalDate erwarteterBis = vorherigesJahr.withDayOfYear(vorherigesJahr.lengthOfYear());
	        Zeitraum zeitraum = Zeitraum.vorherigesJahr();
	        assertEquals(erwarteterVon, zeitraum.getVon());
	        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void benutzerdefinierterZeitraum_soll_ersten_und_letzten_tag_liefern() {
		
        LocalDate datumStart = LocalDate.of(2025, 2, 15);
        LocalDate datumEnde = LocalDate.of(2025, 12, 15);
        LocalDate erwarteterVon = datumStart;
        LocalDate erwarteterBis = datumEnde;
        Zeitraum zeitraum = Zeitraum.benutzerdefinierterZeitraum(datumStart, datumEnde);
        assertEquals(erwarteterVon, zeitraum.getVon());
        assertEquals(erwarteterBis, zeitraum.getBis());
	}
	
	@Test
	void toString_liefert_richtigen_text() {
		LocalDate datum = LocalDate.of(2025, 2, 15);
		Zeitraum zeitraum = Zeitraum.monatAusDatum(datum);
		String erwarteterText = "Von: 2025-02-01 Bis: 2025-02-28";
		assertEquals(erwarteterText, zeitraum.toString());
	}

}
