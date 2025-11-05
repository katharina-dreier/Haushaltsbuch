package org.meinprojekt.haushalt.ui;

import java.text.ParseException;

import org.meinprojekt.haushalt.core.KontoAktionen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class DialogKonto {

	@FXML TextField txtName, txtInhaber, txtSaldo, txtInstitut;
	@FXML Button btnAbbrechen, btnOk;

	private Stage stage; // wird vom MainController gesetzt
	private boolean saved = false;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public boolean isSaved() {
		return saved;
	}

	@FXML
	private void initialize() {

		if (btnOk != null)
			btnOk.setDefaultButton(true);
		if (btnAbbrechen != null)
			btnAbbrechen.setCancelButton(true);

		txtName.requestFocus();
		// Button nur aktivieren, wenn alle Felder ausgefüllt sind
		btnOk.disableProperty().bind(txtName.textProperty().isEmpty().or(txtInstitut.textProperty().isEmpty())
				.or(txtInhaber.textProperty().isEmpty()).or(txtSaldo.textProperty().isEmpty()));
		// Enter-Taste springt zum nächsten Feld
		txtName.setOnAction(e -> txtInhaber.requestFocus());
		txtInhaber.setOnAction(e -> txtSaldo.requestFocus());
		txtSaldo.setOnAction(e -> txtInstitut.requestFocus());
		txtInstitut.setOnAction(e -> {
			try {
				handleButtonActionOK();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// Nur Zahlen und maximal 2 Nachkommastellen im Saldo-Feld erlauben
		txtSaldo.setTextFormatter(new TextFormatter<>(change -> {
			String neu = change.getControlNewText();
			// Erlaubt: nur Ziffern, optional , oder . und bis zu 2 Nachkommastellen
			return neu.matches("\\d*(?:[\\.,]\\d{0,2})?") ? change : null;
		}));

	}

	@FXML
	private void handleButtonActionOK() throws ParseException {
		var nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
		nf.setGroupingUsed(true);
		Number n = nf.parse(txtSaldo.getText().trim());
		double saldo = n.doubleValue();
		KontoAktionen.kontoErstellen(txtName.getText(), txtInhaber.getText(), saldo, txtInstitut.getText());
		btnOk.getScene().getWindow().hide();
	}

	@FXML
	private void handleAbbrechen() {
		var window = btnAbbrechen.getScene().getWindow();
		window.hide();

	}
}
