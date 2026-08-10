package com.tuning.tuningprototype.models.db;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
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
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * The main model being used to run the experiment
     * Ex: 'CLAUDE_OPUS_4_7'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "agent_model", nullable = false, length = 50)
    private AgentModel agentModel;

    /**
     * The main prompt written by the user that denotes the stock trading strategy used for backtesting against
     * market data.
     * Ex: 'You are a stock trader with a focus on the technology sector. You typically trade based off of Long-term
     * growth and R&D investment from earning reports, but make judgments for holding, selling, or buying in through
     * sentiments in current events.'
     *
     */
    @Column(name = "strategy_prompt", nullable = false, length = 10000)
    private String strategyPrompt;

    /**
     * The rolling windows that the experiment will be checking market data and potentially making decisions at.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sampling_window", nullable = false, length = 50)
    private SamplingWindow samplingWindow;

    /**
     * Start of experiment in Unix time, can be in past for backtesting or current/future for scheduled/continous
     */
    @Column(name = "experiment_start_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long experimentStartTime;

    /**
     * End of experiment in Unix time, can be in past for backtesting, can be future for continuous.
     */
    @Column(name = "experiment_end_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long experimentEndTime;

    /**
     * The status of the experiment, whether it is in draft, in progress, completed, failed, etc
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_status", nullable = false, length = 50)
    private ExperimentStatus experimentStatus;

    /**
     * The Unix time of when the experiment was created by the user
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The id of the user that created this experiment.
     */
    @Column(name = "created_user_id", nullable = false)
    private Long createdUserId;

    /**
     * The Unix time of when the experiment was last modified by the user
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Samples taken during this experiment. Lazy.
     */
    @OneToMany(mappedBy = "experimentId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<Sample> samples = new ArrayList<>();

    /**
     * Wallets associated with this experiment. Lazy.
     */
    @OneToMany(mappedBy = "experimentId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<Wallet> wallets = new ArrayList<>();
}
