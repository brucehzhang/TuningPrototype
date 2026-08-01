package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Account;
import com.tuning.tuningprototype.models.AccountDto;

public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto dto);
}