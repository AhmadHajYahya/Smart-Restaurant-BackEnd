package com.smartRestaurant.GCS;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("img_blob")
public class ImgBlob implements Persistable<String>{
	@Id
	private String imgBlobId;

	@Column("blob_name")
	private String blobName;

	@Column("blob_generation")
	private long blobGeneration;

	@Column("image_url")
	private String imageUrl;
	
	@Transient
	private boolean isNew = true;

	public ImgBlob() {

	}

	public ImgBlob(String blobName, long blobGeneration, String imageUrl) {
		this.blobName = blobName;
		this.blobGeneration = blobGeneration;
		this.imageUrl = imageUrl;
	}

	public String getImgBlobId() {
		return imgBlobId;
	}

	public void setImgBlobId(String imgBlobId) {
		this.imgBlobId = imgBlobId;
	}

	public String getBlobName() {
		return blobName;
	}

	public void setBlobName(String blobName) {
		this.blobName = blobName;
	}

	public long getBlobGeneration() {
		return blobGeneration;
	}

	public void setBlobGeneration(long blobGeneration) {
		this.blobGeneration = blobGeneration;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "ImgBlob [imgBlobId=" + imgBlobId + ", blobName=" + blobName + ", blobGeneration=" + blobGeneration
				+ "]";
	}

	@Override
	public String getId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
	
	public void setIsNew(boolean value) {
		isNew = value;
	}

}
