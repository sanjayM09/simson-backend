package com.example.kkBazar.controller.addProduct;

import java.sql.Date;

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
import com.example.kkBazar.entity.product.productDescription.ProductDescription;
import com.example.kkBazar.service.addProduct.ProductDescriptionService;

@RestController
@CrossOrigin
public class ProductDescriptionController {

	@Autowired
	private ProductDescriptionService productDescriptionService;

	@GetMapping("/productDescription/view")
	public ResponseEntity<Object> getProductDescriptionDetails(
			@RequestParam(required = true) String productDescription) {
		if ("productDescriptionDetails".equals(productDescription)) {
			return ResponseEntity.ok(productDescriptionService.listAll());
		} else {
			String errorMessage = "Invalid value for 'productDescription'. Expected 'productDescriptionDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/productDescription/save")
	public ResponseEntity<String> saveProductDescriptionDetails(@RequestBody ProductDescription productDescription) {
		try {
			productDescription.setCreatedAt(new Date(System.currentTimeMillis()));
			productDescriptionService.SaveProductDescriptionDetails(productDescription);
			long id = productDescription.getProductDescriptionId();
			return ResponseEntity.ok("ProductDescription saved successfully. ProductDescription ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving productDescription: " + e.getMessage());
		}
	}

	@PutMapping("productDescription/edit/{id}")
	public ResponseEntity<ProductDescription> updateOrder(@PathVariable("id") Long productDescription_id,
			@RequestBody ProductDescription productDescription) {
		try {
			ProductDescription existingProductDescription = productDescriptionService
					.findByProductDescriptionId(productDescription_id);

			if (existingProductDescription == null) {
				return ResponseEntity.notFound().build();
			}
			existingProductDescription.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingProductDescription.setDescriptionName(productDescription.getDescriptionName());
			existingProductDescription.setProductId(productDescription.getProductId());
			existingProductDescription.setProductDescriptionList(productDescription.getProductDescriptionList());

			productDescriptionService.SaveProductDescriptionDetails(existingProductDescription);
			return ResponseEntity.ok(existingProductDescription);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/productDescription/delete/{id}")
	public ResponseEntity<String> deletproductDescription(@PathVariable("id") Long id) {
		productDescriptionService.deleteByProductDescription(id);
		return ResponseEntity.ok("ProductDescription deleted successfully");
	}
}
