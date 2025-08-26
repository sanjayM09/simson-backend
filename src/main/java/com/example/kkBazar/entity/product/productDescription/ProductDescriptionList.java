package com.example.kkBazar.entity.product.productDescription;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_description_list")
public class ProductDescriptionList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productDescriptionListId;
	private String name;
	private String value;

	public long getProductDescriptionListId() {
		return productDescriptionListId;
	}

	public void setProductDescriptionListId(long productDescriptionListId) {
		this.productDescriptionListId = productDescriptionListId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ProductDescriptionList() {
		super();
	}

}
