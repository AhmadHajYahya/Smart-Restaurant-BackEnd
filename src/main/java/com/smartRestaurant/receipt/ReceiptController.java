package com.smartRestaurant.receipt;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.ReceiptBoundary;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	private final ReceiptService receiptService;

	public ReceiptController(ReceiptService receiptService) {
		super();
		this.receiptService = receiptService;
	}

	@GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllReceipts() {
		return this.receiptService.getAllReceipts();
	}

	@GetMapping(value = "/receipt", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getReceipt(@RequestParam String id) {
		return this.receiptService.getReceipt(id);
	}

	@PostMapping(value = "/create-receipt", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createReceipt(@RequestBody ReceiptBoundary receipt) {
		return this.receiptService.createReceipt(receipt);
	}

	@DeleteMapping(value = "/delete", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> deleteReceipt(@RequestParam String id) {
		return this.receiptService.deleteReceipt(id);
	}
}
