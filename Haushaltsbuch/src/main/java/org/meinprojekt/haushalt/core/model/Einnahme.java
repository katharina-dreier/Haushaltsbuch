package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;


public class Einnahme extends Buchung{
	
	
	private String sender;
	
	
	public Einnahme(double betragEin, String Kategorie, String beschreibung, Konto konto, String sender, LocalDate buchungsDatum, String transferID, boolean isUmbuchung) {
		super(betragEin, Kategorie, buchungsDatum);
		super.setKonto(konto);
		this.sender = sender;
		super.setEmpfaenger(konto.getInhaber());
		super.setBuchungsart("Einnahme");
		super.setTransferID(transferID);
		super.setIsUmbuchung(isUmbuchung);
		super.setBeschreibung(beschreibung);
		konto.buchungen.add(this); //Buchung zur Liste hinzufügen
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



	
	