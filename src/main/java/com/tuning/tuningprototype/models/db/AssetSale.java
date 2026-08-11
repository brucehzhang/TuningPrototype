package com.tuning.tuningprototype.models.db;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
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
     * The id of the decision that resulted in this sale.
     */
    @Column(name = "sale_decision_id", nullable = false)
    private Long saleDecisionId;

    /**
     * The id purchase lot being sold.
     */
    @Column(name = "purchase_lot_id", nullable = false)
    private Long purchaseLotId;

    /**
     * The ticker symbol sold
     */
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;

    /**
     * The price per unit at the time of sale
     */
    @Column(name = "sale_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal salePrice;

    /**
     * The amount/quantity sold
     */
    @Column(name = "sale_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal saleAmount;

    /**
     * The Unix time representing when this sale was done
     */
    @Column(name = "sale_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long saleTime;

    /**
     * The Unix time of when the asset sale was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the asset sale was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;
}