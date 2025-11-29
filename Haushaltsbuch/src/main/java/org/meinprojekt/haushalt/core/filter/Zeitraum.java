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
			LocalDate von = jetzt.withDayOfMonth(1);
			LocalDate bis = jetzt.withDayOfMonth(jetzt.lengthOfMonth());
			return new Zeitraum(von, bis);
	    }
	    
	    public static Zeitraum vorherigerMonat() {
	    	            LocalDate letzerMonat = LocalDate.now().minusMonths(1);
	    	            LocalDate von = letzerMonat.withDayOfMonth(1);
	    	            LocalDate bis = letzerMonat.withDayOfMonth(letzerMonat.lengthOfMonth());
	    	            return new Zeitraum(von, bis);            
	    }
	    
	    
	    public static Zeitraum aktuellesJahr()  {
	    	            LocalDate jetzt = LocalDate.now();
	    	            LocalDate von = jetzt.withDayOfYear(1);
	    	            LocalDate bis = jetzt.withDayOfYear(jetzt.lengthOfYear());
	    	            return new Zeitraum(von, bis);
	    }
	    
		public static Zeitraum vorherigesJahr() {
			LocalDate letztesJahr = LocalDate.now().minusYears(1);
			LocalDate von = letztesJahr.withDayOfYear(1);
			LocalDate bis = letztesJahr.withDayOfYear(letztesJahr.lengthOfYear());
			return new Zeitraum(von, bis);
		}
	    
		public static Zeitraum benutzerdefinierterZeitraum(LocalDate von, LocalDate bis) {
			return new Zeitraum(von, bis);
	}
		
		@Override
	public String toString() {
		return "Von: " + von.toString() + " Bis: " + bis.toString();
	}


}
