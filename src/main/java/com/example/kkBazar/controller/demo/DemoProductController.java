package com.example.kkBazar.controller.demo;

import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.demo.DemoProduct;
import com.example.kkBazar.entity.demo.DemoProductImages;
import com.example.kkBazar.repository.demo.DemoProductImageRepository;
import com.example.kkBazar.service.demo.DemoProductService;

@RestController
@CrossOrigin
public class DemoProductController {

	@Autowired
	private DemoProductService demoProductService;

	@Autowired
	private DemoProductImageRepository demoProductImageRepository;

	@PostMapping("/demo/save")
	public ResponseEntity<?> saveProductWithDemo(@RequestBody DemoProduct product) {
		try {
			List<DemoProductImages> productImages = product.getDemoProductImages();

			for (DemoProductImages productLoop : productImages) {

				String base64Image = productLoop.getUrl();
				byte[] imageBytes = Base64.getDecoder().decode(base64Image);
				Blob blob = null;
				try {
					blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				productLoop.setFile(blob);
			}
			product.setCreatedAt(new Date(System.currentTimeMillis()));
			demoProductService.SaveProductDetails(product);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving product: " + e.getMessage());
		}
	}



	@PutMapping("/demo/update/{productId}")
	public ResponseEntity<?> updateProductWithDemo(@PathVariable("productId") Long productId,
			@RequestBody DemoProduct updatedProduct) {
		try {
			Optional<DemoProduct> existingProductOptional = demoProductService.findProductById(productId);
//			existingProductOptional.setup(new Date(System.currentTimeMillis()));
			if (existingProductOptional.isPresent()) {
				DemoProduct existingProduct = existingProductOptional.get();

				if (updatedProduct.getProductName() != null) {
					existingProduct.setProductName(updatedProduct.getProductName());
				}

				List<DemoProductImages> updatedImages = updatedProduct.getDemoProductImages();
				if (updatedImages != null && !updatedImages.isEmpty()) {
					for (DemoProductImages updatedImage : updatedImages) {
						Long demoProImagesId = updatedImage.getDemoProImagesId();
						System.out.println("demoProImagesId: " + demoProImagesId);

						if (updatedImage.isDeleted()) {
							markImageAsDeleted(existingProduct, demoProImagesId);
						} else {
							updateOrAddImage(existingProduct, updatedImage);
						}
					}
				}

				demoProductService.SaveProductDetails(existingProduct);

				return ResponseEntity.ok(existingProduct);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Demo Product not found with id: " + productId);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating demo product: " + e.getMessage());
		}
	}

	private void markImageAsDeleted(DemoProduct existingProduct, Long demoProImagesId) {
		List<DemoProductImages> productImages = existingProduct.getDemoProductImages();
		productImages.forEach(image -> {
			if (demoProImagesId.equals(image.getDemoProImagesId())) {
				image.setDeleted(true);
			}
		});
	}

	private void updateOrAddImage(DemoProduct existingProduct, DemoProductImages updatedImage) {
		Long demoProImagesId = updatedImage.getDemoProImagesId();
		if (demoProImagesId != null) {
			updateProductImagePatch(updatedImage, demoProImagesId);
		} else {
			DemoProductImages newImage = createNewProductImage(updatedImage);
			existingProduct.getDemoProductImages().add(newImage);
		}
	}

	private void updateProductImagePatch(DemoProductImages updatedImage, Long demoProImagesId) {
		Optional<DemoProductImages> existingImageOptional = demoProductService.findProductImageById(demoProImagesId);

		existingImageOptional.ifPresent(existingImage -> {
			existingImage.setUrl(updatedImage.getUrl());
			setBlobFromBase64(existingImage, updatedImage.getUrl());
			demoProductImageRepository.save(existingImage);
		});
	}

	private DemoProductImages createNewProductImage(DemoProductImages updatedImage) {
		DemoProductImages newImage = new DemoProductImages();
		newImage.setUrl(updatedImage.getUrl());
		setBlobFromBase64(newImage, updatedImage.getUrl());
		return newImage;
	}

	private void setBlobFromBase64(DemoProductImages productImages, String base64Image) {
		try {
			byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
			productImages.setFile(blob);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/demoo")
	public ResponseEntity<?> getAll() {
		List<Map<String, Object>> mainList = new ArrayList<>();
		List<Map<String, Object>> demoDetailsList = demoProductImageRepository.getAllDemoDetails();

		Map<String, Map<String, List<Map<String, Object>>>> demoDetailsGroupMap = demoDetailsList.stream()
				.collect(Collectors.groupingBy(action -> action.get("demo_id").toString(), Collectors
						.groupingBy(action -> action.get("demo_pro_images_id").toString(), Collectors.toList())));

		for (Entry<String, Map<String, List<Map<String, Object>>>> demoEntry : demoDetailsGroupMap.entrySet()) {
			Map<String, Object> demoMap = new HashMap<>();
			demoMap.put("demoId", demoEntry.getKey());

			List<Map<String, Object>> subList = new ArrayList<>();
			for (Entry<String, List<Map<String, Object>>> proEntry : demoEntry.getValue().entrySet()) {
				Map<String, Object> proMap = createProMapFromDemoDetails(proEntry);
				demoMap.put("productName", proEntry.getValue().get(0).get("product_name"));
				subList.add(proMap);
			}

			demoMap.put("demoProductImages", subList);
			mainList.add(demoMap);
		}

		return ResponseEntity.ok(mainList);
	}

	private Map<String, Object> createProMapFromDemoDetails(Entry<String, List<Map<String, Object>>> proEntry) {
		Map<String, Object> proMap = new HashMap<>();
		proMap.put("demoProImagesId", proEntry.getKey());

		int randomNumber = generateRandomNumber();
		String fileExtension = getFileExtensionForImage(proEntry.getValue());
		String imageUrl = "demo/" + randomNumber + "/" + proEntry.getKey() + "." + fileExtension;
		proMap.put("url", imageUrl);

		return proMap;
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	private String getFileExtensionForImage(List<Map<String, Object>> demoDetails) {
		Map<String, Object> imageDetails = demoDetails.get(0);
		if (imageDetails == null || imageDetails.get("url") == null || imageDetails.get("url").toString().isEmpty()) {
			return "jpg";
		}
		String url = imageDetails.get("url").toString();
		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}

	@GetMapping("demo/{randomNumber}/{id:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		String fileExtension = parts[1];

		Long imageId;
		try {
			imageId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}

		DemoProductImages image = demoProductService.getId(imageId);
		if (image == null) {
			return ResponseEntity.notFound().build();
		}

		byte[] imageBytes;
		try {
			imageBytes = image.getFile().getBytes(1, (int) image.getFile().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		if ("jpg".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if ("png".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else {

			headers.setContentType(MediaType.IMAGE_JPEG);
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	private String determineFileExtension(byte[] fileBytes) {
		try {
			String fileSignature = bytesToHex(Arrays.copyOfRange(fileBytes, 0, 4));
			if (fileSignature.startsWith("89504E47")) {
				return "png";
			} else if (fileSignature.startsWith("FFD8FF")) {
				return "jpg";
			} else if (fileSignature.startsWith("52494646")) {
				return "webp";
			} else if (fileSignature.startsWith("47494638")) {
				return "gif";
			} else if (fileSignature.startsWith("66747970") || fileSignature.startsWith("00000020")) {
				return "mp4";
			} else if (fileSignature.startsWith("25504446")) {
				return "pdf";
			}
		} catch (Exception e) {
		}
		return "unknown";
	}

	private MediaType determineMediaType(String extension) {
		switch (extension) {
		case "png":
			return MediaType.IMAGE_PNG;
		case "jpg":
			return MediaType.IMAGE_JPEG;
		case "pdf":
			return MediaType.APPLICATION_PDF;
		case "webp":
			return MediaType.parseMediaType("image/webp");
		case "gif":
			return MediaType.parseMediaType("image/gif");
		case "mp4":
			return MediaType.parseMediaType("video/mp4");
		default:
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

}
