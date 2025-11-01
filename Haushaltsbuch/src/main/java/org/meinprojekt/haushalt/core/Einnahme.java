package org.meinprojekt.haushalt.core;

import java.time.LocalDate;


public class Einnahme extends Buchung{
	
	
	private String sender;
	
	
	public Einnahme(double betragEin, String Kategorie, Konto konto, String sender, LocalDate buchungsDatum) {
		super(betragEin, Kategorie, buchungsDatum);
		super.setKonto(konto);
		this.sender = sender;
		super.setEmpfaenger(konto.getInhaber());
		super.setBuchungsart("Einnahme");
		konto.buchungen.add(this); //Buchung zur Liste hinzufügen
	}

	public Einnahme(Konto konto, LocalDate datum, String art, String kategorie, String empfaenger, String sender,
			double betrag, double kontostand) {
		super(konto, datum, art, kategorie, empfaenger, sender, betrag, kontostand);
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



	
	