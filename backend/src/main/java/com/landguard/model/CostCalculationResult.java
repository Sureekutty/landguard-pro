package com.landguard.model;

public class CostCalculationResult {
    private double sqFt;
    private double cents;
    private double ratePerSqFt;
    private double baseLandPrice;
    private double stampDuty;       // 7% TN Stamp Duty
    private double registrationFee;  // 4% TN Registration Fee
    private double totalGovtTax;     // 11% Statutory Government Charges
    private double legalOpinionFee; // Title Search Opinion & Drafting (~15,000)
    private double mutationAndKist;  // Patta Mutation & Panchayat Kist (~5,000)
    private double fencingReserve;   // Stone fixing & boundary fencing (~20,000)
    private double totalOutflow;

    public CostCalculationResult() {}

    public CostCalculationResult(double sqFt, double cents, double ratePerSqFt, double baseLandPrice,
                                 double stampDuty, double registrationFee, double totalGovtTax,
                                 double legalOpinionFee, double mutationAndKist, double fencingReserve,
                                 double totalOutflow) {
        this.sqFt = sqFt;
        this.cents = cents;
        this.ratePerSqFt = ratePerSqFt;
        this.baseLandPrice = baseLandPrice;
        this.stampDuty = stampDuty;
        this.registrationFee = registrationFee;
        this.totalGovtTax = totalGovtTax;
        this.legalOpinionFee = legalOpinionFee;
        this.mutationAndKist = mutationAndKist;
        this.fencingReserve = fencingReserve;
        this.totalOutflow = totalOutflow;
    }

    public double getSqFt() { return sqFt; }
    public void setSqFt(double sqFt) { this.sqFt = sqFt; }

    public double getCents() { return cents; }
    public void setCents(double cents) { this.cents = cents; }

    public double getRatePerSqFt() { return ratePerSqFt; }
    public void setRatePerSqFt(double ratePerSqFt) { this.ratePerSqFt = ratePerSqFt; }

    public double getBaseLandPrice() { return baseLandPrice; }
    public void setBaseLandPrice(double baseLandPrice) { this.baseLandPrice = baseLandPrice; }

    public double getStampDuty() { return stampDuty; }
    public void setStampDuty(double stampDuty) { this.stampDuty = stampDuty; }

    public double getRegistrationFee() { return registrationFee; }
    public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }

    public double getTotalGovtTax() { return totalGovtTax; }
    public void setTotalGovtTax(double totalGovtTax) { this.totalGovtTax = totalGovtTax; }

    public double getLegalOpinionFee() { return legalOpinionFee; }
    public void setLegalOpinionFee(double legalOpinionFee) { this.legalOpinionFee = legalOpinionFee; }

    public double getMutationAndKist() { return mutationAndKist; }
    public void setMutationAndKist(double mutationAndKist) { this.mutationAndKist = mutationAndKist; }

    public double getFencingReserve() { return fencingReserve; }
    public void setFencingReserve(double fencingReserve) { this.fencingReserve = fencingReserve; }

    public double getTotalOutflow() { return totalOutflow; }
    public void setTotalOutflow(double totalOutflow) { this.totalOutflow = totalOutflow; }
}
