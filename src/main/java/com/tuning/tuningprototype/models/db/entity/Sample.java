package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.converters.UnixTimestampConverter;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
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
@Table(name = "samples")
// Sample represents a single point in time where an experiment checks market data and may produce decisions
public class Sample {
    /**
     * Id of the sample being taken
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The id experiment this sample belongs to.
     */
    @Column(name = "experiment_id", nullable = false)
    private Long experimentId;

    /**
     * The market data/news/insights gathered at this sampling point, provided to the agent for decision making
     */
    @Column(name = "market_insights", length = 10000)
    private String marketInsights;

    /**
     * The Unix time in seconds representing when this sample was taken
     */
    @Column(name = "sampling_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long samplingTime;

    /**
     * The status of the sample, whether it is pending, completed, failed, etc
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sampling_status", nullable = false, length = 50)
    private SamplingStatus samplingStatus;

    /**
     * The Unix time of when the sample was created
     */
    @Column(name = "created_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long createdTime;

    /**
     * The Unix time of when the sample was last modified
     */
    @Column(name = "modified_time", nullable = false)
    @Convert(converter = UnixTimestampConverter.class)
    private Long modifiedTime;

    /**
     * Decisions made from this sample. Lazy.
     */
    @OneToMany(mappedBy = "sampleId", fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private List<Decision> decisions = new ArrayList<>();
}