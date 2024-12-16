package com.smartRestaurant.whatsAppMessages;

import com.smartRestaurant.boundaries.WhatsAppMessageBoundary;

public interface WhatsAppService {

	void sendVerificationCodeWhatsAppMessage(WhatsAppMessageBoundary boundary);

	void sendReceiptDocURLWhatsAppMessage(WhatsAppMessageBoundary boundary);
}
