package com.tuning.tuningprototype.models;

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
@Table(name = "accounts")
// Account represents an organization or billing entity that one or more users belong to
public class Account {
    /**
     * Id of the account
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the account
     */
    private String name;

    /**
     * The Unix time of when the account was created
     */
    private Long createdAt;

    /**
     * The Unix time of when the account was last modified
     */
    private Long modifiedAt;

    /**
     * Users belonging to this account. Lazy — only loaded when explicitly fetched.
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();
}