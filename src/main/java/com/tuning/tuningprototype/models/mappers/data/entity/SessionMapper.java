package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.SessionDto;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionDto toDto(Session session) {
        if (session == null) return null;

        return new SessionDto(
                session.getId(),
                session.getUserId(),
                session.getSessionToken(),
                session.getCreatedTime(),
                session.getExpiresTime(),
                session.getModifiedTime()
        );
    }

    public Session toEntity(SessionDto dto) {
        if (dto == null) return null;

        return Session.builder()
                .id(dto.id())
                .userId(dto.userId())
                .sessionToken(dto.sessionToken())
                .createdTime(dto.createdTime())
                .expiresTime(dto.expiresTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}
