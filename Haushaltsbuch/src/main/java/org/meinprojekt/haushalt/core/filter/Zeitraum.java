package org.meinprojekt.haushalt.core.filter;

import java.time.LocalDate;

public class Zeitraum {
	    private final LocalDate von;
	    private final LocalDate bis;

	    // Konstruktor + Getter
	    
		public Zeitraum(LocalDate von, LocalDate bis) {
			this.von = von;
			this.bis = bis;
		}
		
		public LocalDate getVon() {
			return von;
			}
		
		public LocalDate getBis() {
			return bis;
			}
		
		// Statische Fabrikmethoden
	    public static Zeitraum aktuellerMonat() {
			LocalDate jetzt = LocalDate.now();
			return monatAusDatum(jetzt);
	    }
	    
		public static Zeitraum monatAusDatum(LocalDate datum) {
        	            LocalDate von = datum.withDayOfMonth(1);
        	            LocalDate bis = datum.withDayOfMonth(datum.lengthOfMonth());
        	            return new Zeitraum(von, bis);
	    }
	    
	    public static Zeitraum vorherigerMonat() {
	    	            LocalDate letzerMonat = LocalDate.now().minusMonths(1);
	    	            return monatAusDatum(letzerMonat);            
	    }
	    
	    
	    public static Zeitraum aktuellesJahr()  {
	    	            LocalDate jetzt = LocalDate.now();
	    	            return jahrAusDatum(jetzt);
	    }
	    
	    public static Zeitraum jahrAusDatum(LocalDate datum) {
            LocalDate von = datum.withDayOfYear(1);
            LocalDate bis = datum.withDayOfYear(datum.lengthOfYear());
            return new Zeitraum(von, bis);
}
	    
		public static Zeitraum vorherigesJahr() {
			LocalDate letztesJahr = LocalDate.now().minusYears(1);
			return jahrAusDatum(letztesJahr);
		}
	    
		public static Zeitraum benutzerdefinierterZeitraum(LocalDate von, LocalDate bis) {
			return new Zeitraum(von, bis);
	}
		
		@Override
	public String toString() {
		return "Von: " + von.toString() + " Bis: " + bis.toString();
	}


}
