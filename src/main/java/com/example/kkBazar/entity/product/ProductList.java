package com.example.kkBazar.entity.product;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "product_list")
public class ProductList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productListId;
	private double mrp;
	private double buyRate;
	private double sellRate;
	private double discountPercentage;
	private double discountAmount;
	private double gst;
	private double alertQuantity;
	private double gstTaxAmount;
	private double totalAmount;
	private double quantity;
	private boolean deleted;
	private int weight;
	private int pieces;
	private String returnType;
	private boolean returnStatus;
	private int returnCount;
	private double stockIn;
//	  @Column(columnDefinition = "TEXT")
//	private String description;
	private String unit;

	private Long sizeId;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "productListId", referencedColumnName = "productListId")
	private List<ProductVarient> varientList;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "productListId", referencedColumnName = "productListId")
	private List<ProductVarientImages> varientImages;

	
	
	
	public Long getSizeId() {
		return sizeId;
	}

	public void setSizeId(Long sizeId) {
		this.sizeId = sizeId;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public boolean isReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(boolean returnStatus) {
		this.returnStatus = returnStatus;
	}

	public int getReturnCount() {
		return returnCount;
	}

	public void setReturnCount(int returnCount) {
		this.returnCount = returnCount;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getPieces() {
		return pieces;
	}

	public void setPieces(int pieces) {
		this.pieces = pieces;
	}

	public Long getProductListId() {
		return productListId;
	}

	public void setProductListId(Long productListId) {
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

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getGst() {
		return gst;
	}

	public void setGst(double gst) {
		this.gst = gst;
	}

	public double getAlertQuantity() {
		return alertQuantity;
	}

	public void setAlertQuantity(double alertQuantity) {
		this.alertQuantity = alertQuantity;
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public double getStockIn() {
		return stockIn;
	}

	public void setStockIn(double stockIn) {
		this.stockIn = stockIn;
	}

//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<ProductVarient> getVarientList() {
		return varientList;
	}

	public void setVarientList(List<ProductVarient> varientList) {
		this.varientList = varientList;
	}

	public List<ProductVarientImages> getVarientImages() {
		return varientImages;
	}

	public void setVarientImages(List<ProductVarientImages> varientImages) {
		this.varientImages = varientImages;
	}

	public ProductList() {
		super();
	}

}
