package com.example.kkBazar.entity.user;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer_feedback")
public class CustomerFeedback {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerFeedbackId;
	private long userId;
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
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "customerFeedbackId", referencedColumnName = "customerFeedbackId")
	private List<CustomerFeedbackList> customerFeedbackList;

	public long getCustomerFeedbackId() {
		return customerFeedbackId;
	}

	public void setCustomerFeedbackId(long customerFeedbackId) {
		this.customerFeedbackId = customerFeedbackId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<CustomerFeedbackList> getCustomerFeedbackList() {
		return customerFeedbackList;
	}

	public void setCustomerFeedbackList(List<CustomerFeedbackList> customerFeedbackList) {
		this.customerFeedbackList = customerFeedbackList;
	}

	public CustomerFeedback() {
		super();
	}

}
