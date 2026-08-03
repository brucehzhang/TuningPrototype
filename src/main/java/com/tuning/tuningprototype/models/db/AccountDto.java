package com.tuning.tuningprototype.models.db;

import java.util.List;

// Dto record for creating and manipulating accounts.
// `users` is null unless explicitly populated by the service/mapper layer.
public record AccountDto(
        Long id,
        String name,
        Long createdTime,
        Long modifiedTime,
        List<UserDto> users) {}