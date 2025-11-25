package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;

public class Ausgabe extends Buchung{
	
	
	private String empfaenger;
	
	public Ausgabe(double betrag, String Kategorie, Konto konto, String empfaenger, LocalDate buchungsDatum, String transferID, boolean isUmbuchung) {
		super(betrag, Kategorie, buchungsDatum);
		this.empfaenger = empfaenger;
		super.setSender(konto.getInhaber());
		super.setBuchungsart("Ausgabe");
		super.setKonto(konto);
		super.setTransferID(transferID);
		super.setIsUmbuchung(isUmbuchung);
		konto.buchungen.add(this); // Buchung zur Liste hinzufügen
	}

	public Ausgabe(Konto konto2, LocalDate datum, String art, String kategorie, String empfaenger2, String sender,
			double betrag, double kontostand, String transferID, boolean isUmbuchung) {
		super(konto2, datum, art, kategorie, empfaenger2, sender, betrag, kontostand, transferID, isUmbuchung);
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
