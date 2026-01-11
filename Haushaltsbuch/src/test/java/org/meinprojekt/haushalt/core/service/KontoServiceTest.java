package org.meinprojekt.haushalt.core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.meinprojekt.haushalt.core.model.Buchung;
import org.meinprojekt.haushalt.core.model.Konto;

class KontoServiceTest {

	@Test
	void getStartSaldoMonatsanfang_liefert_korrekten_Saldo_fuer_Konto() {
		
		Konto konto = new Konto("MeinKonto", "Max Mustermann", 0.0, "MeineBank");
		
		Buchung buchung1 = new Buchung();
		buchung1.setBetrag(1000.0);
		buchung1.setBuchungsart("Einnahme");
		buchung1.setBuchungsDatum(LocalDate.of(2024, 1, 15));
		Buchung buchung2 = new Buchung();
		buchung2.setBetrag(300.0);
		buchung2.setBuchungsart("Ausgabe");
		buchung2.setBuchungsDatum(LocalDate.of(2024, 1, 20));
		Buchung buchung3 = new Buchung();
		buchung3.setBetrag(500.0);
		buchung3.setBuchungsart("Einnahme");
		buchung3.setBuchungsDatum(LocalDate.now());
		Buchung buchung4 = new Buchung();
		buchung4.setBetrag(200.0);
		buchung4.setBuchungsart("Ausgabe");	
		buchung4.setBuchungsDatum(LocalDate.now());
		Buchung buchung5 = new Buchung();
		buchung5.setBetrag(100.0);
		buchung5.setBuchungsart("Anderes");
		buchung5.setBuchungsDatum(LocalDate.now());

		konto.getBuchungen().add(buchung1);
		konto.getBuchungen().add(buchung2);
		konto.getBuchungen().add(buchung3);
		konto.getBuchungen().add(buchung4);
		konto.getBuchungen().add(buchung5);

		double startSaldo = KontoService.getStartSaldoMonatsanfang(konto);
		assertEquals(700.0, startSaldo, 0.01);
		
		Konto konto2 = new Konto("LeeresKonto", "Inhaber", 0.0, "Bank");
		double startSaldo2 = KontoService.getStartSaldoMonatsanfang(konto2);
		assertEquals(0.0, startSaldo2, 0.01);
	}
	
	@Test
	void getStartSaldoMonatsanfang_liefert_korrekten_Saldo_fuer_alle_Konten() {
		
		Konto konto1 = new Konto("Konto1", "Inhaber1", 0.0, "Bank1");
		Konto konto2 = new Konto("Konto2", "Inhaber2", 0.0, "Bank2");

		Buchung buchung1 = new Buchung();
		buchung1.setBetrag(1500.0);
		buchung1.setBuchungsart("Einnahme");
		buchung1.setBuchungsDatum(LocalDate.of(2024, 1, 10));
		Buchung buchung2 = new Buchung();
		buchung2.setBetrag(400.0);
		buchung2.setBuchungsart("Ausgabe");
		buchung2.setBuchungsDatum(LocalDate.of(2024, 1, 18));

		Buchung buchung3 = new Buchung();
		buchung3.setBetrag(800.0);
		buchung3.setBuchungsart("Einnahme");
		buchung3.setBuchungsDatum(LocalDate.of(2024, 1, 5));
		Buchung buchung4 = new Buchung();
		buchung4.setBetrag(250.0);
		buchung4.setBuchungsart("Ausgabe");
		buchung4.setBuchungsDatum(LocalDate.of(2024, 1, 22));

		konto1.getBuchungen().add(buchung1);
		konto1.getBuchungen().add(buchung2);
		konto2.getBuchungen().add(buchung3);
		konto2.getBuchungen().add(buchung4);
		
		Konto.getKonten().put(konto1.getKontonummer(), konto1);
		Konto.getKonten().put(konto2.getKontonummer(), konto2);

		double startSaldo = KontoService.getStartSaldoMonatsanfang();
	

		assertEquals(1650.0, startSaldo, 0.01);
	}
		

}
