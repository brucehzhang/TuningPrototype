package com.tuning.tuningprototype.models.responses;

import com.tuning.tuningprototype.models.IndividualNewsArticle;

import java.util.List;
import java.util.Map;

// Api response for retrieving news article data
public record NewsDataResponse(
        // Map of stock ticker to list of individual newsArticles related to the ticker
        Map<String, List<IndividualNewsArticle>> stockNewsArticles,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {}