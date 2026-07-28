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
@Table(name = "purchase_lots")
// PurchaseLot represents a single purchased position, resulting from a buy decision, that can later be sold
public class PurchaseLot {
    /**
     * Id of the purchase lot
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The decision that resulted in this purchase. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_decision_id", nullable = false)
    private Decision purchaseDecision;

    /**
     * The wallet this purchase was funded from. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    /**
     * The ticker symbol purchased
     */
    private String ticker;

    /**
     * The price per unit at the time of purchase
     */
    private BigDecimal purchasePrice;

    /**
     * The amount/quantity purchased
     */
    private BigDecimal purchaseAmount;

    /**
     * The Unix time of when the purchase lot was created
     */
    private Long createdTime;

    /**
     * The Unix time of when the purchase lot was last modified
     */
    private Long modifiedTime;

    /**
     * Asset sales that closed out (fully or partially) this purchase lot. Lazy.
     */
    @OneToMany(mappedBy = "purchaseLot", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssetSale> assetSales = new ArrayList<>();
}