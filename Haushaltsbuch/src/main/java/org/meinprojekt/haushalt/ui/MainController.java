package org.meinprojekt.haushalt.ui;

import java.util.Optional;

import org.meinprojekt.haushalt.core.Datenstroeme;
import org.meinprojekt.haushalt.core.Konto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private TableView<Konto> tbl; // später typisieren (z. B. TableView<Buchung>)
	@FXML
	private Label sumLbl;
	@FXML
	private Button btnNeuesKonto;
	@FXML
	private TableColumn<Konto, Integer> colId;
	@FXML
	private TableColumn<Konto, String> colName;
	@FXML
	private TableColumn<Konto, Double> colKontostand;
	@FXML
	private TableColumn<Konto, String> colInstitut;

	@FXML
	private void initialize() {
		// Daten laden
		Datenstroeme.kontenUebersichtAnlegen();
		Datenstroeme.ladeKontenAusDatei();
		Datenstroeme.ladeBuchungenFuerAlleKonten();

		// Spalten mit Attributen der Buchung verknüpfen
		colId.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
		colName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
		colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
		colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));

		ObservableList<Konto> kontoListe = FXCollections.observableArrayList(Konto.getAlleKonten());

		// Liste der Tabelle zuweisen
		tbl.setItems(kontoListe);

		sumLbl.setText("Summe: " + Konto.getGesamtSumme());
	}

	@FXML
	private void handleNeuesKonto() {
		 try {
	            // FXML laden
	            FXMLLoader loader = new FXMLLoader(
	                getClass().getResource("/org/meinprojekt/haushalt/ui/konto-dialog.fxml")
	            );
	            Parent root = loader.load();

	            // Controller des Dialogs holen
	            DialogKonto dialogController = loader.getController();

	            // neue modale Stage bauen
	            Stage dialogStage = new Stage();
	            dialogStage.setTitle("Neues Konto anlegen");
	            dialogStage.initModality(Modality.APPLICATION_MODAL);
	            // Owner auf das Hauptfenster setzen (damit das Dialogfenster im Vordergrund bleibt)
	            dialogStage.initOwner(btnNeuesKonto.getScene().getWindow());
	          

	            dialogStage.setResizable(false);
	            dialogStage.setScene(new Scene(root));
	         //dem Dialog-Controller die Stage geben
	            dialogController.setStage(dialogStage);
	         // anzeigen und warten, bis geschlossen wurde
	            dialogStage.showAndWait();
	            aktualisiereTabelle();

	        } catch (Exception ex) {
	            Alert a = new Alert(Alert.AlertType.ERROR, "Dialog konnte nicht geladen werden:\n" + ex.getMessage());
	            a.setHeaderText("Fehler beim Öffnen");
	            a.showAndWait();
	            ex.printStackTrace();
	        }
		 
		 
	    }
	public void aktualisiereTabelle() {
		tbl.setItems(FXCollections.observableArrayList(Konto.getAlleKonten()));
	}

	}

