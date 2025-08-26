package com.example.kkBazar.controller.addProduct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.repository.addProduct.ProductListRepository;
import com.example.kkBazar.service.addProduct.ProductListService;

@RestController
@CrossOrigin
public class ProductListController {
	@Autowired
	private ProductListService productListService;

	@Autowired
	private ProductListRepository productListRepository;

	@GetMapping("/productList/view")
	public ResponseEntity<Object> getProductListDetails(@RequestParam(required = true) String productList) {
		if ("productListDetails".equals(productList)) {
			return ResponseEntity.ok(productListService.listAll());
		} else {
			String errorMessage = "Invalid value for 'productList'. Expected 'productListDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/checkOutOfStock")
	public ResponseEntity<?> checkOutOfStockProducts() {
		try {
			List<ProductList> productList = productListRepository.findAll();

			List<String> outOfStockProducts = new ArrayList<>();

			for (ProductList product : productList) {
				if (product.getQuantity() < 5) {
					outOfStockProducts.add("Product List ID: " + product.getProductListId() + " is out of stock.");
				}
			}

			if (outOfStockProducts.isEmpty()) {
				return ResponseEntity.ok("All products are in stock.");
			} else {
				return ResponseEntity.ok(outOfStockProducts);
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error checking out-of-stock products: " + e.getMessage());
		}
	}

}
