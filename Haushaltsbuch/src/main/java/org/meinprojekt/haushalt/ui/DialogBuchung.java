package org.meinprojekt.haushalt.ui;

import java.text.ParseException;
import java.time.LocalDate;

import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.service.BuchungsService;
import org.meinprojekt.haushalt.core.service.KontoService;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class DialogBuchung {

	@FXML
	private ComboBox<Konto> cmbQuellKonto, cmbZielKonto;
	@FXML
	private DatePicker dpDatum;
	@FXML
	private ComboBox<String> cmbKategorie;
	@FXML
	TextField txtEmpfaenger, txtSender, txtBetrag;
	@FXML
	private Label lblQuellKonto, lblZielKonto, lblEmpfaenger, lblSender, lblKategorie, lblBetrag, lblDatum;

	@FXML
	Button btnAbbrechen;
	@FXML
	Button btnOk;
	ObservableList<Konto> alleKonten = KontoService.getAlleKontenAlsObservableList();

	private boolean editMode = false;

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

		cmbQuellKonto.setItems(alleKonten);
		cmbQuellKonto.setPromptText("Konto auswählen...");
		kontoListeKonvertieren(cmbQuellKonto);
		cmbZielKonto.setItems(alleKonten);
		cmbZielKonto.setPromptText("Konto auswählen...");
		kontoListeKonvertieren(cmbZielKonto);

		dpDatum.setValue(LocalDate.now());

		cmbKategorie.setEditable(true);
		cmbKategorie.setItems(javafx.collections.FXCollections.observableArrayList(Buchung.listeMitKategorien));
		cmbKategorie.setPromptText("Kategorie eingeben...");

		if (btnOk != null)
			btnOk.setDefaultButton(true);
		if (btnAbbrechen != null)
			btnAbbrechen.setCancelButton(true);

		// cmbQuellKonto.requestFocus();
		// Button nur aktivieren, wenn alle Felder ausgefüllt sind
		btnOk.disableProperty().bind(dpDatum.valueProperty().isNull().or(txtBetrag.textProperty().isEmpty())
				.or(cmbKategorie.getEditor().textProperty().isEmpty()));

		// Nur Zahlen und maximal 2 Nachkommastellen im Betrag-Feld erlauben
		txtBetrag.setTextFormatter(new TextFormatter<>(change -> {
			String neu = change.getControlNewText();
			// Erlaubt: nur Ziffern, optional , oder . und bis zu 2 Nachkommastellen
			return neu.matches("\\d*(?:[\\.,]\\d{0,2})?") ? change : null;
		}));

	}

	@FXML
	private void handleButtonActionOK() throws ParseException {
			try {
				var nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
				nf.setGroupingUsed(true);
				double betrag = nf.parse(txtBetrag.getText().trim()).doubleValue();

				var datum = dpDatum.getValue();
				var kat = cmbKategorie.getEditor().getText().trim();

				switch (buchungsart) {
				case "Einnahme" -> {
					Konto ziel = cmbZielKonto.getValue();
					String sender = txtSender.getText().trim();
					if (!editMode) { 
						BuchungsService.einnahmeTätigen(betrag, kat, ziel, sender, datum, "", false);}
					else {
						if (original.getIsUmbuchung()) {
							BuchungsService.umbuchungBearbeiten(original, ziel, betrag, datum);
						}
						else {BuchungsService.buchungBearbeiten(original, betrag, kat, ziel, sender, datum);}
					}
				}
				case "Ausgabe" -> {
					Konto quell = cmbQuellKonto.getValue();
					String empfaenger = txtEmpfaenger.getText().trim();
					if (!editMode) {
						BuchungsService.ausgabeTätigen(betrag, kat, quell, empfaenger, datum, "", false);
					} else {
						if (original.getIsUmbuchung()) {
							BuchungsService.umbuchungBearbeiten(original, quell, betrag, datum);
						} else
						BuchungsService.buchungBearbeiten(original, betrag, kat, quell, empfaenger, datum);
					}
				}
				case "Umbuchung" -> {
					Konto quell = cmbQuellKonto.getValue();
					Konto ziel = cmbZielKonto.getValue();
					
					if (quell == ziel) {
						new Alert(Alert.AlertType.WARNING, "Quelle und Ziel dürfen nicht gleich sein.").showAndWait();
						return;
					}
					BuchungsService.umbuchungTätigen(betrag, quell, ziel, datum);

				}
				}
				// schließen
				btnOk.getScene().getWindow().hide();
				saved = true;

			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Eingabe prüfen: " + ex.getMessage()).showAndWait();
			}
		} 
		
	

	@FXML
	private void handleAbbrechen() {
		var window = btnAbbrechen.getScene().getWindow();
		window.hide();

	}

	private String buchungsart;

	public void setBuchungsart(String art) {
		this.buchungsart = art;
		applyBuchungsart();
	}

	private void applyBuchungsart() {
		if (buchungsart == null)
			return;

		// alles erstmal aus
		setRowVisible(lblQuellKonto, cmbQuellKonto, false);
		setRowVisible(lblZielKonto, cmbZielKonto, false);
		setRowVisible(lblEmpfaenger, txtEmpfaenger, false);
		setRowVisible(lblSender, txtSender, false);

		switch (buchungsart) {
		case "Einnahme" -> {
			setRowVisible(lblZielKonto, cmbZielKonto, true);
			setRowVisible(lblSender, txtSender, true);
		}
		case "Ausgabe" -> {
			setRowVisible(lblQuellKonto, cmbQuellKonto, true);
			setRowVisible(lblEmpfaenger, txtEmpfaenger, true);
		}
		case "Umbuchung" -> {
			setRowVisible(lblQuellKonto, cmbQuellKonto, true);
			setRowVisible(lblZielKonto, cmbZielKonto, true);
			setRowVisible(lblKategorie, cmbKategorie, false);
		}
		default -> {
			// optional: Warnung oder Fallback
		}
		}

		// Zusätzliche Pflichtfelder pro Art kontrolliert deaktivieren:
		if ("Einnahme".equals(buchungsart)) {
			btnOk.disableProperty().bind(dpDatum.valueProperty().isNull().or(txtBetrag.textProperty().isEmpty())
					.or(cmbKategorie.getEditor().textProperty().isEmpty()).or(cmbZielKonto.valueProperty().isNull()));
		} else if ("Ausgabe".equals(buchungsart)) {
			btnOk.disableProperty().bind(dpDatum.valueProperty().isNull().or(txtBetrag.textProperty().isEmpty())
					.or(cmbKategorie.getEditor().textProperty().isEmpty()).or(cmbQuellKonto.valueProperty().isNull()));
		} else if ("Umbuchung".equals(buchungsart)) {
			btnOk.disableProperty().bind(dpDatum.valueProperty().isNull().or(txtBetrag.textProperty().isEmpty())
					.or(cmbQuellKonto.valueProperty().isNull()).or(cmbZielKonto.valueProperty().isNull()));
		}
	}

	private void setRowVisible(Node label, Node field, boolean visible) {
		label.setVisible(visible);
		label.setManaged(visible);
		field.setVisible(visible);
		field.setManaged(visible);
	}

