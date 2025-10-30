package org.meinprojekt.haushalt.ui;

import java.lang.ModuleLayer.Controller;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.meinprojekt.haushalt.core.Buchung;
import org.meinprojekt.haushalt.core.Datenstroeme;
import org.meinprojekt.haushalt.core.Konto;
import org.meinprojekt.haushalt.core.KontoAktionen;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
	
	@FXML private VBox kontenArea;
	@FXML private VBox buchungenArea;
	
	@FXML TabPane tabPane;
	@FXML private Tab tabGesamt, tabEinnahmen, tabAusgaben, tabUmbuchungen;
	
	@FXML private Label sumLbl;
	@FXML private Button btnNeuesKonto, btnNeueAus, btnNeueEin, btnNeueUmb;
	
	@FXML private TableView<Konto> tblKonten; 
	@FXML private TableColumn<Konto, Integer> colId;
	@FXML private TableColumn<Konto, String> colName;
	@FXML private TableColumn<Konto, Double> colKontostand;
	@FXML private TableColumn<Konto, String> colInstitut;
	
	@FXML private TableView<Buchung> tblBuchungen;
	@FXML private TableColumn<Buchung, String> colBuchDatum;
	@FXML private TableColumn<Buchung, String> colKat;
	@FXML private TableColumn<Buchung, String> colEmpf;
	@FXML private TableColumn<Buchung, String> colSend;
	@FXML private TableColumn<Buchung, Double> colBetrag;
	
	private final ObservableList<Konto> kontenListe = FXCollections.observableArrayList();
	private final ObservableList<Buchung> buchungsListe = FXCollections.observableArrayList();
	private FilteredList<Buchung> gefilterteBuchungsListe;
	private SortedList<Buchung> sortierteBuchungsListe;


	@FXML
	private void initialize() {
		// Daten laden
		Datenstroeme.kontenUebersichtAnlegen();
		Datenstroeme.kategorieUebersichtAnlegen();
		Datenstroeme.ladeKontenAusDatei();
		Datenstroeme.ladeKategorienAusDatei();
		Datenstroeme.ladeBuchungenFuerAlleKonten();
		tblBuchungen.setPlaceholder(new Label("Keine Buchungen"));
		
		gefilterteBuchungsListe = new FilteredList<>(buchungsListe, b -> true);
		sortierteBuchungsListe = new SortedList<>(gefilterteBuchungsListe);
		
		sortierteBuchungsListe.comparatorProperty().bind(tblBuchungen.comparatorProperty());
		tblBuchungen.setItems(sortierteBuchungsListe);
		tblBuchungen.setPlaceholder(new Label("Keine Buchungen vorhanden"));
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> applyTabFilter());

		showKonten();
	
		tblKonten.setVisible(true);
		tblKonten.setManaged(true);
		tblBuchungen.setVisible(false);
		tblBuchungen.setManaged(false);

		// Spalten mit Attributen der Buchung verknüpfen
		colId.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
		colName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
		colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
		colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));
		
		colBuchDatum.setCellValueFactory(new PropertyValueFactory<>("buchungsDatum"));
		colKat.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
		colEmpf.setCellValueFactory(new PropertyValueFactory<>("empfaenger"));
		colSend.setCellValueFactory(new PropertyValueFactory<>("sender"));
		colBetrag.setCellValueFactory(cellData -> {
		    Buchung b = cellData.getValue();
		    double value = b.getBetrag();

		    if (b.getBuchungsart().equalsIgnoreCase("Ausgabe")) {
		        value = -value;
		    } 		    

		    return new ReadOnlyObjectWrapper<>(value);
		});
		
		colBetrag.setCellFactory(column -> new TableCell<>() {
		    @Override
		    protected void updateItem(Double betrag, boolean empty) {
		        super.updateItem(betrag, empty);
		        if (empty || betrag == null) {
		            setText(null);
		        } else {
		            setText(String.format("%.2f €", betrag));
		            if (betrag < 0) setStyle("-fx-text-fill: red;");
		            else setStyle("-fx-text-fill: green;");
		        }
		    }
		});


		// Liste der Tabelle zuweisen
		kontenListe.setAll(Konto.getAlleKonten());   // einmalig befüllen
		tblKonten.setItems(kontenListe);

	    // Gesamtsumme anzeigen
		String sumText = String.format("Summe: %.2f €", Konto.getGesamtSumme());
		sumLbl.setText(sumText);
		
		// Listener für die Auswahl eines Kontos in der Tabelle
		tblKonten.getSelectionModel().selectedItemProperty().addListener((obs, altesKonto, neuesKonto) -> {
			buchungenAnzeigen(neuesKonto);
		});
		
	}

	@FXML
	private void handleNeuesKonto() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/konto-dialog.fxml";
		String titel = "Neues Konto anlegen";
		DialogKonto controller = dialogOeffnen(btnNeuesKonto, fxmlPfad, titel, null);
	}
	
	@FXML
	private void handleNeueEinnahme() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Einnahme anlegen";
		dialogOeffnen(btnNeueEin, fxmlPfad, titel, (DialogBuchung c) -> {
            c.setBuchungsart("Einnahme");}
		);  
		aktualisiereTabelle();
		}
	
	@FXML
	private void handleNeueAusgabe() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Ausgabe anlegen";
		dialogOeffnen(btnNeueAus, fxmlPfad, titel, (DialogBuchung c) -> {
            c.setBuchungsart("Ausgabe");}
		);
		aktualisiereTabelle();
		}
	
	@FXML
	private void handleNeueUmbuchung() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Umbuchung anlegen";
		dialogOeffnen(btnNeueUmb, fxmlPfad, titel, (DialogBuchung c) -> {
            c.setBuchungsart("Umbuchung");}
		);
		aktualisiereTabelle();
	}
	
	public void aktualisiereTabelle() {
		kontenListe.setAll(Konto.getAlleKonten());
		tblKonten.refresh();
	}
	
	public void buchungenAnzeigen(Konto konto) {
		if (konto != null) {
			// Aktualisiere die Buchungstabelle mit den Buchungen des ausgewählten Kontos
			List<Buchung> liste = konto.getBuchungen();
			buchungsListe.setAll(liste);
			applyTabFilter();
			showBuchungen();
		} else {
			tblBuchungen.setVisible(false);
			tblBuchungen.setManaged(false);
			tblKonten.setVisible(true);
			tblKonten.setManaged(true);
		}
		

	}
	
	
	private <T> T dialogOeffnen(Button btn, String fxmlPfad, String titel, Consumer<T> setup) {
        try {
            // FXML laden
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(fxmlPfad)
            );
            Parent root = loader.load();
            // Controller des Dialogs holen
            T dialogController = loader.getController();
            if (setup != null) setup.accept(dialogController);
             // neue modale Stage bauen
            Stage dialogStage = new Stage();
            dialogStage.setTitle(titel);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            // Owner auf das Hauptfenster setzen (damit das Dialogfenster im Vordergrund bleibt)
            dialogStage.initOwner(btn.getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
         // anzeigen und warten, bis geschlossen wurde
            dialogStage.showAndWait();
            aktualisiereTabelle();
            return dialogController;

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Dialog konnte nicht geladen werden:\n" + ex.getMessage());
            a.setHeaderText("Fehler beim Öffnen");
            a.showAndWait();
            ex.printStackTrace();
            return null;
        }
    }
	
	private void showKonten() {
	    kontenArea.setVisible(true);
	    kontenArea.setManaged(true);
	    buchungenArea.setVisible(false);
	    buchungenArea.setManaged(false);
	}
	
	private void showBuchungen() {
	    kontenArea.setVisible(true);
	    kontenArea.setManaged(true);
	    buchungenArea.setVisible(true);
	    buchungenArea.setManaged(true);
	    tblBuchungen.setVisible(true);
		tblBuchungen.setManaged(true);
	}
	private void applyTabFilter() {
	    if (gefilterteBuchungsListe == null) return; // falls noch nicht initialisiert

	    var aktTab = tabPane.getSelectionModel().getSelectedItem();
	    if (aktTab == tabEinnahmen) {
	    	gefilterteBuchungsListe.setPredicate(b -> "EINNAHME".equalsIgnoreCase(b.getBuchungsart()));
	    } else if (aktTab == tabAusgaben) {
	    	gefilterteBuchungsListe.setPredicate(b -> "AUSGABE".equalsIgnoreCase(b.getBuchungsart()));
	    } else if (aktTab == tabUmbuchungen) {
	    	gefilterteBuchungsListe.setPredicate(b -> "UMBUCHUNG".equalsIgnoreCase(b.getKategorie()));
	    } else {
	        // Gesamt
	    	gefilterteBuchungsListe.setPredicate(b -> true);
	    }
	}

	}

