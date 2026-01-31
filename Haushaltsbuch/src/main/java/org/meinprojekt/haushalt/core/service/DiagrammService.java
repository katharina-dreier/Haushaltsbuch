package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.DiagrammDaten;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.DiagrammDaten.Aufloesung;

public class DiagrammService {
	
	private DiagrammService() {
	    throw new IllegalStateException("Utility class");
	  }

	public static DiagrammDaten berechneDiagrammDaten(List<Buchung> gefilterteBuchungsListe,
			Zeitraum zeitraum) {

		Aufloesung aufloesung = bestimmeAufloesungXAchse(zeitraum);
		Map<LocalDate, Double> gefilterteEinnahmen = gefilterteBuchungenFuellen(gefilterteBuchungsListe,
				Buchungstyp.EINNAHME, aufloesung);
		Map<LocalDate, Double> gefilterteAusgaben = gefilterteBuchungenFuellen(gefilterteBuchungsListe,
				Buchungstyp.AUSGABE, aufloesung);
		double summeEinnahmen = summeAusMapBerechnen(gefilterteEinnahmen);
		double summeAusgaben = summeAusMapBerechnen(gefilterteAusgaben);
		double summeDifferenz = summeEinnahmen + summeAusgaben;
		double maxWert = berechneMaxWert(gefilterteEinnahmen, gefilterteAusgaben);
		double tickEinheit = berechneTickEinheit(maxWert);
		List<LocalDate> xWerteSortiert = bestimmeXWerte(zeitraum, aufloesung);

		return new DiagrammDaten(
			    new DiagrammDaten.Reihen(gefilterteEinnahmen, gefilterteAusgaben),
			    new DiagrammDaten.Summen(summeEinnahmen, summeAusgaben, summeDifferenz),
			    new DiagrammDaten.Skalierung(maxWert, tickEinheit),
			    new DiagrammDaten.Achse(xWerteSortiert, aufloesung)
			);

	}
	


	
	static List<LocalDate> bestimmeXWerte(Zeitraum zeitraum, Aufloesung aufloesung) {
		List<LocalDate> dates = new ArrayList<>();
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

	static Map<LocalDate, Double> gefilterteBuchungenFuellen(List<Buchung> gefilterteBuchungsListe, Buchungstyp typ, Aufloesung aufloesung) {

		Map<LocalDate, Double> gefilterteBuchungen = new HashMap<>();

		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			LocalDate key = bestimmeKey(aufloesung, datum);
			if (typ == buchung.getBuchungstyp()) {
				gefilterteBuchungen.merge(key, buchung.getBetrag(), Double::sum);
			}
		}
		return gefilterteBuchungen;
	}

	static Aufloesung bestimmeAufloesungXAchse(Zeitraum zeitraum) {
		LocalDate start = zeitraum.getVon();
		LocalDate ende = zeitraum.getBis();
		long tageImZeitraum = ChronoUnit.DAYS.between(start, ende);
		long monateImZeitraum = ChronoUnit.MONTHS.between(start, ende);
		if (tageImZeitraum <= 90) {
			return Aufloesung.TAGE;
			
		} else if (monateImZeitraum <= 12) {
			return Aufloesung.MONATE;
			
		} else {
			return Aufloesung.JAHRE;
			
		}
	}

	static LocalDate bestimmeKey(Aufloesung aufloesung, LocalDate datum) {
		LocalDate key = datum;
			switch (aufloesung) {
			case TAGE: return key;
			case MONATE: return LocalDate.of(datum.getYear(), datum.getMonth(), 1);
			case JAHRE: return LocalDate.of(datum.getYear(), 1, 1);
			}
		return key;
	}

	static double berechneMaxWert(Map<LocalDate, Double> gefilterteEinnahmen,
			Map<LocalDate, Double> gefilterteAusgaben) {
		return Math.max(
				gefilterteEinnahmen.values().stream().mapToDouble(Double::doubleValue).max().orElse(0),
				gefilterteAusgaben.values().stream().mapToDouble(Double::doubleValue).max().orElse(0));
		
	}

	static double berechneTickEinheit(double maxWert) {
		double schritt = 200;
		if (maxWert > 1000)
			schritt = 500;
		if (maxWert > 3000)
			schritt = 1000;
		return schritt;

	}

	static double summeAusMapBerechnen(Map<LocalDate, Double> gefilterteBuchungen) {
		return gefilterteBuchungen.values().stream().mapToDouble(Double::doubleValue).sum();
	}

}
