package com.example.kkBazar.entity.product.dto;

import java.sql.Blob;

import javax.persistence.Column;

public class ProductImagesDto {

	private long productImagesId;
	private Blob productImagesUpload;

	@Column(columnDefinition = "MEDIUMTEXT")
	private String productImagesUploadUrl;

	public long getProductImagesId() {
		return productImagesId;
	}

	public void setProductImagesId(long productImagesId) {
		this.productImagesId = productImagesId;
	}

	public Blob getProductImagesUpload() {
		return productImagesUpload;
	}

	public void setProductImagesUpload(Blob productImagesUpload) {
		this.productImagesUpload = productImagesUpload;
	}

	public String getProductImagesUploadUrl() {
		return productImagesUploadUrl;
	}

	public void setProductImagesUploadUrl(String productImagesUploadUrl) {
		this.productImagesUploadUrl = productImagesUploadUrl;
	}



}
