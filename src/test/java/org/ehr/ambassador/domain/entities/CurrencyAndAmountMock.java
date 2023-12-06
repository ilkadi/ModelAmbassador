package org.ehr.ambassador.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigInteger;

@Data
@Builder
@Getter
public class CurrencyAndAmountMock implements CurrencyAndAmount {
    private Currency currency;
    private BigInteger minorUnits;
}