package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Account;
import com.tuning.tuningprototype.models.User;
import com.tuning.tuningprototype.models.UserDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    @Lazy
    private final AccountMapper accountMapper;
    @Lazy
    private final ExperimentMapper experimentMapper;

    @Override
    public UserDto toDto(User user) {
        if (user == null) return null;

        Account account = user.getAccount();

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                account != null ? account.getId() : null,
                Hibernate.isInitialized(account) ? accountMapper.toDto(account) : null,
                user.getLicenseType(),
                user.getCreatedAt(),
                user.getModifiedAt(),
                Hibernate.isInitialized(user.getExperiments())
                        ? user.getExperiments().stream().map(experimentMapper::toDto).toList() : null
        );
    }

    @Override
    public User toEntity(UserDto dto, Account accountReference) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .middleName(dto.middleName())
                .lastName(dto.lastName())
                .account(accountReference)
                .licenseType(dto.licenseType())
                .createdAt(dto.createdAt())
                .modifiedAt(dto.modifiedAt())
                .build();
    }
}