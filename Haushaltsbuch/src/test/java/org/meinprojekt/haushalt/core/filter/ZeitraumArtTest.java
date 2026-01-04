package org.meinprojekt.haushalt.core.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ZeitraumArtTest {

	@Test
    void zeitraumAusArt_soll_exception_werfen_fuer_alleZeiten() {
        assertThrows(IllegalArgumentException.class,
                () -> ZeitraumArt.zeitraumAusArt(ZeitraumArt.ALLE_ZEITEN));
    }

    @Test
    void zeitraumAusArt_soll_exception_werfen_fuer_benutzerdefiniert() {
        assertThrows(IllegalArgumentException.class,
                () -> ZeitraumArt.zeitraumAusArt(ZeitraumArt.BENUTZERDEFINIERT));
    }
    
    @Test
    void toString_soll_label_zurueckgeben() {
        assertEquals("Aktueller Monat", ZeitraumArt.AKTUELLER_MONAT.toString());
        assertEquals("Vorheriger Monat", ZeitraumArt.VORHERIGER_MONAT.toString());
        assertEquals("Aktuelles Jahr", ZeitraumArt.AKTUELLES_JAHR.toString());
        assertEquals("Vorheriges Jahr", ZeitraumArt.VORHERIGES_JAHR.toString());
        assertEquals("Gesamter Zeitraum", ZeitraumArt.ALLE_ZEITEN.toString());
        assertEquals("Benutzerdefinierter Zeitraum", ZeitraumArt.BENUTZERDEFINIERT.toString());
    }
    
    @Test
	void zeitraumAusArt_soll_zeitraum_fuer_aktuellerMonat_zurueckgeben() {
		Zeitraum zeitraum = ZeitraumArt.zeitraumAusArt(ZeitraumArt.AKTUELLER_MONAT);
		Zeitraum erwarteterZeitraum = Zeitraum.aktuellerMonat();
		assertEquals(erwarteterZeitraum.getVon(), zeitraum.getVon());
        assertEquals(erwarteterZeitraum.getBis(), zeitraum.getBis());
	}
    
    @Test
        void zeitraumAusArt_soll_zeitraum_fuer_vorherigerMonat_zurueckgeben() {
    	        Zeitraum zeitraum = ZeitraumArt.zeitraumAusArt(ZeitraumArt.VORHERIGER_MONAT);
    	        Zeitraum erwarteterZeitraum = Zeitraum.vorherigerMonat();
    	        assertEquals(erwarteterZeitraum.getVon(), zeitraum.getVon());
    	        assertEquals(erwarteterZeitraum.getBis(), zeitraum.getBis());
    }

    @Test
    void zeitraumAusArt_soll_zeitraum_fuer_vorherigesJahr_zurueckgeben() {
	        Zeitraum zeitraum = ZeitraumArt.zeitraumAusArt(ZeitraumArt.VORHERIGES_JAHR);
	        Zeitraum erwarteterZeitraum = Zeitraum.vorherigesJahr();
	        assertEquals(erwarteterZeitraum.getVon(), zeitraum.getVon());
	        assertEquals(erwarteterZeitraum.getBis(), zeitraum.getBis());
}
    
    @Test
   	void zeitraumAusArt_soll_zeitraum_fuer_aktuellesJahr_zurueckgeben() {
   		Zeitraum zeitraum = ZeitraumArt.zeitraumAusArt(ZeitraumArt.AKTUELLES_JAHR);
   		Zeitraum erwarteterZeitraum = Zeitraum.aktuellesJahr();
   		assertEquals(erwarteterZeitraum.getVon(), zeitraum.getVon());
           assertEquals(erwarteterZeitraum.getBis(), zeitraum.getBis());
   	}
    

}
