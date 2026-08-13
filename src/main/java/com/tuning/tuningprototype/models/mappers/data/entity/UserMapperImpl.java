package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.db.entity.UserDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    private final ExperimentMapper _experimentMapper;

    public UserMapperImpl(@Lazy ExperimentMapper experimentMapper) {
        _experimentMapper = experimentMapper;
    }

    @Override
    public UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getAccountId(),
                user.getLicenseType(),
                user.getCreatedTime(),
                user.getModifiedTime(),
                Hibernate.isInitialized(user.getExperiments())
                        ? user.getExperiments().stream().map(_experimentMapper::toDto).toList() : null
        );
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .middleName(dto.middleName())
                .lastName(dto.lastName())
                .accountId(dto.accountId())
                .licenseType(dto.licenseType())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}