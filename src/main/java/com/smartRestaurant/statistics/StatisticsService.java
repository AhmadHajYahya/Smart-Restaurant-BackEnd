package com.smartRestaurant.statistics;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface StatisticsService {

	Mono<ResponseEntity<ApiResponse>> getStatistics();

}
