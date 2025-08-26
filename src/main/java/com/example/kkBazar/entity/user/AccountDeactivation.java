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
@Table(name = "accountDeactivation")
public class AccountDeactivation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountDeactivationId;
	private String reasonForDeactivation;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	private String currentPassword;
	private String repeatPassword;
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
	public Long getAccountDeactivationId() {
		return accountDeactivationId;
	}
	public void setAccountDeactivationId(Long accountDeactivationId) {
		this.accountDeactivationId = accountDeactivationId;
	}
	public String getReasonForDeactivation() {
		return reasonForDeactivation;
	}
	public void setReasonForDeactivation(String reasonForDeactivation) {
		this.reasonForDeactivation = reasonForDeactivation;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getRepeatPassword() {
		return repeatPassword;
	}
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public AccountDeactivation() {
		super();
	}


}
