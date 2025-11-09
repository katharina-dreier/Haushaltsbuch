package org.meinprojekt.haushalt.core;



public class TestKlasse {

	public static void main(String[] args) {
		
		Konto konto1 = new Konto("Gehaltskonto", "Katharina", 0, "Volksbank");
		System.out.println(konto1);
		
		System.out.println(Datenstroeme.ordnerpfad + Datenstroeme.bildeDateiName(konto1));
		
		
			
			System.out.println(konto1.buchungen);

	}

}
