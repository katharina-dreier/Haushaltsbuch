package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.DiagrammDaten;
import org.meinprojekt.haushalt.core.model.DiagrammDaten.Aufloesung;

import javafx.collections.transformation.FilteredList;

public class DiagrammService {

	public static DiagrammDaten berechneDiagrammDaten(FilteredList<Buchung> gefilterteBuchungsListe,
			Zeitraum zeitraum) {

		Aufloesung aufloesung = bestimmeAufloesungXAchse(zeitraum);
		Map<LocalDate, Double> gefilterteEinnahmen = gefilterteBuchungenFuellen(gefilterteBuchungsListe, zeitraum,
				"Einnahme", aufloesung);
		Map<LocalDate, Double> gefilterteAusgaben = gefilterteBuchungenFuellen(gefilterteBuchungsListe, zeitraum,
				"Ausgabe", aufloesung);
		double summeEinnahmen = summeAusMapBerechnen(gefilterteEinnahmen);
		double summeAusgaben = summeAusMapBerechnen(gefilterteAusgaben);
		double summeDifferenz = summeEinnahmen - summeAusgaben;
		double maxWert = berechneMaxWert(gefilterteEinnahmen, gefilterteAusgaben);
		double tickEinheit = berechneTickEinheit(maxWert);
		List<LocalDate> xWerteSortiert = bestimmeXWerte(zeitraum, aufloesung);

		return new DiagrammDaten(gefilterteEinnahmen, gefilterteAusgaben, summeEinnahmen, summeAusgaben, summeDifferenz,
				maxWert, tickEinheit, xWerteSortiert, aufloesung);
	}

	
	private static List<LocalDate> bestimmeXWerte(Zeitraum zeitraum, Aufloesung aufloesung) {
		List<LocalDate> dates = new java.util.ArrayList<>();
		LocalDate start = zeitraum.getVon();
		LocalDate ende = zeitraum.getBis();
		LocalDate key = bestimmeKey(aufloesung, start);

		while (!key.isAfter(ende)) {
			dates.add(key);
			switch (aufloesung) {
			case TAGE -> key = key.plusDays(1);
			case MONATE -> key = key.plusMonths(1).withDayOfMonth(1);
			case JAHRE -> key = key.plusYears(1).withDayOfYear(1);
			}
		}
		return dates;
	}

	private static Map<LocalDate, Double> gefilterteBuchungenFuellen(FilteredList<Buchung> gefilterteBuchungsListe,
			Zeitraum zeitraum, String buchungsart, Aufloesung aufloesung) {

		Map<LocalDate, Double> gefilterteBuchungen = new HashMap<>();

		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			LocalDate key = bestimmeKey(aufloesung, datum);
			if (buchungsart.equalsIgnoreCase(buchung.getBuchungsart())) {
				gefilterteBuchungen.merge(key, buchung.getBetrag(), Double::sum);
			}
		}
		return gefilterteBuchungen;
	}

	private static Aufloesung bestimmeAufloesungXAchse(Zeitraum zeitraum) {
		LocalDate start = zeitraum.getVon();
		LocalDate ende = zeitraum.getBis();
		long tageImZeitraum = ChronoUnit.DAYS.between(start, ende);
		long monateImZeitraum = ChronoUnit.MONTHS.between(start, ende);
		if (tageImZeitraum <= 90) {
			Aufloesung aufloesung = Aufloesung.TAGE;
			return aufloesung;
		} else if (monateImZeitraum <= 12) {
			Aufloesung aufloesung = Aufloesung.MONATE;
			return aufloesung;
		} else {
			Aufloesung aufloesung = Aufloesung.JAHRE;
			return aufloesung;
		}
	}

	private static LocalDate bestimmeKey(Aufloesung aufloesung, LocalDate datum) {
		LocalDate key = null;
			switch (aufloesung) {
			case TAGE -> key = datum;
			case MONATE -> key = LocalDate.of(datum.getYear(), datum.getMonth(), 1);
			case JAHRE -> key = LocalDate.of(datum.getYear(), 1, 1);
			}
		return key;
	}

	/*private static Map<String, Double> setzeKeysFuerZeitraum(Aufloesung aufloesung, Zeitraum zeitraum) {
		Map<String, Double> gefilterteBuchungen = new java.util.LinkedHashMap<>();
		LocalDate start = zeitraum.getVon();
		LocalDate ende = zeitraum.getBis();
		for (LocalDate datum = start; !datum.isAfter(ende); datum = datum.plusDays(1)) {
			LocalDate key = bestimmeKey(aufloesung, datum);
			gefilterteBuchungen.put(key, 0.0);
		}
		return gefilterteBuchungen;
	}*/

	private static double berechneMaxWert(Map<LocalDate, Double> gefilterteEinnahmen,
			Map<LocalDate, Double> gefilterteAusgaben) {
		double maxWert = Math.max(
				gefilterteEinnahmen.values().stream().mapToDouble(Double::doubleValue).max().orElse(0),
				gefilterteAusgaben.values().stream().mapToDouble(Double::doubleValue).max().orElse(0));
		return maxWert;
	}

	private static double berechneTickEinheit(double maxWert) {
		double schritt = 200;
		if (maxWert > 1000)
			schritt = 500;
		if (maxWert > 3000)
			schritt = 1000;
		return schritt;

	}

	private static double summeAusMapBerechnen(Map<LocalDate, Double> gefilterteBuchungen) {
		return gefilterteBuchungen.values().stream().mapToDouble(Double::doubleValue).sum();
	}

}
