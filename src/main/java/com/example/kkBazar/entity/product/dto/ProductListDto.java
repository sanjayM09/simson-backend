package com.example.kkBazar.entity.product.dto;

import java.util.List;

public class ProductListDto {

	private long productListId;
	private double mrp;
	private double buyRate;
	private double sellRate;
	private int discountPercentage;
	private double discountAmount;
	private int gst;
	private double gstTaxAmount;
	private double totalAmount;
	private int quantity;

	private List<ProductVarientDto> varientList;

	private List<ProductVarientImageDto> varientImages;

	public long getProductListId() {
		return productListId;
	}

	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public double getBuyRate() {
		return buyRate;
	}

	public void setBuyRate(double buyRate) {
		this.buyRate = buyRate;
	}

	public double getSellRate() {
		return sellRate;
	}

	public void setSellRate(double sellRate) {
		this.sellRate = sellRate;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public int getGst() {
		return gst;
	}

	public void setGst(int gst) {
		this.gst = gst;
	}

	public double getGstTaxAmount() {
		return gstTaxAmount;
	}

	public void setGstTaxAmount(double gstTaxAmount) {
		this.gstTaxAmount = gstTaxAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<ProductVarientDto> getVarientList() {
		return varientList;
	}

	public void setVarientList(List<ProductVarientDto> varientList) {
		this.varientList = varientList;
	}

	public List<ProductVarientImageDto> getVarientImages() {
		return varientImages;
	}

	public void setVarientImages(List<ProductVarientImageDto> varientImages) {
		this.varientImages = varientImages;
	}

}
