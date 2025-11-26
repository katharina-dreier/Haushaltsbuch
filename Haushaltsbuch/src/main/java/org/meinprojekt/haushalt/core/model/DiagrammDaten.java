package org.meinprojekt.haushalt.core.model;

import java.util.List;
import java.util.Map;

public class DiagrammDaten {
	
	private final Map<String, Double> gefilterteEinnahmen;
    private final Map<String, Double> gefilterteAusgaben;
    private final double summeEinnahmen;
    private final double summeAusgaben;
    private final double summeDifferenz;
    private final double maxWert;
    private final String yAchsenLabel;
    private final double tickEinheitYAchse;
    private final String xAchsenLabel;
    private final List<String> xWerteSortiert;
    
	public DiagrammDaten(Map<String, Double> gefilterteEinnahmen, Map<String, Double> gefilterteAusgaben,
			double summeEinnahmen, double summeAusgaben, double summeDifferenz, double maxWert,String yAchselnLabel, double tickEinheit, String xAchsenLabel,
			List<String> xWerteSortiert) {
		this.gefilterteEinnahmen = gefilterteEinnahmen;
		this.gefilterteAusgaben = gefilterteAusgaben;
		this.summeEinnahmen = summeEinnahmen;
		this.summeAusgaben = summeAusgaben;
		this.summeDifferenz = summeDifferenz;
		this.maxWert = maxWert;
		this.yAchsenLabel = yAchselnLabel;
		this.tickEinheitYAchse = tickEinheit;
		this.xAchsenLabel = xAchsenLabel;
		this.xWerteSortiert = xWerteSortiert;
	}

	public Map<String, Double> getGefilterteEinnahmen() {
		return gefilterteEinnahmen;
	}

	public Map<String, Double> getGefilterteAusgaben() {
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
	
	public String getyAchsenLabel() {
		return yAchsenLabel;
	}

	public double getTickEinheit() {
		return tickEinheitYAchse;
	}
	
	public String getxAchsenLabel() {
		return xAchsenLabel;
	}

	public List<String> getxWerteSortiert() {
		return xWerteSortiert;
	}

	
}
