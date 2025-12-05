package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
	private Haeufigkeit haeufigkeit;
	private LocalDate naechsteZahlungAm;
	
	public WiederkehrendeZahlung(LocalDate naechsteZahlungAm, Haeufigkeit häufigkeit, String buchungsart, String kategorie, String beschreibung, String empfaenger, String sender, double betrag, Konto konto) {
		this.betrag = betrag;
		this.kategorie = kategorie;
		this.beschreibung = beschreibung;
		this.konto = konto;
		this.empfaenger = empfaenger;
		this.sender = sender;
		this.buchungsart = buchungsart;
		this.haeufigkeit = häufigkeit;
		this.naechsteZahlungAm = naechsteZahlungAm;
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

	public String getFormatiertesDatum() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        return naechsteZahlungAm.format(formatter); 
	    }
	

}
