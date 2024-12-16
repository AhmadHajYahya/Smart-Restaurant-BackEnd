package com.smartRestaurant.mealOrder;

import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.boundaries.MealOrderBoundary;
import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPreparationService {

	private final MealOrderServiceImplementation mealOrderService;

	public MealPreparationService(MealOrderServiceImplementation mealOrderService) {
		this.mealOrderService = mealOrderService;
	}

	// Prioritize meal orders for preparation
	@SuppressWarnings("unchecked")
	public Mono<ResponseEntity<ApiResponse>> prioritizeMealOrders(Status status) {
		return mealOrderService.getAllMealOrders(status).flatMap(response -> {
			ApiResponse apiResponse = response.getBody();
			if (apiResponse != null && apiResponse.getData() instanceof List) {
				List<MealOrderBoundary> mealOrderBoundaries = (List<MealOrderBoundary>) apiResponse.getData();
				return sortMealOrders(mealOrderBoundaries).flatMap(sortedBoundaries -> MyUtils
						.MonoResponseEntity(HttpStatus.OK, MsgCreator.fetched("Meal orders prioritized"), sortedBoundaries));
			} else {
				return MyUtils.MonoResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve meal orders",
						null);
			}
		}).onErrorResume(e -> MyUtils.MonoResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
	}

	// Sort meal orders based on priority score and return the response
	private Mono<List<MealOrderBoundary>> sortMealOrders(List<MealOrderBoundary> mealOrderBoundaries) {
		// Stream through the list of MealOrderBoundary objects and sort them by the
		// calculated score in descending order
		List<MealOrderBoundary> sortedBoundaries = mealOrderBoundaries.stream()
				.sorted((mo1, mo2) -> Integer.compare(calculateScore(mo2), calculateScore(mo1)))
				.collect(Collectors.toList()); // Collect the sorted list

		return Mono.just(sortedBoundaries); // Wrap the sorted list in a Mono and return
	}

	// Calculate a priority score for a meal order
	private int calculateScore(MealOrderBoundary mealOrderBoundary) {
		int orderTimeWeight = 5; // Weight for the order time score
		int preparationTimeWeight = 3; // Weight for the preparation time score
		int orderCountWeight = 4; // Weight for the order count score

		// Calculate the order time score based on the time since the order was placed
		int orderTimeScore = (int) (System.currentTimeMillis() - mealOrderBoundary.getDate()
				.atTime(mealOrderBoundary.getTime()).toInstant(ZoneOffset.UTC).toEpochMilli());

		// Sum of preparation times for all meals in the order to calculate the
		// preparation time score
		int preparationTimeScore = mealOrderBoundary.getMeals().stream().mapToInt(MealBoundary::getPreparingTime).sum();

		// Sum of order counts for all meals in the order to calculate the order count
		// score
		int orderCountScore = mealOrderBoundary.getMeals().stream()
				.mapToInt(meal -> meal.getOrderCount() != null ? meal.getOrderCount().intValue() : 0).sum();

		// Total score is a weighted sum of the individual scores
		return (orderTimeWeight * orderTimeScore) + (preparationTimeWeight * preparationTimeScore)
				+ (orderCountWeight * orderCountScore);
	}

}
