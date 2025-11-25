package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;
import java.util.UUID;

public class Umbuchung extends Buchung{
	
	private Konto kontoVon;
	private Konto kontoNach;
	

	public Umbuchung(double betrag, Konto kontoVon, Konto kontoNach, LocalDate buchungsDatum) {
		super(betrag, "Umbuchung", buchungsDatum);
		this.kontoVon = kontoVon;
		this.kontoNach = kontoNach;
		super.setSender(kontoVon.getKontoName() + "(" + kontoVon.getKreditinstitut() + ")");
		super.setEmpfaenger(kontoNach.getKontoName() + "(" + kontoNach.getKreditinstitut() + ")");
		super.setTransferID(UUID.randomUUID().toString());
		super.setIsUmbuchung(true);
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
