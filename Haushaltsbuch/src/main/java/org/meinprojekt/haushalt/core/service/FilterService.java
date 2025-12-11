package org.meinprojekt.haushalt.core.service;

import java.util.Set;
import java.util.function.Predicate;

import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.model.Buchung;

public class FilterService {
	
	public Predicate<Buchung> predicateFuerBuchungsArt(String buchungsart) {
		if (buchungsart != null && !buchungsart.isEmpty()) {
			return buchung -> buchungsart.equalsIgnoreCase(buchung.getBuchungsart());
		}
		return _ -> true; 
	}
	
	public Predicate<Buchung> predicateFuerZeitraum(Zeitraum zeitraum) {
		
		if (zeitraum != null) {
			return buchung -> {
				if (buchung.getBuchungsDatum() == null) {
					return false;
				}
				return !buchung.getBuchungsDatum().isBefore(zeitraum.getVon())
						&& !buchung.getBuchungsDatum().isAfter(zeitraum.getBis());
			};
		}
		return _ -> true; 
	}
	
	public Predicate<Buchung> predicateFuerKategorie(String kategorie) {
		if (kategorie != null && !kategorie.isEmpty()) {
			return buchung -> kategorie.equalsIgnoreCase(buchung.getKategorie());
		}
		return _ -> true; 
	}
	
	public Predicate<Buchung> predicateFuerEmpfaenger(String empfaenger) {
		if (empfaenger != null && !empfaenger.isEmpty()) {
			return buchung -> empfaenger.equalsIgnoreCase(buchung.getEmpfaenger());
		}
		return _ -> true; 
	}
	
	public Predicate<Buchung> predicateFuerSender(String sender) {
		if (sender != null && !sender.isEmpty()) {
			return buchung -> sender.equalsIgnoreCase(buchung.getSender());
		}
		return _ -> true; 
	}
	
	public Predicate<Buchung> predicateFuerBetrag(Double betragMin, Double betragMax) {
		return buchung -> {
			boolean meetsMin = (betragMin == null) || (buchung.getBetrag() >= betragMin);
			boolean meetsMax = (betragMax == null) || (buchung.getBetrag() <= betragMax);
			return meetsMin && meetsMax;
		};
	}

	public Predicate<Buchung> predicateFuerKategorien(Set<String> ausgewaehlteKategorien) {
		if (ausgewaehlteKategorien != null && !ausgewaehlteKategorien.isEmpty()) {
			return buchung -> ausgewaehlteKategorien.contains(buchung.getKategorie());
		}
		return _ -> true;
	}

}
