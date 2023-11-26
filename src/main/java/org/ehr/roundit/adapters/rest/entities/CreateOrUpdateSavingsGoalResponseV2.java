package org.ehr.roundit.adapters.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehr.roundit.domain.entities.SavingsGoal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateSavingsGoalResponseV2 implements SavingsGoal {
    private String savingsGoalUid;
}
