package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DiagrammDaten {
	
	 public enum Aufloesung {
	        TAGE, MONATE, JAHRE
	    }
	
	private final Map<LocalDate, Double> gefilterteEinnahmen;
    private final Map<LocalDate, Double> gefilterteAusgaben;
    private final double summeEinnahmen;
    private final double summeAusgaben;
    private final double summeDifferenz;
    private final double maxWert;
    private final double tickEinheitYAchse;
    private final List<LocalDate> xWerteSortiert;
    private final Aufloesung aufloesung;

    
	public DiagrammDaten(Map<LocalDate, Double> gefilterteEinnahmen, Map<LocalDate, Double> gefilterteAusgaben,
			double summeEinnahmen, double summeAusgaben, double summeDifferenz, double maxWert, double tickEinheit,
			List<LocalDate> xWerteSortiert, Aufloesung aufloesung) {
		this.gefilterteEinnahmen = gefilterteEinnahmen;
		this.gefilterteAusgaben = gefilterteAusgaben;
		this.summeEinnahmen = summeEinnahmen;
		this.summeAusgaben = summeAusgaben;
		this.summeDifferenz = summeDifferenz;
		this.maxWert = maxWert;
		this.tickEinheitYAchse = tickEinheit;
		this.xWerteSortiert = xWerteSortiert;
		this.aufloesung = aufloesung;
	}

	public Map<LocalDate, Double> getGefilterteEinnahmen() {
		return gefilterteEinnahmen;
	}

	public Map<LocalDate, Double> getGefilterteAusgaben() {
		return gefilterteAusgaben;
	}

	public double getSummeEinnahmen() {
		return summeEinnahmen;
	}

	public double getSummeAusgaben() {
		return summeAusgaben;
	}

	public double getSummeDifferenz() {
		return summeDifferenz;
	}

	public double getMaxWert() {
		return maxWert;
	}
	

	public double getTickEinheit() {
		return tickEinheitYAchse;
	}
	

	public List<LocalDate> getxWerteSortiert() {
		return xWerteSortiert;
	}
	
	public Aufloesung getAufloesung() {
		return aufloesung;
	}
	

	
}
