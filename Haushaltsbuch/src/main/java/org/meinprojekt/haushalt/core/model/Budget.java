package org.meinprojekt.haushalt.core.model;

public class Budget {
	
	private double maxBudgetfuerKategorie;
	private double bereitsVerbrauchtesBudget;
	private String kategorie;
	private boolean budgetUeberschritten;
	
	public Budget(double maxBudgetfuerKategorie,double bereitsVerbrauchtesBudget, String kategorie, boolean budgetUeberschritten) {
		this.maxBudgetfuerKategorie = maxBudgetfuerKategorie;
		this.bereitsVerbrauchtesBudget = bereitsVerbrauchtesBudget;
		this.kategorie = kategorie;
		this.budgetUeberschritten = budgetUeberschritten;
	}
	
	public double getMaxBudgetfuerKategorie() {
		return maxBudgetfuerKategorie;
	}

	public double getBereitsVerbrauchtesBudget() {
		return bereitsVerbrauchtesBudget;
	}

	public String getKategorie() {
		return kategorie;
	}

	public boolean isBudgetUeberschritten() {
		if (bereitsVerbrauchtesBudget > maxBudgetfuerKategorie) {
			budgetUeberschritten = true;
		} else {
			budgetUeberschritten = false;
		}
		return budgetUeberschritten;
	}
	
	public void setMaxBudgetfuerKategorie(double maxBudgetfuerKategorie) {
		this.maxBudgetfuerKategorie = maxBudgetfuerKategorie;
	}
	
	public void setBereitsVerbrauchtesBudget(double bereitsVerbrauchtesBudget) {
		this.bereitsVerbrauchtesBudget = bereitsVerbrauchtesBudget;
	}
	
	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public void setBudgetUeberschritten(boolean budgetUeberschritten) {
		this.budgetUeberschritten = budgetUeberschritten;
	}
	
	public double berechneVerfuegbaresBudget() {
		return maxBudgetfuerKategorie - bereitsVerbrauchtesBudget;
	}
	
	public void aktualisiereBereitsVerbrauchtesBudget(double betrag) {
		this.bereitsVerbrauchtesBudget += betrag;
	}
	

}
