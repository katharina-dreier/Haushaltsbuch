package org.meinprojekt.haushalt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Konto {
	private static int anzahlKonten = 0; //hier müssen noch methoden eingefügt werden um die Daten aus der Datei zu laden
	private static double gesamtSumme = 0;  //hier müssen noch methoden eingefügt werden um die Daten aus der Datei zu laden
	private int kontonummer;
	private String kontoName;
	private String inhaber;
	private double kontostand;
	private String kreditinstitut;
	
    public List<Buchung> buchungen;
    static Map<Integer, Konto> konten = new HashMap<>();
   
	
	public Konto(String kontoName, String inhaber, double kontostand, String Kreditinstitut) {
		// Attribute belegen
		this.kontoName = kontoName;
		this.inhaber = inhaber;
		this.kontostand = kontostand;
		this.kreditinstitut = Kreditinstitut;
		anzahlKonten++;
		kontonummer = anzahlKonten;
		// Gesamtsumme aktualisieren
		gesamtSumme += kontostand;
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

	public double getKontostand() {
		return kontostand;
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

	public static double getGesamtSumme() {
		return gesamtSumme;
	}
	
	public static List<Konto> getAlleKonten() {
	    return new ArrayList<>(konten.values());
	}

	
	// Einzahlungen, Auszahlungen und Umbuchungen
	
	public void einzahlen(Buchung buchung) {
		kontostand += buchung.getBetrag();
		gesamtSumme += buchung.getBetrag();
	}
	
	public void auszahlen(Buchung buchung) {
		kontostand -= buchung.getBetrag();
		gesamtSumme -= buchung.getBetrag();
	}
	
	@Override
	public String toString() {
		return "Konto von " + inhaber + ", Institut: "+ kreditinstitut+", Kontostand: " + kontostand + " Euro";
	}
	public String toCSV() {		
		return kontonummer + ";" + kreditinstitut + ";" + kontoName + ";" + inhaber + ";" + kontostand;
	}
	
	public void addBuchung(Buchung b) {
        buchungen.add(b);
    }

	public void removeBuchung(Buchung b) {
		buchungen.remove(b);
	}
	
	
}
