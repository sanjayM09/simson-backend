package com.example.kkBazar.entity.product.dto;

import java.util.List;

public class ProductDto {

	private long productId;
	private long categoryId;
	private long brandId;
	private String productName;
	private String categoryName;
	private String brandName;

	private List<ProductListDto> productList;

	private List<ProductImagesDto> productImages;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getBrandId() {
		return brandId;
	}

	public void setBrandId(long brandId) {
		this.brandId = brandId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<ProductListDto> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductListDto> productList) {
		this.productList = productList;
	}

	public List<ProductImagesDto> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductImagesDto> productImages) {
		this.productImages = productImages;
	}

}
