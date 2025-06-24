import java.util.Scanner;

public class KontoAktionen {
	
	static Konto gewaehltesKonto;
	static Scanner scan = new Scanner(System.in);
	
	//Notwendige Daten von der Konsole einlesen und Konto erstellen
		public static void kontoErstellen() {
			System.out.println("Bitte geben Sie den Namen des Kontoinhabers ein: ");
			String name = scan.nextLine();
			System.out.println("Bitte geben Sie den Namen des Kontos ein: ");
			String kontoName = scan.nextLine();
			System.out.println("Bitte geben Sie den Kontostand ein: ");
			double kontostand = scan.nextDouble();
			scan.nextLine();
			System.out.println("Bitte geben Sie die Bank ein: ");
			String bank = scan.nextLine();
			Konto konto = new Konto(kontoName, name, kontostand, bank);
			Konto.konten.put(konto.getKontonummer(), konto);
			// Konto in die Datei einfügen
			Datenstroeme.kontoHinzufuegen(konto);
			System.out.println("Konto wurde erstellt: " + konto);
		}
		
		//Kontenliste anzeigen und Konto nach Eingabe auswählen
		public static Konto kontoAuswaehlen() {
			System.out.println("Bitte wähle ein Konto aus (Gib dazu die Nummer ein):");
			for (Konto konto : Konto.konten.values()) {
				System.out.println(konto.getKontonummer() + ": " + konto.getKontoName() + " bei " + konto.getKreditinstitut() + " (Verfügbarer Kontostand: " + konto.getKontostand() + " Euro)");
			}
			int eingabe = scan.nextInt();
			scan.nextLine(); // Zeilenumbruch löschen
			gewaehltesKonto = Konto.konten.get(eingabe);
			System.out.println("Du hast \"" + gewaehltesKonto.getKontoName() + "\" gewählt.");
			return gewaehltesKonto;
		}

		public static void kontenListeAnzeigen() {
			System.out.println("Folgende Konten sind gespeichert:");
			for (Konto konto : Konto.konten.values()) {
				if(konto.getKreditinstitut() != null) {
					System.out.println(konto.getKontonummer() + ": " + konto.getKontoName() + " bei "
						+ konto.getKreditinstitut() + " (Verfügbarer Kontostand: " + konto.getKontostand() + " Euro)");
			}
				else {
                    System.out.println(konto.getKontonummer() + ": " + konto.getKontoName() 
                    + " (Verfügbarer Kontostand: " + konto.getKontostand() + " Euro)");
                }
			
		}
		}
}
