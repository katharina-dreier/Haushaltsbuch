package org.meinprojekt.haushalt.core.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Buchung {
	
	
	private double betrag;
	private String kategorie;
	private String beschreibung;
	private LocalDate buchungsDatum;
	private String buchungsart; // Einnahme, Ausgabe, Umbuchung
	private String empfaenger;
	private String sender;
	private Konto konto; 
	private boolean isUmbuchung = false;
	private String transferID = "";
	
	public static ArrayList<String> listeMitKategorien = new ArrayList<>();

	public Buchung(double betrag, String kategorie, LocalDate buchungsDatum) {
		this.betrag = betrag;
		this.kategorie = kategorie;
		this.beschreibung = "";
		this.buchungsDatum = buchungsDatum;
		this.buchungsart = "Buchung";
		this.empfaenger = "";
		this.sender = "";
		kategorieHinzufuegen(kategorie);
	}
	
	public Buchung (Konto konto, LocalDate datum, String buchungsart, String kategorie, String beschreibung, String empfänger , String sender , double betrag2, String transferID, boolean isUmbuchung) {
		this.konto = konto;
		this.buchungsDatum = datum;
		this.buchungsart = buchungsart;
		this.kategorie = kategorie;
		this.beschreibung = beschreibung;
		this.setEmpfaenger(empfänger);
		this.setSender(sender);
		this.betrag = betrag2;
		kategorieHinzufuegen(kategorie);
		this.transferID = transferID;
		this.isUmbuchung = isUmbuchung;
	}
	
	public String getTransferID() {
		return transferID;
	}
	
	public void setTransferID(String transferID) {
		this.transferID = transferID;
	}
	
	public boolean getIsUmbuchung() {
		return isUmbuchung;
	}
	
	public void setIsUmbuchung(boolean isUmbuchung) {
		this.isUmbuchung = isUmbuchung;
	}
	
	public String getFormatiertesDatum() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return buchungsDatum.format(formatter); 
    }
	
	public Konto getKonto() {
		return konto;
	}


	public void setKonto(Konto konto) {
		this.konto = konto;
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
	
	
	public void kategorieHinzufuegen(String kategorie) {
		if (kategorie != null && !kategorie.isBlank() && !listeMitKategorien.contains(kategorie)) {
			listeMitKategorien.add(kategorie);
		}
	}
	
	public String getBuchungsart() {
        return buchungsart;
	}

	public void setBuchungsart(String buchungsart) {
		this.buchungsart = buchungsart;
	}
	

	public String getKontoAnzeige() {
	    if (konto == null) return "";
	    return konto.getKontoName() + " (" + konto.getKreditinstitut() + ")";
	}

	



}
