package org.meinprojekt.haushalt.ui;


import org.meinprojekt.haushalt.core.Datenstroeme;
import org.meinprojekt.haushalt.core.Konto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

  @FXML private TableView<Konto> tbl;   // später typisieren (z. B. TableView<Buchung>)
  @FXML private Label sumLbl;
  @FXML private Button btnNeu;
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
    // Hier später: Spalten anlegen, Daten laden, Summe berechnen usw.
	Datenstroeme.kontenUebersichtAnlegen();
	Datenstroeme.ladeKontenAusDatei();
	Datenstroeme.ladeBuchungenFuerAlleKonten();
	
	colId.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
    colName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
    colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
    colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));
    
    ObservableList<Konto> kontoListe = FXCollections.observableArrayList(Konto.getAlleKonten());

        // Liste der Tabelle zuweisen
        tbl.setItems(kontoListe);

	 
    sumLbl.setText("Summe: 0,00 €");
  }

  @FXML
  private void onNeu() {
    // Hier später: Dialog öffnen, neue Buchung anlegen, Tabelle aktualisieren
    System.out.println("Neue Buchung…");
  }
}
