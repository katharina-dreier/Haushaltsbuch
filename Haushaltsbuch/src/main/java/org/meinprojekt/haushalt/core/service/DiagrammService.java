package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.DiagrammDaten;

import javafx.collections.transformation.FilteredList;

public class DiagrammService {
	
	public static DiagrammDaten berechneDiagrammDaten(FilteredList<Buchung> gefilterteBuchungsListe) {
		
		Map<String, Double> gefilterteEinnahmen = gefilterteEinnahmenFuellen(gefilterteBuchungsListe);
	    Map<String, Double> gefilterteAusgaben = gefilterteAusgabenFuellen(gefilterteBuchungsListe);
	    double summeEinnahmen = summeAusMapBerechnen(gefilterteEinnahmen);
	    double summeAusgaben = summeAusMapBerechnen(gefilterteAusgaben);
	    double summeDifferenz = summeEinnahmen - summeAusgaben;
	    double maxWert = berechneMaxWert(gefilterteEinnahmen, gefilterteAusgaben);
	    double tickEinheit = berechneTickEinheit(maxWert);
	    String yAchsenLabel    = "Betrag in €";
	    String xAchsenLabel    = "Monat";
	    List<String> xWerteSortiert = xWerteSortieren(gefilterteEinnahmen, gefilterteAusgaben);
		
		return new DiagrammDaten(gefilterteEinnahmen, gefilterteAusgaben, summeEinnahmen, summeAusgaben, summeDifferenz, maxWert, yAchsenLabel, tickEinheit, xAchsenLabel, xWerteSortiert);
	}

	

	private static List<String> xWerteSortieren(Map<String, Double> gefilterteEinnahmen,
			Map<String, Double> gefilterteAusgaben) {
		 Set<String> alle = new TreeSet<>();
	        alle.addAll(gefilterteEinnahmen.keySet());
	        alle.addAll(gefilterteAusgaben.keySet());
	        return List.copyOf(alle);
	}



	private static Map<String, Double> gefilterteAusgabenFuellen(FilteredList<Buchung> gefilterteBuchungsListe) {
		
		Map<String, Double> gefilterteAusgaben = new java.util.HashMap<>();
		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			String monatKey = datum.getYear() + "-" + String.format("%02d", datum.getMonthValue());
			if ("AUSGABE".equalsIgnoreCase(buchung.getBuchungsart())) {
				gefilterteAusgaben.merge(monatKey, buchung.getBetrag(), Double::sum);
			}
		}
		return gefilterteAusgaben;
	}

	private static double berechneMaxWert(Map<String, Double> gefilterteEinnahmen,
			Map<String, Double> gefilterteAusgaben) {
		double maxWert = Math.max(gefilterteEinnahmen.values().stream().mapToDouble(Double::doubleValue).max().orElse(0),
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
	
	private static Map<String, Double> gefilterteEinnahmenFuellen(FilteredList<Buchung> gefilterteBuchungsListe) {
		Map<String, Double> gefilterteEinnahmen = new java.util.HashMap<>();
		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			String monatKey = datum.getYear() + "-" + String.format("%02d", datum.getMonthValue());
			if ("EINNAHME".equalsIgnoreCase(buchung.getBuchungsart())) {
				gefilterteEinnahmen.merge(monatKey, buchung.getBetrag(), Double::sum);
			}
		}
		return gefilterteEinnahmen;
	}
	
	private static double summeAusMapBerechnen(Map<String, Double> gefilterteBuchungen) {
        return gefilterteBuchungen.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        
}
