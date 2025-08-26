package com.example.kkBazar.entity.productDetails;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "discount_list")
public class DiscountList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long discountListId;
	private double discountAmount;
	private double discountPercentage;
	private long productListId;
	private double mrp;
	private double buyRate;
	private double sellRate;
	private double gstTaxAmount;
	private double totalAmount;
	private double gst;
	private Date startDate;
	private Date endDate;
	private boolean status;
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

	public double getGst() {
		return gst;
	}

	public void setGst(double gst) {
		this.gst = gst;
	}

	public long getDiscountListId() {
		return discountListId;
	}

	public void setDiscountListId(long discountListId) {
		this.discountListId = discountListId;
	}

//	public double getDiscountAmount() {
//		return discountAmount;
//	}
//
//	public void setDiscountAmount(double discountAmount) {
//		this.discountAmount = discountAmount;
//	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public long getProductListId() {
		return productListId;
	}

	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public DiscountList() {
		super();
	}

}
