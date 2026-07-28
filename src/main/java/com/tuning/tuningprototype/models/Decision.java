package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.DecisionType;
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
     * The sample this decision was made from. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;

    /**
     * The type of decision made by the agent
     * Ex: 'BUY', 'SELL', 'HOLD'
     */
    private DecisionType decisionType;

    /**
     * The ticker symbol the decision pertains to
     */
    private String ticker;

    /**
     * The agent's reasoning behind making this decision
     */
    private String reasoning;

    /**
     * The Unix time representing when this decision was made
     */
    private Long decisionTime;

    /**
     * The Unix time of when the decision was created
     */
    private Long createdTime;

    /**
     * The Unix time of when the decision was last modified
     */
    private Long modifiedTime;

    /**
     * Purchase lots resulting from this decision (when decisionType is a buy). Lazy.
     */
    @OneToMany(mappedBy = "purchaseDecision", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PurchaseLot> purchaseLots = new ArrayList<>();

    /**
     * Asset sales resulting from this decision (when decisionType is a sell). Lazy.
     */
    @OneToMany(mappedBy = "saleDecision", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AssetSale> assetSales = new ArrayList<>();
}