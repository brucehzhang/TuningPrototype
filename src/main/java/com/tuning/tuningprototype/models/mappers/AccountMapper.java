package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Account;
import com.tuning.tuningprototype.models.db.AccountDto;

public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto dto);
}