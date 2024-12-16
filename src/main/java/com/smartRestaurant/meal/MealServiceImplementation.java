package com.smartRestaurant.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smartRestaurant.GCS.ImageService;
import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.classRelations.MealOrderMealRepository;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.IdGenerator;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MealServiceImplementation implements MealService {
	private final MealRepository mealRepository;
	private final ImageService imageService;
	private final MealOrderMealRepository mealOrderMealRepository;

	public MealServiceImplementation(MealRepository mealRepository, ImageService imageService,
			MealOrderMealRepository mealOrderMealRepository) {
		super();
		this.mealRepository = mealRepository;
		this.imageService = imageService;
		this.mealOrderMealRepository = mealOrderMealRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllMeals(Boolean bool) {
		Flux<Meal> meals = null;
		if (bool == null) {
			meals = this.mealRepository.findAll();
		} else {
			meals = this.mealRepository.findByIsAvailable(bool);
		}
		return meals.map(MealBoundary::new).collectList().map(
				mealBoundaries -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meals"), mealBoundaries))
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getMeal(String id) {
		return this.mealRepository.findById(id)
				// If the meal is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal"))))
				// Map the found meal to a MealBoundary object
				.map(MealBoundary::new)
				// Create a response entity with the meal information
				.map(mealBoundary -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meal"), mealBoundary))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> addMeal(MealBoundary meal, MultipartFile imageFile) {
		return validateMealInput(meal)
				// Generate a unique meal ID
				.then(generateMealId()).flatMap(mealId -> {
					meal.setMealId(mealId);
					meal.setRating(0.0);
					meal.setNumberOfRating(0L);
					meal.setOrderCount(0L);
					// Handle image and save the meal
					return handleImageAndSaveMeal(meal, imageFile);
				})
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	// Validate input fields of the meal
	private Mono<Void> validateMealInput(MealBoundary meal) {
		if (MyUtils.isNullOrEmpty(meal.getTitle()) || MyUtils.isNullOrEmpty(meal.getDescription())
				|| MyUtils.isNull(meal.isAvailable()) || MyUtils.isNull(meal.getPrice())
				|| MyUtils.isNull(meal.getCategory()) || MyUtils.isNull(meal.getPreparingTime())) {
			return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
		}
		return Mono.empty();
	}

	// Generate a unique meal ID
	private Mono<String> generateMealId() {
		return this.mealRepository.countAllMeals().defaultIfEmpty(0)
				.map(maxNum -> IdGenerator.generateMealId(maxNum + 1));
	}

	// Handle the image file and save the meal
	private Mono<ResponseEntity<ApiResponse>> handleImageAndSaveMeal(MealBoundary meal, MultipartFile imageFile) {
		if (MyUtils.isNullOrEmpty(meal.getImageURL()) && imageFile != null) {
			return saveImageAndSaveMeal(meal, imageFile);
		} else if (MyUtils.isNotNullAndNotEmpty(meal.getImageURL())) {
			return saveMeal(meal);
		} else {
			return Mono.error(new IllegalArgumentException("Please enter an image"));
		}
	}

	// Save the image file and then save the meal
	private Mono<ResponseEntity<ApiResponse>> saveImageAndSaveMeal(MealBoundary meal, MultipartFile imageFile) {
		return this.imageService.saveImage(imageFile).flatMap(imgName -> {
			String imageURL = this.imageService.getImageURL(imgName);
			meal.setImageURL(imageURL);
			return saveMeal(meal);
		});
	}

	// Save the meal to the repository
	private Mono<ResponseEntity<ApiResponse>> saveMeal(MealBoundary meal) {
		return Mono.just(meal).map(MealBoundary::toEntity).flatMap(this.mealRepository::save).map(savedMeal -> MyUtils
				.responseEntity(HttpStatus.CREATED, MsgCreator.created("Meal"), new MealBoundary(savedMeal)));
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> updateMeal(MealBoundary meal, MultipartFile imageFile) {
		// Find the meal by ID
		return this.mealRepository.findById(meal.getMealId())
				// If the meal is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal"))))
				// If the meal is found, update its details
				.flatMap(entity -> {
					// Update meal details if they are provided
					if (MyUtils.isNotNullAndNotEmpty(meal.getTitle())) {
						entity.setTitle(meal.getTitle());
					}
					if (MyUtils.isNotNull(meal.isAvailable())) {
						entity.setAvailable(meal.isAvailable());
					}
					if (MyUtils.isNotNullAndNotEmpty(meal.getDescription())) {
						entity.setDescription(meal.getDescription());
					}
					if (MyUtils.isNotNull(meal.getCategory())) {
						entity.setCategory(meal.getCategory());
					}
					if (MyUtils.isNotNull(meal.getPrice())) {
						entity.setPrice(meal.getPrice());
					}

					entity.setNew(false);
					// Update the image asynchronously if a new image is provided
					return updateImageAsync(entity, meal, imageFile).flatMap(imgUrl -> {
						entity.setImageURL(imgUrl);
						return mealRepository.save(entity);
					});
				})
				// Map the updated meal to a response entity
				.map(updatedMeal -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.updated("Meal"),
						new MealBoundary(updatedMeal)))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	private Mono<String> updateImageAsync(Meal entity, MealBoundary meal, MultipartFile imageFile) {
		if (imageFile != null) {
			return this.imageService.updateImage(entity.getImageURL(), imageFile)
					.flatMap(imageName -> Mono.just(this.imageService.getImageURL(imageName)));
		} else if (MyUtils.isNotNullAndNotEmpty(meal.getImageURL())) {
			return Mono.just(meal.getImageURL());
		}
		return Mono.just(entity.getImageURL());
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> deleteMeal(String id) {
		return this.mealRepository.findById(id)
				// If the meal is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal"))))
				.flatMap(meal -> this.mealOrderMealRepository.deleteAllByMealId(id)
						.then(this.mealRepository.delete(meal))
						.then(this.imageService.deleteImage(meal.getImageURL()))
						.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null)))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> rateMeal(String mealId, Double newRating) {
		// Validate input parameters
		if (MyUtils.isNullOrEmpty(mealId) || MyUtils.isNull(newRating)) {
			return Mono.error(new IllegalArgumentException(MsgCreator.nullOrEmpty()));
		}

		// Find the meal by ID
		return this.mealRepository.findById(mealId)
				// If the meal is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal"))))
				// If the meal is found, update its rating and save it
				.flatMap(meal -> {
					meal.updateRating(newRating);
					meal.setNew(false);
					return this.mealRepository.save(meal);
				})
				// Map the saved meal to a response entity
				.map(savedMeal -> MyUtils.responseEntity(HttpStatus.OK, "Thank you for rating", null))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors);
	}

}
