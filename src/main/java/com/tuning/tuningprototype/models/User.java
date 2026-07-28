package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.LicenseType;
import jakarta.persistence.*;
import lombok.*;

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
    private String firstName;

    /**
     * Middle name of the user, optional
     */
    private String middleName;

    /**
     * Last name of the user, optional
     */
    private String lastName;

    /**
     * The account this user belongs to. Lazy — stays a proxy until accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * The type of license this user holds
     * Ex: 'FREE', 'PRO', 'ENTERPRISE'
     */
    private LicenseType licenseType;

    /**
     * The Unix time of when the user was created
     */
    private Long createdAt;

    /**
     * The Unix time of when the user was last modified
     */
    private Long modifiedAt;

    /**
     * Experiments created by this user. Lazy.
     */
    @OneToMany(mappedBy = "createdByUser", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Experiment> experiments = new ArrayList<>();
}