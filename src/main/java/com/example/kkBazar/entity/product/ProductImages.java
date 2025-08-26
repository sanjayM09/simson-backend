package com.example.kkBazar.entity.product;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "product_images")
public class ProductImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productImagesId;
//	@JsonDeserialize(using = BlobDeserializer.class)
	@JsonIgnore
	private Blob productImagesUpload;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String productImagesUploadUrl;
	private boolean deleted;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getProductImagesId() {
		return productImagesId;
	}

	public void setProductImagesId(Long productImagesId) {
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

	public ProductImages() {
		super();
	}

}
