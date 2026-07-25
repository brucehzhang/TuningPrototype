package com.tuning.tuningprototype.models;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public record IndividualNewsArticle(
        // Author of the news article
        String author,
        // Content of the article, potentially HTML
        String content,
        // Created at
        OffsetDateTime createdAt,
        // Headline of the article
        String headline,
        // id of the article from source system
        Long id,
        // Images of the news article
        List<Image> images,
        // Source of the article
        String source,
        // Summary of the article
        String summary,
        // Symbols associated with the news article
        List<String> symbols,
        // Updated at
        OffsetDateTime updatedAt,
        // URL of the article (if applicable)
        URI url) {
    public record Image(
            // image size classification: thumb, small, large
            String size,
            // URL to the image from the article
            URI url) {}
}
