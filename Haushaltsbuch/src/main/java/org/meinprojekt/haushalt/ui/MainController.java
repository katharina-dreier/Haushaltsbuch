package org.meinprojekt.haushalt.ui;

import java.lang.ModuleLayer.Controller;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.meinprojekt.haushalt.core.Buchung;
import org.meinprojekt.haushalt.core.BuchungsAktionen;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private VBox kontenArea, buchungenArea;
	@FXML
	TabPane tabPane;
	@FXML
	private Tab tabGesamt, tabEinnahmen, tabAusgaben, tabUmbuchungen;

	@FXML
	private Label sumLbl, buchSumLbl;
	@FXML
	private Button btnNeuesKonto, btnNeueAus, btnNeueEin, btnNeueUmb;

	@FXML
	private TableView<Konto> tblKonten;
	@FXML
	private TableColumn<Konto, Integer> colId;
	@FXML
	private TableColumn<Konto, String> colName;
	@FXML
	private TableColumn<Konto, Double> colKontostand;
	@FXML
	private TableColumn<Konto, String> colInstitut;
	@FXML
	private TableColumn<Konto, Void> colDeleteKonto;

	@FXML
	private TableView<Buchung> tblBuchungen;
	@FXML
	private TableColumn<Buchung, String> colBuchDatum;
	@FXML
	private TableColumn<Buchung, String> colKat;
	@FXML
	private TableColumn<Buchung, String> colEmpf;
	@FXML
	private TableColumn<Buchung, String> colSend;
	@FXML
	private TableColumn<Buchung, Double> colBetrag;
	@FXML
	private TableColumn<Buchung, Void> colDeleteBuchung;

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

		// Buchungsliste filtern und sortieren
		buchungsListe.setAll(Konto.getAlleBuchungen());
		gefilterteBuchungsListe = new FilteredList<>(buchungsListe, b -> true);
		sortierteBuchungsListe = new SortedList<>(gefilterteBuchungsListe); 
		sortierteBuchungsListe.comparatorProperty().bind(tblBuchungen.comparatorProperty()); 
		tblBuchungen.setItems(sortierteBuchungsListe);
		berechneSumme(gefilterteBuchungsListe);
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> applyTabFilter());
		
		// Bereiche anzeigen
		showKonten();
		showBuchungen();

		// Spalten mit Attributen verknüpfen
		colId.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
		colName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
		colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));
		colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
		setupKontoLoeschen();

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
					if (betrag < 0)
						setStyle("-fx-text-fill: red;");
					else
						setStyle("-fx-text-fill: green;");
				}
			}
		});

		setupBuchungLoeschen();

		// Liste der Tabelle zuweisen
		kontenListe.setAll(Konto.getAlleKonten()); // einmalig befüllen
		tblKonten.setItems(kontenListe);

		// Gesamtsumme anzeigen
		updateGesamtSummeLabel();

		// Listener für die Auswahl eines Kontos in der Tabelle
		tblKonten.getSelectionModel().selectedItemProperty().addListener((obs, altesKonto, neuesKonto) -> {
			buchungenAnzeigen(neuesKonto);
		});

		// Doppelklick auf Buchung zum Bearbeiten
		tblBuchungen.setRowFactory(tv -> {
			TableRow<Buchung> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				// Nur echte Zeilen + Doppelklick
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Buchung selected = row.getItem();
					oeffneBearbeitenDialog(selected);
				}
			});
			return row;
		});

		// Doppelklick auf Konto zum Bearbeiten
		tblKonten.setRowFactory(tv -> {
			TableRow<Konto> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				// Nur echte Zeilen + Doppelklick
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Konto selected = row.getItem();
					oeffneBearbeitenDialog(selected);
				}
			});
			return row;
		});

	}

	@FXML
	private void handleNeuesKonto() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/konto-dialog.fxml";
		String titel = "Neues Konto anlegen";
		dialogOeffnen(btnNeuesKonto, fxmlPfad, titel, (DialogKonto c) -> {
		});
		updateGesamtSummeLabel();
	}

	@FXML
	private void handleNeueEinnahme() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Einnahme anlegen";
		dialogOeffnen(btnNeueEin, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Einnahme");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
	}

	@FXML
	private void handleNeueAusgabe() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Ausgabe anlegen";
		dialogOeffnen(btnNeueAus, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Ausgabe");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
	}

	@FXML
	private void handleNeueUmbuchung() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Umbuchung anlegen";
		dialogOeffnen(btnNeueUmb, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Umbuchung");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
	}

	public void aktualisiereTabelle() {
		System.out.println("Aktualisiere Konten-Tabelle...");
		kontenListe.setAll(Konto.getAlleKonten());
		System.out.println(kontenListe);
		tblKonten.refresh();
		System.out.println("Tabelle aktualisiert.");
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
			System.out.println("Öffne Dialog: " + titel);
			// FXML laden
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPfad));
			Parent root = loader.load();
			// Controller des Dialogs holen
			T dialogController = loader.getController();
			if (setup != null)
				setup.accept(dialogController);
			// neue modale Stage bauen
			Stage dialogStage = new Stage();
			dialogStage.setTitle(titel);
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			// Owner auf das Hauptfenster setzen (damit das Dialogfenster im Vordergrund
			// bleibt)
			dialogStage.initOwner(btn.getScene().getWindow());
			dialogStage.setResizable(false);
			dialogStage.setScene(new Scene(root));
			System.out.println("Dialog offen, warte auf Schließen...");
			// anzeigen und warten, bis geschlossen wurde
			dialogStage.showAndWait();
			System.out.println("Dialog geschlossen.");
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
	}

	private void showBuchungen() {
		buchungenArea.setVisible(true);
		buchungenArea.setManaged(true);
		tblBuchungen.setVisible(true);
		tblBuchungen.setManaged(true);
	}

	private void applyTabFilter() {
		if (gefilterteBuchungsListe == null)
			return; // falls noch nicht initialisiert

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
		buchSumLbl.setText(String.format("Summe: %.2f €", berechneSumme(gefilterteBuchungsListe)));
	}

	private void setupBuchungLoeschen() {
		colDeleteBuchung = new TableColumn<>("");
		colDeleteBuchung.setPrefWidth(36); // schmal
		colDeleteBuchung.setSortable(false);
		colDeleteBuchung.setResizable(false);

		colDeleteBuchung.setCellFactory(tc -> new TableCell<>() {
			private final Button btn = new Button("✖"); // oder "X"
			{
				btn.setFocusTraversable(false);
				btn.setMinSize(24, 24);
				btn.setMaxSize(24, 24);
				btn.setStyle("-fx-font-weight: bold; -fx-text-fill: #a00; -fx-padding: 0;");

				btn.setOnAction(e -> {

					Buchung b = getTableView().getItems().get(getIndex());
					bestaetigeUndLoesche(b);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btn);
				setText(null);
			}
		});

		if (!tblBuchungen.getColumns().contains(colDeleteBuchung)) {
			tblBuchungen.getColumns().add(colDeleteBuchung);
		}
	}

	private void setupKontoLoeschen() {

		colDeleteKonto = new TableColumn<>("");
		colDeleteKonto.setPrefWidth(36); // schmal
		colDeleteKonto.setSortable(false);
		colDeleteKonto.setResizable(false);

		colDeleteKonto.setCellFactory(tc -> new TableCell<>() {
			private final Button btn = new Button("✖"); // oder "X"
			{
				btn.setFocusTraversable(false);
				btn.setMinSize(24, 24);
				btn.setMaxSize(24, 24);
				btn.setStyle("-fx-font-weight: bold; -fx-text-fill: #a00; -fx-padding: 0;");

				btn.setOnAction(e -> {
					Konto k = getTableView().getItems().get(getIndex());
					bestaetigeUndLoesche(k);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btn);
				setText(null);
			}
		});

		if (!tblKonten.getColumns().contains(colDeleteKonto)) {
			tblKonten.getColumns().add(colDeleteKonto);
		}

	}

	private void bestaetigeUndLoesche(Buchung b) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Buchung löschen");
		confirm.setHeaderText("Buchung wirklich löschen?");
		confirm.setContentText(String.format("Datum: %s%nArt: %s%nKategorie: %s%nBetrag: %.2f €",
				b.getFormatiertesDatum(), b.getBuchungsart(), b.getKategorie(), b.getBetrag()));

		confirm.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				BuchungsAktionen.loescheBuchung(b);
			}
			buchungsListe.setAll(b.getKonto().getBuchungen());
			tblKonten.refresh();
			updateGesamtSummeLabel();

		});
	}

	private void bestaetigeUndLoesche(Konto k) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konto löschen");
		confirm.setHeaderText("Konto wirklich löschen? Alle beteiligten Buchungen werden ebenfalls gelöscht!");
		confirm.setContentText(String.format("Konto: %s%nInhaber: %s%nInstitut: %s", k.getKontoName(), k.getInhaber(),
				k.getKreditinstitut()));

		confirm.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				KontoAktionen.loescheKonto(k);
			}
			kontenListe.setAll(Konto.getAlleKonten());
			tblKonten.refresh();
			updateGesamtSummeLabel();

		});
	}

	private void oeffneBearbeitenDialog(Buchung b) {

		// String art = b.getBuchungsart();
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Buchung bearbeiten";

		switch (b.getBuchungsart()) {
		case "Einnahme":
			dialogOeffnen(btnNeueEin, fxmlPfad, titel, (DialogBuchung c) -> {
				c.setEditMode(true);
				c.prefillFields(b);
				c.setOriginal(b);
			});
			break;

		case "Ausgabe":
			dialogOeffnen(btnNeueAus, fxmlPfad, titel, (DialogBuchung c) -> {
				c.setEditMode(true);
				c.prefillFields(b);
				c.setOriginal(b);
			});
			break;
		default:
			System.out.println("⚠️ Unbekannte Buchungsart: " + b.getBuchungsart());
		}
	}

	private void oeffneBearbeitenDialog(Konto konto) {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/konto-dialog.fxml";
		String titel = "Konto bearbeiten";

		dialogOeffnen(btnNeuesKonto, fxmlPfad, titel, (DialogKonto c) -> {
			c.setEditMode(true);
			try {
				c.prefillKontodaten(konto);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.setOriginal(konto);
		});
		updateGesamtSummeLabel();
	}

	public double berechneSumme(FilteredList<Buchung> Liste) {
		double summe = 0.0;

		for (Buchung b : Liste) {
			if (b == null)
				continue;

			double betrag = b.getBetrag();
			if ("Ausgabe".equals(b.getBuchungsart())) {
				betrag *= -1; // Ausgaben negativ zählen
			}

			summe += betrag;
		}
		return summe;
	}

	public double berechneGesamtsummeKonten(ObservableList<Konto> kontenListe) {
		double summe = 0.0;
		for (Konto k : kontenListe) {
			if (k != null) {
				summe += k.getKontostand();
			}
		}
		return summe;
	}

	private void updateGesamtSummeLabel() {
		double summe = berechneGesamtsummeKonten(tblKonten.getItems());
		sumLbl.setText(String.format("Gesamt: %.2f €", summe));
	}

}
