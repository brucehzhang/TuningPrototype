package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Account;
import com.tuning.tuningprototype.models.db.entity.AccountDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    private final UserMapper _userMapper;

    public AccountMapper(@Lazy UserMapper userMapper) {
        _userMapper = userMapper;
    }

    public AccountDto toDto(Account account) {
        if (account == null) return null;

        return new AccountDto(
                account.getId(),
                account.getName(),
                account.getCreatedTime(),
                account.getModifiedTime(),
                Hibernate.isInitialized(account.getUsers())
                        ? account.getUsers().stream().map(_userMapper::toDto).toList() : null
        );
    }

    public Account toEntity(AccountDto dto) {
        if (dto == null) return null;

        return Account.builder()
                .id(dto.id())
                .name(dto.name())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}