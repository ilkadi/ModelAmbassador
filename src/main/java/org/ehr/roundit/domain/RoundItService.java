package org.ehr.roundit.domain;

import org.ehr.roundit.domain.entities.Currency;

import java.math.BigInteger;

public interface RoundItService {
    BigInteger weeklySaverForCurrency(String saverName, Currency currency);
}
