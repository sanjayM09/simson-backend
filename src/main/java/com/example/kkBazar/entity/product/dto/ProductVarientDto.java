package com.example.kkBazar.entity.product.dto;

public class ProductVarientDto {

	private long productVarientId;
	private String varientName;
	private String varientValue;

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

	public long getProductVarientId() {
		return productVarientId;
	}

	public void setProductVarientId(long productVarientId) {
		this.productVarientId = productVarientId;
	}
	
	

}
