package com.example.kkBazar.entity.product;

import java.sql.Blob;

public class ProductImagesRequest {

	private long productId;
	private String productImagesUploadUrl;
	private Blob productImagesUpload;

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getProductImagesUploadUrl() {
		return productImagesUploadUrl;
	}

	public void setProductImagesUploadUrl(String productImagesUploadUrl) {
		this.productImagesUploadUrl = productImagesUploadUrl;
	}

	public Blob getProductImagesUpload() {
		return productImagesUpload;
	}

	public void setProductImagesUpload(Blob productImagesUpload) {
		this.productImagesUpload = productImagesUpload;
	}

}
