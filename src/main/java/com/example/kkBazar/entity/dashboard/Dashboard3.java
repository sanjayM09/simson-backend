package com.example.kkBazar.entity.dashboard;

import java.sql.Blob;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "dashboard3")
public class Dashboard3 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dashboard3Id;
	private long categoryId;
	  @Column(columnDefinition = "TEXT")
	private String description;
	private String title;
	@JsonIgnore
	private Blob fileUpload;
	private String url;
	private String dashboardStatus;
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
	public String getDashboardStatus() {
		return dashboardStatus;
	}

	public void setDashboardStatus(String dashboardStatus) {
		this.dashboardStatus = dashboardStatus;
	}

	public long getDashboard3Id() {
		return dashboard3Id;
	}

	public void setDashboard3Id(long dashboard3Id) {
		this.dashboard3Id = dashboard3Id;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Blob getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(Blob fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Dashboard3() {
		super();
	}

}
