package com.smartRestaurant.calculations;

import java.util.List;
import org.springframework.stereotype.Component;

import com.smartRestaurant.meal.Meal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CalcTotalPrice {

    // Calculate the total price of all meals in a Flux using reduce
    public static Mono<Double> calcTotalPrice(Flux<Meal> meals) {
        return meals.reduce(0.0, (subtotal, meal) -> subtotal + meal.getPrice());
    }

    public static Mono<Double> calcTotalPrice(List<Meal> meals) {
        return Flux.fromIterable(meals)  // Convert List to Flux
                .reduce(0.0, (subtotal, meal) -> subtotal + meal.getPrice());
    }

}
