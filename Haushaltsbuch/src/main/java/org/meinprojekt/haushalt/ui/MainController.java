package org.meinprojekt.haushalt.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MainController {

  @FXML private TableView<?> tbl;   // später typisieren (z. B. TableView<Buchung>)
  @FXML private Label sumLbl;
  @FXML private Button btnNeu;

  @FXML
  private void initialize() {
    // Hier später: Spalten anlegen, Daten laden, Summe berechnen usw.
    sumLbl.setText("Summe: 0,00 €");
  }

  @FXML
  private void onNeu() {
    // Hier später: Dialog öffnen, neue Buchung anlegen, Tabelle aktualisieren
    System.out.println("Neue Buchung…");
  }
}
