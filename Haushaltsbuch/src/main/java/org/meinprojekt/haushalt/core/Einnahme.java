package org.meinprojekt.haushalt.core;

import java.time.LocalDate;


public class Einnahme extends Buchung{
	
	private Konto konto;
	private String sender;
	
	
	public Einnahme(double betragEin, String Kategorie, Konto konto, String sender, LocalDate buchungsDatum) {
		super(betragEin, Kategorie, buchungsDatum);
		this.konto = konto;
		this.sender = sender;
		super.setEmpfaenger(konto.getInhaber());
		super.setBuchungsart("Einnahme");
		konto.buchungen.add(this); //Buchung zur Liste hinzufügen
	}

	@Override
	public Konto getKonto() {
		return konto;
	}

	public void setKonto(Konto konto) {
		this.konto = konto;
	}
	
	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
	@Override
	public String toString() {
		return "Einnahme von " + " in Höhe von " + getBetrag() + " Euro" + " am " + super.getFormatiertesDatum()+ " Kategorie: "+ getKategorie();
	}


}
	
	