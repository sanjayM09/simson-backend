                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     package com.example.kkBazar.controller.addProduct;

import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.kkBazar.entity.addProduct.Brand;
import com.example.kkBazar.repository.addProduct.BrandRepository;
import com.example.kkBazar.service.addProduct.BrandService;

@RestController
@CrossOrigin
public class BrandController {

	@Autowired
	private BrandService brandService;
	@Autowired
	private BrandRepository brandRepository;

	@GetMapping("/brand/view")
	public ResponseEntity<Object> getBrandDetails(@RequestParam(required = true) String brand) {
		if ("brandDetails".equals(brand)) {
			return ResponseEntity.ok(brandService.listBrands());
		} else {
			String errorMessage = "Invalid value for 'brand'. Expected 'brandDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/brand/save")
	public ResponseEntity<String> saveBrandDetails(@RequestBody Brand brand) {
		try {
			brand.setCreatedAt(new Date(System.currentTimeMillis()));
			String brandname=brand.getBrandName();
			Optional<Brand> existingemail = brandRepository.findByBrandName(brandname);
			if (existingemail.isPresent()) {
				return ResponseEntity.badRequest().body("Same BrandName not allowed");
			}
			brandService.SaveBrandDetails(brand);
			long id = brand.getBrandId();
			return ResponseEntity.ok("Brand Details saved successfully. Brand ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving brand: " + e.getMessage());
		}
	}

	@PutMapping("/brand/edit/{id}")
	public ResponseEntity<Brand> updateBrand(@PathVariable("id") Long brandId, @RequestBody Brand brand) {
		try {
			Brand existingBrand = brandService.findBrandById(brandId);

			if (existingBrand == null) {
				return ResponseEntity.notFound().build();
			}
			existingBrand.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingBrand.setBrandName(brand.getBrandName());

			brandService.SaveBrandDetails(existingBrand);
			return ResponseEntity.ok(existingBrand);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	

	@DeleteMapping("/brand/delete/{brandId}")
	public ResponseEntity<String> deleteBrandId(@PathVariable("brandId") Long brandId) {
		brandService.deleteBrandById(brandId);
		return ResponseEntity.ok("Brand deleted successfully With Id :" + brandId);

	}

}
