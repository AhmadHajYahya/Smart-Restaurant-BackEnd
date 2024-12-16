package com.smartRestaurant.table;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.smartRestaurant.boundaries.RestaurantTableBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

public interface TableService {
	public Mono<ResponseEntity<ApiResponse>> takeTable(String userId, Integer guestsNumber);

	public Mono<ResponseEntity<ApiResponse>> freeTable(String tableId);

	public Mono<ResponseEntity<ApiResponse>> getAllTables(Boolean isTaken);

	public Mono<Map<Integer, List<RestaurantTable>>> getAllTablesMap();

	public Mono<ResponseEntity<ApiResponse>> getTable(String id);

	public Mono<ResponseEntity<ApiResponse>> addTable(RestaurantTableBoundary table);

	public Mono<ResponseEntity<ApiResponse>> updateTable(RestaurantTableBoundary table);

	public Mono<ResponseEntity<ApiResponse>> deleteTable(String id);

	public Mono<ResponseEntity<ApiResponse>> getGuestsLoad();

	public Mono<ResponseEntity<ApiResponse>> getTableIdByUserId(String userId);

}
