package com.example.kkBazar.service.addProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.product.Product;
import com.example.kkBazar.repository.addProduct.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;

//view
	public List<Product> listAll() {
		return this.productRepository.findAll();
	}

//save
	public Product SaveProductDetails(Product product) {
		return productRepository.save(product);
	}

	public Product findId(Long productId) {
		return productRepository.findById(productId).get();
	}

//delete
	public void deleteProductId(Long id) {
		productRepository.deleteById(id);
	}

	 public Optional<Product> findProductById(Long productId) {
	        return productRepository.findById(productId);
	    }
}
