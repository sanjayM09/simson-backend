//package com.example.kkBazar.entity.demo;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "images_to_delete")
//public class ImagesToDelete {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	@ManyToOne
//	@JoinColumn(name = "demoId")
//	private DemoProduct demoProduct;
//
//	private Long imageId;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public DemoProduct getDemoProduct() {
//		return demoProduct;
//	}
//
//	public void setDemoProduct(DemoProduct demoProduct) {
//		this.demoProduct = demoProduct;
//	}
//
//	public Long getImageId() {
//		return imageId;
//	}
//
//	public void setImageId(Long imageId) {
//		this.imageId = imageId;
//	}
//
//	public ImagesToDelete() {
//		super();
//	}
//
//}
