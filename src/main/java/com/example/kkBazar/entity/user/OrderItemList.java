package com.example.kkBazar.entity.user;


import java.sql.Blob;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_item_list")
public class OrderItemList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private long orderItemListId;
	private double quantity;
	private long productListId;
	private String orderStatus;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String reason;
	private double totalPrice;
	private double discountPercentage;
	private double mrp;
	private double buyRate;
	private double sellRate;
	private double discountAmount;
	private double gst;
	private double alertQuantity;
	private double gstTaxAmount;
	private boolean pending;
	private boolean delivered;
	private boolean cancelled;
	private boolean confirmed;
	private double totalAmount;
	private boolean returnPending;
	private boolean returnAccepted;
	private boolean returnRejected;
	private String orderId;
	private LocalDate deliveredDate;
	private long userOrderId;
	private boolean returnCancelled;
	private LocalDate cancelledDate;
	private LocalDate confirmedDate;	
	@Column(columnDefinition = "MEDIUMTEXT")
	private String pdfUrl;
	@JsonIgnore
	private Blob pdf;
	private String url;
	private boolean  invoicePdf;
	
	
	
	
	public boolean isInvoicePdf() {
		return invoicePdf;
	}

	public void setInvoicePdf(boolean invoicePdf) {
		this.invoicePdf = invoicePdf;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public Blob getPdf() {
		return pdf;
	}

	public void setPdf(Blob pdf) {
		this.pdf = pdf;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LocalDate getCancelledDate() {
		return cancelledDate;
	}

	public void setCancelledDate(LocalDate cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public LocalDate getConfirmedDate() {
		return confirmedDate;
	}

	public void setConfirmedDate(LocalDate confirmedDate) {
		this.confirmedDate = confirmedDate;
	}

	public boolean isReturnCancelled() {
		return returnCancelled;
	}

	public void setReturnCancelled(boolean returnCancelled) {
		this.returnCancelled = returnCancelled;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public long getUserOrderId() {
		return userOrderId;
	}

	public void setUserOrderId(long userOrderId) {
		this.userOrderId = userOrderId;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}




	public LocalDate getDeliveredDate() {
		return deliveredDate;
	}

	public void setDeliveredDate(LocalDate deliveredDate) {
		this.deliveredDate = deliveredDate;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isReturnPending() {
		return returnPending;
	}

	public void setReturnPending(boolean returnPending) {
		this.returnPending = returnPending;
	}

	public boolean isReturnAccepted() {
		return returnAccepted;
	}

	public void setReturnAccepted(boolean returnAccepted) {
		this.returnAccepted = returnAccepted;
	}

	public boolean isReturnRejected() {
		return returnRejected;
	}

	public void setReturnRejected(boolean returnRejected) {
		this.returnRejected = returnRejected;
	}

	public long getOrderItemListId() {
		return orderItemListId;
	}

	public void setOrderItemListId(long orderItemListId) {
		this.orderItemListId = orderItemListId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public long getProductListId() {
		return productListId;
	}

	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderItemList() {
		super();
	}

}
