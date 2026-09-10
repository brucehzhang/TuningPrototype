package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import com.tuning.tuningprototype.models.enums.LicenseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
// User represents an individual who can create and run experiments under a given account
public class User {
    /**
     * Id of the user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name of the user
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Middle name of the user, optional
     */
    @Column(name = "middle_name", length = 50)
    private String middleName;

    /**
     * Last name of the user, optional
     */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /**
     * Unique username used to identify and log in the user.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Unique email address of the user.
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Bcrypt hash of the user's password. Never the plaintext password.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * The id of the account this user belongs to.
     */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /**
     * The type of license this user holds
     * Ex: 'FREE', 'PRO', 'ENTERPRISE'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false, length = 50)
    private LicenseType licenseType;

    /**
     * The Unix time of when the user was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the user was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Experiments created by this user. Lazy.
     */
    @OneToMany(mappedBy = "createdUserId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<Experiment> experiments = new ArrayList<>();
}
