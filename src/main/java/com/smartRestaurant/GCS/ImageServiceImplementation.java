package com.smartRestaurant.GCS;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.StorageOptions;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ImageServiceImplementation implements ImageService {
	private final ImgBlobRepository imgRepository;

	@Value("${GCSB.project.id}")
	private String projectId;

	@Value("${GCSB.bucket.name}")
	private String bucketName;
	
	@Value("${google.application.credentials}")
	private String GAC; // GOOGLE_APPLICATION_CREDENTIALS
	
	public ImageServiceImplementation(ImgBlobRepository imgRepository) {
		super();
		this.imgRepository = imgRepository;
	}

	// Save an image into Google Storage Bucket.
	public Mono<String> saveImage(MultipartFile img) {
		// Create a Mono that defers the creation of GoogleCredentials and Storage
		return Mono.fromCallable(() -> {
			return GoogleCredentials.fromStream(new FileInputStream(GAC));
		}).subscribeOn(Schedulers.boundedElastic()) // Perform blocking I/O operation on a separate thread
				.map(credentials -> {
					// Build the Storage object with the credentials
					return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build()
							.getService();
				}).flatMap(storage -> {
					// Process the image upload within the flatMap to ensure it's part of the
					// reactive stream
					String imgName = generateUniqueImageName(img.getOriginalFilename());
					String contentType = img.getContentType();

					BlobId blobId = BlobId.of(bucketName, imgName);
					BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

					// Wrap the upload in a Mono.fromCallable to handle it reactively
					return Mono.fromCallable(() -> {
						return storage.create(blobInfo, img.getBytes());
					}).subscribeOn(Schedulers.boundedElastic()) // Ensure this blocking operation is also performed on a
																// bounded elastic scheduler
							.flatMap(blob -> {
								// After the upload, save the image details to the repository
								ImgBlob imgBlob = new ImgBlob(blob.getName(), blob.getGeneration(),
										getImageURL(imgName));
								return this.imgRepository.save(imgBlob).thenReturn(imgName);
							});
				});
	}

	// Return the image URL.
	public String getImageURL(String imageName) {
		return "https://storage.googleapis.com/" + bucketName + "/" + imageName;
	}

	// Generate a unique image name.
	private String generateUniqueImageName(String img) {
		int dotIndex = img.lastIndexOf(".");
		String imgName = img.substring(0, dotIndex);
		String extension = img.substring(dotIndex);

		return imgName + System.currentTimeMillis() + extension;
	}

	// Delete an image from Google Storage Bucket.
	public Mono<Void> deleteImage(String imageUrl) {
		return Mono.fromCallable(() -> {
			// Load GoogleCredentials reactively
			return GoogleCredentials.fromStream(new FileInputStream(GAC));
		}).subscribeOn(Schedulers.boundedElastic()) // Use boundedElastic to perform blocking I/O operations
				.flatMap(credentials -> this.imgRepository.findByimageUrl(imageUrl).flatMap(imgBlob -> {
					// Create the storage instance with the credentials
					Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials)
							.build().getService();
					
					// Perform the delete operation reactively
					return Mono.fromCallable(() -> {
						storage.delete(bucketName, imgBlob.getBlobName(),
								BlobSourceOption.generationMatch(imgBlob.getBlobGeneration()));
						return imgBlob;
					}).subscribeOn(Schedulers.boundedElastic()) // Perform blocking delete operation reactively
							.then(this.imgRepository.deleteById(imgBlob.getImgBlobId())); // Delete the database record
				}));
	}

	// Update an image in Google Storage Bucket.
	public Mono<String> updateImage(String imageName, MultipartFile file) {

		return deleteImage(imageName).then(Mono.defer(() -> saveImage(file)));

	}
}
