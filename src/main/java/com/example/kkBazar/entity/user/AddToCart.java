package com.example.kkBazar.entity.user;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "add_to_cart")
public class AddToCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long addToCartId;
	private long productListId;
	private long userId;
	private long productImagesId;
	private int quantity;
	private boolean status;
	private double totalPrice;
	
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
	

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public long getAddToCartId() {
		return addToCartId;
	}

	public void setAddToCartId(long addToCartId) {
		this.addToCartId = addToCartId;
	}

	public long getProductListId() {
		return productListId;
	}

	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProductImagesId() {
		return productImagesId;
	}

	public void setProductImagesId(long productImagesId) {
		this.productImagesId = productImagesId;
	}

	public AddToCart() {
		super();
	}

}