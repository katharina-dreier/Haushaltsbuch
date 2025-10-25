package org.meinprojekt.haushalt.core;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Buchung {
	
	private double betrag;
	private String kategorie;
	private LocalDate buchungsDatum;
	private String buchungsart; // Einnahme, Ausgabe, Umbuchung
	private String empfaenger;
	private String sender;
	private double kontostandNachBuchung; // optional

	
	
	

	public Buchung(double betrag, String Kategorie, LocalDate buchungsDatum) {
		this.betrag = betrag;
		this.kategorie = Kategorie;
		this.buchungsDatum = buchungsDatum;
		this.buchungsart = "Buchung";
		this.empfaenger = "";
		this.sender = "";
		
	}
	
	public Buchung (LocalDate datum, String buchungsart, String kategorie , String empfänger , String sender , double betrag2, double kontostand) {
		this.buchungsDatum = datum;
		this.buchungsart = buchungsart;
		this.kategorie = kategorie;
		this.setEmpfaenger(empfänger);
		this.setSender(sender);
		this.betrag = betrag2;
		this.kontostandNachBuchung = kontostand;
	}
	
	public String getFormatiertesDatum() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return buchungsDatum.format(formatter); 
    }
	
	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public LocalDate getBuchungsDatum() {
		return buchungsDatum;
	}

	public void setBuchungsDatum(LocalDate buchungsDatum) {
		this.buchungsDatum = buchungsDatum;
	}

	public double getBetrag() {
		return betrag;
	}

	public void setBetrag(double betrag) {
		this.betrag = betrag;
	}

	@Override
	public String toString() {
		return "Buchung über " + betrag + " Euro, Kategorie: " + kategorie + ", Datum: " + getFormatiertesDatum();
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
	


}
