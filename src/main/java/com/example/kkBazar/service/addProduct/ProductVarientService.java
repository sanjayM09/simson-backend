package com.example.kkBazar.service.addProduct;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.product.ProductImages;
import com.example.kkBazar.entity.product.ProductVarient;
import com.example.kkBazar.repository.addProduct.ProductVarientRepository;

@Service
public class ProductVarientService {

	@Autowired
	private ProductVarientRepository productVarientRepository;

	public Optional<ProductVarient> findProductVarientById(Long productVarientId) {
		return productVarientRepository.findById(productVarientId);
	}
	
	public ProductVarient SaveProductDetails(ProductVarient varient) {
		return productVarientRepository.save(varient);
	}
}
