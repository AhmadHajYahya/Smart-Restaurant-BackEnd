package com.smartRestaurant.menu;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.StringBoundary;
import com.smartRestaurant.enums.Category;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/menu")
public class MenuController {

	private final MenuService menuService;

	public MenuController(MenuService menuService) {
		super();
		this.menuService = menuService;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllAvailableMeals() {
		return this.menuService.getAvailableMeals();
	}

	@GetMapping(value = "/category", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllByCategory(
			@RequestParam(name = "category", required = true) Category category) {
		return this.menuService.getAllByCategory(category);
	}

	@GetMapping(value = "/sort-by/{sortField}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> sortBy(@RequestParam(name = "category", required = true) Category category,
			@PathVariable(name = "sortField", required = true) String sortField) {
		return this.menuService.sortBy(category, sortField);
	}

	@PostMapping(path = "/search-by/contains", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> searchInTitleAndDescription(@RequestBody StringBoundary str) {
		return this.menuService.searchInTitleAndDescription(str.getStr());
	}

}
