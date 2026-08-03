package com.tuning.tuningprototype.models.db;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
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
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * The Unix time of when the account was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the account was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Users belonging to this account. Lazy — only loaded when explicitly fetched.
     */
    @OneToMany(mappedBy = "accountId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<User> users = new ArrayList<>();
}
