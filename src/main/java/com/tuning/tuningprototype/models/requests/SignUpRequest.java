package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.LicenseType;

// Request for registering a new user under an existing account
public record SignUpRequest(String firstName,
                            String middleName,
                            String lastName,
                            String username,
                            String email,
                            String password, // raw password, hashed by the service before storage
                            Long accountId, // optional, null for personal use with no account/company
                            LicenseType licenseType) {
}
