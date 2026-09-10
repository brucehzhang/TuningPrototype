package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import org.springframework.stereotype.Component;

@Component
public class UserActivityRequestMapper {

    /**
     * Builds a new User from a sign up request. The password must already be hashed by the caller.
     */
    public User toUserEntity(SignUpRequest request, String passwordHash, Long nowEpochSeconds) {
        return User.builder()
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordHash)
                .accountId(request.accountId())
                .licenseType(request.licenseType())
                .createdTime(nowEpochSeconds)
                .modifiedTime(nowEpochSeconds)
                .build();
    }

    /**
     * Builds a new Session for a freshly authenticated user.
     */
    public Session toSessionEntity(Long userId, String sessionToken, Long nowEpochSeconds, Long expiresTime) {
        return Session.builder()
                .userId(userId)
                .sessionToken(sessionToken)
                .createdTime(nowEpochSeconds)
                .expiresTime(expiresTime)
                .modifiedTime(nowEpochSeconds)
                .build();
    }
}