//Anzeigeformat für Konten im ComboBox festlegen
	public void kontoListeKonvertieren(ComboBox<Konto> comboBox) {
		comboBox.setConverter(new javafx.util.StringConverter<Konto>() {
			@Override
			public String toString(Konto k) {
				return k == null ? "" : k.getKontoName() + "(" + k.getKreditinstitut() + ")";
			}

			@Override
			public Konto fromString(String s) {
				return null;
			}
		});
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void prefillFields(Buchung buchung) {
		if (buchung == null)
			return;

		applyBuchungsart();
		dpDatum.setValue(buchung.getBuchungsDatum());
		txtBetrag.setText(String.format("%.2f", buchung.getBetrag()));
		cmbKategorie.getEditor().setText(buchung.getKategorie());

		switch (buchung.getBuchungsart()) {
		case "Einnahme" -> {
			cmbZielKonto.setValue(buchung.getKonto());
			txtSender.setText(buchung.getSender());
			setBuchungsart("Einnahme");
			break;
		}

		case "Ausgabe" -> {
			cmbQuellKonto.setValue(buchung.getKonto());
			txtEmpfaenger.setText(buchung.getEmpfaenger());
			setBuchungsart("Ausgabe");
			break;
		}
		default -> {
			System.out.println("Fehler beim befüllen. Unbekannte Buchungsart: " + buchung.getBuchungsart());
		}
		
		}
	}

	private Buchung original;

	public void setOriginal(Buchung b) {
		this.original = b;
	}

}
