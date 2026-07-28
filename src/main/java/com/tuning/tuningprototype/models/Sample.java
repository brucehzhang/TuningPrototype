package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.SamplingStatus;
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
     * The experiment this sample belongs to. Lazy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    /**
     * The market data/news/insights gathered at this sampling point, provided to the agent for decision making
     */
    private String marketInsights;

    /**
     * The Unix time representing when this sample was taken
     */
    private Long samplingTime;

    /**
     * The status of the sample, whether it is pending, completed, failed, etc
     */
    private SamplingStatus samplingStatus;

    /**
     * The Unix time of when the sample was created
     */
    private Long createdTime;

    /**
     * The Unix time of when the sample was last modified
     */
    private Long modifiedTime;

    /**
     * Decisions made from this sample. Lazy.
     */
    @OneToMany(mappedBy = "sample", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Decision> decisions = new ArrayList<>();
}