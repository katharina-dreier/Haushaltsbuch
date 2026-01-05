package org.meinprojekt.haushalt.core.model;

import static org.junit.jupiter.api.Assertions.*;

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
}
