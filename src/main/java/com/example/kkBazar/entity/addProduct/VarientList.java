package com.example.kkBazar.entity.addProduct;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="varientList")
public class VarientList {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long varientListId;
	private String varientListName;
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
	public long getVarientListId() {
		return varientListId;
	}
	public void setVarientListId(long varientListId) {
		this.varientListId = varientListId;
	}
	public String getVarientListName() {
		return varientListName;
	}
	public void setVarientListName(String varientListName) {
		this.varientListName = varientListName;
	}
	public VarientList() {
		super();
	}
	
}
