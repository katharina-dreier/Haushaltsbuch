package org.meinprojekt.haushalt.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.meinprojekt.haushalt.core.filter.Zeitraum;
import org.meinprojekt.haushalt.core.filter.ZeitraumArt;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.BuchungsDaten.Buchungstyp;
import org.meinprojekt.haushalt.core.model.DiagrammDaten;
import org.meinprojekt.haushalt.core.model.DiagrammDaten.Aufloesung;
import org.meinprojekt.haushalt.core.model.Konto;
import org.meinprojekt.haushalt.core.model.WiederkehrendeZahlung;
import org.meinprojekt.haushalt.core.service.BuchungsService;
import org.meinprojekt.haushalt.core.service.DiagrammService;
import org.meinprojekt.haushalt.core.service.FilterService;
import org.meinprojekt.haushalt.core.service.KontoService;
import org.meinprojekt.haushalt.core.service.WiederkehrendeZahlungenService;
import org.meinprojekt.haushalt.speicher.Datenstroeme;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	@FXML
	private VBox kontenArea, buchungenArea, ansichtenArea, uebersichtArea;
	@FXML
	private HBox diagrammLegende;
	@FXML
	private AreaChart<String, Number> einnahmenAusgabenDiagramm;
	@FXML
	private CategoryAxis xAchseEinnahmenAusgabenDiagramm;
	@FXML
	private NumberAxis yAchseEinnahmenAusgabenDiagramm;
	@FXML
	TabPane tabPaneBuchungen, tabPaneAnsichten;
	@FXML
	private Tab tabGesamt, tabEinnahmen, tabAusgaben, tabUmbuchungen, tabDiagramme, tabWiederkehrendeZahlungen,
			tabBuchungen;

	@FXML
	private Label summeAlleKontenLbl, summeBuchungenLbl;
	@FXML
	private Label legendeEinnahmenlbl, legendeAusgabenlbl, legendeDifferenzlbl;
	@FXML
	private Label lblVon, lblBis;
	@FXML
	private Label lblEinnahmenUebersicht, lblNochOffeneEinnahmenUebersicht, summeNochOffeneEinnahmenUebersicht,
			lblAusgabenUebersicht, lblKontostandNachFixkostenUebersicht, summeEinnahmenUebersicht,
			summeAusgabenUebersicht, summeNachFixkostenUebersicht, summeNachFixkostenVeraenderungUebersicht,
			lblNochOffeneFixkostenUebersicht, summeNochOffeneFixkostenUebersicht;
	@FXML
	private Label lblTopKat1Name, lblTopKat1Betrag, lblTopKat2Name, lblTopKat2Betrag, lblTopKat3Name, lblTopKat3Betrag;
	@FXML
	private Button btnNeuesKonto, btnNeueAusgabe, btnNeueEinnahme, btnNeueUmbuchung, btnAuswahlAnwenden;

	@FXML
	private ChoiceBox<ZeitraumArt> auswahlBox;
	@FXML
	private DatePicker startDatumPicker, endDatumPicker;
	@FXML
	private MenuButton kategorieFilterButton;
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
	private TableColumn<Buchung, String> colBeschreibung;
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

	@FXML
	private TableView<WiederkehrendeZahlung> tblWiederkehrendeBuchungen;
	@FXML
	private TableColumn<WiederkehrendeZahlung, LocalDate> colNaechstesBuchungsDatum;
	@FXML
	private TableColumn<WiederkehrendeZahlung, String> colWKKategorie;
	@FXML
	private TableColumn<WiederkehrendeZahlung, String> colWKBeschreibung;
	@FXML
	private TableColumn<WiederkehrendeZahlung, String> colWKEmpfaenger;
	@FXML
	private TableColumn<WiederkehrendeZahlung, String> colWKSender;
	@FXML
	private TableColumn<WiederkehrendeZahlung, Double> colWKBetrag;
	@FXML
	private TableColumn<WiederkehrendeZahlung, String> colWKKonto;
	@FXML
	private TableColumn<WiederkehrendeZahlung, Void> colWKAusfuehren;

	private final ObservableList<Konto> kontenListe = FXCollections.observableArrayList();
	private final ObservableList<Buchung> buchungsListe = FXCollections.observableArrayList();
	private final ObservableList<WiederkehrendeZahlung> wiederkehrendeBuchungsListe = FXCollections
			.observableArrayList();
	private FilteredList<Buchung> gefilterteBuchungsListe;
	private SortedList<Buchung> sortierteBuchungsListe;
	private final NumberFormat euro = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	private final FilterService filterService = new FilterService();
	private Predicate<Buchung> aktuellerTabFilter = _ -> true;
	private Predicate<Buchung> aktuellerZeitraumFilter = _ -> true;
	private Predicate<Buchung> aktuellerKategorieFilter = _ -> true;
	private Predicate<Buchung> kombinierterFilter = _ -> true;
	private final Map<CheckMenuItem, String> kategoriemap = new HashMap<>();
	private Zeitraum aktuellerZeitraum = null;

	@FXML
	private void initialize() {

		Datenstroeme.kontenUebersichtAnlegen();
		Datenstroeme.kategorieUebersichtAnlegen();
		Datenstroeme.ladeKontenAusDatei();
		Datenstroeme.ladeKategorienAusDatei();
		Datenstroeme.ladeBuchungenFuerAlleKonten();
		Datenstroeme.ladeWiederkehrendeZahlungenFuerAlleKonten();

		ladeBuchungenListe();
		ladeWiederkehrendeBuchungenListe();
		initialisiereKategorieAuswahlBox();
		initialisiereZeitraumAuswahlBox();

		tabPaneAnsichten.getSelectionModel().select(tabDiagramme);
		tabPaneAnsichten.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> {
			if (neu == tabDiagramme) {
				ladeEinnahmenAusgabenDiagramm();
			}
			if (neu == tabWiederkehrendeZahlungen) {
				ladeWiederkehrendeBuchungenListe();
			}
			if (neu == tabBuchungen) {
				ladeBuchungenListe();
			}
		});

		tabPaneBuchungen.getSelectionModel().select(tabGesamt);
		tabPaneBuchungen.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> {
			applyTabFilter();
		});

		setupKontenTabelle();
		setupKontoLoeschen();
		setupKontoBearbeiten();

		setupBuchungenTabelle();
		setupBuchungLoeschen();
		setupBuchungBearbeiten();

		setupWiederkehrendeBuchungenTabelle();
		setupWiederkehrendeZahlungBearbeiten();
		setupWiederkehrendeZahlungAusfuehren();

		// Liste der Tabelle zuweisen
		kontenListe.setAll(Konto.getAlleKonten());
		tblKonten.setItems(kontenListe);

		updateGesamtSummeLabel();

		// Listener für die Auswahl eines Kontos in der Tabelle
		tblKonten.getSelectionModel().selectedItemProperty().addListener((obs, altesKonto, neuesKonto) -> {
			buchungenAnzeigen(neuesKonto);
			ansichtAktualisieren();
		});

		summeBuchungenAktualisieren();
		ladeEinnahmenAusgabenDiagramm();
		initialisiereTooltips();
		ladeUebersicht();

	}

	private void ladeUebersicht() {
		updateUebersichtEinnahmen();
		updateUebersichtNochOffeneEinnahmen();
		updateUebersichtAusgaben();

		updateUebersichtNochOffeneFixkosten();
		updateUebersichtNachFixkosten();
		updateAusgabenNachKategorienUebersicht();

	}

	private void updateAusgabenNachKategorienUebersicht() {
		FilterService filterService = new FilterService();
		Zeitraum zeitraum = Zeitraum.aktuellerMonat();
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		FilteredList<Buchung> gefilterteBuchungsListeFuerÜbersicht = new FilteredList<>(buchungsListe, predicate);
		List<Map.Entry<String, Double>> topAusgabenNachKategorien = BuchungsService.bestimmeAusgabenNachKategorien(gefilterteBuchungsListeFuerÜbersicht);
		clearTopKategorieLabels();
		setTopKategorieLabel(lblTopKat1Name, lblTopKat1Betrag, topAusgabenNachKategorien, 0);
		setTopKategorieLabel(lblTopKat2Name, lblTopKat2Betrag, topAusgabenNachKategorien, 1);
		setTopKategorieLabel(lblTopKat3Name, lblTopKat3Betrag, topAusgabenNachKategorien, 2);

	}

	private void clearTopKategorieLabels() {
		lblTopKat1Name.setText("");
		lblTopKat1Betrag.setText("");
		lblTopKat2Name.setText("");
		lblTopKat2Betrag.setText("");
		lblTopKat3Name.setText("");
		lblTopKat3Betrag.setText("");
	}

	private void setTopKategorieLabel(Label nameLabel, Label valueLabel, List<Map.Entry<String, Double>> liste, int index) {
			if (index >= liste.size()) {
			// Falls es weniger als 3 Kategorien gibt: Zeile leer lassen
			return;
		}

		Map.Entry<String, Double> entry = liste.get(index);
		String kategorie = entry.getKey();
		double betrag = entry.getValue();
		double prozent = (betrag / berechneSummeAusgabenAktuellerMonat()) * 100;

		nameLabel.setText((index + 1) + ". " + kategorie);
		valueLabel.setText(String.format("%,.2f € (%.0f%%)", betrag, prozent));
	}

	private void updateUebersichtEinnahmen() {
		String einnahmenText = "Einnahmen in diesem Monat: ";
		lblEinnahmenUebersicht.setText(einnahmenText);
		double summeEinnahmen = berechneSummeEinnahmenAktuellerMonat();
		summeEinnahmenUebersicht.setText(euro.format(summeEinnahmen));
	}

	private double berechneSummeEinnahmenAktuellerMonat() {
		FilterService filterService = new FilterService();
		Zeitraum zeitraum = Zeitraum.aktuellerMonat();
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		FilteredList<Buchung> gefilterteBuchungsListeFuerÜbersicht = new FilteredList<>(buchungsListe, predicate);
		double summeEinnahmen = BuchungsService.berechneSummeEinnahmen(gefilterteBuchungsListeFuerÜbersicht);
		return summeEinnahmen;
	}

	private void updateUebersichtNochOffeneEinnahmen() {
		String nochOffeneEinnahmenText = "Noch offen";
		lblNochOffeneEinnahmenUebersicht.setText(nochOffeneEinnahmenText);
		Konto konto = tblKonten.getSelectionModel().getSelectedItem();
		if (konto != null) {
			double summeNochOffeneEinnahmen = WiederkehrendeZahlungenService
					.berechneNochOffeneEinnahmenImAktuellenMonat(konto);
			summeNochOffeneEinnahmenUebersicht.setText(euro.format(summeNochOffeneEinnahmen));
			return;
		} else {
			double summeNochOffeneEinnahmen = WiederkehrendeZahlungenService
					.berechneNochOffeneEinnahmenImAktuellenMonat();
			summeNochOffeneEinnahmenUebersicht.setText(euro.format(summeNochOffeneEinnahmen));
		}
	}

	private void updateUebersichtAusgaben() {
		String ausgabenText = "Ausgaben in diesem Monat: ";
		lblAusgabenUebersicht.setText(ausgabenText);
		double summeAusgaben = berechneSummeAusgabenAktuellerMonat();
		summeAusgabenUebersicht.setText(euro.format(summeAusgaben));
	}

	private double berechneSummeAusgabenAktuellerMonat() {
		FilterService filterService = new FilterService();
		Zeitraum zeitraum = Zeitraum.aktuellerMonat();
		Predicate<Buchung> predicate = filterService.predicateFuerZeitraum(zeitraum);
		FilteredList<Buchung> gefilterteBuchungsListeFuerÜbersicht = new FilteredList<>(buchungsListe, predicate);
		double summeAusgaben = BuchungsService.berechneSummeAusgaben(gefilterteBuchungsListeFuerÜbersicht);
		return summeAusgaben;
	}

	private void updateUebersichtNochOffeneFixkosten() {
		String nochOffeneFixkostenText = "Noch offen";
		lblNochOffeneFixkostenUebersicht.setText(nochOffeneFixkostenText);
		Konto konto = tblKonten.getSelectionModel().getSelectedItem();
		if (konto != null) {
			double summeNochOffeneFixkosten = WiederkehrendeZahlungenService
					.berechneNochOffeneFixkostenImAktuellenMonat(konto);
			summeNochOffeneFixkostenUebersicht.setText(euro.format(summeNochOffeneFixkosten));
			return;
		} else {
			double summeNochOffeneFixkosten = WiederkehrendeZahlungenService
					.berechneNochOffeneFixkostenImAktuellenMonat();
			summeNochOffeneFixkostenUebersicht.setText(euro.format(summeNochOffeneFixkosten));
		}
	}

	private void updateUebersichtNachFixkosten() {
		Konto konto = tblKonten.getSelectionModel().getSelectedItem();
		if (konto == null) {
			String nachFixkostenText = "Vorrausichtliche Summe aller Konten am Monatsende";
			lblKontostandNachFixkostenUebersicht.setText(nachFixkostenText);
			double aktuellerKontostand = berechneGesamtSummeKonten(kontenListe);
			double summeEinnahmen = berechneSummeEinnahmenAktuellerMonat();
			double summeAusgaben = berechneSummeAusgabenAktuellerMonat();
			double summeEinnahmenOffen = WiederkehrendeZahlungenService.berechneNochOffeneEinnahmenImAktuellenMonat();
			double summeAusgabenOffen = WiederkehrendeZahlungenService.berechneNochOffeneFixkostenImAktuellenMonat();
			double summeNochOffeneZahlungen = summeEinnahmenOffen - summeAusgabenOffen;
			double summeVeraenderung = summeEinnahmen + summeEinnahmenOffen - summeAusgaben - summeAusgabenOffen;
			double summeNachFixkosten = aktuellerKontostand + summeNochOffeneZahlungen;
			summeNachFixkostenUebersicht.setText(euro.format(summeNachFixkosten));
			summeNachFixkostenVeraenderungUebersicht.setText(euro.format(summeVeraenderung));
			summeNachFixkostenVeraenderungUebersicht.getStyleClass()
					.setAll(summeVeraenderung >= 0 ? "overview-value-plus" : "overview-value-minus");
		} else {
			String nachFixkostenText = "Vorrausichtlicher Kontostand am Monatsende: ";
			lblKontostandNachFixkostenUebersicht.setText(nachFixkostenText);
			double aktuellerKontostand = konto.getKontostand();
			double summeEinnahmen = berechneSummeEinnahmenAktuellerMonat();
			double summeAusgaben = berechneSummeAusgabenAktuellerMonat();
			double summeEinnahmenOffen = WiederkehrendeZahlungenService
					.berechneNochOffeneEinnahmenImAktuellenMonat(konto);
			double summeAusgabenOffen = WiederkehrendeZahlungenService
					.berechneNochOffeneFixkostenImAktuellenMonat(konto);
			double summeNochOffeneZahlungen = summeEinnahmenOffen - summeAusgabenOffen;
			double summeVeraenderung = summeEinnahmen + summeEinnahmenOffen - summeAusgaben - summeAusgabenOffen;
			double summeNachFixkosten = aktuellerKontostand + summeNochOffeneZahlungen;
			summeNachFixkostenUebersicht.setText(euro.format(summeNachFixkosten));
			summeNachFixkostenVeraenderungUebersicht.setText(euro.format(summeVeraenderung));
			summeNachFixkostenVeraenderungUebersicht.getStyleClass()
					.setAll(summeVeraenderung >= 0 ? "overview-value-plus" : "overview-value-minus");
		}
	}

	private void initialisiereKategorieAuswahlBox() {
		kategorieFilterButton.getItems().clear();
		kategoriemap.clear();

		for (String kat : Buchung.getListeMitKategorien()) {
			CheckMenuItem item = new CheckMenuItem(kat);
			item.setSelected(true); // standardmäßig alle an
			item.selectedProperty().addListener((obs, alt, neu) -> aktualisiereKategorieFilter());
			kategorieFilterButton.getItems().add(item);
			kategoriemap.put(item, kat);
		}

		kategorieFilterButton.setText("Alle Kategorien");
	}

	private Set<String> getAusgewaehlteKategorien() {
		Set<String> selected = new HashSet<>();
		for (CheckMenuItem item : kategoriemap.keySet()) {
			if (item.isSelected()) {
				selected.add(kategoriemap.get(item));
			}
		}
		return selected;
	}

	private void aktualisiereKategorieFilter() {

		Set<String> ausgewaehlteKategorien = getAusgewaehlteKategorien();
		aktuellerKategorieFilter = filterService.predicateFuerKategorien(ausgewaehlteKategorien);
		updateKategorieButtonText(ausgewaehlteKategorien);
		aktualisiereFilter();
	}

	private void updateKategorieButtonText(Set<String> ausgewaehlteKategorien) {
		if (ausgewaehlteKategorien.isEmpty()) {
			kategorieFilterButton.setText("Keine Kategorie");
		} else if (ausgewaehlteKategorien.size() == Buchung.getListeMitKategorien().size()) {
			kategorieFilterButton.setText("Alle Kategorien");
		} else if (ausgewaehlteKategorien.size() == 1) {
			kategorieFilterButton.setText(ausgewaehlteKategorien.iterator().next());
		} else {
			kategorieFilterButton.setText(ausgewaehlteKategorien.size() + " ausgewählt");
		}

	}

	private LocalDate getMaxDatum(FilteredList<Buchung> gefilterteBuchungsListe) {
		LocalDate maxDatum = null;
		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			if (maxDatum == null || datum.isAfter(maxDatum)) {
				maxDatum = datum;
			}
		}
		return maxDatum;
	}

	private LocalDate getMinDatum(FilteredList<Buchung> gefilterteBuchungsListe) {
		LocalDate minDatum = null;
		for (Buchung buchung : gefilterteBuchungsListe) {
			LocalDate datum = buchung.getBuchungsDatum();
			if (minDatum == null || datum.isBefore(minDatum)) {
				minDatum = datum;
			}
		}
		return minDatum;
	}

	private void setupBuchungBearbeiten() {
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

	}

	private void setupWiederkehrendeZahlungBearbeiten() {
		// Doppelklick auf Buchung zum Bearbeiten
		tblWiederkehrendeBuchungen.setRowFactory(tv -> {
			TableRow<WiederkehrendeZahlung> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				// Nur echte Zeilen + Doppelklick
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					WiederkehrendeZahlung selected = row.getItem();
					oeffneBearbeitenDialog(selected);
				}
			});
			return row;
		});

	}

	private void setupKontoBearbeiten() {
		// Doppelklick auf Konto zum Bearbeiten
		tblKonten.setRowFactory(_ -> {
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

	private void initialisiereZeitraumAuswahlBox() {
		auswahlBox.getItems().setAll(ZeitraumArt.values());
		System.out.println("Auswahlbox Werte gesetzt.");
		auswahlBox.setValue(ZeitraumArt.AKTUELLER_MONAT); // Standardauswahl
		aktuellerZeitraum = ZeitraumArt.zeitraumAusArt(auswahlBox.getValue());
		System.out.println("Aktueller Zeitraum gesetzt: " + aktuellerZeitraum);
		setzeFilterSichtbarkeiten(auswahlBox.getValue());
		applyZeitraumFilter(auswahlBox.getValue());
		System.out.println("Zeitraumfilter angewendet für: " + auswahlBox.getValue());
		prefillDatumPicker(auswahlBox.getValue());
		System.out.println("DatumPicker vorbefüllt.");

		auswahlBox.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> {
			handleZeitraumAuswahlAenderung(alt, neu);

		});
	}

	private void handleZeitraumAuswahlAenderung(ZeitraumArt alt, ZeitraumArt neu) {

		if (neu == null) {
			return;
		}
		ZeitraumArt vorherigeAuswahl = alt;
		ZeitraumArt neueAuswahl = neu;

		setzeFilterSichtbarkeiten(neueAuswahl);
		switch (neueAuswahl) {
		case BENUTZERDEFINIERT -> {
			prefillDatumPicker(vorherigeAuswahl);

		}
		case ALLE_ZEITEN -> {
			applyZeitraumFilter(ZeitraumArt.ALLE_ZEITEN);
			LocalDate minDatum = getMinDatum(gefilterteBuchungsListe);
			LocalDate maxDatum = getMaxDatum(gefilterteBuchungsListe);
			aktuellerZeitraum = Zeitraum.benutzerdefinierterZeitraum(minDatum, maxDatum);

		}
		case AKTUELLER_MONAT, VORHERIGER_MONAT, AKTUELLES_JAHR, VORHERIGES_JAHR -> {
			aktuellerZeitraum = ZeitraumArt.zeitraumAusArt(neueAuswahl);
			applyZeitraumFilter(neueAuswahl);

		}
		default -> {

		}
		}
	}

	private void prefillDatumPicker(ZeitraumArt value) {
		if (aktuellerZeitraum != null) {
			startDatumPicker.setValue(aktuellerZeitraum.getVon());
			endDatumPicker.setValue(aktuellerZeitraum.getBis());
		} else {
			startDatumPicker.setValue(null);
			endDatumPicker.setValue(null);
		}

	}

	private void setzeFilterSichtbarkeiten(ZeitraumArt neu) {
		boolean benutzerdefiniert = neu == ZeitraumArt.BENUTZERDEFINIERT;
		lblVon.setVisible(benutzerdefiniert);
		lblVon.setManaged(benutzerdefiniert);
		startDatumPicker.setVisible(benutzerdefiniert);
		startDatumPicker.setManaged(benutzerdefiniert);
		lblBis.setVisible(benutzerdefiniert);
		lblBis.setManaged(benutzerdefiniert);
		endDatumPicker.setVisible(benutzerdefiniert);
		endDatumPicker.setManaged(benutzerdefiniert);
		btnAuswahlAnwenden.setVisible(benutzerdefiniert);
		btnAuswahlAnwenden.setManaged(benutzerdefiniert);

		if (!benutzerdefiniert) {
			// evtl später andere Sichtbarkeiten anpassen
		}

	}

	private void applyZeitraumFilter(ZeitraumArt zeitraumArt) {

		// if (zeitraumArt == ZeitraumArt.BENUTZERDEFINIERT) {
		switch (zeitraumArt) {
		case BENUTZERDEFINIERT -> {
			LocalDate von = startDatumPicker.getValue();
			LocalDate bis = endDatumPicker.getValue();
			aktuellerZeitraum = Zeitraum.benutzerdefinierterZeitraum(von, bis);
			aktuellerZeitraumFilter = filterService.predicateFuerZeitraum(aktuellerZeitraum);

		}
		case ALLE_ZEITEN -> {
			aktuellerZeitraumFilter = _ -> true; // Alle Zeiten
			aktualisiereFilter();
			aktuellerZeitraum = Zeitraum.benutzerdefinierterZeitraum(getMinDatum(gefilterteBuchungsListe),
					getMaxDatum(gefilterteBuchungsListe));

		}
		case AKTUELLER_MONAT, VORHERIGER_MONAT, AKTUELLES_JAHR, VORHERIGES_JAHR -> {
			Zeitraum zeitraum = ZeitraumArt.zeitraumAusArt(zeitraumArt);
			if (zeitraum != null) {
				aktuellerZeitraumFilter = filterService.predicateFuerZeitraum(zeitraum);

			} else {
				aktuellerZeitraumFilter = _ -> true; // Alle Zeiten

			}
		}
		default -> aktualisiereFilter();
		}
		aktualisiereFilter();
	}

	private void aktualisiereFilter() {
		kombinierterFilter = aktuellerTabFilter.and(aktuellerZeitraumFilter).and(aktuellerKategorieFilter);
		gefilterteBuchungsListe.setPredicate(kombinierterFilter);
		ansichtAktualisieren();
	}

	private void ansichtAktualisieren() {
		summeBuchungenAktualisieren();
		ladeBuchungenListe();
		ladeWiederkehrendeBuchungenListe();
		ladeEinnahmenAusgabenDiagramm();
		ladeUebersicht();
	}

	@FXML
	private void handleAuswahlAnwenden() {
		LocalDate von = startDatumPicker.getValue();
		LocalDate bis = endDatumPicker.getValue();
		if (von != null && bis != null && !bis.isBefore(von)) {
			applyZeitraumFilter(ZeitraumArt.BENUTZERDEFINIERT);
			ansichtAktualisieren();
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ungültiger Zeitraum");
			alert.setHeaderText("Bitte gültige Start- und Enddaten eingeben.");
			alert.setContentText("Das Enddatum darf nicht vor dem Startdatum liegen.");
			alert.showAndWait();
		}
	}

	private void setupBuchungenTabelle() {
		colBuchungsDatum.setCellValueFactory(new PropertyValueFactory<>("buchungsDatum"));
		colKategorie.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
		colBeschreibung.setCellValueFactory(new PropertyValueFactory<>("beschreibung"));
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
		tblBuchungen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
	}

	private void setupWiederkehrendeBuchungenTabelle() {
		colNaechstesBuchungsDatum.setCellValueFactory(new PropertyValueFactory<>("naechsteZahlungAm"));
		colNaechstesBuchungsDatum.setSortType(TableColumn.SortType.ASCENDING);
		tblWiederkehrendeBuchungen.getSortOrder().clear();
		tblWiederkehrendeBuchungen.getSortOrder().add(colNaechstesBuchungsDatum);
		colWKKategorie.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
		colWKBeschreibung.setCellValueFactory(new PropertyValueFactory<>("beschreibung"));
		colWKEmpfaenger.setCellValueFactory(new PropertyValueFactory<>("empfaenger"));
		colWKSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
		colWKBetrag.setCellValueFactory(new PropertyValueFactory<>("betrag"));
		colWKBetrag.setCellValueFactory(cellData -> {
			WiederkehrendeZahlung zahlung = cellData.getValue();
			double value = zahlung.getBetrag();

			if (zahlung.getBuchungsart().equalsIgnoreCase("Ausgabe")) {
				value = -value;
			}

			return new ReadOnlyObjectWrapper<>(value);
		});

		colWKBetrag.setCellFactory(column -> new TableCell<>() {
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
		colWKKonto.setCellValueFactory(new PropertyValueFactory<>("kontoAnzeige"));
		tblWiederkehrendeBuchungen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		tblWiederkehrendeBuchungen.sort();
	}

	private void setupKontenTabelle() {
		colKontonummer.setCellValueFactory(new PropertyValueFactory<>("kontonummer"));
		colKontoName.setCellValueFactory(new PropertyValueFactory<>("kontoName"));
		colInstitut.setCellValueFactory(new PropertyValueFactory<>("kreditinstitut"));
		colKontostand.setCellValueFactory(new PropertyValueFactory<>("kontostand"));
		colKontostand.setCellFactory(tc -> new TableCell<Konto, Double>() {
			private final Label lblWert = new Label();
			private final Label lblDiff = new Label();
			private final HBox box = new HBox(6, lblWert, lblDiff);

			{
				box.setAlignment(Pos.CENTER_LEFT);
			}

			@Override
			protected void updateItem(Double kontostand, boolean empty) {
				super.updateItem(kontostand, empty);

				if (empty || kontostand == null) {
					setGraphic(null);
					return;
				}
				Konto konto = (Konto) getTableRow().getItem();
				if (konto == null) {
					setGraphic(null);
					return;
				}
				double startSaldo = KontoService.getStartSaldoMonatsanfang(konto);
				double differenz = kontostand - startSaldo;

				lblWert.setText(String.format("%,.2f €", kontostand));
				lblWert.getStyleClass().setAll("konto-betrag");

				lblDiff.setText(String.format("%+.0f €", differenz));
				lblDiff.getStyleClass().setAll(differenz >= 0 ? "konto-diff-positiv" : "konto-diff-negativ");

				setGraphic(box);
			}
		});

		tblKonten.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
	}

	private void ladeBuchungenListe() {
		Konto konto = tblKonten.getSelectionModel().getSelectedItem();
		if (konto != null) {
			buchungsListe.setAll(konto.getBuchungen());
		} else
			buchungsListe.setAll(Konto.getAlleBuchungen());
		gefilterteBuchungsListe = new FilteredList<>(buchungsListe, kombinierterFilter);
		sortierteBuchungsListe = new SortedList<>(gefilterteBuchungsListe);
		sortierteBuchungsListe.comparatorProperty().bind(tblBuchungen.comparatorProperty());
		tblBuchungen.setItems(sortierteBuchungsListe);
		tblBuchungen.getSortOrder().add(colBuchungsDatum);
		tblBuchungen.sort();
		berechneSumme(gefilterteBuchungsListe);
	}

	private void ladeWiederkehrendeBuchungenListe() {
		wiederkehrendeBuchungsListe.setAll(Konto.getAlleWiederkehrendeZahlungen());
		tblWiederkehrendeBuchungen.setItems(wiederkehrendeBuchungsListe);
		tblWiederkehrendeBuchungen.getSortOrder().add(colNaechstesBuchungsDatum);
		tblWiederkehrendeBuchungen.sort();
	}

	private void ladeEinnahmenAusgabenDiagramm() {
		if (ansichtenArea == null)
			return;
		Zeitraum zeitraum = aktuellerZeitraum;
		DiagrammDaten diagrammDaten = DiagrammService.berechneDiagrammDaten(gefilterteBuchungsListe, zeitraum);
		einnahmenAusgabenDiagramm.setTitle(null);
		einnahmenAusgabenDiagramm.setLegendVisible(false);
		einnahmenAusgabenDiagramm.setAnimated(false);

		// achsenLabelSetzenEinnahmenAusgabenDiagramm(diagrammDaten);

		double summeEinnahmen = diagrammDaten.getSummeEinnahmen();
		double summeAusgaben = diagrammDaten.getSummeAusgaben();
		double summeDifferenz = diagrammDaten.getSummeDifferenz();

		XYChart.Series<String, Number> einnahmenSerie = new XYChart.Series<>();
		XYChart.Series<String, Number> ausgabenSerie = new XYChart.Series<>();

		serienFuellen(diagrammDaten, einnahmenSerie, ausgabenSerie);

		einnahmenAusgabenDiagramm.getData().clear();
		einnahmenAusgabenDiagramm.getData().add(einnahmenSerie);
		einnahmenAusgabenDiagramm.getData().add(ausgabenSerie);

		yAchseAnpassenEinnamenAusgabenDiagramm(diagrammDaten);

		legendeBerechnenUndSetzen(summeEinnahmen, summeAusgaben, summeDifferenz);
		initialisiereTooltips();

	}

	private void legendeBerechnenUndSetzen(double summeEinnahmen, double summeAusgaben, double summeDifferenz) {
		String legendeEinnahmen = euro.format(summeEinnahmen);
		legendeEinnahmenlbl.setText(legendeEinnahmen);

		String legendeAusgaben = euro.format(summeAusgaben);
		legendeAusgabenlbl.setText(legendeAusgaben);

		String legendeDifferenz = euro.format(summeDifferenz);
		legendeDifferenzlbl.setText(legendeDifferenz);
	}

	private void serienFuellen(DiagrammDaten diagrammDaten, Series<String, Number> einnahmenSerie,
			Series<String, Number> ausgabenSerie) {
		Map<LocalDate, Double> gefilterteEinnahmen = diagrammDaten.getGefilterteEinnahmen();
		Map<LocalDate, Double> gefilterteAusgaben = diagrammDaten.getGefilterteAusgaben();
		List<LocalDate> alleWerte = diagrammDaten.getxWerteSortiert();
		Aufloesung aufloesung = diagrammDaten.getAufloesung();

		for (LocalDate wert : alleWerte) {

			DateTimeFormatter formatter = switch (aufloesung) {
			case TAGE -> DateTimeFormatter.ofPattern("dd.MM.", Locale.GERMAN);
			case MONATE -> DateTimeFormatter.ofPattern("MMM yyyy", Locale.GERMAN);
			case JAHRE -> DateTimeFormatter.ofPattern("yyyy", Locale.GERMAN);
			};
			String label = wert.format(formatter);
			double ein = gefilterteEinnahmen.getOrDefault(wert, 0.0);
			double aus = gefilterteAusgaben.getOrDefault(wert, 0.0);

			einnahmenSerie.getData().add(new XYChart.Data<>(label, ein));
			ausgabenSerie.getData().add(new XYChart.Data<>(label, aus));
		}

	}

	private void yAchseAnpassenEinnamenAusgabenDiagramm(DiagrammDaten diagrammDaten) {
		double maxWert = diagrammDaten.getMaxWert();
		double schritt = diagrammDaten.getTickEinheit();

		yAchseEinnahmenAusgabenDiagramm.setAutoRanging(false);
		yAchseEinnahmenAusgabenDiagramm.setLowerBound(0);
		yAchseEinnahmenAusgabenDiagramm.setUpperBound(Math.ceil(maxWert / schritt) * schritt);
		yAchseEinnahmenAusgabenDiagramm.setTickUnit(schritt);
		yAchseEinnahmenAusgabenDiagramm.setMinorTickVisible(false);
	}

	private void initialisiereTooltips() {
		for (XYChart.Series<String, Number> series : einnahmenAusgabenDiagramm.getData()) {
			for (XYChart.Data<String, Number> data : series.getData()) {
				String text = euro.format(data.getYValue().doubleValue());
				Tooltip tooltip = new Tooltip(text);
				tooltip.setShowDelay(Duration.millis(100));
				tooltip.setHideDelay(Duration.millis(200));
				tooltip.setShowDuration(Duration.seconds(10));
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
		ladeEinnahmenAusgabenDiagramm();
	}

	@FXML
	private void handleNeueEinnahme() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Einnahme anlegen";
		dialogOeffnen(btnNeueEinnahme, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart(Buchungstyp.EINNAHME);
			c.applyBuchungsart();
		});
		aktualisiereKontenTabelle();
		updateGesamtSummeLabel();
		ansichtAktualisieren();
		initialisiereKategorieAuswahlBox();
	}

	@FXML
	private void handleNeueAusgabe() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Ausgabe anlegen";
		dialogOeffnen(btnNeueAusgabe, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart(Buchungstyp.AUSGABE);
			c.applyBuchungsart();
		});
		aktualisiereKontenTabelle();
		updateGesamtSummeLabel();
		ansichtAktualisieren();
		initialisiereKategorieAuswahlBox();
	}

	@FXML
	private void handleNeueUmbuchung() {
		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Neue Umbuchung anlegen";
		dialogOeffnen(btnNeueUmbuchung, fxmlPfad, titel, (DialogBuchung c) -> {
			c.setBuchungsart(Buchungstyp.UMBUCHUNG);
			c.applyBuchungsart();
		});
		aktualisiereKontenTabelle();
		updateGesamtSummeLabel();
		ansichtAktualisieren();
		initialisiereKategorieAuswahlBox();
	}

	public void aktualisiereKontenTabelle() {
		kontenListe.setAll(Konto.getAlleKonten());
		tblKonten.refresh();
	}

	public void buchungenAnzeigen(Konto konto) {
		if (konto != null) {
			// Aktualisiere die Buchungstabelle mit den Buchungen des ausgewählten Kontos
			List<Buchung> liste = konto.getBuchungen();
			buchungsListe.setAll(liste);
			applyTabFilter();
			applyZeitraumFilter(auswahlBox.getValue());
			colKonto.setVisible(false);

		} else {
			// Kein Konto ausgewählt, zeige alle Buchungen
			buchungsListe.setAll(Konto.getAlleBuchungen());
			applyTabFilter();
			colKonto.setVisible(true);
		}

	}

	private <T> T dialogOeffnen(Button btn, String fxmlPfad, String titel, Consumer<T> setup) {
		try {
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
			dialogStage.showAndWait();
			aktualisiereKontenTabelle();

			return dialogController;

		} catch (Exception ex) {
			Alert a = new Alert(Alert.AlertType.ERROR, "Dialog konnte nicht geladen werden:\n" + ex.getMessage());
			a.setHeaderText("Fehler beim Öffnen");
			a.showAndWait();
			ex.printStackTrace();
			return null;
		}
	}

	private void applyTabFilter() {
		if (gefilterteBuchungsListe == null) {
			System.out.println("⚠️ Gefilterte Buchungsliste ist null!");
			return;
		}

		var aktTab = tabPaneBuchungen.getSelectionModel().getSelectedItem();
		if (aktTab == tabEinnahmen) {
			aktuellerTabFilter = filterService.predicateFuerBuchungsArt("EINNAHME");

		} else if (aktTab == tabAusgaben) {
			aktuellerTabFilter = filterService.predicateFuerBuchungsArt("AUSGABE");
		} else if (aktTab == tabUmbuchungen) {
			aktuellerTabFilter = filterService.predicateFuerKategorie("UMBUCHUNG");
		} else {
			aktuellerTabFilter = b -> true;
		}
		aktualisiereFilter();
		ansichtAktualisieren();

	}

	private void summeBuchungenAktualisieren() {
		summeBuchungenLbl.setText(String.format("Summe: %.2f €", berechneSumme(gefilterteBuchungsListe)));
	}

	private void setupBuchungLoeschen() {
		colBuchungLoeschen = new TableColumn<>("");
		colBuchungLoeschen.setSortable(false);
		colBuchungLoeschen.setResizable(false);

		colBuchungLoeschen.setCellFactory(tc -> new TableCell<>() {
			private final Button btnBuchungLoeschen = new Button("\uD83D\uDDD1");
			private final HBox box = new HBox(btnBuchungLoeschen);
			{
				box.setAlignment(Pos.CENTER);
				box.setSpacing(0);

				btnBuchungLoeschen.getStyleClass().add("delete-button");
				btnBuchungLoeschen.setFocusTraversable(false);

				Tooltip tooltip = new Tooltip("Löschen");
				Tooltip.install(btnBuchungLoeschen, tooltip);

				btnBuchungLoeschen.setOnAction(e -> {
					Buchung buchung = getTableView().getItems().get(getIndex());
					bestaetigeUndLoesche(buchung);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(box);
				}
				setText(null);
			}
		});

		if (!tblBuchungen.getColumns().contains(colBuchungLoeschen)) {
			tblBuchungen.getColumns().add(colBuchungLoeschen);
		}

	}

	private void setupKontoLoeschen() {

		colKontoLoeschen = new TableColumn<>("");
		colKontoLoeschen.setSortable(false);
		colKontoLoeschen.setResizable(false);

		colKontoLoeschen.setCellFactory(tc -> new TableCell<>() {
			private final Button btnKontoLoeschen = new Button("\uD83D\uDDD1"); // oder "X"
			private final HBox box = new HBox(btnKontoLoeschen);
			{
				box.setAlignment(Pos.CENTER);
				box.setSpacing(0);

				btnKontoLoeschen.getStyleClass().add("delete-button");
				btnKontoLoeschen.setFocusTraversable(false);

				Tooltip tooltip = new Tooltip("Löschen");
				Tooltip.install(btnKontoLoeschen, tooltip);

				btnKontoLoeschen.setOnAction(e -> {
					Konto k = getTableView().getItems().get(getIndex());
					bestaetigeUndLoesche(k);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(box);
				}
				setText(null);
			}
		});

		if (!tblKonten.getColumns().contains(colKontoLoeschen)) {
			tblKonten.getColumns().add(colKontoLoeschen);
		}

	}

	private void setupWiederkehrendeZahlungAusfuehren() {

		colWKAusfuehren.setSortable(false);
		colWKAusfuehren.setResizable(false);

		colWKAusfuehren.setCellFactory(tc -> new TableCell<WiederkehrendeZahlung, Void>() {

			private final Label statusLabel = new Label();
			private final Button btnBuchen = new Button("\u21BB"); // ↻ „ausführen“
			private final HBox box = new HBox(6, statusLabel, btnBuchen);

			{
				box.setAlignment(Pos.CENTER);
				box.getStyleClass().add("wkz-cell");

				statusLabel.getStyleClass().add("wkz-status");

				btnBuchen.getStyleClass().add("wkz-action-button");
				btnBuchen.setFocusTraversable(false);

				Tooltip tooltip = new Tooltip("Jetzt buchen");
				tooltip.setShowDelay(Duration.millis(150));
				Tooltip.install(btnBuchen, tooltip);

				btnBuchen.setOnAction(e -> {
					WiederkehrendeZahlung wz = getTableView().getItems().get(getIndex());
					WiederkehrendeZahlungenService.wiederkehrendeZahlungAusfuehren(wz);
					ansichtAktualisieren();
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || getTableRow() == null || getTableRow().getItem() == null) {
					setGraphic(null);
					return;
				}

				WiederkehrendeZahlung wz = (WiederkehrendeZahlung) getTableRow().getItem();

				boolean faellig = WiederkehrendeZahlungenService.isNochFaellig(wz);

				if (faellig) {
					statusLabel.setText("\u23F3"); // ⏳
					statusLabel.getStyleClass().setAll("wkz-status", "wkz-status-pending");
					btnBuchen.setVisible(true);
					btnBuchen.setManaged(true);
				} else {
					statusLabel.setText("\u2713"); // ✓
					statusLabel.getStyleClass().setAll("wkz-status", "wkz-status-done");
					btnBuchen.setVisible(false); // Button ausblenden
					btnBuchen.setManaged(false);
				}

				setGraphic(box);
			}
		});
	}

	private void bestaetigeUndLoesche(Buchung b) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Buchung löschen");
		confirm.setHeaderText("Buchung wirklich löschen?");
		confirm.setContentText(String.format("Datum: %s%nArt: %s%nKategorie: %s%nBetrag: %.2f €",
				b.getFormatiertesDatum(), b.getBuchungsart(), b.getKategorie(), b.getBetrag()));

		confirm.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				BuchungsService.loescheBuchung(b);
			}

			ansichtAktualisieren();
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
				KontoService.loescheKonto(k);
			}
			kontenListe.setAll(Konto.getAlleKonten());
			tblKonten.refresh();
			ansichtAktualisieren();
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
				c.setIsWiederkehrend(false);
				c.prefillFields(b);
				c.setOriginal(b);
				c.applyBuchungsart();

			});
			break;

		case "Ausgabe":
			dialogOeffnen(btnNeueAusgabe, fxmlPfad, titel, (DialogBuchung c) -> {
				c.setEditMode(true);
				c.setIsWiederkehrend(false);
				c.prefillFields(b);
				c.setOriginal(b);
				c.applyBuchungsart();

			});
			break;
		default:
			System.out.println("⚠️ Unbekannte Buchungsart: " + b.getBuchungsart());
		}
	}

	private void oeffneBearbeitenDialog(WiederkehrendeZahlung zahlung) {

		String fxmlPfad = "/org/meinprojekt/haushalt/ui/buchung-dialog.fxml";
		String titel = "Wiederkehrende Zahlung bearbeiten";

		switch (zahlung.getBuchungsart()) {
		case "Einnahme":
			dialogOeffnen(btnNeueEinnahme, fxmlPfad, titel, (DialogBuchung c) -> {

				c.setIsWiederkehrend(true);
				c.prefillFields(zahlung);
				c.setOriginal(zahlung);
				c.setEditMode(true);
				c.applyBuchungsart();

			});
			break;

		case "Ausgabe":
			dialogOeffnen(btnNeueAusgabe, fxmlPfad, titel, (DialogBuchung c) -> {

				c.setIsWiederkehrend(true);
				c.prefillFields(zahlung);
				c.setOriginal(zahlung);
				c.setEditMode(true);
				c.applyBuchungsart();

			});
			break;
		default:
			System.out.println("⚠️ Unbekannte Buchungsart: " + zahlung.getBuchungsart());
		}
		ansichtAktualisieren();
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

	public double berechneGesamtSummeKonten(ObservableList<Konto> kontenListe) {
		double summe = 0.0;
		for (Konto k : kontenListe) {
			if (k != null) {
				summe += k.getKontostand();
			}
		}
		return summe;
	}

	private void updateGesamtSummeLabel() {
		double summe = berechneGesamtSummeKonten(tblKonten.getItems());
		summeAlleKontenLbl.setText(String.format("Gesamt: %.2f €", summe));
	}

}
