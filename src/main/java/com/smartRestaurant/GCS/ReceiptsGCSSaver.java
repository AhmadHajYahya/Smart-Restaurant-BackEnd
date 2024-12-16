package com.smartRestaurant.GCS;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ReceiptsGCSSaver {
	@Value("${GCSB.project.id}")
	private String projectId;

	@Value("${GCSB.receipts.bucket.name}")
	private String bucketName;

	@Value("${google.application.credentials}")
	private String GAC; // GOOGLE_APPLICATION_CREDENTIALS

	public ReceiptsGCSSaver() {

	}

	// Save an pdf into Google Storage Bucket.
	public Mono<String> saveReceipt(byte[] pdfContent, String orderId, String customerName) {
		return Mono.fromCallable(() -> {
			return GoogleCredentials.fromStream(new FileInputStream(GAC));
		}).subscribeOn(Schedulers.boundedElastic()) // Perform blocking I/O operation on a separate thread
				.map(credentials -> {
					// Build the Storage object with the credentials
					return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build()
							.getService();
				}).flatMap(storage -> {

					String fileName = customerName + System.currentTimeMillis() + orderId + ".pdf";
					BlobId blobId = BlobId.of(bucketName, fileName);
					BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();
					storage.create(blobInfo, pdfContent);
					return Mono.just(String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName));
				});
	}
}
