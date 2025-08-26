package com.example.kkBazar.entity.companyProfile;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="invoiceList")
public class InvoiceList {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long invoiceListId;
	private long orderItemListId;
	private String statusType;
	private LocalDate date;
	private boolean status;
	private long userId;
	private Date invoiceListDate;
	public long getInvoiceListId() {
		return invoiceListId;
	}
	public void setInvoiceListId(long invoiceListId) {
		this.invoiceListId = invoiceListId;
	}
	public long getOrderItemListId() {
		return orderItemListId;
	}
	public void setOrderItemListId(long orderItemListId) {
		this.orderItemListId = orderItemListId;
	}
	public String getStatusType() {
		return statusType;
	}
	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Date getInvoiceListDate() {
		return invoiceListDate;
	}
	public void setInvoiceListDate(Date invoiceListDate) {
		this.invoiceListDate = invoiceListDate;
	}
	public InvoiceList() {
		super();
	}
	
	
	
}
