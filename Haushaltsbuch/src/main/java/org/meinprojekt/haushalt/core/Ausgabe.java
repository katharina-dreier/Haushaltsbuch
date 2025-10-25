package org.meinprojekt.haushalt.core;

import java.time.LocalDate;

public class Ausgabe extends Buchung{
	
	private Konto konto;
	private String empfaenger;
	
	public Ausgabe(double betrag, String Kategorie, Konto konto, String empfaenger, LocalDate buchungsDatum) {
		super(betrag, Kategorie, buchungsDatum);
		this.konto = konto;
		this.empfaenger = empfaenger;
		konto.buchungen.add(this); // Buchung zur Liste hinzufügen
	}

	public Konto getKonto() {
		return konto;
	}

	public void setKonto(Konto konto) {
		this.konto = konto;
	}

	public String getEmpfaenger() {
		return empfaenger;
	}
	
	@Override
	public void setEmpfaenger(String empfaenger) {
		this.empfaenger = empfaenger;
	}
	
	
	@Override
	public String toString() {
		return "Ausgabe von " + getBetrag() + " Euro bei " + empfaenger + " am " + getFormatiertesDatum()+ " Kategorie: "+ getKategorie();
	}


}
