package com.example.kkBazar.service.whatsapp.pdf;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pdf")
public class Pdf {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pdfId;
	private String mobileNumber;
	private String message;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String pdfUrl;
	private Blob pdf;
	private String url;
	private long orderItemId;
	
	

	public long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public long getPdfId() {
		return pdfId;
	}

	public void setPdfId(long pdfId) {
		this.pdfId = pdfId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public Pdf() {
		super();
	}

}
