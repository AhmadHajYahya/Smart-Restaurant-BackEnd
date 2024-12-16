package com.smartRestaurant.callWaiter;

import java.util.Random;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smartRestaurant.boundaries.CallWaiterBoundary;
import com.smartRestaurant.enums.Roles;
import com.smartRestaurant.general.ApiResponse;
import com.smartRestaurant.general.ExceptionHandler;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.general.MyUtils;
import com.smartRestaurant.user.User;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Mono;

@Service
public class CallWaiterServiceImplementation implements CallWaiterService {

	private final UserRepository userRepository;
	private final CallWaiterRepository callWaiterRepository;
	private Random random;

	public CallWaiterServiceImplementation(UserRepository userRepository, CallWaiterRepository callWaiterRepository) {
		super();
		this.userRepository = userRepository;
		this.random = new Random();
		this.callWaiterRepository = callWaiterRepository;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> callWaiter(String tableId) {
		return this.userRepository.findAllByRoleAndIsOnlineTrue(Roles.WAITER).collectList().flatMap(waiters -> {
			int index;
			if (waiters.size() == 1) {
				index = 0;
			} else {
				index = random.nextInt(waiters.size());
			}
			User w = waiters.get(index);
			return this.callWaiterRepository.save(new CallWaiter(UUID.randomUUID().toString(), w.getUserId(), tableId))
					.flatMap(callWaiter -> MyUtils.MonoResponseEntity(HttpStatus.OK,
							"Waiter will reach you as soon as possible.", null));
		})

		;
	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> getAllWaiterCalls(String waiterId) {
		return this.callWaiterRepository.findAllByWaiterId(waiterId).map(CallWaiterBoundary::new).collectList()
				.map(tableList -> MyUtils.responseEntity(HttpStatus.OK, MsgCreator.fetched("Calls"), tableList)).log();

	}

	@Override
	public Mono<ResponseEntity<ApiResponse>> deleteCall(String callId) {
		return this.callWaiterRepository.deleteById(callId)
				.then(MyUtils.MonoResponseEntity(HttpStatus.NO_CONTENT, "", null))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Call"))))
				.onErrorResume(ExceptionHandler::handleErrors).log();
	}
}
