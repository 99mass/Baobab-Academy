package com.example.baobab_academy.dtos;

import lombok.Data;
import lombok.Builder;
import java.util.Map;

@Data
@Builder
public class RatingStatsResponse {
    private double averageRating;
    private long totalRatings;
    private Map<Integer, Long> ratingDistribution; // nombre de votes par étoile
}
