package com.tuning.tuningprototype.models.db.entity;

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
     * The id of the decision that resulted in this purchase.
     */
    @Column(name = "purchase_decision_id", nullable = false)
    private Long purchaseDecisionId;

    /**
     * The id of the wallet this purchase was funded from.
     */
    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    /**
     * The ticker symbol purchased
     */
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;

    /**
     * The price per unit at the time of purchase
     */
    @Column(name = "purchase_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal purchasePrice;

    /**
     * The amount/quantity purchased
     */
    @Column(name = "purchase_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal purchaseQuantity;

    /**
     * The Unix time representing when this purchase was made
     */
    @Column(name = "purchase_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long purchaseTime;

    /**
     * The Unix time of when the purchase lot was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the purchase lot was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Asset sales that closed out (fully or partially) this purchase lot. Lazy.
     */
    @OneToMany(mappedBy = "purchaseLotId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<AssetSale> assetSales = new ArrayList<>();
}