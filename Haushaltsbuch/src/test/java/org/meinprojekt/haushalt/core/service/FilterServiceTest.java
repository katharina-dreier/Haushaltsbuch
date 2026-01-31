package org.meinprojekt.haushalt.core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;

class FilterServiceTest {

	private FilterService filterService;

	@BeforeEach
	void setUp() {
		filterService = new FilterService();
	}


	@Test
	void predicateFuerBuchungsArt_gibt_false_wenn_andere_art() {
		Predicate<Buchung> predicate = filterService.predicateFuerBuchungsArt(Buchungstyp.EINNAHME);

		Buchung buchung = new Buchung();
		buchung.setBuchungstyp(Buchungstyp.AUSGABE);

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerBuchungsArt_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerBuchungsArt(null);

		Buchung buchung = new Buchung();
		buchung.setBuchungstyp(Buchungstyp.AUSGABE);

		assertTrue(predicate.test(buchung));
	}


	@Test
	void predicateFuerZeitraum_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(null);

		Buchung buchung = new Buchung();
		buchung.setBuchungsDatum(java.time.LocalDate.now());

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerZeitraum_buchungsdatum_null_gibt_false() {
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate ende = LocalDate.of(2024, 12, 31);
		Zeitraum zeitraum = new Zeitraum(start, ende);
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		Buchung buchung = new Buchung();
		buchung.setBuchungsDatum(null);

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerZeitraum_buchung_im_zeitraum_gibt_true() {
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate ende = LocalDate.of(2024, 12, 31);
		Zeitraum zeitraum = new Zeitraum(start, ende);
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		Buchung buchung = new Buchung();
		buchung.setBuchungsDatum(LocalDate.of(2024, 6, 15));

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerZeitraum_buchung_vor_zeitraum_gibt_false() {
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate ende = LocalDate.of(2024, 12, 31);
		Zeitraum zeitraum = new Zeitraum(start, ende);
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		Buchung buchung = new Buchung();
		buchung.setBuchungsDatum(LocalDate.of(2023, 6, 15));

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerZeitraum_buchung_nach_zeitraum_gibt_false() {
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate ende = LocalDate.of(2024, 12, 31);
		Zeitraum zeitraum = new Zeitraum(start, ende);
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		Buchung buchung = new Buchung();
		buchung.setBuchungsDatum(LocalDate.of(2025, 6, 15));

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerKategorie_filtert_case_insensitive() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorie("Lebensmittel");

		Buchung buchung = new Buchung();
		buchung.setKategorie("leBeNsMiTtEl");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerKategorie_gibt_false_wenn_andere_kategorie() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorie("Lebensmittel");

		Buchung buchung = new Buchung();
		buchung.setKategorie("Miete");

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerKategorie_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorie(null);

		Buchung buchung = new Buchung();
		buchung.setKategorie("Miete");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerKategorie_leer_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorie("");

		Buchung buchung = new Buchung();
		buchung.setKategorie("Miete");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerEmpfaenger_filtert_case_insensitive() {
		Predicate<Buchung> predicate = filterService.predicateFuerEmpfaenger("Max Mustermann");

		Buchung buchung = new Buchung();
		buchung.setEmpfaenger("mAx mUsTeRmAnN");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerEmpfaenger_gibt_false_wenn_andere_empfaenger() {
		Predicate<Buchung> predicate = filterService.predicateFuerEmpfaenger("Max Mustermann");

		Buchung buchung = new Buchung();
		buchung.setEmpfaenger("Erika Musterfrau");

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerEmpfaenger_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerEmpfaenger(null);

		Buchung buchung = new Buchung();
		buchung.setEmpfaenger("Erika Musterfrau");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerEmpfaenger_leer_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerEmpfaenger("");

		Buchung buchung = new Buchung();
		buchung.setEmpfaenger("Erika Musterfrau");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerSender_filtert_case_insensitive() {
		Predicate<Buchung> predicate = filterService.predicateFuerSender("Max Mustermann");

		Buchung buchung = new Buchung();
		buchung.setSender("mAx mUsTeRmAnN");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerSender_gibt_false_wenn_andere_sender() {
		Predicate<Buchung> predicate = filterService.predicateFuerSender("Max Mustermann");

		Buchung buchung = new Buchung();
		buchung.setSender("Erika Musterfrau");

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerSender_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerSender(null);

		Buchung buchung = new Buchung();
		buchung.setSender("Erika Musterfrau");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerSender_leer_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerSender("");

		Buchung buchung = new Buchung();
		buchung.setSender("Erika Musterfrau");

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerBetrag_testet_betrag_innerhalb_grenzen() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(50.0, 150.0);

		Buchung buchung = new Buchung();
		buchung.setBetrag(100.0);

		assertTrue(predicate.test(buchung));
	}

	@Test
	void predicateFuerBetrag_testet_betrag_unter_min() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(50.0, 150.0);

		Buchung buchung = new Buchung();
		buchung.setBetrag(30.0);

		assertFalse(predicate.test(buchung));
	}

	@Test
	void predicateFuerBetrag_testet_betrag_ueber_max() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(50.0, 150.0);
		Buchung buchung = new Buchung();
		buchung.setBetrag(200.0);
		assertFalse(predicate.test(buchung));
	}
	
	@Test
	void predicateFuerBetrag_testet_betrag_mit_null_grenzen() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(null, null);

		Buchung buchung = new Buchung();
		buchung.setBetrag(1000.0);

		assertTrue(predicate.test(buchung));
	}
	
	@Test
	void predicateFuerBetrag_testet_betrag_mit_null_min() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(null, 150.0);

		Buchung buchung = new Buchung();
		buchung.setBetrag(100.0);

		assertTrue(predicate.test(buchung));
	}
	
	@Test
	void predicateFuerBetrag_testet_betrag_mit_null_max() {
		Predicate<Buchung> predicate = filterService.predicateFuerBetrag(50.0, null);

		Buchung buchung = new Buchung();
		buchung.setBetrag(100.0);

		assertTrue(predicate.test(buchung));
	}
	
	@Test
	void predicateFuerKategorien_filtert_kategorien() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorien(Set.of("Miete", "Lebensmittel"));

		Buchung buchung1 = new Buchung();
		buchung1.setKategorie("Miete");
		assertTrue(predicate.test(buchung1));

		Buchung buchung2 = new Buchung();
		buchung2.setKategorie("Freizeit");
		assertFalse(predicate.test(buchung2));
	}
	
	@Test
	void predicateFuerKategorien_null_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorien(null);

		Buchung buchung = new Buchung();
		buchung.setKategorie("Miete");

		assertTrue(predicate.test(buchung));
	}
	
	@Test
	void predicateFuerKategorien_leer_laesst_alles_durch() {
		Predicate<Buchung> predicate = filterService.predicateFuerKategorien(Set.of());

		Buchung buchung = new Buchung();
		buchung.setKategorie("Miete");

		assertTrue(predicate.test(buchung));
	}
	
	

}