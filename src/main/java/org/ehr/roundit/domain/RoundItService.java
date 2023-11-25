package org.ehr.roundit.domain;

import org.ehr.roundit.domain.entities.RoundItPortData;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;

import java.math.BigInteger;

public interface RoundItService {
    BigInteger roundItAndSave(RoundItPortData roundItPortData) throws UnableToProvideDataException;
}
