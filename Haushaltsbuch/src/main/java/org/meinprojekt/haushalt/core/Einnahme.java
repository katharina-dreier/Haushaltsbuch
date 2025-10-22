package org.meinprojekt.haushalt.core;

import java.time.LocalDate;


public class Einnahme extends Buchung{
	
	private Konto konto;
	private String sender;
	
	
	public Einnahme(double betragEin, String Kategorie, Konto konto, String sender, LocalDate buchungsDatum) {
		super(betragEin, Kategorie, buchungsDatum);
		this.konto = konto;
		this.sender = sender;
		konto.buchungen.add(this); //Buchung zur Liste hinzufügen
	}

	public Konto getKonto() {
		return konto;
	}

	public void setKonto(Konto konto) {
		this.konto = konto;
	}
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
	@Override
	public String toString() {
		return "Einnahme von " + " in Höhe von " + getBetrag() + " Euro" + " am " + super.getFormatiertesDatum()+ " Kategorie: "+ getKategorie();
	}


}
	
	