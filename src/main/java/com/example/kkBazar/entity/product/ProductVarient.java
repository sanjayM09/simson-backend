package com.example.kkBazar.entity.product;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_varient")
public class ProductVarient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productVarientId;
	private String varientName;
	private String varientValue;
	private boolean deleted;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getProductVarientId() {
		return productVarientId;
	}

	public void setProductVarientId(Long productVarientId) {
		this.productVarientId = productVarientId;
	}

	public String getVarientName() {
		return varientName;
	}

	public void setVarientName(String varientName) {
		this.varientName = varientName;
	}

	public String getVarientValue() {
		return varientValue;
	}

	public void setVarientValue(String varientValue) {
		this.varientValue = varientValue;
	}

	public ProductVarient() {
		super();
	}

}
