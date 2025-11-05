package org.meinprojekt.haushalt.core;

import java.time.LocalDate;

public class Fixkosten extends Ausgabe{
	
	private LocalDate faelligkeitsDatum;
	private String häufigkeit;
	
	public Fixkosten(double betrag, String Kategorie, Konto konto, String empfaenger, LocalDate buchungsDatum,
			LocalDate faelligkeitsDatum, String häufigkeit) {
		super(betrag, Kategorie, konto, empfaenger, buchungsDatum, "", false);
		this.faelligkeitsDatum = faelligkeitsDatum;
		this.häufigkeit = häufigkeit;
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
	
	

}
