package org.meinprojekt.haushalt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Konto {
	private static int anzahlKonten = 0; 
	private int kontonummer;
	private String kontoName;
	private String inhaber;
	private double kontostand;
	private double kontostandBeiErstellung;
	private String kreditinstitut;
	
    public List<Buchung> buchungen;
    static Map<Integer, Konto> konten = new HashMap<>();
   
    
        // Konstruktor

	public Konto(String kontoName, String inhaber, double kontostand, String kreditinstitut) {
		// Attribute belegen
		this.kontoName = kontoName;
		this.inhaber = inhaber;
		this.kontostandBeiErstellung = kontostand;
		this.kreditinstitut = kreditinstitut;
		anzahlKonten++;
		kontonummer = anzahlKonten;		
		// Konto in die Liste einfügen
		this.buchungen = new ArrayList<>();
	}
	// Getter und Setter
	public int getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(int kontonummer) {
		this.kontonummer = kontonummer;
	}

	public String getKontoName() {
		return kontoName;
	}

	public void setKontoName(String kontoName) {
		this.kontoName = kontoName;
	}

	public void setBuchungen(List<Buchung> buchungen) {
		this.buchungen = buchungen;
	}

	public static int getAnzahlKonten() {
		return anzahlKonten;
	}

	public static void setAnzahlKonten(int anzahlKonten) {
		Konto.anzahlKonten = anzahlKonten;
	}

	public String getInhaber() {
		return inhaber;
	}

	public void setInhaber(String inhaber) {
		this.inhaber = inhaber;
	}


	public void setKontostand(double kontostand) {
		this.kontostand = kontostand;
	}

	public String getKreditinstitut() {
		return kreditinstitut;
	}

	public void setKreditinstitut(String kreditinstitut) {
		this.kreditinstitut = kreditinstitut;
	}
	
	 public List<Buchung> getBuchungen() {
	        return buchungen;
	    }

	
	public static List<Konto> getAlleKonten() {
	    return new ArrayList<>(konten.values());
	}
	
	public static List<Buchung> getAlleBuchungen() {
		List<Buchung> alleBuchungen = new ArrayList<>();
		for (Konto konto : konten.values()) {
			alleBuchungen.addAll(konto.getBuchungen());
		}
		return alleBuchungen;
	}

	
	// Einzahlungen, Auszahlungen und Umbuchungen
	
	/*public void einzahlen(Buchung buchung) {
		kontostand += buchung.getBetrag();
		System.out.println("Einzahlung von " + buchung.getBetrag() + " Euro auf Konto: " + kontoName);
		System.out.println("Neuer Kontostand nach Einzahlung: " + kontostand + " Euro");
	}
	
	public void auszahlen(Buchung buchung) {
		kontostand -= buchung.getBetrag();
		System.out.println("Auszahlung von " + buchung.getBetrag() + " Euro von Konto: " + kontoName);
		System.out.println("Neuer Kontostand nach Auszahlung: " + kontostand + " Euro");
	}*/
	
	@Override
	public String toString() {
		return "Konto " + kontoName + " von " + inhaber + ", Institut: "+ kreditinstitut+", Kontostand: " + this.getKontostand() + " Euro";
	}
	public String toCSV() {		
		return kontonummer + ";" + kreditinstitut + ";" + kontoName + ";" + inhaber + ";" + kontostandBeiErstellung;
	}
	
	public void addBuchung(Buchung b) {
        buchungen.add(b);
    }

	public void removeBuchung(Buchung b) {
		buchungen.remove(b);
	}
	public double getKontostandBeiErstellung() {
		return kontostandBeiErstellung;
	}
	public void setKontostandBeiErstellung(double saldo) {
		this.kontostandBeiErstellung = saldo;
		
	}
	
	public double getKontostand() {
		double berechneterKontostand = 0.0;
		for (Buchung b : buchungen) {
			if (b.getBuchungsart().equals("Einnahme")) {
				berechneterKontostand += b.getBetrag();
			} else if (b.getBuchungsart().equals("Ausgabe")) {
				berechneterKontostand -= b.getBetrag();
			}
		}
		return berechneterKontostand;
	}
	
	
}
