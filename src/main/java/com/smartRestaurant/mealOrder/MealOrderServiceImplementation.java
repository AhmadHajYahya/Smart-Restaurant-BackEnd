package com.smartRestaurant.mealOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.GuestUserBoundary;
import com.smartRestaurant.boundaries.MealBoundary;
import com.smartRestaurant.boundaries.MealOrderBoundary;
import com.smartRestaurant.boundaries.NewMealOrderBoundary;
import com.smartRestaurant.boundaries.RegisteredUserBoundary;
import com.smartRestaurant.boundaries.RestaurantTableBoundary;
import com.smartRestaurant.classRelations.MealOrderMeal;
import com.smartRestaurant.classRelations.MealOrderMealRepository;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.IdGenerator;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.meal.Meal;
import com.smartRestaurant.meal.MealRepository;
import com.smartRestaurant.receipt.ReceiptRepository;
import com.smartRestaurant.table.RestaurantTable;
import com.smartRestaurant.table.TableRepository;
import com.smartRestaurant.user.User;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MealOrderServiceImplementation implements MealOrderService {
	private final MealOrderRepository mealOrderRepository;
	private final MealRepository mealRepository;
	private final UserRepository userRepository;
	private final TableRepository tableRepository;
	private final MealOrderMealRepository mealOrderMealRepository;
	private final ReceiptRepository receiptRepository;
	public MealOrderServiceImplementation(MealOrderRepository mealOrderRepository, MealRepository mealRepository,
			UserRepository userRepository, TableRepository tableRepository,
			MealOrderMealRepository mealOrderMealRepository, ReceiptRepository receiptRepository) {
		super();
		this.mealOrderRepository = mealOrderRepository;
		this.mealRepository = mealRepository;
		this.userRepository = userRepository;
		this.tableRepository = tableRepository;
		this.mealOrderMealRepository = mealOrderMealRepository;
		this.receiptRepository = receiptRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> createMealOrder(NewMealOrderBoundary mealOrder) {
	    // Step 1: Find the user by ID
	    return this.userRepository.findById(mealOrder.getUserId())
	        // If the user is not found, emit an error signal
	        .switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User"))))
	        // Step 2: If the user is found, proceed to find the table by ID
	        .flatMap(user -> this.tableRepository.findById(mealOrder.getTableId())
	            // If the table is not found, emit an error signal
	            .switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Table"))))
	            // Step 3: If the table is found, generate a new meal order ID
	            .flatMap(table -> this.mealOrderRepository.countAllMealOrders().defaultIfEmpty(0)
	                .map(maxNum -> IdGenerator.generateMealOrderId(maxNum + 1))
	                // Step 4: Create a new meal order with the generated ID and save it
	                .flatMap(mealOrderId -> {
	                    MealOrder newMealOrder = new MealOrder(mealOrder.getUserId(), mealOrder.getTableId());
	                    newMealOrder.setmOrderID(mealOrderId);
	                    newMealOrder.setStatus(Status.ACCEPTED);
	                    return this.mealOrderRepository.save(newMealOrder)
	                        // Step 5: Update the user's order count and save the user
	                        .flatMap(savedMealOrder -> {
	                            user.setOrdersCount(user.getOrdersCount() + 1);
	                            user.setNew(false);
	                            return this.userRepository.save(user)
	                                // Step 6: Save each meal in the order and associate it with the meal order
	                                .flatMap(updatedUser -> Flux.fromIterable(mealOrder.getMealIds())
	                                    .flatMap(mealId -> this.mealRepository.findById(mealId)
	                                        .flatMap(meal -> {
	                                            meal.updateOrderCount();
	                                            meal.setNew(false);
	                                            return this.mealRepository.save(meal)
	                                                .flatMap(updatedMeal -> this.mealOrderMealRepository
	                                                    .save(new MealOrderMeal(savedMealOrder.getmOrderID(), mealId)));
	                                        }))
	                                    // Step 7: After all meals are saved, fetch and return the meal order details
	                                    .then(this.getMealOrder(savedMealOrder.getmOrderID(),null)));
	                        });
	                })))
	        // Handle any errors that occur during the process
	        .onErrorResume(ExceptionHandler::handleErrors)
	        // Log the process
	        .log();
	}


	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllMealOrders(Status status) {
		Flux<MealOrder> mealOrders = (status != null) ? this.mealOrderRepository.findAllByStatus(status)
				: this.mealOrderRepository.findAll();

		return mealOrders
				.flatMap(mealOrder -> this.userRepository.findById(mealOrder.getUserId())
						.flatMap(user -> this.tableRepository.findById(mealOrder.getTableId())
								.flatMap(table -> this.mealOrderMealRepository
										.findMealIdsByMealOrderId(mealOrder.getmOrderID())
										.flatMap(mealId -> this.mealRepository.findById(mealId)).collectList()
										.map(meals -> createMealOrderBoundary(mealOrder, user, table, meals)))))
				.collectList()
				.flatMap(mealOrderBoundaries -> MyUtils.MonoResponseEntity(HttpStatus.OK,
						MsgCreator.fetched(status != null ? status + " Meal orders" : "Meal orders"),
						mealOrderBoundaries));

	}

	private MealOrderBoundary createMealOrderBoundary(MealOrder mealOrder, User user, RestaurantTable table,
			List<Meal> meals) {
		List<MealBoundary> mealBoundaries = meals.stream().map(MealBoundary::new).collect(Collectors.toList());

		if (user.getRole().equals(Roles.GUEST_USER)) {
			GuestUserBoundary guestUser = new GuestUserBoundary(user);
			return new MealOrderBoundary(mealOrder.getmOrderID(), guestUser, new RestaurantTableBoundary(table),
					mealOrder.getDate(), mealOrder.getTime(), mealOrder.getStatus(), mealBoundaries);
		} else {
			RegisteredUserBoundary registeredUser = new RegisteredUserBoundary(user);
			return new MealOrderBoundary(mealOrder.getmOrderID(), registeredUser, new RestaurantTableBoundary(table),
					mealOrder.getDate(), mealOrder.getTime(), mealOrder.getStatus(), mealBoundaries);
		}
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getMealOrder(String id, String userId) {
		Mono<MealOrder> mO = null;
		
		if(userId != null && id ==null) {
			mO = this.mealOrderRepository.findByUserIdAndDateLikeAndStatusNot(userId, LocalDate.now(),Status.DONE);
		}
		else if (userId == null && id !=null) {
			mO = this.mealOrderRepository.findById(id);
		}
		else {
			return Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal order")));
		}
		return mO
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal order"))))
				.flatMap(mealOrder -> this.userRepository.findById(mealOrder.getUserId())
						.flatMap(user -> this.tableRepository.findById(mealOrder.getTableId())
								.flatMap(table -> this.mealOrderMealRepository
										.findMealIdsByMealOrderId(mealOrder.getmOrderID())
										.flatMap(mealId -> this.mealRepository.findById(mealId)).collectList()
										.map(meals -> {
											List<MealBoundary> mealBoundaries = meals.stream()
													.map(meal -> new MealBoundary(meal)).collect(Collectors.toList());
											int totalPreparingTime = meals.stream()
						                             .mapToInt(Meal::getPreparingTime)
						                             .sum();
											if (user.getRole().equals(Roles.GUEST_USER)) {
												GuestUserBoundary u = new GuestUserBoundary(user);
												return new MealOrderBoundary(mealOrder.getmOrderID(), u,
														new RestaurantTableBoundary(table), mealOrder.getDate(),
														mealOrder.getTime(), mealOrder.getStatus(), mealBoundaries,totalPreparingTime);
											}
											return new MealOrderBoundary(mealOrder.getmOrderID(),
													new RegisteredUserBoundary(user),
													new RestaurantTableBoundary(table), mealOrder.getDate(),
													mealOrder.getTime(), mealOrder.getStatus(), mealBoundaries,totalPreparingTime);
										}))))
				.map(mealOrder -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Meal order"), mealOrder))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> updateMealOrderStatus(String mealOrderId, Status status) {
		// Validate the status input
		if (status == null) {
			return Mono.error(new IllegalArgumentException("Status is null."));
		}

		// Find the meal order by ID
		return this.mealOrderRepository.findById(mealOrderId)
				// If the meal order is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal order"))))
				// If the meal order is found, update its status and save it
				.flatMap(mealOrder -> {
					mealOrder.setStatus(status);
					mealOrder.setNew(false);
					return this.mealOrderRepository.save(mealOrder);
				})
				// Map the updated meal order to a response entity
				.map(savedMealOrder -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.updated("Meal order status"),
						null))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> deleteMealOrder(String id) {
		// Find the meal order by ID
		return this.mealOrderRepository.findById(id)
				// If the meal order is not found, emit an error signal
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal order"))))
				// If the meal order is found, delete the related records and the meal order
				.flatMap(mealOrder -> this.mealOrderMealRepository.deleteAllByMealOrderId(mealOrder.getmOrderID())
						.then(this.receiptRepository.deleteAllBymealOrderId( id))
						.then(this.mealOrderRepository.delete(mealOrder))
						.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null)))
				// Handle any errors that occur during the process
				.onErrorResume(ExceptionHandler::handleErrors)
				// Log the process
				.log();
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getUserMealOrderHistory(String userId) {
		return this.userRepository.findById(userId)
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User")))).flatMap(user -> {
					if (!user.getRole().equals(Roles.REGISTERED_USER)) {
						return Mono.error(new SecurityException("You don't have permission to fetch orders history"));
					}

					return this.mealOrderRepository.findAllByUserId(userId)
							.flatMap(mealOrder -> this.mealOrderMealRepository
									.findMealIdsByMealOrderId(mealOrder.getmOrderID())
									.flatMap(mealId -> this.mealRepository.findById(mealId)).collectList()
									.map(meals -> buildMealOrderBoundary(mealOrder, user, meals)))
							.collectList().flatMap(mealOrderBoundaries -> MyUtils.MonoResponseEntity(HttpStatus.OK,
									MsgCreator.fetched("Meal orders"), mealOrderBoundaries));
				}).onErrorResume(ExceptionHandler::handleErrors).log();
	}

	private MealOrderBoundary buildMealOrderBoundary(MealOrder mealOrder, User user, List<Meal> meals) {
		List<MealBoundary> mealBoundaries = meals.stream().map(MealBoundary::new).collect(Collectors.toList());

		RegisteredUserBoundary registeredUser = new RegisteredUserBoundary(user);
		return new MealOrderBoundary(mealOrder.getmOrderID(), registeredUser, null, mealOrder.getDate(),
				mealOrder.getTime(), mealOrder.getStatus(), mealBoundaries);

	}

}
