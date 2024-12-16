package com.smartRestaurant.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.smartRestaurant.table.RestaurantTable;

import reactor.core.publisher.Mono;

public interface TableSearchService {
	public Mono<Set<RestaurantTable>> searchTables(LocalDate date, LocalTime time, int guestsNumber);
	public Mono<Set<RestaurantTable>> find(LocalDate date, LocalTime time, int... seats);
}
