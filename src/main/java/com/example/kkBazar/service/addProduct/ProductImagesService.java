package com.example.kkBazar.service.addProduct;

import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.product.Product;
import com.example.kkBazar.entity.product.ProductImages;
import com.example.kkBazar.repository.addProduct.ProductImagesRepository;

@Service
public class ProductImagesService {

	@Autowired
	private ProductImagesRepository productImagesRepository;

	// view
	public List<ProductImages> listAll() {
		return this.productImagesRepository.findAll();
	}

	// save
	public ProductImages SaveProductDetails(ProductImages product) {
		return productImagesRepository.save(product);
	}

	public ProductImages findId(Long productId) {
		return productImagesRepository.findById(productId).get();
	}

	// delete
	public void deleteProductId(Long id) {
		productImagesRepository.deleteById(id);
	}

	public Optional<ProductImages> getById1(long id) {
		return Optional.of(productImagesRepository.findById(id).get());
	}

	public Optional<ProductImages> findProductImagesById(Long productImageId) {
		return productImagesRepository.findById(productImageId);
	}

	@Transactional
	public void saveProductDetails(long productId, byte[] productImagesUpload, String productImagesUploadUrl) {
		productImagesRepository.saveProductDetails(productId, productImagesUploadUrl, productImagesUpload);
	}

	@Transactional
	public void saveVarientImageDetails(long productListId, byte[] productVarientImage, String productVarientImageUrl) {
		productImagesRepository.saveVarientImageDetails(productListId, productVarientImageUrl, productVarientImage);
	}

//	public void addNewProductImage(Product product, ProductImages newProductImage) {
//		productImagesRepository.addNewProductImage(newProductImage);
//		product.getProductImages().add(newProductImage);
//	}

}
