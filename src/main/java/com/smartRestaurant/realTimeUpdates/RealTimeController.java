package com.smartRestaurant.realTimeUpdates;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.enums.Status;
import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/real-time/update")
public class RealTimeController {
	private final int _1min = 60; // Stream data every 60 seconds
	private final int _15sec = 15; // Stream data every 15 seconds
	private final int _10sec = 10; // Stream data every 10 seconds
	private final RealTimeService realTimeService;
	public RealTimeController(RealTimeService realTimeService) {
		super();
		this.realTimeService = realTimeService;
	}

	@GetMapping(value = "/guests-load", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> guestsLoad() {
		return Flux.interval(Duration.ofSeconds(_10sec)).flatMap(tick -> realTimeService.guestsLoad())
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/statistics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> statistics() {
		return Flux.interval(Duration.ofSeconds(_1min)).flatMap(tick -> realTimeService.statistics())
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/order-status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> orderStatus(
			@RequestParam(name = "orderId", required = true) String orderId) {
		return Flux.interval(Duration.ofSeconds(_15sec)).flatMap(tick -> realTimeService.orderStatus(orderId))
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/waiter-calls", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> waiterCalls(
			@RequestParam(name = "waiterId", required = true) String waiterId) {
		return Flux.interval(Duration.ofSeconds(_15sec)).flatMap(tick -> realTimeService.waiterCalls(waiterId))
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/menu", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> menu() {
		return Flux.interval(Duration.ofSeconds(_1min)).flatMap(tick -> realTimeService.menu())
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/prioritized-orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> prioritizeOrders(
			@RequestParam(name = "status", required = true) Status status) {
		return Flux.interval(Duration.ofSeconds(_10sec)).flatMap(tick -> realTimeService.prioritizeOrders(status))
				.map(data -> ServerSentEvent.<ApiResponse>builder(data.getBody()).build());
	}

	@GetMapping(value = "/meal-orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse>> allMealOrders(
			@RequestParam(name = "status", required = false) Status status) {
		return Flux.interval(Duration.ofSeconds(_10sec)).flatMap(tick -> realTimeService.allMealOrders(status)
				.map(response -> ServerSentEvent.<ApiResponse>builder(response.getBody()).build()));
	}
}
