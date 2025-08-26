package com.example.kkBazar.entity.dashboard;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dashboard1")
public class Dashboard1 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dashboard1Id;
	private long productId;
	private long productImagesId;
	private long productListId;
	  @Column(columnDefinition = "TEXT")
	private String description;
	private boolean status;
	private String dashboardStatus;
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
	public String getDashboardStatus() {
		return dashboardStatus;
	}

	public void setDashboardStatus(String dashboardStatus) {
		this.dashboardStatus = dashboardStatus;
	}

	public long getDashboard1Id() {
		return dashboard1Id;
	}

	public void setDashboard1Id(long dashboard1Id) {
		this.dashboard1Id = dashboard1Id;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getProductImagesId() {
		return productImagesId;
	}

	public void setProductImagesId(long productImagesId) {
		this.productImagesId = productImagesId;
	}

	public long getProductListId() {
		return productListId;
	}

	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Dashboard1() {
		super();
	}

}
