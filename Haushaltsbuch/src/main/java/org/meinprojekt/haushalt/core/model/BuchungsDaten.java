package org.meinprojekt.haushalt.core.model;

import java.time.LocalDate;
import java.util.Objects;

public final class BuchungsDaten {

	public enum Buchungstyp {
		EINNAHME("Einnahme"), AUSGABE("Ausgabe"), UMBUCHUNG("Umbuchung");
		
		private final String label;

		Buchungstyp(String label) {
			this.label = label;
		}
		 @Override
		    public String toString() {
		        return label;  
		    }
		 public static Buchungstyp typAusString(String typ) {
			 if (typ.equalsIgnoreCase("Einnahme")) {
				 return Buchungstyp.EINNAHME;}
			 else if (typ.equalsIgnoreCase("Ausgbe")) {
					 return Buchungstyp.AUSGABE;
				 }
			 else return Buchungstyp.UMBUCHUNG;
			 
			 
		 }
	}
		
	

	// Pflicht
	private final double betrag;
	private final String kategorie;
	private final LocalDate buchungsdatum;
	private final Konto konto;
	private final Buchungstyp typ;

	// Optional
	private final String beschreibung;
	private final String gegenpartei; // z.B. Sender bei Einnahme, Empfänger bei Ausgabe
	private final TransferInfo transfer;

	private BuchungsDaten(Builder b) {
		this.betrag = b.betrag;
		this.kategorie = b.kategorie;
		this.buchungsdatum = b.buchungsdatum;
		this.konto = b.konto;
		this.typ = b.typ;
		this.beschreibung = b.beschreibung;
		this.gegenpartei = b.gegenpartei;
		this.transfer = b.transfer;
	}

	/** Startpunkt: nur Pflichtfelder */
	public static Builder builder(double betrag, String kategorie, LocalDate buchungsdatum, Konto konto,
			Buchungstyp typ) {
		return new Builder(betrag, kategorie, buchungsdatum, konto, typ);
	}

	public record TransferInfo(String transferId, boolean istUmbuchung) {

	}

	public static final class Builder {
		private final double betrag;
		private final String kategorie;
		private final LocalDate buchungsdatum;
		private final Konto konto;
		private final Buchungstyp typ;

		private String beschreibung;
		private String gegenpartei;
		private TransferInfo transfer;

		private Builder(double betrag, String kategorie, LocalDate buchungsdatum, Konto konto, Buchungstyp typ) {
			// Validierung
			if (betrag == 0)
				throw new IllegalArgumentException("Betrag darf nicht 0 sein");
			this.kategorie = Objects.requireNonNull(kategorie, "Kategorie fehlt");
			this.buchungsdatum = Objects.requireNonNull(buchungsdatum, "Buchungsdatum fehlt");
			this.konto = Objects.requireNonNull(konto, "Konto fehlt");
			this.typ = Objects.requireNonNull(typ, "Buchungstyp fehlt");
			this.betrag = betrag;
		}

		public Builder beschreibung(String beschreibung) {
			this.beschreibung = beschreibung;
			return this;
		}

		/** Sender (Einnahme) oder Empfänger (Ausgabe) als neutrale "Gegenpartei" */
		public Builder gegenpartei(String gegenpartei) {
			this.gegenpartei = gegenpartei;
			return this;
		}

		public Builder transfer(String transferId, boolean istUmbuchung) {
			this.transfer = new TransferInfo(transferId, istUmbuchung);
			return this;
		}

		public BuchungsDaten build() {
			return new BuchungsDaten(this);
		}
	}

	// Getter
	public double getBetrag() {
		return betrag;
	}

	public String getKategorie() {
		return kategorie;
	}

	public LocalDate getBuchungsdatum() {
		return buchungsdatum;
	}

	public Konto getKonto() {
		return konto;
	}

	public Buchungstyp getTyp() {
		return typ;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String getGegenpartei() {
		return gegenpartei;
	}

	public TransferInfo getTransfer() {
		return transfer;
	}
}