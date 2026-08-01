package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Account;
import com.tuning.tuningprototype.models.AccountDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapperImpl implements AccountMapper {

    @Lazy
    private final UserMapper userMapper;

    @Override
    public AccountDto toDto(Account account) {
        if (account == null) return null;

        return new AccountDto(
                account.getId(),
                account.getName(),
                account.getCreatedAt(),
                account.getModifiedAt(),
                Hibernate.isInitialized(account.getUsers())
                        ? account.getUsers().stream().map(userMapper::toDto).toList() : null
        );
    }

    @Override
    public Account toEntity(AccountDto dto) {
        if (dto == null) return null;

        return Account.builder()
                .id(dto.id())
                .name(dto.name())
                .createdAt(dto.createdAt())
                .modifiedAt(dto.modifiedAt())
                .build();
    }
}