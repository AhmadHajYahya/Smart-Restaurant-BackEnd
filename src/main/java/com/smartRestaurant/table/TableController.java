package com.smartRestaurant.table;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.general.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tables")
public class TableController {
	private final TableService tableService;

	public TableController(TableService tableService) {
		super();
		this.tableService = tableService;
	}

	@GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getAllTables(
			@RequestParam(name = "taken", required = false) Boolean isTaken) {
		return this.tableService.getAllTables(isTaken);
	}

	@GetMapping(value = "/table", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getTable(@RequestParam(name = "tableId", required = true) String id) {
		return this.tableService.getTable(id);
	}
	@GetMapping(value = "/tableId", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> getTableIdByUserId(@RequestParam(name = "userId", required = true) String userId) {
		return this.tableService.getTableIdByUserId(userId);
	}
	@PostMapping(value = "/take-table", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<ResponseEntity<ApiResponse>> takeTable(@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "guestsNumber", required = true) Integer guestsNumber) {
		return this.tableService.takeTable(userId, guestsNumber);
	}

	@PutMapping("/free-table")
	public Mono<ResponseEntity<ApiResponse>> freeTable(
			@RequestParam(name = "tableId", required = true) String tableId) {
		return this.tableService.freeTable(tableId);
	}

	@GetMapping(value = "/guests-load", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ApiResponse>> guestsLoad() {
		return this.tableService.getGuestsLoad();
	}
}
