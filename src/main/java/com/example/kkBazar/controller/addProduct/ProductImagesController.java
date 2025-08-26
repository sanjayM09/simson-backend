package com.example.kkBazar.controller.addProduct;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.kkBazar.entity.product.ProductImages;
import com.example.kkBazar.entity.product.ProductVarientImages;
import com.example.kkBazar.service.addProduct.ProductImagesService;
import com.example.kkBazar.service.addProduct.ProductVarientImageService;
import com.example.kkBazar.service.addProduct.ProductVarientService;

@RestController
@CrossOrigin
public class ProductImagesController {

	@Autowired
	private ProductImagesService productImagesService;

	@Autowired
	private ProductVarientService productVarientService;

	@Autowired
	private ProductVarientImageService productVarientImageService;

	@PutMapping("/varientImage/edit/{id}")
	public ResponseEntity<?> updateVarient(@PathVariable("id") Long productVarientImagesId,
			@RequestBody ProductVarientImages updatedImage) {
		try {
			Optional<ProductVarientImages> existingProductOptional = productVarientImageService
					.findProductVarientImagesById(productVarientImagesId);

			if (existingProductOptional.isPresent()) {
				ProductVarientImages existingImage = existingProductOptional.get();

				if (existingImage != null) {
					if (updatedImage.getProductVarientImageUrl() != null) {
						existingImage.setProductVarientImageUrl(updatedImage.getProductVarientImageUrl());
					}

					if (updatedImage.getProductVarientImageUrl() != null
							&& !updatedImage.getProductVarientImageUrl().isEmpty()) {
						String base64Image = updatedImage.getProductVarientImageUrl();
						byte[] imageBytes = Base64.getDecoder().decode(base64Image);
						Blob blob = createBlob(imageBytes);
						existingImage.setProductVarientImage(blob);
					}
				}

				productVarientImageService.SaveProductDetails(existingImage);

				return ResponseEntity.ok(existingImage);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Product not found with id: " + productVarientImagesId);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating product: " + e.getMessage());
		}
	}

	@PutMapping("/productImage/edit/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable("id") Long productImagesId,
			@RequestBody ProductImages updatedImage) {
		try {
			Optional<ProductImages> existingProductOptional = productImagesService
					.findProductImagesById(productImagesId);

			if (existingProductOptional.isPresent()) {
				ProductImages existingImage = existingProductOptional.get();

				if (existingImage != null) {
					if (updatedImage.getProductImagesUploadUrl() != null) {
						existingImage.setProductImagesUploadUrl(updatedImage.getProductImagesUploadUrl());
					}

					if (updatedImage.getProductImagesUploadUrl() != null
							&& !updatedImage.getProductImagesUploadUrl().isEmpty()) {
						String base64Image = updatedImage.getProductImagesUploadUrl();
						byte[] imageBytes = Base64.getDecoder().decode(base64Image);
						Blob blob = createBlob(imageBytes);
						existingImage.setProductImagesUpload(blob);
					}
				}

				productImagesService.SaveProductDetails(existingImage);

				return ResponseEntity.ok(existingImage);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Product not found with id: " + productImagesId);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating product: " + e.getMessage());
		}
	}

	@PostMapping("/product/images/save")
	public ResponseEntity<String> uploadProductImage(@RequestParam("productId") long productId,
			@RequestParam("type") String type, @RequestParam("productImagesUpload") MultipartFile productImagesUpload) {

		try {
			byte[] imageData = productImagesUpload.getBytes();
			String productImagesUploadUrl = Base64.getEncoder().encodeToString(imageData);
			productImagesService.saveProductDetails(productId, imageData, productImagesUploadUrl);

			return ResponseEntity.ok("Product image uploaded successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid Base64 encoding for productImagesUploadUrl.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading product image.");
		}
	}

	@PostMapping("/varient/images/save")
	public ResponseEntity<String> uploadVarientImage(@RequestParam("productListId") long productListId,

			@RequestParam("productVarientImage") MultipartFile productVarientImage) {

		try {
			byte[] imageData = productVarientImage.getBytes();
			String productVarientImageUrl = Base64.getEncoder().encodeToString(imageData);
			productImagesService.saveVarientImageDetails(productListId, imageData, productVarientImageUrl);

			return ResponseEntity.ok("varient image uploaded successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid Base64 encoding for productVarientImageUrl.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading Varient image.");
		}
	}

	private Blob createBlob(byte[] bytes) throws SQLException {
		if (bytes != null) {
			return new javax.sql.rowset.serial.SerialBlob(bytes);
		}
		return null;
	}
}
