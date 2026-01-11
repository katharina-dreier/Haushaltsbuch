package org.meinprojekt.haushalt.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Konto {
	private static int anzahlKonten = 0; 
	private int kontonummer;
	private String kontoName;
	private String inhaber;
	private double kontostandBeiErstellung;
	private String kreditinstitut;
	
    public List<Buchung> buchungen;
    public List<WiederkehrendeZahlung> wiederkehrendeZahlungen;
    private static Map<Integer, Konto> konten = new HashMap<>();
   
    
        // Konstruktoren
    
		public Konto() {
    	anzahlKonten++;
    	kontonummer = anzahlKonten;
    	this.buchungen = new ArrayList<>();
		this.wiederkehrendeZahlungen = new ArrayList<>();
		}

	public Konto(String kontoName, String inhaber, double kontostand, String kreditinstitut) {
		// Attribute belegen
		this.kontoName = kontoName;
		this.inhaber = inhaber;
		this.kontostandBeiErstellung = kontostand;
		this.kreditinstitut = kreditinstitut;
		anzahlKonten++;
		kontonummer = anzahlKonten;		
		this.buchungen = new ArrayList<>();
		this.wiederkehrendeZahlungen = new ArrayList<>();
	}
	
	// Getter und Setter
	public int getKontonummer() {
		return kontonummer;
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
	
	public void setWiederkehrendeZahlungen(List<WiederkehrendeZahlung> wiederkehrendeZahlungen) {
		this.wiederkehrendeZahlungen = wiederkehrendeZahlungen;
	}

	public static int getAnzahlKonten() {
		return anzahlKonten;
	}


	public String getInhaber() {
		return inhaber;
	}

	public void setInhaber(String inhaber) {
		this.inhaber = inhaber;
	}

	public String getKreditinstitut() {
		return kreditinstitut;
	}

	public void setKreditinstitut(String kreditinstitut) {
		this.kreditinstitut = kreditinstitut;
	}
	
	public double getKontostandBeiErstellung() {
		return kontostandBeiErstellung;
	}
	public void setKontostandBeiErstellung(double saldo) {
		this.kontostandBeiErstellung = saldo;
		
	}
	
	 public List<Buchung> getBuchungen() {
	        return buchungen;
	    }
	 
		public List<WiederkehrendeZahlung> getWiederkehrendeZahlungen() {
			return wiederkehrendeZahlungen;
		}

	
	public static List<Konto> getAlleKonten() {
	    return new ArrayList<>(getKonten().values());
	}
	
	public static List<Buchung> getAlleBuchungen() {
		List<Buchung> alleBuchungen = new ArrayList<>();
		for (Konto konto : getKonten().values()) {
			alleBuchungen.addAll(konto.getBuchungen());
		}
		return alleBuchungen;
	}
	
	public static List<WiederkehrendeZahlung> getAlleWiederkehrendeZahlungen() {
		List<WiederkehrendeZahlung> alleWiederkehrendeZahlungen = new ArrayList<>();
		for (Konto konto : getKonten().values()) {
			alleWiederkehrendeZahlungen.addAll(konto.getWiederkehrendeZahlungen());
		}
		return alleWiederkehrendeZahlungen;
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
	
	@Override
	public String toString() {
		return "Konto " + kontoName + " von " + inhaber + ", Institut: "+ kreditinstitut+", Kontostand: " + this.getKontostand() + " Euro";
	}
	public String toCSV() {		
		return kontonummer + ";" + kreditinstitut + ";" + kontoName + ";" + inhaber + ";" + kontostandBeiErstellung;
	}

	public static Map<Integer, Konto> getKonten() {
		return konten;
	}

	public static void setKonten(Map<Integer, Konto> konten) {
		Konto.konten = konten;
	}

	
	
}
