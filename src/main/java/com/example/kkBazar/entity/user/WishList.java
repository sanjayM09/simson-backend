package com.example.kkBazar.entity.user;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wish_list")
public class WishList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long wishListId;
	private long productListId;
	private long userId;
	private long productImagesId;
	private boolean status;
	private Date createdAt;
	private Date updatedAt;

	

	public long getWishListId() {
		return wishListId;
	}



	public void setWishListId(long wishListId) {
		this.wishListId = wishListId;
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



	public boolean isStatus() {
		return status;
	}



	public void setStatus(boolean status) {
		this.status = status;
	}



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



	public WishList() {
		super();
	}

}
