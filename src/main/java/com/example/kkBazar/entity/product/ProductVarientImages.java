package com.example.kkBazar.entity.product;

import java.sql.Blob;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "product_varient_images")
public class ProductVarientImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productVarientImagesId;
	@JsonIgnore
	private Blob productVarientImage;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String productVarientImageUrl;
	@Column(columnDefinition = "BOOLEAN DEFAULT false")
	private boolean deleted;
	private String type;
	
	private Date createdAt;
	private Date updatedAt;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Long getProductVarientImagesId() {
		return productVarientImagesId;
	}
	public void setProductVarientImagesId(Long productVarientImagesId) {
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
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ProductVarientImages() {
		super();
	}


	

}
