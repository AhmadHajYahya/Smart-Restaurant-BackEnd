package com.smartRestaurant.menu;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.enums.Category;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.meal.Meal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MenuServiceImplementation implements MenuService {

	private final MenuRepository menuRepository;

	public MenuServiceImplementation(MenuRepository menuRepository) {
		super();
		this.menuRepository = menuRepository;
	}

	// default view
	@Override
	public Mono<ResponseEntity<ApiResponse>> getAvailableMeals() {
		return this.menuRepository.findByIsAvailableTrue().map(MealBoundary::new).collectList()
				.map(mealBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Available meals"),
						mealBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllByCategory(Category category) {
		return this.menuRepository.findAllByCategoryAndIsAvailableTrue(category).map(MealBoundary::new).collectList()
				.map(mealBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meals"),
						mealBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> sortBy(Category category, String sortField) {
		Flux<Meal> meals;
		String sortBy = sortField.toLowerCase();
		switch (sortBy) {
		case "price":
			meals = this.menuRepository.findByCategoryAndIsAvailableTrueOrderByPriceAsc(category);
			break;
		case "name":
			meals = this.menuRepository.findByCategoryAndIsAvailableTrueOrderByTitle(category);
			break;
		case "rating":
			meals = this.menuRepository.findByCategoryAndIsAvailableTrueOrderByRatingAsc(category);
			break;
		case "popular":
			meals = this.menuRepository.findByCategoryAndIsAvailableTrueOrderByOrderCountAsc(category);
			break;
		default:
			meals = this.menuRepository.findAllByCategoryAndIsAvailableTrue(category);
		}

		return meals.map(MealBoundary::new).collectList().map(
				mealBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meals"), mealBoundaries))
				.log();

	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> searchInTitleAndDescription(String str) {
		if (MyUtils.isNullOrEmpty(str)) {
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, MsgCreator.nullOrEmpty(), null);
		}
		return this.menuRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(str, str)
				.map(meal -> new MealBoundary(meal)).collectList().map(mealBoundaries -> MyUtils
						.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meals"), mealBoundaries))
				.log();
	}

}
