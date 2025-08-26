package com.example.kkBazar.repository.addProduct;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.product.productDescription.ProductDescription;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, Long> {

}
