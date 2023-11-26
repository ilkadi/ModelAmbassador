package org.ehr.roundit.adapters.rest.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceV2 {
    private CurrencyAndAmountImpl clearedBalance;
    private CurrencyAndAmountImpl effectiveBalance;
    private CurrencyAndAmountImpl pendingTransactions;
    private CurrencyAndAmountImpl acceptedOverdraft;
    private CurrencyAndAmountImpl amount;
    private CurrencyAndAmountImpl totalClearedBalance;
    private CurrencyAndAmountImpl totalEffectiveBalance;
}
