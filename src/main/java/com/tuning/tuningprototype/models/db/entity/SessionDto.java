package com.tuning.tuningprototype.models.db.entity;

// Dto record for creating and manipulating sessions.
public record SessionDto(
        Long id,
        Long userId,
        String sessionToken,
        Long createdTime,
        Long expiresTime,
        Long modifiedTime) {}
