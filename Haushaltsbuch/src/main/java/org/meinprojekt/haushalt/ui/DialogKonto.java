package org.meinprojekt.haushalt.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.service.KontoService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class DialogKonto {

	@FXML TextField txtName;
	@FXML TextField txtInhaber;
	@FXML TextField txtSaldo;
	@FXML TextField txtInstitut;
	@FXML Button btnAbbrechen;
	@FXML Button btnOk;
	

	
	private boolean saved = false;
	private boolean editMode = false;
	
	private Konto original;

	public void setOriginal(Konto konto) {
		this.original = konto;
	}
	
	public boolean isEditMode() {
		return editMode;
	}
	
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}



	public boolean isSaved() {
		return saved;
	}

	@FXML
	private void initialize() {
		
		Objects.requireNonNull(btnOk, "btnOk wurde nicht aus dem FXML injiziert");
	    Objects.requireNonNull(btnAbbrechen, "btnAbbrechen wurde nicht aus dem FXML injiziert");

	    btnOk.setDefaultButton(true);
	    btnAbbrechen.setCancelButton(true);

		txtName.requestFocus();
		// Button nur aktivieren, wenn alle Felder ausgefüllt sind
		btnOk.disableProperty().bind(txtName.textProperty().isEmpty().or(txtInstitut.textProperty().isEmpty())
				.or(txtInhaber.textProperty().isEmpty()).or(txtSaldo.textProperty().isEmpty()));
		// Enter-Taste springt zum nächsten Feld
		txtName.setOnAction(_ -> txtInhaber.requestFocus());
		txtInhaber.setOnAction(_ -> txtSaldo.requestFocus());
		txtSaldo.setOnAction(_ -> txtInstitut.requestFocus());
		txtInstitut.setOnAction(_ -> {
			try {
				handleButtonActionOK();
			} catch (ParseException e1) {
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

		var nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		nf.setGroupingUsed(true);
		Number n = nf.parse(txtSaldo.getText());

		double saldo = n.doubleValue();
		String kontoname = txtName.getText();
		String inhaber = txtInhaber.getText();
		String institut = txtInstitut.getText();
		if (!editMode) { 
		KontoService.kontoErstellen(kontoname, inhaber, saldo, institut);}
		else {KontoService.kontoBearbeiten(original, saldo, inhaber);}
		btnOk.getScene().getWindow().hide();
	}

	@FXML
	private void handleAbbrechen() {
		var window = btnAbbrechen.getScene().getWindow();
		window.hide();

	}
	
	public void prefillKontodaten(Konto konto) {
        txtName.setText(konto.getKontoName());
        txtInhaber.setText(konto.getInhaber());
    	txtSaldo.setText(String.format("%.2f", konto.getKontostandBeiErstellung()));
        
        txtInstitut.setText(konto.getKreditinstitut());
        txtName.setDisable(true); // Kontoname im Bearbeitungsmodus nicht änderbar
        txtInstitut.setDisable(true); // Institut im Bearbeitungsmodus nicht änderbar
        
	}
}
