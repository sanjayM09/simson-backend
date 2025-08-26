package com.example.kkBazar.service.addProduct;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.product.productDescription.ProductDescription;
import com.example.kkBazar.repository.addProduct.ProductDescriptionRepository;

@Service
public class ProductDescriptionService {

	@Autowired
	private ProductDescriptionRepository productDescriptionRepository;
	
	
	//view
	public List<ProductDescription> listAll() {
		return this.productDescriptionRepository.findAll();
	}

	//save
	public ProductDescription SaveProductDescriptionDetails(ProductDescription productDescription) {
		return productDescriptionRepository.save(productDescription);
	}
	//edit 
	public ProductDescription findByProductDescriptionId(long id) {
		return productDescriptionRepository.findById(id).get();
	}
	//delete
		public void deleteByProductDescription(Long id) {
			this.productDescriptionRepository.deleteById(id);
		}
		
		

}
