package com.tuning.tuningprototype.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asset_sales")
// AssetSale represents the sale of a previously purchased lot, resulting from a sell decision
public class AssetSale {
    /**
     * Id of the asset sale
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The decision that resulted in this sale. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_decision_id", nullable = false)
    private Decision saleDecision;

    /**
     * The purchase lot being sold. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_lot_id", nullable = false)
    private PurchaseLot purchaseLot;

    /**
     * The ticker symbol sold
     */
    private String ticker;

    /**
     * The price per unit at the time of sale
     */
    private BigDecimal salePrice;

    /**
     * The amount/quantity sold
     */
    private BigDecimal saleAmount;

    /**
     * The Unix time of when the asset sale was created
     */
    private Long createdTime;

    /**
     * The Unix time of when the asset sale was last modified
     */
    private Long modifiedTime;
}