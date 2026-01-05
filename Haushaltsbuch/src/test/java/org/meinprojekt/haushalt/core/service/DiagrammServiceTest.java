package org.meinprojekt.haushalt.core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.DiagrammDaten;
import org.meinprojekt.haushalt.core.model.DiagrammDaten.Aufloesung;

class DiagrammServiceTest {

	@Test
	void tick_einheit_unter_1000_liefert_200() {
		double ergebnis = DiagrammService.berechneTickEinheit(850);
		assertEquals(200, ergebnis);

		double ergebnis2 = DiagrammService.berechneTickEinheit(1000);
		assertEquals(200, ergebnis2);
	}

	@Test
	void tick_einheit_zwischen_1001_und_3000_liefert_500() {
		double ergebnis = DiagrammService.berechneTickEinheit(1001);
		assertEquals(500, ergebnis);

		double ergebnis2 = DiagrammService.berechneTickEinheit(3000);
		assertEquals(500, ergebnis2);
	}

	@Test
	void tick_einheit_ueber_3000_liefert_1000() {
		double ergebnis = DiagrammService.berechneTickEinheit(3001);
		assertEquals(1000, ergebnis);

		double ergebnis2 = DiagrammService.berechneTickEinheit(10000);
		assertEquals(1000, ergebnis2);
	}

	@Test
	void tick_einheit_gleich_0_liefert_200() {
		double ergebnis = DiagrammService.berechneTickEinheit(0);
		assertEquals(200, ergebnis);
	}

	@Test
	void summeAusMapBerechnen_liefert_korrekte_Summe() {
		Map<LocalDate, Double> testMap = new HashMap<>();
		testMap.put(LocalDate.of(2024, 1, 1), 100.0);
		testMap.put(LocalDate.of(2024, 1, 2), 200.0);
		testMap.put(LocalDate.of(2024, 1, 3), 300.0);

		double ergebnis = DiagrammService.summeAusMapBerechnen(testMap);
		assertEquals(600.0, ergebnis);
	}

	@Test
	void summeAusMapBerechnen_liefert_0_fuer_leere_Map() {
		Map<LocalDate, Double> testMap = new HashMap<>();

		double ergebnis = DiagrammService.summeAusMapBerechnen(testMap);
		assertEquals(0.0, ergebnis);
	}

	@Test
	void summeAusMapBerechnen_liefert_korrekte_Summe_fuer_negative_Werte() {
		Map<LocalDate, Double> testMap = new HashMap<>();
		testMap.put(LocalDate.of(2024, 1, 1), -100.0);
		testMap.put(LocalDate.of(2024, 1, 2), -200.0);
		testMap.put(LocalDate.of(2024, 1, 3), -300.0);

		double ergebnis = DiagrammService.summeAusMapBerechnen(testMap);
		assertEquals(-600.0, ergebnis);
	}

	@Test
	void summeAusMapBerechnen_liefert_korrekte_Summe_fuer_gemischte_Werte() {
		Map<LocalDate, Double> testMap = new HashMap<>();
		testMap.put(LocalDate.of(2024, 1, 1), 100.0);
		testMap.put(LocalDate.of(2024, 1, 2), -50.0);
		testMap.put(LocalDate.of(2024, 1, 3), 200.0);
		testMap.put(LocalDate.of(2024, 1, 4), -25.0);

		double ergebnis = DiagrammService.summeAusMapBerechnen(testMap);
		assertEquals(225.0, ergebnis);
	}

	@Test
	void maxWert_berechnet_korrekten_Maximalwert() {
		Map<LocalDate, Double> einnahmen = new HashMap<>();
		einnahmen.put(LocalDate.of(2024, 1, 1), 500.0);
		einnahmen.put(LocalDate.of(2024, 1, 2), 1500.0);
		einnahmen.put(LocalDate.of(2024, 1, 3), 2500.0);

		Map<LocalDate, Double> ausgaben = new HashMap<>();
		ausgaben.put(LocalDate.of(2024, 1, 1), 800.0);
		ausgaben.put(LocalDate.of(2024, 1, 2), 1200.0);
		ausgaben.put(LocalDate.of(2024, 1, 3), 3000.0);

		double ergebnis = DiagrammService.berechneMaxWert(einnahmen, ausgaben);
		assertEquals(3000.0, ergebnis);
	}

