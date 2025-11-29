package org.meinprojekt.haushalt.core.filter;

public enum ZeitraumArt {
	AKTUELLER_MONAT("Aktueller Monat"),
	VORHERIGER_MONAT("Vorheriger Monat"),
	AKTUELLES_JAHR("Aktuelles Jahr"),
	VORHERIGES_JAHR("Vorheriges Jahr"),
	ALLE_ZEITEN("Gesamter Zeitraum"),
	BENUTZERDEFINIERT("Benutzerdefinierter Zeitraum");

	 private final String label;

	    ZeitraumArt(String label) {
	        this.label = label;
	    }

	    @Override
	    public String toString() {
	        return label;  
	    }
	    
	    public static Zeitraum zeitraumAusArt(ZeitraumArt zeitraumArt) {
	    	switch (zeitraumArt) {
	        case AKTUELLER_MONAT: return Zeitraum.aktuellerMonat();
	        case VORHERIGER_MONAT: return Zeitraum.vorherigerMonat();
	        case AKTUELLES_JAHR: return Zeitraum.aktuellesJahr();
	        case VORHERIGES_JAHR: return Zeitraum.vorherigesJahr();
	      
	    }
	    	
	    return null;
	
	}}


