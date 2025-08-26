package com.example.kkBazar.service.addProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.product.ProductVarient;
import com.example.kkBazar.repository.addProduct.ProductListRepository;

@Service
public class ProductListService {
@Autowired
private ProductListRepository productListRepository;
//view
	public List<ProductList> listAll() {
		return this.productListRepository.findAll();
	}
	
	
	public Optional<ProductList> findProductListById(Long productListId) {
        return productListRepository.findById(productListId);
    }
	
	
	public ProductList SaveProductDetails(ProductList productList) {
		return productListRepository.save(productList);
	}
}
