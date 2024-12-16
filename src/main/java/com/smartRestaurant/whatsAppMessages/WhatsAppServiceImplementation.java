package com.smartRestaurant.whatsAppMessages;

import com.smartRestaurant.boundaries.WhatsAppMessageBoundary;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppServiceImplementation implements WhatsAppService {

	@Value("${twilio.accountSid}")
	private String accountSid;

	@Value("${twilio.authToken}")
	private String authToken;

	@Value("${twilio.whatsappNumber}")
	private String whatsappNumber;

	@Value("${country.phone.code}")
	private String countryPhoneCode;

	@Value("${twilio.verificationCodeTemplate.sid}")
	private String verificationCodeTemplateSid;

	@Value("${twilio.receiptTemplate.sid}")
	private String receiptTemplateSid;

	@Value("${twilio.messageService.sid}")
	private String msgServiceSid;

	@PostConstruct
	public void init() {
		Twilio.init(accountSid, authToken);
	}

	@Override
	public void sendVerificationCodeWhatsAppMessage(WhatsAppMessageBoundary boundary) {

		String vars = "{\"1\": \"" + boundary.getName() + "\", \"2\": \"" + boundary.getContent() + "\"}";
		Message.creator(new PhoneNumber("whatsapp:" + countryPhoneCode + boundary.getTo()),
				new PhoneNumber("whatsapp:" + whatsappNumber), "").setContentVariables(vars)
				.setContentSid(verificationCodeTemplateSid).setMessagingServiceSid(msgServiceSid).create();

	}

	@Override
	public void sendReceiptDocURLWhatsAppMessage(WhatsAppMessageBoundary boundary) {
		String vars = "{\"1\": \"" + boundary.getName() + "\", \"2\": \"" + boundary.getContent() + "\"}";
		Message.creator(new PhoneNumber("whatsapp:" + countryPhoneCode + boundary.getTo()),
				new PhoneNumber("whatsapp:" + whatsappNumber), "").setContentVariables(vars)
		.setContentSid(receiptTemplateSid).setMessagingServiceSid(msgServiceSid).create();

	}

}