	@Test
	void maxWert_berechnet_korrekten_Maximalwert_wenn_einnahmen_hoeher_sind() {
		Map<LocalDate, Double> einnahmen = new HashMap<>();
		einnahmen.put(LocalDate.of(2024, 1, 1), 5000.0);
		einnahmen.put(LocalDate.of(2024, 1, 2), 1500.0);
		einnahmen.put(LocalDate.of(2024, 1, 3), 2500.0);

		Map<LocalDate, Double> ausgaben = new HashMap<>();
		ausgaben.put(LocalDate.of(2024, 1, 1), 800.0);
		ausgaben.put(LocalDate.of(2024, 1, 2), 1200.0);
		ausgaben.put(LocalDate.of(2024, 1, 3), 3000.0);

		double ergebnis = DiagrammService.berechneMaxWert(einnahmen, ausgaben);
		assertEquals(5000.0, ergebnis);
	}

	@Test
	void maxWert_berechnet_0_wenn_beide_Maps_leer_sind() {
		Map<LocalDate, Double> einnahmen = new HashMap<>();
		Map<LocalDate, Double> ausgaben = new HashMap<>();

		double ergebnis = DiagrammService.berechneMaxWert(einnahmen, ausgaben);
		assertEquals(0.0, ergebnis);
	}

	@Test
	void bestimmeKey_liefert_korrekten_Key_fuer_Tage() {
		LocalDate datum = LocalDate.of(2024, 3, 15);
		LocalDate ergebnis = DiagrammService.bestimmeKey(Aufloesung.TAGE, datum);
		assertEquals(LocalDate.of(2024, 3, 15), ergebnis);
	}

	@Test
	void bestimmeKey_liefert_korrekten_Key_fuer_Monate() {
		LocalDate datum = LocalDate.of(2024, 3, 15);
		LocalDate ergebnis = DiagrammService.bestimmeKey(Aufloesung.MONATE, datum);
		assertEquals(LocalDate.of(2024, 3, 1), ergebnis);
	}

	@Test
	void bestimmeKey_liefert_korrekten_Key_fuer_Jahre() {
		LocalDate datum = LocalDate.of(2024, 3, 15);
		LocalDate ergebnis = DiagrammService.bestimmeKey(Aufloesung.JAHRE, datum);
		assertEquals(LocalDate.of(2024, 1, 1), ergebnis);
	}

	@Test
	void bestimmeKey_liefert_korrekten_Key_fuer_Schaltjahr() {
		LocalDate datum = LocalDate.of(2020, 2, 29);

		LocalDate ergebnisTage = DiagrammService.bestimmeKey(Aufloesung.TAGE, datum);
		assertEquals(LocalDate.of(2020, 2, 29), ergebnisTage);

		LocalDate ergebnisMonate = DiagrammService.bestimmeKey(Aufloesung.MONATE, datum);
		assertEquals(LocalDate.of(2020, 2, 1), ergebnisMonate);

		LocalDate ergebnisJahre = DiagrammService.bestimmeKey(Aufloesung.JAHRE, datum);
		assertEquals(LocalDate.of(2020, 1, 1), ergebnisJahre);
	}

	@Test
	void bestimmeKey_liefert_korrekten_Key_fuer_Jahreswechsel() {
		LocalDate datum = LocalDate.of(2023, 12, 31);

		LocalDate ergebnisTage = DiagrammService.bestimmeKey(Aufloesung.TAGE, datum);
		assertEquals(LocalDate.of(2023, 12, 31), ergebnisTage);

		LocalDate ergebnisMonate = DiagrammService.bestimmeKey(Aufloesung.MONATE, datum);
		assertEquals(LocalDate.of(2023, 12, 1), ergebnisMonate);

		LocalDate ergebnisJahre = DiagrammService.bestimmeKey(Aufloesung.JAHRE, datum);
		assertEquals(LocalDate.of(2023, 1, 1), ergebnisJahre);
	}

