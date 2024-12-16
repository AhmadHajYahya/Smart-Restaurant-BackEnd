package com.smartRestaurant.callWaiter;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/call-waiter")
public class CallWaiterController {
	private final CallWaiterService callWaiterService;

	public CallWaiterController(CallWaiterService callWaiterService) {
		super();
		this.callWaiterService = callWaiterService;
	}

	@GetMapping(value = "/create-call", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> createCall(
			@RequestParam(name = "tableId", required = true) String tableId) {
		return this.callWaiterService.callWaiter(tableId);
	}

	@GetMapping(value = "/get-calls", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getCalls(
			@RequestParam(name = "waiterId", required = true) String waiterId) {
		return this.callWaiterService.getAllWaiterCalls(waiterId);
	}

	@DeleteMapping(value = "/delete-call", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> deleteCall(@RequestParam(name = "callId", required = true) String callId) {
		return this.callWaiterService.deleteCall(callId);
	}

}
