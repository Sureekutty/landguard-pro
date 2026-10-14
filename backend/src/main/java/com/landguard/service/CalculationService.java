package com.landguard.service;

import com.landguard.model.CostCalculationResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationService {

    public static final double SQ_FT_PER_CENT = 435.6;
    public static final double TN_STAMP_DUTY_RATE = 0.07;     // 7%
    public static final double TN_REGISTRATION_RATE = 0.04;   // 4%
    public static final double LEGAL_OPINION_ESTIMATE = 15000.0;
    public static final double MUTATION_KIST_ESTIMATE = 5000.0;
    public static final double FENCING_RESERVE_ESTIMATE = 20000.0;

    public CostCalculationResult calculateTotalCost(double sqFt, double ratePerSqFt) {
        if (sqFt < 0) sqFt = 0;
        if (ratePerSqFt < 0) ratePerSqFt = 0;

        double basePrice = round(sqFt * ratePerSqFt);
        double cents = round(sqFt / SQ_FT_PER_CENT);
        double stampDuty = round(basePrice * TN_STAMP_DUTY_RATE);
        double regFee = round(basePrice * TN_REGISTRATION_RATE);
        double totalGovtTax = round(stampDuty + regFee);

        double legalOpinion = (sqFt > 0) ? LEGAL_OPINION_ESTIMATE : 0.0;
        double mutationKist = (sqFt > 0) ? MUTATION_KIST_ESTIMATE : 0.0;
        double fencing = (sqFt > 0) ? FENCING_RESERVE_ESTIMATE : 0.0;

        double totalOutflow = round(basePrice + totalGovtTax + legalOpinion + mutationKist + fencing);

        return new CostCalculationResult(
            round(sqFt),
            cents,
            round(ratePerSqFt),
            basePrice,
            stampDuty,
            regFee,
            totalGovtTax,
            legalOpinion,
            mutationKist,
            fencing,
            totalOutflow
        );
    }

    private double round(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
