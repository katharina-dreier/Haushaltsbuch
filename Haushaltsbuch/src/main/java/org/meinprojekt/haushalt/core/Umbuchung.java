package org.meinprojekt.haushalt.core;

import java.time.LocalDate;

public class Umbuchung extends Buchung{
	
	private Konto kontoVon;
	private Konto kontoNach;

	public Umbuchung(double betrag, Konto kontoVon, Konto kontoNach, LocalDate buchungsDatum) {
		super(betrag, "Umbuchung", buchungsDatum);
		this.kontoVon = kontoVon;
		this.kontoNach = kontoNach;
		kontoVon.buchungen.add(this); // Buchung zur Liste hinzufügen
		kontoNach.buchungen.add(this); // Buchung zur Liste hinzufügen
	}

	public Konto getKontoVon() {
		return kontoVon;
	}

	public void setKontoVon(Konto kontoVon) {
		this.kontoVon = kontoVon;
	}
	
	public Konto getKontoNach() {
		return kontoNach;
	}

	public void setKontoNach(Konto kontoNach) {
		this.kontoNach = kontoNach;
	}

	@Override
	public String toString() {
		return "Umbuchung von " + this.getBetrag() + " Euro von " + kontoVon + " zu " + kontoNach + " am " + super.getFormatiertesDatum();
	}



}
