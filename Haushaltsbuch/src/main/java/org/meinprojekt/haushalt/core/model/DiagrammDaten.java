package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DiagrammDaten {

	public enum Aufloesung {
		TAGE, MONATE, JAHRE
	}

	public record Reihen(Map<LocalDate, Double> einnahmen, Map<LocalDate, Double> ausgaben) {
	}

	public record Summen(double einnahmen, double ausgaben, double differenz) {
	}

	public record Skalierung(double maxWert, double tickEinheitYAchse) {
	}

	public record Achse(List<LocalDate> xWerteSortiert, Aufloesung aufloesung) {
	}

	private final Reihen reihen;
	private final Summen summen;
	private final Skalierung skalierung;
	private final Achse achse;

	public DiagrammDaten(Reihen reihen, Summen summen, Skalierung skalierung, Achse achse) {
		this.reihen = reihen;
		this.summen = summen;
		this.skalierung = skalierung;
		this.achse = achse;
	}

	public Map<LocalDate, Double> getGefilterteEinnahmen() {
		return reihen.einnahmen();
	}

	public Map<LocalDate, Double> getGefilterteAusgaben() {
		return reihen.ausgaben();
	}

	public double getSummeEinnahmen() {
		return summen.einnahmen();
	}

	public double getSummeAusgaben() {
		return summen.ausgaben();
	}

	public double getSummeDifferenz() {
		return summen.differenz();
	}

	public double getMaxWert() {
		return skalierung.maxWert();
	}

	public double getTickEinheit() {
		return skalierung.tickEinheitYAchse();
	}

	public List<LocalDate> getxWerteSortiert() {
		return achse.xWerteSortiert();
	}

	public Aufloesung getAufloesung() {
		return achse.aufloesung();
	}

}
