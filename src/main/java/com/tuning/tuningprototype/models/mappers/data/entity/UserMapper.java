package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.db.entity.UserDto;

public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}