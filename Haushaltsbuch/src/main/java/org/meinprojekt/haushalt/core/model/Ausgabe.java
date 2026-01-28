package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;

public class Ausgabe extends Buchung{
	
	
	private String empfaenger;
	
	public Ausgabe(double betrag, String kategorie, String beschreibung, Konto konto, String empfaenger, LocalDate buchungsDatum, String transferID, boolean isUmbuchung) {
		super(betrag, kategorie, buchungsDatum);
		this.empfaenger = empfaenger;
		super.setSender(konto.getInhaber());
		super.setBuchungsart("Ausgabe");
		super.setKonto(konto);
		super.setTransferID(transferID);
		super.setIsUmbuchung(isUmbuchung);
		super.setBeschreibung(beschreibung);
		konto.buchungen.add(this); // Buchung zur Liste hinzufügen
	}

	public Ausgabe(Konto konto2, LocalDate datum, String art, String kategorie, String beschreibung, String empfaenger2, String sender,
			double betrag, String transferID, boolean isUmbuchung) {
		super(konto2, datum, art, kategorie, beschreibung, empfaenger2, sender, betrag, transferID, isUmbuchung);
	}

	@Override
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
