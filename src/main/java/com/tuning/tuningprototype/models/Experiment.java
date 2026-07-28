package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
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
@Table(name = "experiments")
//Experiment is the top level entity that a user will create to test an agent with a provided strategy prompt
public class Experiment {

    /**
     * Id of the experiment being run
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name assigned to the experiment, either default or by the User
     */
    private String name;

    /**
     * The main model being used to run the experiment
     * Ex: 'CLAUDE_OPUS_4_7'
     */
    private AgentModel agentModel;

    /**
     * The main prompt written by the user that denotes the stock trading strategy used for backtesting against
     * market data.
     * Ex: 'You are a stock trader with a focus on the technology sector. You typically trade based off of Long-term
     * growth and R&D investment from earning reports, but make judgments for holding, selling, or buying in through
     * sentiments in current events.'
     *
     */
    private String strategyPrompt;

    /**
     * The rolling windows that the experiment will be checking market data and potentially making decisions at.
     */
    private SamplingWindow samplingWindow;

    /**
     * The amount of money that the experiment should start its backtest with
     */
    private BigDecimal startingMoneyAmount;

    /**
     * The currency code of the money, defaults to USD.
     */
    private String currencyCode;

    /**
     * Start of experiment in Unix time
     */
    private Long experimentStartTime;

    /**
     * End of experiment in Unix time
     */
    private Long experimentEndTime;

    /**
     * The status of the experiment, whether it is in draft, in progress, completed, failed, etc
     */
    private ExperimentStatus experimentStatus;

    /**
     * The Unix time of when the experiment was created by the user
     */
    private Long createdTime;

    /**
     * The user that created this experiment. Lazy — stays a proxy until accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    /**
     * The Unix time of when the experiment was last modified by the user
     */
    private Long modifiedTime;

    /**
     * Samples taken during this experiment. Lazy.
     */
    @OneToMany(mappedBy = "experiment", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Sample> samples = new ArrayList<>();

    /**
     * Wallets associated with this experiment. Lazy.
     */
    @OneToMany(mappedBy = "experiment", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Wallet> wallets = new ArrayList<>();
}
