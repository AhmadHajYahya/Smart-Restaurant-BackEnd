package com.smartRestaurant.receipt;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.ReceiptBoundary;
import com.smartRestaurant.calculations.CalcTotalPrice;
import com.smartRestaurant.classRelations.MealOrderMealRepository;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.IdGenerator;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.meal.Meal;
import com.smartRestaurant.meal.MealRepository;
import com.smartRestaurant.mealOrder.MealOrderService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReceiptServiceImplementation implements ReceiptService {
	private final ReceiptRepository receiptRepository;
	private final MealOrderMealRepository mealOrderMealRepository;
	private final MealRepository mealRepository;
	private final RecieptPDFGeneratorService pdfService;

	public ReceiptServiceImplementation(ReceiptRepository receiptRepository,
			MealOrderMealRepository mealOrderMealRepository, MealRepository mealRepository,
			MealOrderService mealOrderService, RecieptPDFGeneratorService pdfService) {
		super();
		this.receiptRepository = receiptRepository;
		this.mealOrderMealRepository = mealOrderMealRepository;
		this.mealRepository = mealRepository;
		this.pdfService = pdfService;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllReceipts() {
		return receiptRepository.findAll().map(ReceiptBoundary::new).collectList()
				.map(list -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Receipts"), list)).log();
	}

	@Override
	// Retrieve a Receipt by id.
	public Mono<ResponseEntity<ApiResponse>> getReceipt(String id) {
		return receiptRepository.findById(id)
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Receipt"))))
				.map(ReceiptBoundary::new).map(receiptBoundary -> MyUtils.responseEntity(HttpStatus.OK,
						MsgCreator.fetched("Receipt"), receiptBoundary))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}

	@Override
	// Create a new Receipt.
	public Mono<ResponseEntity<ApiResponse>> createReceipt(ReceiptBoundary receipt) {
		if (MyUtils.isNullOrEmpty(receipt.getMealOrderId())) {
			return MyUtils.MonoResponseEntity(HttpStatus.BAD_REQUEST, MsgCreator.nullOrEmpty(), null);
		}

		Flux<Meal> mealOrderMeals = this.mealOrderMealRepository.findMealIdsByMealOrderId(receipt.getMealOrderId())
				.flatMap(mealId -> {
					return this.mealRepository.findById(mealId);
				});

		// 1. Calculate total price (assuming calcTotalPrice is a reactive function)
		return Mono.just(receipt)
				.flatMap(
						recpt -> CalcTotalPrice.calcTotalPrice(mealOrderMeals)
								.map(totalPrice -> new Receipt(receipt.getMealOrderId(), totalPrice))
								.flatMap(r -> this.receiptRepository.countAllReceipts().defaultIfEmpty(0)
										.map(maxNum -> IdGenerator.generateReceiptId(maxNum + 1))
										.flatMap(receiptId -> {
											r.setReceiptID(receiptId);
											return pdfService.generatePdf(r.getMealOrderId(), r.getReceiptID(),
													r.getTotal_price(), mealOrderMeals).flatMap(docURL -> {
														r.setReceiptDoc(docURL);
														r.setDate(LocalDate.now());
														return this.receiptRepository.save(r);
													});
										}))
								.map(savedReceipt -> MyUtils.responseEntity(HttpStatus.CREATED,
										MsgCreator.created("Receipt"),
										new ReceiptBoundary(savedReceipt.getReceiptID(), savedReceipt.getMealOrderId(),
												savedReceipt.getTotal_price(), savedReceipt.getReceiptDoc()))))
				.log();
	}

	@Override
	// Delete a Receipt by id.
	public Mono<ResponseEntity<ApiResponse>> deleteReceipt(String id) {
		return this.receiptRepository.findById(id)
				.flatMap(receipt -> this.receiptRepository.delete(receipt)
						.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null)))
				.switchIfEmpty(MyUtils.MonoResponseEntity(HttpStatus.NOT_FOUND, MsgCreator.notFound("Receipt"), null))
				.log();
	}
}
