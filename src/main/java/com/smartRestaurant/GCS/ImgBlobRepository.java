package com.smartRestaurant.GCS;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface ImgBlobRepository extends ReactiveCrudRepository<ImgBlob, String> {

	Mono<ImgBlob> findByblobName(String blobName);
	Mono<ImgBlob> findByimageUrl(String imageUrl);
}