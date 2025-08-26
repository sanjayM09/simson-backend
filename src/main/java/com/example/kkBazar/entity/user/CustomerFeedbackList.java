package com.example.kkBazar.entity.user;

import java.sql.Blob;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer_feedback_list")
public class CustomerFeedbackList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerFeedbackListId;
	  @Column(columnDefinition = "TEXT")
	private String description;
	private long productListId;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	private Blob image;
	private String url;
	private int starRate;
	public long getCustomerFeedbackListId() {
		return customerFeedbackListId;
	}
	public void setCustomerFeedbackListId(long customerFeedbackListId) {
		this.customerFeedbackListId = customerFeedbackListId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getProductListId() {
		return productListId;
	}
	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Blob getImage() {
		return image;
	}
	public void setImage(Blob image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStarRate() {
		return starRate;
	}
	public void setStarRate(int starRate) {
		this.starRate = starRate;
	}
	public CustomerFeedbackList() {
		super();
	}

	
}
