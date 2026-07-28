package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.LicenseType;
import java.util.List;

// Dto record for creating and manipulating users.
// `accountId` is always available; `account` is null unless explicitly fetched.
public record UserDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        Long accountId,
        AccountDto account,
        LicenseType licenseType,
        Long createdAt,
        Long modifiedAt,
        List<ExperimentDto> experiments) {}