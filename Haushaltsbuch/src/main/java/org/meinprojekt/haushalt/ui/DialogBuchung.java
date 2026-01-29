package org.meinprojekt.haushalt.ui;

import java.text.ParseException;
import java.time.LocalDate;

import org.meinprojekt.haushalt.core.filter.ZeitraumArt;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung.Haeufigkeit;
import org.meinprojekt.haushalt.core.service.BuchungsService;
import org.meinprojekt.haushalt.core.service.KontoService;
import org.meinprojekt.haushalt.core.service.WiederkehrendeZahlungenService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class DialogBuchung {

	@FXML
	private ComboBox<Konto> cmbQuellKonto, cmbZielKonto;
	@FXML
	private CheckBox chkIsWiederkehrend;
	@FXML
	private DatePicker dpDatum;
	@FXML
	private ComboBox<String> cmbKategorie;
	@FXML
	private ComboBox<Haeufigkeit> cmbHaeufigkeit;
	@FXML
	private TextArea txtBeschreibung;
	@FXML
	TextField txtEmpfaenger, txtSender, txtBetrag;
	@FXML
	private Label lblQuellKonto, lblZielKonto, lblIsWiederkehrend, lblHaeufigkeit, lblEmpfaenger, lblSender, lblKategorie, lblBeschreibung, lblBetrag,
			lblDatum;

	@FXML
	Button btnAbbrechen;
	@FXML
	Button btnOk;
	ObservableList<Konto> alleKonten = FXCollections.observableArrayList();
			

	
	private Stage stage; // wird vom MainController gesetzt
	
	private Buchung original;
	private WiederkehrendeZahlung originalWiederkehrend;
	
	private boolean editMode = false;
	private boolean saved = false;
	private boolean isWiederkehrend = false;
	
	private Buchungstyp buchungsart;

	public void setIsWiederkehrend(boolean isWiederkehrend) {
		this.isWiederkehrend = isWiederkehrend;
	}
	
	public boolean getIsWiederkehrend() {
		return isWiederkehrend;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setOriginal(Buchung b) {
		this.original = b;
	}
	
	public void setOriginal(WiederkehrendeZahlung w) {
		this.originalWiederkehrend = w;
	}

	public WiederkehrendeZahlung getOriginal() {
		return originalWiederkehrend;
	}
	
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isSaved() {
		return saved;
	}
	
	public void setBuchungsart(Buchungstyp typ) {
		this.buchungsart = typ;
		System.out.println("Set Buchungsart: " + buchungsart);
	}

	@FXML
	private void initialize() {
		
		alleKonten.addAll(Konto.getAlleKonten());
		cmbQuellKonto.setItems(alleKonten);
		cmbQuellKonto.setPromptText("Konto auswählen...");
		kontoListeKonvertieren(cmbQuellKonto);
		cmbZielKonto.setItems(alleKonten);
		cmbZielKonto.setPromptText("Konto auswählen...");
		kontoListeKonvertieren(cmbZielKonto);
		
		lblIsWiederkehrend.setText("Wiederkehrend?");
		cmbHaeufigkeit.setItems(FXCollections.observableArrayList(Haeufigkeit.values()));
		cmbHaeufigkeit.setPromptText("Häufigkeit auswählen...");
		setRowVisible(lblHaeufigkeit, cmbHaeufigkeit, false);
		chkIsWiederkehrend.selectedProperty().addListener((obs, alt, neu) -> {
		    setRowVisible(lblHaeufigkeit, cmbHaeufigkeit, neu);
		});

		dpDatum.setValue(LocalDate.now());

		cmbKategorie.setEditable(true);
		cmbKategorie.setItems(javafx.collections.FXCollections.observableArrayList(Buchung.getListeMitKategorien()));
		cmbKategorie.setPromptText("Kategorie eingeben...");

		if (btnOk != null)
			btnOk.setDefaultButton(true);
		if (btnAbbrechen != null)
			btnAbbrechen.setCancelButton(true);

		// Button nur aktivieren, wenn alle Felder ausgefüllt sind
		btnOk.disableProperty().bind(dpDatum.valueProperty().isNull().or(txtBetrag.textProperty().isEmpty())
				.or(cmbKategorie.getEditor().textProperty().isEmpty()));

		// Nur Zahlen und maximal 2 Nachkommastellen im Betrag-Feld erlauben
		txtBetrag.setTextFormatter(new TextFormatter<>(change -> {
			String neu = change.getControlNewText();
			// Erlaubt: nur Ziffern, optional , oder . und bis zu 2 Nachkommastellen
			return neu.matches("\\d*(?:[\\.,]\\d{0,2})?") ? change : null;
		}));

		applyBuchungsart();
	}

	@FXML
	private void handleButtonActionOK() throws ParseException {
		try {
			var nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY);
			nf.setGroupingUsed(true);
			double betrag = nf.parse(txtBetrag.getText().trim()).doubleValue();

			var datum = dpDatum.getValue();
			var kategorie = cmbKategorie.getEditor().getText().trim();
			var beschreibung = txtBeschreibung.getText().trim();
			var isWiederkehrend = chkIsWiederkehrend.isSelected();
			var haeufigkeit = cmbHaeufigkeit.getValue();
			
			switch (buchungsart) {
			case EINNAHME: {
				Konto ziel = cmbZielKonto.getValue();
				String sender = txtSender.getText().trim();
				if (!editMode) {
					BuchungsService.einnahmeTaetigen(betrag, kategorie, beschreibung, ziel, sender, datum, "", false);
					if (isWiederkehrend) {
						LocalDate naechsteZahlungAm = WiederkehrendeZahlungenService.naechstesBuchungsDatumBerechnen(datum, haeufigkeit);
                        WiederkehrendeZahlungenService.wiederkehrendeZahlungAnlegen(naechsteZahlungAm, haeufigkeit, buchungsart, kategorie, beschreibung, ziel.getInhaber(), sender, betrag, ziel);
                    }
				} else {
					
					if (isWiederkehrend) {
						WiederkehrendeZahlungenService.wiederkehrendeZahlungBearbeiten(originalWiederkehrend, datum,
								haeufigkeit, buchungsart.toString(), kategorie, beschreibung, ziel.getInhaber(),
								sender, betrag);
					}
					else if (original != null) {
						
					if (original.getIsUmbuchung()) {
						BuchungsService.umbuchungBearbeiten(original, beschreibung, ziel, betrag, datum);
					
					}
					else {
						BuchungsService.buchungBearbeiten(original, betrag, kategorie, beschreibung, ziel, sender, datum);
					}
				}

			}
				break;
			}
			case AUSGABE: {
				Konto quell = cmbQuellKonto.getValue();
				String empfaenger = txtEmpfaenger.getText().trim();
				if (!editMode) {
					BuchungsService.ausgabeTaetigen(betrag, kategorie, beschreibung, quell, empfaenger, datum, "", false);
					if (isWiederkehrend) {
						LocalDate naechsteZahlungAm = WiederkehrendeZahlungenService
								.naechstesBuchungsDatumBerechnen(datum, haeufigkeit);
						WiederkehrendeZahlungenService.wiederkehrendeZahlungAnlegen(naechsteZahlungAm, haeufigkeit,
								buchungsart, kategorie, beschreibung, empfaenger, quell.getInhaber(), betrag, quell);
					}
				} else {
					if (isWiederkehrend) {
						WiederkehrendeZahlungenService.wiederkehrendeZahlungBearbeiten(originalWiederkehrend, datum,
								haeufigkeit, buchungsart.toString(), kategorie, beschreibung, empfaenger, quell.getInhaber(),
								betrag);
					}
					else if (original != null) {
					if (original.getIsUmbuchung()) {
						BuchungsService.umbuchungBearbeiten(original, beschreibung, quell, betrag, datum);
					} 
					else
						BuchungsService.buchungBearbeiten(original, betrag, kategorie, beschreibung, quell, empfaenger, datum);
				}
			}
				break;
				}
			
			case UMBUCHUNG: {
				Konto quell = cmbQuellKonto.getValue();
				Konto ziel = cmbZielKonto.getValue();

				if (quell == ziel) {
					new Alert(Alert.AlertType.WARNING, "Quelle und Ziel dürfen nicht gleich sein.").showAndWait();
					return;
				}
				BuchungsService.umbuchungTaetigen(betrag, beschreibung, quell, ziel, datum);
				break;

			}
			}
			
			// schließen
			btnOk.getScene().getWindow().hide();
			saved = true;

		} catch (Exception ex) {
			new Alert(Alert.AlertType.ERROR, "Eingabe prüfen: " + ex.getMessage()).showAndWait();
		}
	}

	


	public void applyBuchungsart() {
		System.out.println("Apply Buchungsart: " + buchungsart);
		if (buchungsart == null)
			return;
		

		// alles erstmal aus
		setRowVisible(lblQuellKonto, cmbQuellKonto, false);
		setRowVisible(lblZielKonto, cmbZielKonto, false);
		setRowVisible(lblEmpfaenger, txtEmpfaenger, false);
		setRowVisible(lblSender, txtSender, false);
		setRowVisible(lblKategorie, cmbKategorie, false);
		setRowVisible(lblIsWiederkehrend, chkIsWiederkehrend, false);
		setRowVisible(lblHaeufigkeit, cmbHaeufigkeit, false);
		
		// immer an:
		setRowVisible(lblBeschreibung, txtBeschreibung, true);
		setRowVisible(lblBetrag, txtBetrag, true);
		setRowVisible(lblDatum, dpDatum, true);

		// pro Art an:

		switch (buchungsart) {
		case EINNAHME-> {
			setRowVisible(lblZielKonto, cmbZielKonto, true);
			setRowVisible(lblSender, txtSender, true);
			setRowVisible(lblKategorie, cmbKategorie, true);
			setRowVisible(lblIsWiederkehrend, chkIsWiederkehrend, true);
		}
		case AUSGABE -> {
			setRowVisible(lblQuellKonto, cmbQuellKonto, true);
			setRowVisible(lblEmpfaenger, txtEmpfaenger, true);
			setRowVisible(lblKategorie, cmbKategorie, true);
			setRowVisible(lblIsWiederkehrend, chkIsWiederkehrend, true);
		}
		case UMBUCHUNG-> {
			setRowVisible(lblQuellKonto, cmbQuellKonto, true);
			setRowVisible(lblZielKonto, cmbZielKonto, true);
		}
		default -> {
			// optional: Warnung oder Fallback
		}
		}
		if (editMode) {
			if (isWiederkehrend) {
	        
	        cmbHaeufigkeit.setValue(originalWiederkehrend.getHaeufigkeit());
	        setRowVisible(lblHaeufigkeit, cmbHaeufigkeit, true);
	       setRowVisible(lblIsWiederkehrend, chkIsWiederkehrend, false);
			lblDatum.setText("Nächste Zahlung am:");
			
	    }
			else {
                chkIsWiederkehrend.setSelected(false);
                setRowVisible(lblIsWiederkehrend, chkIsWiederkehrend, false);
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

	

	public void prefillFields(Buchung buchung) {
		if (buchung == null)
			return;

		
		dpDatum.setValue(buchung.getBuchungsDatum());
		txtBetrag.setText(String.format("%.2f", buchung.getBetrag()));
		cmbKategorie.getEditor().setText(buchung.getKategorie());
		txtBeschreibung.setText(buchung.getBeschreibung());

		switch (buchung.getBuchungstyp()) {
		case EINNAHME-> {
			cmbZielKonto.setValue(buchung.getKonto());
			txtSender.setText(buchung.getSender());
			setBuchungsart(Buchungstyp.EINNAHME);
			break;
		}

		case AUSGABE -> {
			cmbQuellKonto.setValue(buchung.getKonto());
			txtEmpfaenger.setText(buchung.getEmpfaenger());
			setBuchungsart(Buchungstyp.AUSGABE);
			break;
		}
		default -> {
			System.out.println("Fehler beim befüllen. Unbekannte Buchungsart: " + buchung.getBuchungsart());
		}

		}
	}
	
	public void prefillFields(WiederkehrendeZahlung wkz) {
		if (wkz == null)
			return;

		
		dpDatum.setValue(wkz.getNaechsteZahlungAm());
		txtBetrag.setText(String.format("%.2f", wkz.getBetrag()));
		cmbKategorie.getEditor().setText(wkz.getKategorie());
		txtBeschreibung.setText(wkz.getBeschreibung());
		
		
		switch (wkz.getBuchungsart()) {
		case "Einnahme" -> {
            cmbZielKonto.setValue(wkz.getKonto());
            txtSender.setText(wkz.getSender());
            setBuchungsart(Buchungstyp.EINNAHME);
            break;
		}
		case "Ausgabe" -> {
            cmbQuellKonto.setValue(wkz.getKonto());
            txtEmpfaenger.setText(wkz.getEmpfaenger());
            setBuchungsart(Buchungstyp.AUSGABE);
            break;
        }
		default -> {
			System.out.println("Fehler beim befüllen. Unbekannte Buchungsart: " + wkz.getBuchungsart());
		}
		}

		
		chkIsWiederkehrend.setSelected(true);
		cmbHaeufigkeit.setValue(wkz.getHaeufigkeit());
	}
	
	@FXML void handleCheckboxWiederkehrend() {
		if (chkIsWiederkehrend.isSelected()) {
			setRowVisible(lblHaeufigkeit, cmbHaeufigkeit, true);
		}
	}
	
	@FXML
	private void handleAbbrechen() {
		var window = btnAbbrechen.getScene().getWindow();
		window.hide();

	}
	

}
