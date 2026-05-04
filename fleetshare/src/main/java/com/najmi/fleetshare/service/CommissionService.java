package com.najmi.fleetshare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for calculating platform commission and owner payout splits.
 * Used by the Central Payout Module to determine how payments are
 * divided between the platform and fleet owners.
 */
@Service
public class CommissionService {

    @Value("${fleetshare.commission.rate:0.10}")
    private BigDecimal commissionRate;

    @Value("${fleetshare.commission.enabled:true}")
    private boolean commissionEnabled;

    /**
     * Calculates the platform commission and owner payout for a given total amount.
     *
     * @param totalAmount Total payment amount
     * @return Map with "commission", "ownerPayout", and "rate" keys
     */
    public Map<String, BigDecimal> calculateSplit(BigDecimal totalAmount) {
        Map<String, BigDecimal> result = new HashMap<>();

        if (!commissionEnabled || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("commission", BigDecimal.ZERO);
            result.put("ownerPayout", totalAmount != null ? totalAmount : BigDecimal.ZERO);
            result.put("rate", BigDecimal.ZERO);
            return result;
        }

        BigDecimal commission = totalAmount
            .multiply(commissionRate)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal ownerPayout = totalAmount.subtract(commission);

        result.put("commission", commission);
        result.put("ownerPayout", ownerPayout);
        result.put("rate", commissionRate);
        return result;
    }

    /**
     * Returns whether commission/split payment is enabled.
     */
    public boolean isCommissionEnabled() {
        return commissionEnabled;
    }

    /**
     * Returns the current commission rate.
     */
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
}
