package org.meinprojekt.haushalt.core.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class KontoTest {

	@Test
	void kontoErstellung_Initialisiert_KontoKorrekt() {
		String kontoName = "Girokonto";
		String inhaber = "Max Mustermann";
		double kontostand = 1500.0;
		String kreditinstitut = "MeineBank";

		Konto konto = new Konto(kontoName, inhaber, kontostand, kreditinstitut);

		assertEquals(kontoName, konto.getKontoName());
		assertEquals(inhaber, konto.getInhaber());
		assertEquals(kontostand, konto.getKontostandBeiErstellung(), 0.01);
		assertEquals(kreditinstitut, konto.getKreditinstitut());
		assertNotNull(konto.getBuchungen());
		assertNotNull(konto.getWiederkehrendeZahlungen());
	}
	
	@Test
	void kontoNummer_Wird_Einzigartig_Vergaben() {
		Konto konto1 = new Konto("Konto1", "Inhaber1", 1000.0, "Bank1");
		Konto konto2 = new Konto("Konto2", "Inhaber2", 2000.0, "Bank2");

		assertNotEquals(konto1.getKontonummer(), konto2.getKontonummer());
	}
	
	@Test
	void buchungen_und_wiederkehrendeZahlungen_Initialisiert_Als_LeereListen() {
		Konto konto = new Konto("KontoTest", "InhaberTest", 500.0, "BankTest");

		assertTrue(konto.getBuchungen().isEmpty());
		assertTrue(konto.getWiederkehrendeZahlungen().isEmpty());
	}
	
	@Test
	void kontoGetterUndSetter_Funktionieren_Korrekt() {
		Konto konto = new Konto();
		konto.setKontoName("TestKonto");
		konto.setInhaber("TestInhaber");
		konto.setKontostandBeiErstellung(750.0);
		konto.setKreditinstitut("TestBank");
		Map<Integer, Konto> konten = new HashMap<>();
		Konto.setKonten(konten); 
		List<Buchung> buchungen = new ArrayList<>();
		konto.setBuchungen(buchungen);

		assertEquals("TestKonto", konto.getKontoName());
		assertEquals("TestInhaber", konto.getInhaber());
		assertEquals(750.0, konto.getKontostandBeiErstellung(), 0.01);
		assertEquals("TestBank", konto.getKreditinstitut());
		assertEquals(konten, Konto.getKonten());
	}
	
	@Test
	void anzahlKonten_StaticVariable_Wird_Korrekt_Gehoert() {
		int initialAnzahl = Konto.getAnzahlKonten();
		new Konto("KontoA", "InhaberA", 100.0, "BankA");
		new Konto("KontoB", "InhaberB", 200.0, "BankB");

		assertEquals(initialAnzahl + 2, Konto.getAnzahlKonten());
	}
	
	@Test
	void buchungen_werden_korrekt_hinzugefuegt() {
		Konto konto = new Konto("TestKonto", "TestInhaber", 0.0, "TestBank");
		Buchung buchung = new Buchung();
		buchung.setBetrag(500.0);
		buchung.setBuchungsDatum(LocalDate.of(2024, 6, 15));
		buchung.setBuchungsart("Einnahme");

		konto.getBuchungen().add(buchung);

		assertEquals(1, konto.getBuchungen().size());
		assertEquals(500.0, konto.getBuchungen().get(0).getBetrag(), 0.01);
		assertEquals("Einnahme", konto.getBuchungen().get(0).getBuchungsart());
	}
	
	@Test
	void getKontostand_liefert_korrekten_Wert() {
		Konto konto = new Konto("MeinKonto", "Max Mustermann", 0.0, "MeineBank");
		Buchung buchung1 = new Buchung();
		buchung1.setBetrag(1000.0);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBetrag(300.0);
		buchung2.setBuchungsart("Ausgabe");
		Buchung buchung3 = new Buchung();
		buchung3.setBetrag(200.0);
		buchung3.setBuchungsart("Test");
		

		konto.getBuchungen().add(buchung1);
		konto.getBuchungen().add(buchung2);
		konto.getBuchungen().add(buchung3);

		double kontostand = konto.getKontostand();
		assertEquals(700.0, kontostand, 0.01);
	}
	
	@Test
	void kontoToString_Gibt_Korrekte_Darstellung_Zurueck() {
		Konto konto = new Konto("MeinKonto", "Max Mustermann", 0.0, "MeineBank");
		Buchung buchung1 = new Buchung();
		buchung1.setBetrag(1000.0);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBetrag(300.0);
		buchung2.setBuchungsart("Ausgabe");

		konto.getBuchungen().add(buchung1);
		konto.getBuchungen().add(buchung2);

		String kontoString = konto.toString();
		String expectedString = "Konto MeinKonto von Max Mustermann, Institut: MeineBank, Kontostand: 700.0 Euro";

		assertEquals(expectedString, kontoString);
		
	}
	
	@Test
	void toCSV_Gibt_Korrekte_CSV_Darstellung_Zurueck() {
		Konto konto = new Konto("MeinKonto", "Max Mustermann", 1500.0, "MeineBank");
		String csv = konto.toCSV();
		String expectedCSV = konto.getKontonummer() + ";MeineBank;MeinKonto;Max Mustermann;1500.0";
		assertEquals(expectedCSV, csv);
	}
	
	@Test
	void getAlleBuchungen_Gibt_Alle_Buchungen_Ueber_Alle_Konten_Zurueck() {
		Konto konto1 = new Konto("Konto1", "Inhaber1", 0.0, "Bank1");
		Konto konto2 = new Konto("Konto2", "Inhaber2", 0.0, "Bank2");

		Buchung buchung1 = new Buchung();
		buchung1.setBetrag(100.0);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBetrag(50.0);
		buchung2.setBuchungsart("Ausgabe");

		konto1.getBuchungen().add(buchung1);
		konto2.getBuchungen().add(buchung2);
		Konto.getKonten().put(konto1.getKontonummer(), konto1);
		Konto.getKonten().put(konto2.getKontonummer(), konto2);

		assertEquals(2, Konto.getAlleBuchungen().size());
	}

	@Test
	void getAlleWiederkehrendeZahlungen_Gibt_Alle_WiederkehrendeZahlungen_Ueber_Alle_Konten_Zurueck() {
		Konto konto1 = new Konto("Konto1", "Inhaber1", 0.0, "Bank1");
		Konto konto2 = new Konto("Konto2", "Inhaber2", 0.0, "Bank2");

		WiederkehrendeZahlung wz1 = new WiederkehrendeZahlung();
		wz1.setBetrag(100.0);
		wz1.setBeschreibung("Miete");
		WiederkehrendeZahlung wz2 = new WiederkehrendeZahlung();
		wz2.setBetrag(50.0);
		wz2.setBeschreibung("Abonnement");

		konto1.getWiederkehrendeZahlungen().add(wz1);
		konto2.getWiederkehrendeZahlungen().add(wz2);
		Konto.getKonten().put(konto1.getKontonummer(), konto1);
		Konto.getKonten().put(konto2.getKontonummer(), konto2);

		assertEquals(2, Konto.getAlleWiederkehrendeZahlungen().size());
	}
}
