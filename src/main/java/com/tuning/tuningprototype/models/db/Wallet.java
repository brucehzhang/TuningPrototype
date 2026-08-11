package com.tuning.tuningprototype.models.db;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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
// Wallet tracks the overall purchases and sales for a specific currency in an experiment throughout its backtest. One per currency, can be opened mid-experiment through forex exchange.
public class Wallet {
    /**
     * Id of the wallet
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The id of the experiment this wallet belongs to.
     */
    @Column(name = "experiment_id", nullable = false)
    private Long experimentId;

    /**
     * The starting amount of money held in the wallet
     */
    @Column(name = "starting_money_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal startingMoneyAmount;

    /**
     * The Unix time representing when this wallet was opened. Defaults to the start time of the experiment, but can be opened mid-experiment if foreign currency is purchased..
     */
    @Column(name = "opened_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long openedTime;

    /**
     * The currency code of the money, defaults to USD.
     */
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    /**
     * The Unix time of when the wallet was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the wallet was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * All purchase lots funded from this wallet. Lazy.
     */
    @OneToMany(mappedBy = "walletId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<PurchaseLot> purchaseLots = new ArrayList<>();
}