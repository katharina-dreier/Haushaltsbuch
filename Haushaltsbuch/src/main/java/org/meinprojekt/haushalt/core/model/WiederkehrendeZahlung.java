package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;

import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;

import javafx.util.Callback;

public class WiederkehrendeZahlung {
	
	public enum Haeufigkeit {
		MONATLICH("Monatlich"),
		QUARTALSWEISE("Quartalsweise"),
		JAEHRLICH("Jährlich");
		
		 private final String label;

		    Haeufigkeit(String label) {
		        this.label = label;
		    }

		    @Override
		    public String toString() {
		        return label;  
		    }
		    
		    public static Haeufigkeit haeufigkeitFromString(String text) {
		        for (Haeufigkeit h : Haeufigkeit.values()) {
		            if (h.label.equalsIgnoreCase(text)) {
		                return h;
		            }
		        }
		        throw new IllegalArgumentException("Unbekannte Häufigkeit: " + text);

	}
	}
	
	private double betrag;
	private String kategorie;
	private String beschreibung;
	private Konto konto;
	private String empfaenger;
	private String sender;
	private String buchungsart; // Einnahme, Ausgabe
	private Buchungstyp typ;
	

	private Haeufigkeit haeufigkeit;
	private LocalDate naechsteZahlungAm;
	private LocalDate letzteZahlungAm = null;
	
	public WiederkehrendeZahlung() {

	}
	
	
	public WiederkehrendeZahlung(BuchungsDaten daten, Haeufigkeit haeufigkeit) {
		 	this.typ = daten.getTyp();
	        this.konto = daten.getKonto();
	        this.kategorie = daten.getKategorie();
	        this.beschreibung = daten.getBeschreibung();
	        this.haeufigkeit = haeufigkeit;
	        this.naechsteZahlungAm = daten.getBuchungsdatum();

	        if (typ == Buchungstyp.EINNAHME) {
	            this.betrag = Math.abs(daten.getBetrag());
	            this.sender = daten.getGegenpartei();
	            this.empfaenger = konto.getInhaber();
	        } else {
	            this.betrag = -Math.abs(daten.getBetrag());
	            this.sender = konto.getInhaber();
	            this.empfaenger = daten.getGegenpartei();
		
	}
	}

	public Haeufigkeit getHaeufigkeit() {
		return haeufigkeit;
	}

	public void setHaeufigkeit(Haeufigkeit haeufigkeit) {
		this.haeufigkeit = haeufigkeit;
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
	
	public String getKontoAnzeige() {
	    if (konto == null) return "";
	    return konto.getKontoName() + " (" + konto.getKreditinstitut() + ")";
	}

	public void setLetzteZahlungAm(LocalDate aktuellesDatum) {
		this.letzteZahlungAm = aktuellesDatum;
		
	}
	
	public LocalDate getLetzteZahlungAm() {
		return letzteZahlungAm;
	}
	
	public Buchungstyp getBuchungstyp() {
		return typ;
	}


	public void setBuchungstyp(Buchungstyp typ) {
		this.typ = typ;
	}
	

}
