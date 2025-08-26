package com.example.kkBazar.service.addProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.product.ProductImages;
import com.example.kkBazar.entity.product.ProductVarientImages;
import com.example.kkBazar.repository.addProduct.ProductVarientImageRepository;

@Service
public class ProductVarientImageService {

	@Autowired
	private ProductVarientImageRepository repo;

//view
	public List<ProductVarientImages> listAll() {
		return this.repo.findAll();
	}

//save
	public ProductVarientImages SaveProductDetails(ProductVarientImages product) {
		return repo.save(product);
	}

	public ProductVarientImages findId(Long productId) {
		return repo.findById(productId).get();
	}

//delete
	public void deleteProductId(Long id) {
		repo.deleteById(id);
	}

	public Optional<ProductVarientImages> getById1(long id) {
	    return repo.findById(id);
	}


	public Optional<ProductVarientImages> findProductVarientImagesById(Long productVarientImageId) {
        return repo.findById(productVarientImageId);
    }
}
