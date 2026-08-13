package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import com.tuning.tuningprototype.models.enums.DecisionType;
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
@Table(name = "decisions")
// Decision represents an agent's trading decision (buy/sell/hold) made at a given sample
public class Decision {
    /**
     * Id of the decision
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The id of the sample this decision was made from.
     */
    @Column(name = "sample_id", nullable = false)
    private Long sampleId;

    /**
     * The type of decision made by the agent
     * Ex: 'BUY', 'SELL', 'HOLD'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "decision_type", nullable = false, length = 10)
    private DecisionType decisionType;

    /**
     * The ticker symbol the decision pertains to
     */
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;

    /**
     * The agent's reasoning behind making this decision
     */
    @Column(name = "reasoning", nullable = false, length = 10000)
    private String reasoning;

    /**
     * The Unix time representing when this decision was made
     */
    @Column(name = "decision_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long decisionTime;

    /**
     * The Unix time of when the decision was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the decision was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Purchase lots resulting from this decision (when decisionType is a buy). Lazy.
     */
    @OneToMany(mappedBy = "purchaseDecisionId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<PurchaseLot> purchaseLots = new ArrayList<>();

    /**
     * Asset sales resulting from this decision (when decisionType is a sell). Lazy.
     */
    @OneToMany(mappedBy = "saleDecisionId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<AssetSale> assetSales = new ArrayList<>();
}