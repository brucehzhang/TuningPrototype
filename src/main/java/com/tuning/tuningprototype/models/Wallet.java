package com.tuning.tuningprototype.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
// Wallet tracks the current money balance for an experiment throughout its backtest
public class Wallet {
    /**
     * Id of the wallet
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The experiment this wallet belongs to. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    /**
     * The current amount of money held in the wallet
     */
    private BigDecimal currentMoneyAmount;

    /**
     * The Unix time of when the wallet was created
     */
    private Long createdTime;

    /**
     * The Unix time of when the wallet was last modified
     */
    private Long modifiedTime;

    /**
     * All purchase lots funded from this wallet. Lazy.
     */
    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PurchaseLot> purchaseLots = new ArrayList<>();
}