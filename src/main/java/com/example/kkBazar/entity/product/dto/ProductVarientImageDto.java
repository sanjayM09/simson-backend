package com.example.kkBazar.entity.product.dto;

import java.sql.Blob;

import javax.persistence.Column;

public class ProductVarientImageDto {

	private long productVarientImagesId;
	private Blob productVarientImage;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String productVarientImageUrl;

	
	
	public long getProductVarientImagesId() {
		return productVarientImagesId;
	}

	public void setProductVarientImagesId(long productVarientImagesId) {
		this.productVarientImagesId = productVarientImagesId;
	}

	public Blob getProductVarientImage() {
		return productVarientImage;
	}

	public void setProductVarientImage(Blob productVarientImage) {
		this.productVarientImage = productVarientImage;
	}

	public String getProductVarientImageUrl() {
		return productVarientImageUrl;
	}

	public void setProductVarientImageUrl(String productVarientImageUrl) {
		this.productVarientImageUrl = productVarientImageUrl;
	}

}
