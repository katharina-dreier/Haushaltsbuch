import java.util.Scanner;

public class MenueFuehrung {

	static int eingabe = 0;
	static int auswahl;

	static Scanner scan = new Scanner(System.in);


	public static int hauptMenue() {
		System.out.println("Hauptmenü:");
		System.out.println("Bitte wählen Sie einen Menüpunkt aus: ");
		System.out.println("1: Konten erstellen und einsehen");
		System.out.println("2: Buchungen vornehmen");
		System.out.println("3: Statistiken anzeigen");
		System.out.println("9: Programm beenden und schließen");
		auswahl = scan.nextInt();
		System.out.println("Sie haben Nummer " + auswahl + " gewählt.");
		scan.nextLine(); // Zeilenumbruch löschen
		return auswahl;

	}

	public static int Untermenue1() {
		System.out.println("Konten erstellen und einsehen:");
		System.out.println("Bitte wählen Sie eine Aktion aus: ");
		System.out.println("1: Konto erstellen");
		System.out.println("2: Liste aller Konten aufrufen");
		System.out.println("3: Kontostand eines Kontos abrufen");
		System.out.println("4: Zurück zum Hauptmenü");
		auswahl = scan.nextInt();
		System.out.println("Sie haben Nummer " + auswahl + " gewählt.");
		scan.nextLine(); // Zeilenumbruch löschen
		return auswahl;

	}

	public static int Untermenue2() {
		System.out.println("Buchungen vornehmen:");
		System.out.println("Bitte wählen Sie eine Aktion aus: ");
		System.out.println("1: Einnahme vornehmen");
		System.out.println("2: Ausgabe vornehmen");
		System.out.println("3: Umbuchung vornehmen");
		System.out.println("4: Zurück zum Hauptmenü");
		auswahl = scan.nextInt();
		System.out.println("Sie haben Nummer " + auswahl + " gewählt.");
		scan.nextLine(); // Zeilenumbruch löschen
		return auswahl;

	}

	public static int Untermenue3() {
		System.out.println("Statistiken anzeigen:");
		System.out.println("Bitte wählen Sie eine Aktion aus: ");
		System.out.println("1: Gesamtsumme aller Konten abrufen");
		System.out.println("2: Nach Konto filtern");
		System.out.println("3: Nach Kategorie filtern");
		System.out.println("4: Nach Zeitraum filtern");
		System.out.println("5: Nach Ort/Geschäft filtern");
		System.out.println("6: Zurück zum Hauptmenü");
		auswahl = scan.nextInt();
		System.out.println("Sie haben Nummer " + auswahl + " gewählt.");
		scan.nextLine(); // Zeilenumbruch löschen
		return auswahl;

	}

	// alte Menuführung, die nicht mehr verwendet wird:
	/*
	 * public static int hauptMenue() { System.out.println("Hauptmenü:");
	 * System.out.println("Bitte wählen Sie eine Aktion aus: ");
	 * System.out.println("1: Konto erstellen");
	 * System.out.println("2: Einzahlung tätigen");
	 * System.out.println("3: Auszahlung tätigen");
	 * System.out.println("4: Umbuchung tätigen");
	 * System.out.println("5: Kontostand abfragen");
	 * System.out.println("6: Gesamtsumme abfragen");
	 * System.out.println("7: Beenden"); auswahl = scan.nextInt();
	 * System.out.println("Sie haben Nummer " + auswahl + " gewählt.");
	 * scan.nextLine(); // Zeilenumbruch löschen return auswahl; }
	 */

}
