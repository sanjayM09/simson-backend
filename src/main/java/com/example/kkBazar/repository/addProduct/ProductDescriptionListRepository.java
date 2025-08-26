package com.example.kkBazar.repository.addProduct;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.product.productDescription.ProductDescriptionList;

public interface ProductDescriptionListRepository extends JpaRepository<ProductDescriptionList, Long> {

}
