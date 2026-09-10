package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.enums.LicenseType;
import java.util.List;

// Dto record for creating and manipulating users.
public record UserDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String username,
        String email,
        String passwordHash,
        Long accountId,
        LicenseType licenseType,
        Long createdTime,
        Long modifiedTime,
        List<ExperimentDto> experiments) {}