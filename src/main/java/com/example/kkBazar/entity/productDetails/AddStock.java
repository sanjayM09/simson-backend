package com.example.kkBazar.entity.productDetails;

import java.sql.Date;
import java.time.LocalDate;
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
@Table(name = "add_stock")
public class AddStock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long stockId;
	private LocalDate date;
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
	@JoinColumn(name = "stockId", referencedColumnName = "stockId")
	private List<AddStockList> addStockList;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getStockId() {
		return stockId;
	}

	public void setStockId(long stockId) {
		this.stockId = stockId;
	}

	public List<AddStockList> getAddStockList() {
		return addStockList;
	}

	public void setAddStockList(List<AddStockList> addStockList) {
		this.addStockList = addStockList;
	}

	public AddStock() {
		super();
	}

}
