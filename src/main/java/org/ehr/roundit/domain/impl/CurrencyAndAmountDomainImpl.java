package org.ehr.roundit.domain.impl;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;

import java.math.BigInteger;

@Data
@Builder
@Getter
public class CurrencyAndAmountDomainImpl implements CurrencyAndAmount {
    private final Currency currency;
    private final BigInteger minorUnits;
}
