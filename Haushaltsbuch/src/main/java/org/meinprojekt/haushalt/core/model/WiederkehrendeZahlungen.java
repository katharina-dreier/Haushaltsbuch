package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;

public class WiederkehrendeZahlungen {
	
	private double betrag;
	private String kategorie;
	private String beschreibung;
	private Konto konto;
	private String empfaenger;
	private String sender;
	private String buchungsart; // Einnahme, Ausgabe
	private String häufigkeit;
	private LocalDate naechsteZahlungAm;
	
	public WiederkehrendeZahlungen(double betrag, String Kategorie, String beschreibung, Konto konto, String empfaenger, String sender, String buchungsart, String häufigkeit, LocalDate naechsteZahlungAm) {
		this.betrag = betrag;
		this.kategorie = Kategorie;
		this.beschreibung = beschreibung;
		this.konto = konto;
		this.empfaenger = empfaenger;
		this.sender = sender;
		this.buchungsart = buchungsart;
		this.häufigkeit = häufigkeit;
		this.naechsteZahlungAm = naechsteZahlungAm;
	}

	public String getHäufigkeit() {
		return häufigkeit;
	}

	public void setHäufigkeit(String häufigkeit) {
		this.häufigkeit = häufigkeit;
	}

	public double getBetrag() {
		return betrag;
	}

	public void setBetrag(double betrag) {
		this.betrag = betrag;
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
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

	public void setEmpfaenger(String empfaenger) {
		this.empfaenger = empfaenger;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getBuchungsart() {
		return buchungsart;
	}

	public void setBuchungsart(String buchungsart) {
		this.buchungsart = buchungsart;
	}

	public LocalDate getNaechsteZahlungAm() {
		return naechsteZahlungAm;
	}

	public void setNaechsteZahlungAm(LocalDate naechsteZahlungAm) {
		this.naechsteZahlungAm = naechsteZahlungAm;
	}
	
	
	

}
