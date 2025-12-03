package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;

public class Fixkosten extends Ausgabe{
	
	private LocalDate faelligkeitsDatum;
	private String häufigkeit;
	private boolean bezahlt;
	private LocalDate bezahltAm;
	private LocalDate naechsteZahlungAm;
	
	public Fixkosten(double betrag, String Kategorie, String beschreibung, Konto konto, String empfaenger, LocalDate buchungsDatum,
			LocalDate faelligkeitsDatum, String häufigkeit, boolean bezahlt, LocalDate bezahltAm, LocalDate naechsteZahlungAm) {
		super(betrag, Kategorie, beschreibung, konto, empfaenger, buchungsDatum, "", false);
		this.faelligkeitsDatum = faelligkeitsDatum;
		this.häufigkeit = häufigkeit;
		this.bezahlt = bezahlt;
		this.bezahltAm = bezahltAm;
		this.naechsteZahlungAm = naechsteZahlungAm;
	}

	public LocalDate getFaelligkeitsDatum() {
		return faelligkeitsDatum;
	}

	public void setFaelligkeitsDatum(LocalDate faelligkeitsDatum) {
		this.faelligkeitsDatum = faelligkeitsDatum;
	}

	public String getHäufigkeit() {
		return häufigkeit;
	}

	public void setHäufigkeit(String häufigkeit) {
		this.häufigkeit = häufigkeit;
	}

	public boolean isBezahlt() {
		return bezahlt;
	}

	public void setBezahlt(boolean bezahlt) {
		this.bezahlt = bezahlt;
	}

	public LocalDate getBezahltAm() {
		return bezahltAm;
	}

	public void setBezahltAm(LocalDate bezahltAm) {
		this.bezahltAm = bezahltAm;
	}

	public LocalDate getNaechsteZahlungAm() {
		return naechsteZahlungAm;
	}

	public void setNaechsteZahlungAm(LocalDate naechsteZahlungAm) {
		this.naechsteZahlungAm = naechsteZahlungAm;
	}
	
	
	

}
