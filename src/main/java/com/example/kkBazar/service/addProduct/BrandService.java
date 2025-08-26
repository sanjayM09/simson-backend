package com.example.kkBazar.service.addProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.addProduct.Brand;
import com.example.kkBazar.repository.addProduct.BrandRepository;

@Service
public class BrandService {

	@Autowired
	private BrandRepository brandRepository;

	// view
	public List<Brand> listBrands() {
		return this.brandRepository.findAll();
	}

	// save
	public Brand SaveBrandDetails(Brand brand) {
		return brandRepository.save(brand);
	}

	public Brand findBrandById(Long brandId) {
		return brandRepository.findById(brandId).get();
	}

	// delete
	public void deleteBrandById(Long id) {
		brandRepository.deleteById(id);
	}
}
