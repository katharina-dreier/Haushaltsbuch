package org.meinprojekt.haushalt.core.service;

import java.time.LocalDate;
import java.util.ArrayList;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KontoService {
	
	//Aus Dialogfenster erhaltene Daten verwenden um Konto zu erstellen
		public static void kontoErstellen(String kontoName, String inhaber, double kontostand, String kreditinstitut) {
			System.out.println("Kontoerstellung gestartet mit folgenden Daten: " + kontoName + ", " + inhaber + ", " + kontostand + ", " + kreditinstitut);
			Konto konto = new Konto(kontoName, inhaber, kontostand, kreditinstitut);
			Konto.getKonten().put(konto.getKontonummer(), konto);
			BuchungsService.einnahmeTätigen(kontostand, "Kontoerstellung", "Initiale Buchung zu diesem Konto", konto, inhaber, LocalDate.now() , "", false);
			System.out.println("Konto wurde erstellt: " + konto);
		}
		
		public static ObservableList<Konto> getAlleKontenAlsObservableList() {
			ObservableList<Konto> kontenListe = FXCollections.observableArrayList();
			kontenListe.addAll(Konto.getKonten().values());
			return kontenListe;
		}

		public static void loescheKonto(Konto k) {
			System.out.println("Starte mit Löschen von Konto: " + k);
			for (Buchung b : new ArrayList<>(k.buchungen)) {
				BuchungsService.loescheBuchung(b);
				}
			Datenstroeme.kontoLoeschen(k);
			Konto.getKonten().remove(k.getKontonummer());
			Datenstroeme.kontenNeuSpeichern();
			System.out.println("Konto wurde gelöscht: " + k);
		}
		
		public static void kontoBearbeiten(Konto kontoAlt, double saldo, String inhaber) {
			System.out.println("Konto wird bearbeitet: " + kontoAlt + "Saldo: " + saldo + ", Inhaber: " + inhaber);
			kontoAlt.setKontostandBeiErstellung(saldo);
			System.out.println("Kontostand bei Erstellung gesetzt auf: " + kontoAlt.getKontostandBeiErstellung());
			kontoAlt.setInhaber(inhaber);
			for (Buchung b : new ArrayList<>(kontoAlt.getBuchungen())) {
				if (b.getKategorie().contains("Kontoerstellung")) {
					b.setBetrag(saldo);
					System.out.println("Kontoerstellungsbuchung angepasst: " + b);
				}
			}
			System.out.println("Konto nach Bearbeitung: " + kontoAlt);
			Datenstroeme.kontoBuchungenNeuSpeichern(kontoAlt);
			Datenstroeme.kontenNeuSpeichern();
			System.out.println("Datenströme wurden aktualisiert. Konto: " + kontoAlt);
			System.out.println("Konto wurde erfoglreich bearbeitet: " + kontoAlt);
		}

		/*public static void kontoBearbeiten(Konto kontoAlt, String kontoname, String inhaber, double saldo,
				String institut) {
			if (kontoAlt.getKontoName().equals(kontoname) && kontoAlt.getKreditinstitut().equals(institut)) {
				kontoAlt.setKontostandBeiErstellung(saldo);
				kontoAlt.setInhaber(inhaber);
				System.out.println("Konto wurde bearbeitet: " + kontoAlt);
				
			}
			else {
				Konto kontoNeu = new Konto(kontoname, inhaber, saldo, institut);
				kontoNeu.setKontostandBeiErstellung(saldo);
				kontoNeu.setKontonummer(kontoAlt.getKontonummer());
				// Alte Buchungen dem neuen Konto zuweisen
				Konto.konten.put(kontoNeu.getKontonummer(), kontoNeu);
				
				for (Buchung b : new ArrayList<>(kontoAlt.getBuchungen())) {
					kontoNeu.addBuchung(b);
					//kontoAlt.removeBuchung(b);
				}
			
			for(Buchung b : new ArrayList<>(kontoAlt.getBuchungen())) {
				if (b.getKategorie().contains("Kontoerstellung")) {
					b.setBetrag(saldo);
				}
				if (b.getIsUmbuchung()) {
					    BuchungsAktionen.umbuchungBearbeiten(b, kontoNeu, b.getBetrag(), b.getBuchungsDatum());
                   }
			}
			System.out.println("Konto wurde bearbeitet: Konto alt: " + kontoAlt + "Konto neu: "+ kontoNeu);
			Datenstroeme.kontoAendern(kontoAlt, kontoNeu);
			Datenstroeme.kontoBuchungenNeuSpeichern(kontoNeu);
			Konto.konten.remove(kontoAlt.getKontonummer()-1);
			}
			Datenstroeme.kontenNeuSpeichern();
			
		}*/

		/*public static void kontenListeAnzeigen() {
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
			
		}*/
		
		/*public static  ObservableList<String> kontenListeAnzeigen() {
			ObservableList<String> kontenListe = FXCollections.observableArrayList();
					
			for (Konto konto : Konto.konten.values()) {
				if(konto.getKreditinstitut() != null) {
					kontenListe.add(konto.getKontonummer() + ": " + konto.getKontoName() + " ("
						+ konto.getKreditinstitut() + ") - " + konto.getKontostand() + " Euro)");
			}
				else {
					kontenListe.add(konto.getKontonummer() + ": " + konto.getKontoName() 
                    + " - " + konto.getKontostand() + " Euro)");
                }
				
			
		}
			return kontenListe;
		}*/
		/*Alte Methode über Konsole:
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
		}*/
		
}
