package org.ehr.roundit.domain.entities;

import java.math.BigInteger;

public interface CurrencyAndAmount {
    Currency getCurrency();
    BigInteger getMinorUnits();
}
