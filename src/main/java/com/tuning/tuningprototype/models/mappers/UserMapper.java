package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Account;
import com.tuning.tuningprototype.models.User;
import com.tuning.tuningprototype.models.UserDto;

public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto, Account accountReference);
}