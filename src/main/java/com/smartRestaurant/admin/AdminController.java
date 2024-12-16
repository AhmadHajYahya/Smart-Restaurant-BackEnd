package com.smartRestaurant.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.boundaries.RestaurantTableBoundary;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.FilePartToMultipartFileConverter;
import com.smartRestaurant.meal.MealService;
import com.smartRestaurant.statistics.StatisticsService;
import com.smartRestaurant.table.TableService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/smart-restaurant/admin")
public class AdminController {

	private final MealService mealService;
	private final AdminService adminService;
	private final TableService tableService;
	private final StatisticsService statisticsService;
	@Autowired
	private ObjectMapper objectMapper;

	public AdminController(MealService mealService, AdminService adminService, TableService tableService,
			StatisticsService statisticsService) {
		super();
		this.mealService = mealService;
		this.adminService = adminService;
		this.tableService = tableService;
		this.statisticsService = statisticsService;
	}

	@PostMapping(value = "/add/meal", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> addMeal(@RequestPart("meal") String mealJson,
			@RequestPart(name = "ImageFile", required = false) Mono<FilePart> image)
			throws JsonMappingException, JsonProcessingException {

		MealBoundary meal = objectMapper.readValue(mealJson, MealBoundary.class);
		return image.flatMap(img -> FilePartToMultipartFileConverter.convert(img)
				.flatMap(multipartFile -> this.mealService.addMeal(meal, multipartFile)));
	}

	@PutMapping(value = "/update/meal", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> updateMeal(@RequestPart("meal") String mealJson,
			@RequestPart(name = "ImageFile", required = false) Mono<FilePart> image)
			throws JsonMappingException, JsonProcessingException {

		MealBoundary meal = objectMapper.readValue(mealJson, MealBoundary.class);
		return image
				.flatMap(img -> FilePartToMultipartFileConverter.convert(img)
						.flatMap(multipartFile -> this.mealService.updateMeal(meal, multipartFile)))
				.switchIfEmpty(this.mealService.updateMeal(meal, null));
	}

	@PostMapping(value = "/add/table", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> addTable(@RequestBody RestaurantTableBoundary table) {
		return this.tableService.addTable(table);
	}

	@PutMapping(value = "/update/table", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> updateTable(@RequestBody RestaurantTableBoundary table) {
		return this.tableService.updateTable(table);

	}

	@GetMapping(value = "users/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllUsers(@RequestParam(name = "role", required = false) Roles role) {
		return this.adminService.getAllUsers(role);
	}

	@DeleteMapping(value = "/delete", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> deleteObject(@RequestParam(name = "object", required = true) String obj,
			@RequestParam(name = "id", required = true) String id) {
		return this.adminService.delete(obj, id);
	}

	@GetMapping(value = "/statistics", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getStatistics() {
		return statisticsService.getStatistics();
	}
	@GetMapping(value = "/default", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<String> getAdminDefaultDetails() {
		return adminService.getAdminDetails();
	}
}
