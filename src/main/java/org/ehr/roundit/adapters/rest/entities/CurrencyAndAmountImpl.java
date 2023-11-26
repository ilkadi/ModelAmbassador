package org.ehr.roundit.adapters.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAndAmountImpl implements CurrencyAndAmount {
    private Currency currency;
    private BigInteger minorUnits;
}
