package com.smartRestaurant.whatsAppMessages;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartRestaurant.boundaries.WhatsAppMessageBoundary;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

	private final WhatsAppService whatsAppService;

	public WhatsAppController(WhatsAppService whatsAppService) {
		super();
		this.whatsAppService = whatsAppService;
	}

	@PostMapping(value = "/send-code", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void sendVerificationCode(@RequestBody WhatsAppMessageBoundary request) {
		whatsAppService.sendVerificationCodeWhatsAppMessage(request);
	}
	
	@PostMapping(value = "/send-receipt", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void sendWhatsAppMessage(@RequestBody WhatsAppMessageBoundary request) {
		whatsAppService.sendReceiptDocURLWhatsAppMessage(request);
	}

}