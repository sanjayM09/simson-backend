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
import com.example.kkBazar.entity.addProduct.Size;
import com.example.kkBazar.repository.addProduct.BrandRepository;
import com.example.kkBazar.repository.addProduct.SizeRepository;
import com.example.kkBazar.service.addProduct.SizeService;

@RestController
@CrossOrigin
public class SizeController {

	@Autowired
	private SizeService service;
	
	
	@Autowired
	private SizeRepository repository;
	
	@GetMapping("/size/view")
	public ResponseEntity<Object> getBrandDetails(@RequestParam(required = true) String size) {
		if ("sizeDetails".equals(size)) {
			return ResponseEntity.ok(service.listSize());
		} else {
			String errorMessage = "Invalid value for 'size'. Expected 'SizeDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/size/save")
	public ResponseEntity<String> saveBrandDetails(@RequestBody Size size) {
		try {
			size.setCreatedAt(new Date(System.currentTimeMillis()));
			String sizeName = size.getSizeName();
			Optional<Size> existingemail = repository.findBySizeName(sizeName);
			if (existingemail.isPresent()) {
				return ResponseEntity.badRequest().body("Same sizeName not allowed");
			}
			service.SaveSizeDetails(size);
			long id = size.getSizeId();
			return ResponseEntity.ok("Size Details saved successfully. Size ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving Size: " + e.getMessage());
		}
	}
	
	@PutMapping("/size/edit/{id}")
	public ResponseEntity<Size> updateBrand(@PathVariable("id") Long sizeId, @RequestBody Size size) {
		try {
			Size existingBrand = service.findSizeById(sizeId);

			if (existingBrand == null) {
				return ResponseEntity.notFound().build();
			}
			existingBrand.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingBrand.setSizeName(size.getSizeName());
		
			service.SaveSizeDetails(existingBrand);
			return ResponseEntity.ok(existingBrand);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@DeleteMapping("/size/delete/{id}")
	public ResponseEntity<String> deleteBrandId(@PathVariable("id") Long sizeId) {
		service.deleteSizeById(sizeId);
		return ResponseEntity.ok("Size deleted successfully With Id :" + sizeId );

	}
}