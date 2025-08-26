package com.example.kkBazar.service.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.demo.DemoProduct;
import com.example.kkBazar.entity.demo.DemoProductImages;
import com.example.kkBazar.repository.demo.DemoProductImageRepository;
import com.example.kkBazar.repository.demo.DemoProductRepository;

@Service
public class DemoProductService {

	@Autowired
	private DemoProductImageRepository demoProductImageRepository;

	@Autowired
	private DemoProductRepository demoProductRepository;

	// view
	public List<DemoProduct> listAll() {
		return this.demoProductRepository.findAll();
	}

	// save
	public DemoProduct SaveProductDetails(DemoProduct product) {
		return demoProductRepository.save(product);
	}

	public DemoProduct findId(Long productId) {
		return demoProductRepository.findById(productId).get();
	}

	// delete
	public void deleteProductId(Long id) {
		demoProductRepository.deleteById(id);
	}
	
	  public Optional<DemoProductImages> findProductImageById(Long demoProImagesId) {
	        return demoProductImageRepository.findByDemoProImagesId(demoProImagesId);
	    }
	  
	  public Optional<DemoProduct> findProductById(Long productId) {
	        return demoProductRepository.findById(productId);
	    }
	  
	  public DemoProductImages getId(Long id) {
			return demoProductImageRepository.findById(id).get();
		}


}
