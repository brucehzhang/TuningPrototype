package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.User;
import com.tuning.tuningprototype.models.db.UserDto;

public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}