	@Test
	void bestimmeAufloesungXAchse_liefert_TAGE_fuer_zeiträume_bis_90_Tage() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)); // 90 Tage
		Aufloesung ergebnis = DiagrammService.bestimmeAufloesungXAchse(zeitraum);
		assertEquals(Aufloesung.TAGE, ergebnis);
	}

	@Test
	void bestimmeAufloesungXAchse_liefert_MONATE_fuer_zeiträume_bis_12_Monate() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)); // 12 Monate
		Aufloesung ergebnis = DiagrammService.bestimmeAufloesungXAchse(zeitraum);
		assertEquals(Aufloesung.MONATE, ergebnis);
	}

	@Test
	void bestimmeAufloesungXAchse_liefert_JAHRE_fuer_zeiträume_über_12_Monate() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 2, 1)); // über 12 Monate
		Aufloesung ergebnis = DiagrammService.bestimmeAufloesungXAchse(zeitraum);
		assertEquals(Aufloesung.JAHRE, ergebnis);
	}

	@Test
	void bestimmeAufloesungXAchse_liefert_JAHRE_fuer_genau_12_Monate() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2023, 6, 1), LocalDate.of(2024, 5, 31)); // genau 12 Monate
		Aufloesung ergebnis = DiagrammService.bestimmeAufloesungXAchse(zeitraum);
		assertEquals(Aufloesung.MONATE, ergebnis);
	}

	@Test
	void bestimmeAufloesungXAchse_liefert_TAGE_fuer_genau_90_Tage() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)); // genau 90 Tage
		Aufloesung ergebnis = DiagrammService.bestimmeAufloesungXAchse(zeitraum);
		assertEquals(Aufloesung.TAGE, ergebnis);
	}

	@Test
	void gefilterteBuchungenFuellen_filtert_und_addiert() {
		Buchung buchung1 = new Buchung();
		buchung1.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung1.setBetrag(100);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung2.setBetrag(50);
		buchung2.setBuchungsart("Einnahme");
		Buchung buchung3 = new Buchung();
		buchung3.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung3.setBetrag(999);
		buchung3.setBuchungsart("Ausgabe");

		List<Buchung> buchungen = List.of(buchung1, buchung2, buchung3);

		Map<LocalDate, Double> result = DiagrammService.gefilterteBuchungenFuellen(buchungen, "Einnahme",
				Aufloesung.TAGE);

		assertEquals(1, result.size());
		assertEquals(150.0, result.get(LocalDate.of(2025, 1, 10)));
	}

	@Test
	void gefilterteBuchungenFuellen_gibt_leere_Map_fuer_keine_passenden_Buchungen() {
		Buchung buchung1 = new Buchung();
		buchung1.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung1.setBetrag(100);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung2.setBetrag(50);
		buchung2.setBuchungsart("Einnahme");

		List<Buchung> buchungen = List.of(buchung1, buchung2);

		Map<LocalDate, Double> result = DiagrammService.gefilterteBuchungenFuellen(buchungen, "Ausgabe",
				Aufloesung.TAGE);

		assertTrue(result.isEmpty());
	}

	@Test
	void gefilterteBuchungenFuellen_addiert_buchungen_richtig_bei_monatlicher_Aufloesung() {
		Buchung buchung1 = new Buchung();
		buchung1.setBuchungsDatum(LocalDate.of(2025, 1, 15));
		buchung1.setBetrag(200);
		buchung1.setBuchungsart("Einnahme");
		Buchung buchung2 = new Buchung();
		buchung2.setBuchungsDatum(LocalDate.of(2025, 1, 20));
		buchung2.setBetrag(300);
		buchung2.setBuchungsart("Einnahme");
		Buchung buchung3 = new Buchung();
		buchung3.setBuchungsDatum(LocalDate.of(2025, 2, 10));
		buchung3.setBetrag(400);
		buchung3.setBuchungsart("Einnahme");

		List<Buchung> buchungen = List.of(buchung1, buchung2, buchung3);

		Map<LocalDate, Double> result = DiagrammService.gefilterteBuchungenFuellen(buchungen, "Einnahme",
				Aufloesung.MONATE);

		assertEquals(2, result.size());
		assertEquals(500.0, result.get(LocalDate.of(2025, 1, 1)));
		assertEquals(400.0, result.get(LocalDate.of(2025, 2, 1)));
	}

	@Test
	void gefilterteBuchungenFuellen_addiert_buchungen_richtig_bei_jahres_Aufloesung() {
		Buchung buchung1 = new Buchung();
		buchung1.setBuchungsDatum(LocalDate.of(2024, 3, 15));
		buchung1.setBetrag(500);
		buchung1.setBuchungsart("Ausgabe");
		Buchung buchung2 = new Buchung();
		buchung2.setBuchungsDatum(LocalDate.of(2024, 7, 20));
		buchung2.setBetrag(700);
		buchung2.setBuchungsart("Ausgabe");
		Buchung buchung3 = new Buchung();
		buchung3.setBuchungsDatum(LocalDate.of(2025, 1, 10));
		buchung3.setBetrag(300);
		buchung3.setBuchungsart("Ausgabe");

		List<Buchung> buchungen = List.of(buchung1, buchung2, buchung3);

		Map<LocalDate, Double> result = DiagrammService.gefilterteBuchungenFuellen(buchungen, "Ausgabe",
				Aufloesung.JAHRE);

		assertEquals(2, result.size());
		assertEquals(1200.0, result.get(LocalDate.of(2024, 1, 1)));
		assertEquals(300.0, result.get(LocalDate.of(2025, 1, 1)));
	}

	@Test
	void gefilterteBuchungenFuellen_gibt_leere_Map_fuer_leere_Buchungsliste() {
		List<Buchung> buchungen = List.of();

		Map<LocalDate, Double> result = DiagrammService.gefilterteBuchungenFuellen(buchungen, "Einnahme",
				Aufloesung.TAGE);

		assertTrue(result.isEmpty());
	}

	@Test
	void bestimmeXWerte_liefert_korrekte_Tageswerte() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
		List<LocalDate> ergebnis = DiagrammService.bestimmeXWerte(zeitraum, Aufloesung.TAGE);

		assertEquals(5, ergebnis.size());
		assertEquals(LocalDate.of(2024, 1, 1), ergebnis.get(0));
		assertEquals(LocalDate.of(2024, 1, 5), ergebnis.get(4));
	}

	@Test
	void bestimmeXWerte_liefert_korrekte_Monatswerte() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 4, 10));
		List<LocalDate> ergebnis = DiagrammService.bestimmeXWerte(zeitraum, Aufloesung.MONATE);

		assertEquals(4, ergebnis.size());
		assertEquals(LocalDate.of(2024, 1, 1), ergebnis.get(0));
		assertEquals(LocalDate.of(2024, 4, 1), ergebnis.get(3));
	}

	@Test
	void bestimmeXWerte_liefert_korrekte_Jahreswerte() {
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2022, 6, 1), LocalDate.of(2025, 3, 31));
		List<LocalDate> ergebnis = DiagrammService.bestimmeXWerte(zeitraum, Aufloesung.JAHRE);

		assertEquals(4, ergebnis.size());
		assertEquals(LocalDate.of(2022, 1, 1), ergebnis.get(0));
		assertEquals(LocalDate.of(2025, 1, 1), ergebnis.get(3));
	}

	@Test
	void berechneDiagrammDaten_gibt_korrekte_DiagrammDaten_zurueck() {
		Buchung buchung1 = new Buchung();
		buchung1.setBuchungsDatum(LocalDate.of(2024, 1, 10));
		buchung1.setBetrag(500);
		buchung1.setBuchungsart("Einnahme");

		Buchung buchung2 = new Buchung();
		buchung2.setBuchungsDatum(LocalDate.of(2024, 1, 15));
		buchung2.setBetrag(200);
		buchung2.setBuchungsart("Ausgabe");

		Buchung buchung3 = new Buchung();
		buchung3.setBuchungsDatum(LocalDate.of(2024, 2, 5));
		buchung3.setBetrag(300);
		buchung3.setBuchungsart("Einnahme");

		List<Buchung> buchungen = List.of(buchung1, buchung2, buchung3);
		Zeitraum zeitraum = new Zeitraum(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28));

		DiagrammDaten diagrammDaten = DiagrammService.berechneDiagrammDaten(buchungen, zeitraum);

		assertEquals(800.0, diagrammDaten.getSummeEinnahmen());
		assertEquals(200.0, diagrammDaten.getSummeAusgaben());
		assertEquals(600.0, diagrammDaten.getSummeDifferenz());
		assertEquals(Aufloesung.TAGE, diagrammDaten.getAufloesung());
		assertEquals(59, diagrammDaten.getxWerteSortiert().size());

	}
	
	
}
