package org.ehr.roundit.domain.entities;

public interface UserAccount {
    String getAccountUid();
    Currency getCurrency();
    AccountType getAccountType();
}
