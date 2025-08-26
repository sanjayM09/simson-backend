package com.example.kkBazar.entity.product.productDescription;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "product_description")
public class ProductDescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productDescriptionId;
	private String descriptionName;
	private long productId;
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
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "productDescriptionId", referencedColumnName = "productDescriptionId")
	private List<ProductDescriptionList> productDescriptionList;
	
	public long getProductDescriptionId() {
		return productDescriptionId;
	}
	public void setProductDescriptionId(long productDescriptionId) {
		this.productDescriptionId = productDescriptionId;
	}
	public String getDescriptionName() {
		return descriptionName;
	}
	public void setDescriptionName(String descriptionName) {
		this.descriptionName = descriptionName;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public List<ProductDescriptionList> getProductDescriptionList() {
		return productDescriptionList;
	}
	public void setProductDescriptionList(List<ProductDescriptionList> productDescriptionList) {
		this.productDescriptionList = productDescriptionList;
	}
	public ProductDescription() {
		super();
	}

	
}
