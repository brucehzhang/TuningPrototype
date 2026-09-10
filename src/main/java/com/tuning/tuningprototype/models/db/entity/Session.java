package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions")
// Session represents a single logged-in session for a user, identified by an opaque session token
public class Session {
    /**
     * Id of the session
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The id of the user this session belongs to.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Opaque, securely-generated token used to identify this session. Unique across all sessions.
     */
    @Column(name = "session_token", nullable = false, unique = true, length = 255)
    private String sessionToken;

    /**
     * The Unix time of when the session was created, i.e. when the user logged in.
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time at which this session expires and is no longer valid for authentication.
     */
    @Column(name = "expires_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long expiresTime;

    /**
     * The Unix time of when the session was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;
}
