package com.smartRestaurant.GCS;

import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;

public interface ImageService {
	public Mono<String> saveImage(MultipartFile img);

	public String getImageURL(String imageName);

	public Mono<Void> deleteImage(String imageUrl);

	public Mono<String> updateImage(String imageName, MultipartFile file);
}
