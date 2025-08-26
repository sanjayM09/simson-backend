package com.example.kkBazar.entity.user;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_refund")
public class OrderRefund {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderRefundId;
	private long orderItemListId;
	private long orderReturnId;
	private long userId;
	private Long bankId;
	@Column(columnDefinition = "DATE")
	private LocalDate refundDate;
	private String returnStatus;
	private boolean accepted;
	private boolean rejected;
	private boolean pending;
	
	
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
	
	
	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	public long getOrderRefundId() {
		return orderRefundId;
	}

	public void setOrderRefundId(long orderRefundId) {
		this.orderRefundId = orderRefundId;
	}

	public long getOrderItemListId() {
		return orderItemListId;
	}

	public void setOrderItemListId(long orderItemListId) {
		this.orderItemListId = orderItemListId;
	}

	public long getOrderReturnId() {
		return orderReturnId;
	}

	public void setOrderReturnId(long orderReturnId) {
		this.orderReturnId = orderReturnId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public LocalDate getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(LocalDate refundDate) {
		this.refundDate = refundDate;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

}
