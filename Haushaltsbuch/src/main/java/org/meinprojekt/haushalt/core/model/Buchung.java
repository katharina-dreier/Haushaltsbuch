package org.meinprojekt.haushalt.core.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;

public class Buchung {
	
	private final UUID id = UUID.randomUUID();

	private double betrag;
	private String kategorie;
	private String beschreibung;
	private LocalDate buchungsDatum;
	private String buchungsart;// Einnahme, Ausgabe, Umbuchung
	private Buchungstyp typ;
	private String empfaenger;
	private String sender;
	private Konto konto; 
	private boolean isUmbuchung = false;
	private String transferID = "";
	
	public static List<String> listeMitKategorien = new ArrayList<>();

	public Buchung() {
		this.betrag = 0.0;
		this.kategorie = "";
		this.beschreibung = "";
		this.buchungsDatum = LocalDate.now();
		this.buchungsart = "Buchung";
		this.empfaenger = "";
		this.sender = "";
	}
	
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
	
	/*public Buchung (Konto konto, LocalDate datum, String buchungsart, String kategorie, String beschreibung, String empfaenger , String sender , double betrag2, String transferID, boolean isUmbuchung) {
		this.konto = konto;
		this.buchungsDatum = datum;
		this.buchungsart = buchungsart;
		this.kategorie = kategorie;
		this.beschreibung = beschreibung;
		this.setEmpfaenger(empfaenger);
		this.setSender(sender);
		this.betrag = betrag2;
		kategorieHinzufuegen(kategorie);
		this.transferID = transferID;
		this.isUmbuchung = isUmbuchung;
	}*/
	
	public Buchung(BuchungsDaten daten) {
		 this.typ = daten.getTyp();
	        this.konto = daten.getKonto();
	        this.buchungsDatum = daten.getBuchungsdatum();
	        this.kategorie = daten.getKategorie();
	        this.beschreibung = daten.getBeschreibung();

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
	
	public Buchungstyp getBuchungstyp() {
		return typ;
	}
	
	public void setBuchungstyp(Buchungstyp typ) {
		this.typ = typ;
	}
	

	public String getKontoAnzeige() {
	    if (konto == null) return "";
	    return konto.getKontoName() + " (" + konto.getKreditinstitut() + ")";
	}

	



}
