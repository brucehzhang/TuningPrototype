package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Account;
import com.tuning.tuningprototype.models.db.entity.AccountDto;

public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto dto);
}