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
	    
	    
	    public static Zeitraum aktuellesJahr()  {
	    	            LocalDate jetzt = LocalDate.now();
	    	            LocalDate von = jetzt.withDayOfYear(1);
	    	            LocalDate bis = jetzt.withDayOfYear(jetzt.lengthOfYear());
	    	            return new Zeitraum(von, bis);
	    }
	    
		public static Zeitraum benutzerdefinierterZeitraum(LocalDate von, LocalDate bis) {
			return new Zeitraum(von, bis);
	}


}
