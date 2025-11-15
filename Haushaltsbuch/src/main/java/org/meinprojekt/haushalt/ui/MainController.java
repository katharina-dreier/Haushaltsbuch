package org.meinprojekt.haushalt.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class MainController {

	@FXML
	private VBox kontenArea, buchungenArea, statistikArea;
	@FXML
	private HBox diagrammLegende;
	@FXML
	private AreaChart<String, Number> einnahmenAusgabenDiagramm;
	@FXML
	private CategoryAxis xAchseEinnahmenAusgabenDiagramm;
	@FXML
	private NumberAxis yAchseEinnahmenAusgabenDiagramm;
	@FXML
	TabPane tabPane;
	@FXML
	private Tab tabGesamt, tabEinnahmen, tabAusgaben, tabUmbuchungen;

	@FXML
	private Label summeAlleKontenLbl, summeBuchungenLbl, legendeEinnahmenlbl, legendeAusgabenlbl, legendeDifferenzlbl;
	@FXML
	private Button btnNeuesKonto, btnNeueAusgabe, btnNeueEinnahme, btnNeueUmbuchung;

	@FXML
	private TableView<Konto> tblKonten;
	@FXML
	private TableColumn<Konto, Integer> colKontonummer;
	@FXML
	private TableColumn<Konto, String> colKontoName;
	@FXML
	private TableColumn<Konto, Double> colKontostand;
	@FXML
	private TableColumn<Konto, String> colInstitut;
	@FXML
	private TableColumn<Konto, Void> colKontoLoeschen;

	@FXML
	private TableView<Buchung> tblBuchungen;
	@FXML
	private TableColumn<Buchung, String> colBuchungsDatum;
	@FXML
	private TableColumn<Buchung, String> colKategorie;
	@FXML
	private TableColumn<Buchung, String> colEmpfaenger;
	@FXML
	private TableColumn<Buchung, String> colSender;
	@FXML
	private TableColumn<Buchung, Double> colBetrag;
	@FXML
	private TableColumn<Buchung, String> colKonto;
	@FXML
	private TableColumn<Buchung, Void> colBuchungLoeschen;

	private final ObservableList<Konto> kontenListe = FXCollections.observableArrayList();
	private final ObservableList<Buchung> buchungsListe = FXCollections.observableArrayList();
	private FilteredList<Buchung> gefilterteBuchungsListe;
	private SortedList<Buchung> sortierteBuchungsListe;

	@FXML
	private void initialize() {
		
		Datenstroeme.kontenUebersichtAnlegen();
		Datenstroeme.kategorieUebersichtAnlegen();
		Datenstroeme.ladeKontenAusDatei();
		Datenstroeme.ladeKategorienAusDatei();
		Datenstroeme.ladeBuchungenFuerAlleKonten();

		buchungslisteInitialisieren();
		showTabs();
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> applyTabFilter());
		summeBuchungenAktualisieren();

		// Spalten mit Attributen verknüpfen
		colKontonummer.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
		colKontoName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
		colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));
		colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
		colKontostand.setCellFactory(column -> new TableCell<>() {
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
		setupKontoLoeschen();

		colBuchungsDatum.setCellValueFactory(new PropertyValueFactory<>("buchungsDatum"));
		colKategorie.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
		colEmpfaenger.setCellValueFactory(new PropertyValueFactory<>("empfaenger"));
		colSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
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
		colKonto.setCellValueFactory(new PropertyValueFactory<>("kontoAnzeige"));

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
		
		tblKonten.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		tblBuchungen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		showBuchungen();
		initialisierungEinnahmenAusgabenDiagramm();

	}

	private void buchungslisteInitialisieren() {
		buchungsListe.setAll(Konto.getAlleBuchungen());
		gefilterteBuchungsListe = new FilteredList<>(buchungsListe, b -> true);
		sortierteBuchungsListe = new SortedList<>(gefilterteBuchungsListe); 
		sortierteBuchungsListe.comparatorProperty().bind(tblBuchungen.comparatorProperty()); 
		tblBuchungen.setItems(sortierteBuchungsListe);
		berechneSumme(gefilterteBuchungsListe);
			
	}

	private void initialisierungEinnahmenAusgabenDiagramm() {
		 if (statistikArea == null) return;

		    String labelXAchse = "Monat";
		    xAchseEinnahmenAusgabenDiagramm.setLabel(labelXAchse);

		    String labelYAchse = "Betrag";
		    yAchseEinnahmenAusgabenDiagramm.setLabel(labelYAchse);

		    einnahmenAusgabenDiagramm.setTitle(null);            
		    einnahmenAusgabenDiagramm.setLegendVisible(false);
		    einnahmenAusgabenDiagramm.setAnimated(false);

		    XYChart.Series<String, Number> einnahmenSerie = new XYChart.Series<>();
		    XYChart.Series<String, Number> ausgabenSerie  = new XYChart.Series<>();
		    einnahmenSerie.setName("Einnahmen");
		    ausgabenSerie.setName("Ausgaben");

		    Map<String, Double> einnahmenProMonat = new TreeMap<>();
		    Map<String, Double> ausgabenProMonat = new TreeMap<>();

		    double summeEinnahmen = 0;
		    double summeAusgaben  = 0;

		    for (Buchung buchung : buchungsListe) {  
		        LocalDate datum = buchung.getBuchungsDatum(); 
		        String monatKey = datum.getYear() + "-" + String.format("%02d", datum.getMonthValue());

		        if ("EINNAHME".equalsIgnoreCase(buchung.getBuchungsart())) {
		            einnahmenProMonat.merge(monatKey, buchung.getBetrag(), Double::sum);
		            summeEinnahmen += buchung.getBetrag();
		        } else if ("AUSGABE".equalsIgnoreCase(buchung.getBuchungsart())) {
		            ausgabenProMonat.merge(monatKey, buchung.getBetrag(), Double::sum);
		            summeAusgaben += buchung.getBetrag();
		        }
		    }

		    double summeDifferenz = summeEinnahmen - summeAusgaben;

		    // chronologisch durch Monate laufen
		    Set<String> alleMonate = new TreeSet<>();
		    alleMonate.addAll(einnahmenProMonat.keySet());
		    alleMonate.addAll(ausgabenProMonat.keySet());

		    for (String monat : alleMonate) {
		        einnahmenSerie.getData().add(
		                new XYChart.Data<>(monat, einnahmenProMonat.getOrDefault(monat, 0.0))
		        );
		        ausgabenSerie.getData().add(
		                new XYChart.Data<>(monat, ausgabenProMonat.getOrDefault(monat, 0.0))
		        );
		    }

		    einnahmenAusgabenDiagramm.getData().clear();
			einnahmenAusgabenDiagramm.getData().add(einnahmenSerie);
			einnahmenAusgabenDiagramm.getData().add(ausgabenSerie);

		    
		    NumberFormat euro = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		    yAchseEinnahmenAusgabenDiagramm.setTickLabelFormatter(new StringConverter<Number>() {
		        @Override
		        public String toString(Number object) {
		            return euro.format(object.doubleValue());
		        }

		        @Override
		        public Number fromString(String string) {
		            return 0;
		        }
		    });
		    double maxWert = Math.max(
		            einnahmenProMonat.values().stream().mapToDouble(Double::doubleValue).max().orElse(0),
		            ausgabenProMonat.values().stream().mapToDouble(Double::doubleValue).max().orElse(0)
		    );

		    double schritt = 200; 
		    if (maxWert > 1000) schritt = 500;
		    if (maxWert > 3000) schritt = 1000;

		    yAchseEinnahmenAusgabenDiagramm.setAutoRanging(false);
		    yAchseEinnahmenAusgabenDiagramm.setLowerBound(0);
		    yAchseEinnahmenAusgabenDiagramm.setUpperBound(Math.ceil(maxWert / schritt) * schritt);
		    yAchseEinnahmenAusgabenDiagramm.setTickUnit(schritt);
		    yAchseEinnahmenAusgabenDiagramm.setMinorTickVisible(false);

		    String legendeEinnahmen = euro.format(summeEinnahmen);
		    legendeEinnahmenlbl.setText(legendeEinnahmen);

		    String legendeAusgaben = euro.format(summeAusgaben);
		    legendeAusgabenlbl.setText(legendeAusgaben);

		    String legendeDifferenz = euro.format(summeDifferenz);
		    legendeDifferenzlbl.setText(legendeDifferenz);

		   
		    
		    for (XYChart.Series<String, Number> series : einnahmenAusgabenDiagramm.getData()) {
		        for (XYChart.Data<String, Number> data : series.getData()) {
		            String text = euro.format(data.getYValue().doubleValue());
		            Tooltip tooltip = new Tooltip(text);
		            tooltip.setShowDelay(Duration.millis(100));      // nach 0,1 Sekunde
		            tooltip.setHideDelay(Duration.millis(200));      // kurze Verzögerung beim Weggehen
		            tooltip.setShowDuration(Duration.seconds(10));   // wie lange maximal sichtbar
		            Tooltip.install(data.getNode(), tooltip);
		        }
		    }
		}
	

	
	@FXML
	private void handleNeuesKonto() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/konto-dialog.fxml";
		String titel = "Neues Konto anlegen";
		dialogOeffnen(btnNeuesKonto, fxmlPfad, titel, (DialogKonto c) -> {
		});
		updateGesamtSummeLabel();
		initialisierungEinnahmenAusgabenDiagramm();
	}

	@FXML
	private void handleNeueEinnahme() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Einnahme anlegen";
		dialogOeffnen(btnNeueEinnahme, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Einnahme");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
		initialisierungEinnahmenAusgabenDiagramm();
	}

	@FXML
	private void handleNeueAusgabe() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Ausgabe anlegen";
		dialogOeffnen(btnNeueAusgabe, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Ausgabe");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
		initialisierungEinnahmenAusgabenDiagramm();
	}

	@FXML
	private void handleNeueUmbuchung() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Umbuchung anlegen";
		dialogOeffnen(btnNeueUmbuchung, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart("Umbuchung");
		});
		aktualisiereTabelle();
		updateGesamtSummeLabel();
		initialisierungEinnahmenAusgabenDiagramm();
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
			colKonto.setVisible(false);

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
			Scene dialogScene = new Scene(root);
			dialogStage.setScene(dialogScene);
			dialogScene.getStylesheets().addAll(btn.getScene().getStylesheets());
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
	
	private void showTabs() {
		tabPane.setVisible(true);
		tabPane.setManaged(true);
	}

	private void applyTabFilter() {
		if (gefilterteBuchungsListe == null) {
			System.out.println("⚠️ Gefilterte Buchungsliste ist null!");
			return;
		}

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
		summeBuchungenAktualisieren();
	}
	
	private void summeBuchungenAktualisieren() {
		summeBuchungenLbl.setText(String.format("Summe: %.2f €", berechneSumme(gefilterteBuchungsListe)));
	}

	private void setupBuchungLoeschen() {
		colBuchungLoeschen = new TableColumn<>("");
		colBuchungLoeschen.setPrefWidth(36); // schmal
		colBuchungLoeschen.setSortable(false);
		colBuchungLoeschen.setResizable(false);

		colBuchungLoeschen.setCellFactory(tc -> new TableCell<>() {
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

		if (!tblBuchungen.getColumns().contains(colBuchungLoeschen)) {
			tblBuchungen.getColumns().add(colBuchungLoeschen);
		}
	}

	private void setupKontoLoeschen() {

		colKontoLoeschen = new TableColumn<>("");
		colKontoLoeschen.setPrefWidth(36); // schmal
		colKontoLoeschen.setSortable(false);
		colKontoLoeschen.setResizable(false);

		colKontoLoeschen.setCellFactory(tc -> new TableCell<>() {
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

		if (!tblKonten.getColumns().contains(colKontoLoeschen)) {
			tblKonten.getColumns().add(colKontoLoeschen);
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
			dialogOeffnen(btnNeueEinnahme, fxmlPfad, titel, (DialogBuchung c) -> {
				c.setEditMode(true);
				c.prefillFields(b);
				c.setOriginal(b);
			});
			break;

		case "Ausgabe":
			dialogOeffnen(btnNeueAusgabe, fxmlPfad, titel, (DialogBuchung c) -> {
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
		summeAlleKontenLbl.setText(String.format("Gesamt: %.2f €", summe));
	}

}
