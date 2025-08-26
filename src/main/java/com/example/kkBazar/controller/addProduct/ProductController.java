package com.example.kkBazar.controller.addProduct;

import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.example.kkBazar.entity.product.Product;
import com.example.kkBazar.entity.product.ProductImages;
import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.product.ProductVarient;
import com.example.kkBazar.entity.product.ProductVarientImages;
import com.example.kkBazar.repository.addProduct.ProductRepository;
import com.example.kkBazar.service.addProduct.ProductImagesService;
import com.example.kkBazar.service.addProduct.ProductListService;
import com.example.kkBazar.service.addProduct.ProductService;
import com.example.kkBazar.service.addProduct.ProductVarientImageService;
import com.example.kkBazar.service.addProduct.ProductVarientService;

@RestController
@CrossOrigin
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductImagesService productImagesService;
	@Autowired
	private ProductListService productListService;
	@Autowired
	private ProductVarientImageService productVarientImageService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductVarientService productVarientService;

	@PostMapping("/product/save")
	public ResponseEntity<?> saveProductWithDemo(@RequestBody Product product) {
		try {
			product.setCreatedAt(new Date(System.currentTimeMillis()));
			List<ProductImages> productImages = product.getProductImages();
			List<ProductList> productList = product.getProductList();

			for (ProductImages productLoop : productImages) {
				for (ProductList productItem : productList) {

					double quantity = productItem.getQuantity();
					productItem.setStockIn(quantity);

					String returnType = productItem.getReturnType();
					if (returnType.equals("yes")) {
						productItem.setReturnStatus(true);
					} else if (returnType.equals("no")) {
						productItem.setReturnStatus(false);
						productItem.setReturnCount(0);
					}

					List<ProductVarientImages> varientImages = productItem.getVarientImages();
					if (varientImages != null) {
					    for (ProductVarientImages varientImage : varientImages) {
					        String base64Image = varientImage.getProductVarientImageUrl();
					        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
					        Blob blob = null;
					        try {
					            blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
					        } catch (SQLException e) {
					            e.printStackTrace();
					        }
					        varientImage.setProductVarientImage(blob);
					    }
					} 

				}

				String base64Image = productLoop.getProductImagesUploadUrl();
				byte[] imageBytes = Base64.getDecoder().decode(base64Image);
				Blob blob = null;
				try {
					blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				productLoop.setProductImagesUpload(blob);
			}

			productService.SaveProductDetails(product);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving product: " + e.getMessage());
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("product/{randomNumber}/{id}")
	public ResponseEntity<ByteArrayResource> serveFile(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") Long id) {
		Optional<ProductImages> productVarientImagesOptional = productImagesService.getById1(id);

		if (productVarientImagesOptional.isPresent()) {
			ProductImages productVarientImages = productVarientImagesOptional.get();

			if (productVarientImages.getProductImagesUploadUrl() == null) {
				return ResponseEntity.notFound().build();
			}

			try {
				byte[] fileBytes = Base64.getDecoder().decode(productVarientImages.getProductImagesUploadUrl());

				String filename = "file_" + randomNumber + "_" + id;
				String extension = determineFileExtension(fileBytes);
				MediaType mediaType = determineMediaType(extension);

				ByteArrayResource resource = new ByteArrayResource(fileBytes);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(mediaType);
				headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + "." + extension);

				return ResponseEntity.ok().headers(headers).body(resource);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	   @GetMapping("varient/{randomNumber}/{id}")
	    public ResponseEntity<ByteArrayResource> serveToSave(@PathVariable("randomNumber") int randomNumber,@PathVariable("id") Long id) {
	        Optional<ProductVarientImages> productVarientImagesOptional = productVarientImageService.getById1(id);

	        if (productVarientImagesOptional.isPresent()) {
	            ProductVarientImages productVarientImages = productVarientImagesOptional.get();
	            
	            if (productVarientImages.getProductVarientImageUrl() == null) {
	                return ResponseEntity.notFound().build();
	            }

	            try {
	                byte[] fileBytes = Base64.getDecoder().decode(productVarientImages.getProductVarientImageUrl());

	                String filename = "file_" + randomNumber + "_" + id;
	                String extension = determineFileExtension(fileBytes);
	                MediaType mediaType = determineMediaType(extension);

	                ByteArrayResource resource = new ByteArrayResource(fileBytes);
	                HttpHeaders headers = new HttpHeaders();
	                headers.setContentType(mediaType);
	                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + "." + extension);

	                return ResponseEntity.ok().headers(headers).body(resource);
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    }

	   private String determineFileExtension(byte[] fileBytes) {
		    try {
		        String fileSignature = bytesToHex(Arrays.copyOfRange(fileBytes, 0, 8)); // Adjust length based on the AVIF signature length
		        if (fileSignature.startsWith("89504E47")) {
		            return "png";
		        } else if (fileSignature.startsWith("FFD8FF")) {
		            return "jpg";
		        } else if (fileSignature.startsWith("52494646A00C0000")) {
		            return "avif";
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
		        case "avif":  // Add AVIF case
		            return MediaType.parseMediaType("image/avif");
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

	@DeleteMapping("/product/delete/{productId}")
	public ResponseEntity<String> deleteProductId(@PathVariable("productId") Long productId) {
		productService.deleteProductId(productId);
		return ResponseEntity.ok("Product deleted successfully");

	}

	@PutMapping("/product/patch/{productId}")
	public ResponseEntity<?> updateProductd(@PathVariable("productId") Long productId,
			@RequestBody Product updatedProduct) {
		try {
			Optional<Product> existingProductOptional = productService.findProductById(productId);
		
			if (existingProductOptional.isPresent()) {
				Product existingProduct = existingProductOptional.get();
				existingProduct.setUpdatedAt(new Date(System.currentTimeMillis()));
				if (updatedProduct.getProductName() != null) {
					existingProduct.setProductName(updatedProduct.getProductName());
				}
				if (updatedProduct.getBrandId() != 0) {
					existingProduct.setBrandId(updatedProduct.getBrandId());
				}
				if (updatedProduct.getCategoryId() != 0) {
					existingProduct.setCategoryId(updatedProduct.getCategoryId());
				}
				if (updatedProduct.getDescription() != null) {
					existingProduct.setDescription(updatedProduct.getDescription());
				}

				List<ProductImages> updatedImages = updatedProduct.getProductImages();
				if (updatedImages != null && !updatedImages.isEmpty()) {
					for (ProductImages updatedImage : updatedImages) {
						Long productImagesId = updatedImage.getProductImagesId();
						if (Objects.nonNull(productImagesId)) {

							System.out.println("productImagesId: " + productImagesId);

							Optional<ProductImages> existingProductImagesOptional = productImagesService
									.findProductImagesById(productImagesId);

							if (existingProductImagesOptional.isPresent()) {
								ProductImages existingProductImages = existingProductImagesOptional.get();

								if (updatedImage.getProductImagesUploadUrl() != null) {
									existingProductImages
											.setProductImagesUploadUrl(updatedImage.getProductImagesUploadUrl());
								}
								if (updatedImage.isDeleted() != false) {
									existingProductImages.setDeleted(updatedImage.isDeleted());
								}

								if (updatedImage.getProductImagesUpload() != null) {
									existingProductImages.setProductImagesUpload(updatedImage.getProductImagesUpload());
								}

								String base64Image = updatedImage.getProductImagesUploadUrl();

								if (base64Image != null) {
									byte[] imageBytes = Base64.getDecoder().decode(base64Image);
									Blob blob = null;
									try {
										blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									existingProductImages.setProductImagesUpload(blob);
								} else {
									System.out.println("Error: getProductImagesUploadUrl() returned null.");
								}

							}
						} else {
							List<ProductImages> productImages = existingProduct.getProductImages();

							for (ProductImages productLoop : productImages) {
								String base64Image = productLoop.getProductImagesUploadUrl();

								if (base64Image != null) {
									byte[] imageBytes = Base64.getDecoder().decode(base64Image);
									Blob blob = null;

									try {
										blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
									} catch (SQLException e) {
										e.printStackTrace();
									}

									updatedImage.setProductImagesUpload(blob);
								}
							}

							existingProduct.getProductImages().add(updatedImage);
						}

					}
				}

				List<ProductList> updatedProductList = updatedProduct.getProductList();
				if (updatedProductList != null && !updatedProductList.isEmpty()) {
					for (ProductList updatedItem : updatedProductList) {
						Long productListId = updatedItem.getProductListId();
						if (Objects.nonNull(productListId)) {
							Optional<ProductList> existingProductListOptional = productListService
									.findProductListById(productListId);

							if (existingProductListOptional.isPresent()) {
								ProductList existingProductList = existingProductListOptional.get();

								if (updatedItem.getMrp() != 0) {
									existingProductList.setMrp(updatedItem.getMrp());
								}
								if (updatedItem.getBuyRate() != 0) {
									existingProductList.setBuyRate(updatedItem.getBuyRate());
								}
								if (updatedItem.getSellRate() != 0) {
									existingProductList.setSellRate(updatedItem.getSellRate());
								}
								if (updatedItem.getDiscountAmount() != 0) {
									existingProductList.setDiscountAmount(updatedItem.getDiscountAmount());
								}
								if (updatedItem.getDiscountPercentage() != 0) {
									existingProductList.setDiscountPercentage(updatedItem.getDiscountPercentage());
								}
								if (updatedItem.getGst() != 0) {
									existingProductList.setGst(updatedItem.getGst());
								}
								if (updatedItem.getGstTaxAmount() != 0) {
									existingProductList.setGstTaxAmount(updatedItem.getGstTaxAmount());
								}

								if (updatedItem.getUnit() != null) {
									existingProductList.setUnit(updatedItem.getUnit());
								}

//								if (updatedItem.getDescription() != null) {
//									existingProductList.setDescription(updatedItem.getDescription());
//								}
								Long sizeId = updatedItem.getSizeId();
								if (sizeId != null && sizeId != 0) {
								    existingProductList.setSizeId(sizeId);
								}


								if (updatedItem.getAlertQuantity() != 0) {
									existingProductList.setAlertQuantity(updatedItem.getAlertQuantity());
								}
								if (updatedItem.getQuantity() != 0) {
									existingProductList.setQuantity(updatedItem.getQuantity());
								}
								if (updatedItem.getTotalAmount() != 0) {
									existingProductList.setTotalAmount(updatedItem.getTotalAmount());
								}
								if (updatedItem.isDeleted() != false) {
									existingProductList.setDeleted(updatedItem.isDeleted());
								}
								List<ProductVarient> productVarient = updatedItem.getVarientList();
								if (Objects.nonNull(productVarient)) {
									for (ProductVarient updatedProductVarient : productVarient) {
										Long productVarientId = updatedProductVarient.getProductVarientId();
										if (Objects.nonNull(productVarientId)) {
											Optional<ProductVarient> existingProductVarientOptional = productVarientService
													.findProductVarientById(productVarientId);
											if (existingProductVarientOptional.isPresent()) {
												ProductVarient existingProductVarient = existingProductVarientOptional
														.get();
												if (updatedProductVarient.getVarientName() != null) {
													existingProductVarient
															.setVarientName(updatedProductVarient.getVarientName());
												}
																			
											}
										} else {
											existingProductList.getVarientList().add(updatedProductVarient);
										}
									}

								}
								List<ProductVarientImages> productVarientImages = updatedItem.getVarientImages();
								if (Objects.nonNull(productVarientImages)) {
									for (ProductVarientImages updatedProductVarientImages : productVarientImages) {

										Long productVarientImagesId = updatedProductVarientImages
												.getProductVarientImagesId();
										if (Objects.nonNull(productVarientImagesId)) {
											Optional<ProductVarientImages> existingProductVarientImagesOptional = productVarientImageService
													.findProductVarientImagesById(productVarientImagesId);

											if (existingProductVarientImagesOptional.isPresent()) {
												ProductVarientImages existingProductVarientImages = existingProductVarientImagesOptional
														.get();
												if (updatedProductVarientImages.getProductVarientImageUrl() != null) {
													existingProductVarientImages.setProductVarientImageUrl(
															updatedProductVarientImages.getProductVarientImageUrl());
												}
												if (updatedProductVarientImages.isDeleted() != false) {
													existingProductVarientImages
															.setDeleted(updatedProductVarientImages.isDeleted());
												}

												if (updatedProductVarientImages.getProductVarientImage() != null) {
													existingProductVarientImages.setProductVarientImage(
															updatedProductVarientImages.getProductVarientImage());
												}
												String base64Image = updatedProductVarientImages
														.getProductVarientImageUrl();

												if (base64Image != null) {
													byte[] imageBytes = Base64.getDecoder().decode(base64Image);
													Blob blob = null;
													try {
														blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
													} catch (SQLException e) {
														e.printStackTrace();
													}
													existingProductVarientImages.setProductVarientImage(blob);
												} else {
													System.out.println(
															"Error: getProductVarientImageUrl() returned null.");
												}

											}

										} else {

											String base64Image = updatedProductVarientImages
													.getProductVarientImageUrl();
											byte[] imageBytes = Base64.getDecoder().decode(base64Image);
											Blob blob = null;
											try {
												blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
											} catch (SQLException e) {
												e.printStackTrace();
											}
											updatedProductVarientImages.setProductVarientImage(blob);
											existingProductList.getVarientImages().add(updatedProductVarientImages);
										}
									}
								}
							}
						} else {
							existingProduct.getProductList().add(updatedItem);
						}
					}
				}

				productService.SaveProductDetails(existingProduct);

				return ResponseEntity.ok(existingProduct);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + productId);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating product: " + e.getMessage());
		}
	}

	@GetMapping("/product/views")
	public ResponseEntity<Object> getAllProducts(@RequestParam(required = true) String product) {
		if ("productDetails".equals(product)) {
			List<Map<String, Object>> mainProductList = new ArrayList<>();
			List<Map<String, Object>> productRole = productRepository.getAllCategoryWithProductDetails();
			Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productGroupMap = productRole
			        .stream()
			        .collect(Collectors.groupingBy(
			                action -> String.valueOf(action.getOrDefault("product_id", "UNKNOWN")), 
			                Collectors.groupingBy(
			                        action -> String.valueOf(action.getOrDefault("product_list_id", "UNKNOWN")),
			                        Collectors.groupingBy(
			                                action -> String.valueOf(action.getOrDefault("product_varient_id", "UNKNOWN")),
			                                Collectors.groupingBy(
			                                        action -> String.valueOf(action.getOrDefault("product_varient_images_id", "UNKNOWN")),
			                                        Collectors.groupingBy(
			                                                action -> String.valueOf(action.getOrDefault("product_images_id", "UNKNOWN"))
			                                        )
			                                )
			                        )
			                )
			        ));


			for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : productGroupMap
					.entrySet()) {
				Map<String, Object> productMap = new HashMap<>();
				int productId = Integer.parseInt(productLoop.getKey());
				productMap.put("productId", productId);

				List<Map<String, Object>> productList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
						.getValue().entrySet()) {
					Map<String, Object> productListMap = new HashMap<>();
					int productListId = Integer.parseInt(productListLoop.getKey());
					productListMap.put("productListId", productListId);

					List<Map<String, Object>> productVarientList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productVarientLoop : productListLoop
							.getValue().entrySet()) {
						Map<String, Object> productVarientMap = new HashMap<>();
//						int productVarientId = Integer.parseInt(productVarientLoop.getKey());
//						productVarientMap.put("productVarientId", productVarientId);
						String key = productVarientLoop.getKey();
						int productVarientId;

						try {
						    if (key == null || key.equals("null")) {
						        // Handle the case where the key is "null" or actually null
						        productVarientId = -1; // Use a default value as appropriate
						    } else {
						        productVarientId = Integer.parseInt(key);
						    }
						} catch (NumberFormatException e) {
						    // Handle the case where the key is not a valid integer
						    productVarientId = -1; // Use a default value as appropriate
						}

						// Put the result into the map
						productVarientMap.put("productVarientId", productVarientId);

						List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
						for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientImagesLoop : productVarientLoop
								.getValue().entrySet()) {
							Map<String, Object> productVarientImageMap = new HashMap<>();
//							int productVarientImagesId = Integer.parseInt(productVarientImagesLoop.getKey());
//							productVarientImageMap.put("productVarientImagesId", productVarientImagesId);
							String key1 = productVarientImagesLoop.getKey();
							int productVarientImagesId;

							if (key1 == null || key1.equals("null")) {
							    // Handle the case where the key is "null" or actually null
							    productVarientImagesId = -1; // or any other default value you deem appropriate
							} else {
							    try {
							        productVarientImagesId = Integer.parseInt(key1);
							    } catch (NumberFormatException e) {
							        // Handle the case where the key is not a valid integer
							        productVarientImagesId = -1; // or any other default value you deem appropriate
							    }
							}

							productVarientImageMap.put("productVarientImagesId", productVarientImagesId);

							List<Map<String, Object>> productImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> productImageLoop : productVarientImagesLoop
									.getValue().entrySet()) {
								Map<String, Object> productImagesMap = new HashMap<>();
								int productImagesId = Integer.parseInt(productImageLoop.getKey());
								productImagesMap.put("productImagesId", productImagesId);
								productMap.put("brandId", productImageLoop.getValue().get(0).get("brand_id"));
								productMap.put("description",productImageLoop.getValue().get(0).get("description"));
								productMap.put("brandName", productImageLoop.getValue().get(0).get("brand_name"));
								productMap.put("productName", productImageLoop.getValue().get(0).get("product_name"));
								productMap.put("categoryName", productImageLoop.getValue().get(0).get("category_name"));
								productMap.put("categoryName", productImageLoop.getValue().get(0).get("category_name"));
								productMap.put("categoryId", productImageLoop.getValue().get(0).get("category_id"));
								productListMap.put("mrp", productImageLoop.getValue().get(0).get("mrp"));
								productListMap.put("buyRate", productImageLoop.getValue().get(0).get("buy_rate"));
								productListMap.put("sellRate", productImageLoop.getValue().get(0).get("sell_rate"));
								productListMap.put("discountPercentage",
										productImageLoop.getValue().get(0).get("discount_percentage"));
								productListMap.put("discountAmount",
										productImageLoop.getValue().get(0).get("discount_amount"));
								productListMap.put("gst", productImageLoop.getValue().get(0).get("gst"));
								productListMap.put("weight", productImageLoop.getValue().get(0).get("weight"));
								productListMap.put("pieces", productImageLoop.getValue().get(0).get("pieces"));
								productListMap.put("gstTaxAmount",
										productImageLoop.getValue().get(0).get("gst_tax_amount"));
								productListMap.put("totalAmount",
										productImageLoop.getValue().get(0).get("total_amount"));
								productListMap.put("quantity", productImageLoop.getValue().get(0).get("quantity"));
								productListMap.put("varientName",
										productImageLoop.getValue().get(0).get("varient_name"));				
								productListMap.put("unit", productImageLoop.getValue().get(0).get("unit"));
								productListMap.put("alertQuantity",
										productImageLoop.getValue().get(0).get("alert_quantity"));

								productListMap.put("returnType", productImageLoop.getValue().get(0).get("return_type"));
								productListMap.put("returnCount",
										productImageLoop.getValue().get(0).get("return_count"));
								productListMap.put("returnStatus",
										productImageLoop.getValue().get(0).get("return_status"));
								productListMap.put("sizeId", productImageLoop.getValue().get(0).get("size_id"));							
								productListMap.put("sizeName", productImageLoop.getValue().get(0).get("size_name"));
//								productListMap.put("brandId", productImageLoop.getValue().get(0).get("brand_id"));							
//								productListMap.put("brandName", productImageLoop.getValue().get(0).get("brand_name"));
//								productListMap.put("description",
//										productImageLoop.getValue().get(0).get("description"));
								BigInteger categoryIdBigInt = (BigInteger) productImageLoop.getValue().get(0)
										.get("category_id");
								long categoryId = categoryIdBigInt.longValue();

								List<Map<String, Object>> productDetails = productImageLoop.getValue();

								int randomNumber = generateRandomNumber();
								String fileExtension = getFileExtensionForImage(productDetails);
								String imageUrl = "category/" + randomNumber + "/" + categoryId + "." + fileExtension;
								productMap.put("url", imageUrl);

								String productImageUrl = "product/" + randomNumber + "/" + productImagesId;

								String productVarientImageUrl = "" + "varient/" + randomNumber + "/"
										+ productVarientImagesId;

								productVarientImageMap.put("productVarientImageUrl", productVarientImageUrl);
								productImagesMap.put("productImagesUploadUrl", productImageUrl);

								productImagesList.add(productImagesMap);
								productMap.put("productImages", productImagesList);
							}

							productVarientImagesList.add(productVarientImageMap);
						}
	//					productVarientList.add(productVarientMap);
//						productListMap.put("varientImages", productVarientImagesList);
					}
//					productListMap.put("varientList", productVarientList);
					productList.add(productListMap);
				}
				productMap.put("productList", productList);
				mainProductList.add(productMap);
			}
			mainProductList.sort(Comparator.comparing(productData -> {
				Object productIdObj = productData.get("productId");
				return productIdObj != null ? productIdObj.toString() : "";
			}));
			Collections.reverse(mainProductList);
			return ResponseEntity.ok(mainProductList);
		} else {
			String errorMessage = "Invalid value for 'department'. Expected 'Department'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/product/views/{id}")
	public ResponseEntity<Object> getAllProductsWithId(@PathVariable("id") Long productId,
			@RequestParam(required = false) Long userId) {
		List<Map<String, Object>> mainProductList = new ArrayList<>();
		List<Map<String, Object>> productRole = productRepository.getAllCategoryWithProductDetailsWithId(productId);
		Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productGroupMap = productRole
		        .stream()
		        .collect(Collectors.groupingBy(
		                action -> String.valueOf(action.getOrDefault("product_id", "UNKNOWN")), 
		                Collectors.groupingBy(
		                        action -> String.valueOf(action.getOrDefault("product_list_id", "UNKNOWN")),
		                        Collectors.groupingBy(
		                                action -> String.valueOf(action.getOrDefault("product_varient_id", "UNKNOWN")),
		                                Collectors.groupingBy(
		                                        action -> String.valueOf(action.getOrDefault("product_varient_images_id", "UNKNOWN")),
		                                        Collectors.groupingBy(
		                                                action -> String.valueOf(action.getOrDefault("product_images_id", "UNKNOWN"))
		                                        )
		                                )
		                        )
		                )
		        ));


		for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : productGroupMap
				.entrySet()) {
			Map<String, Object> productMap = new HashMap<>();
			int productId1 = Integer.parseInt(productLoop.getKey());
			productMap.put("productId", productId1);

			List<Map<String, Object>> productList = new ArrayList<>();
			for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
					.getValue().entrySet()) {
				Map<String, Object> productListMap = new HashMap<>();
				int productListId = Integer.parseInt(productListLoop.getKey());
				productListMap.put("productListId", productListId);

				List<Map<String, Object>> productVarientList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productVarientLoop : productListLoop
						.getValue().entrySet()) {
					Map<String, Object> productVarientMap = new HashMap<>();
//					int productVarientId = Integer.parseInt(productVarientLoop.getKey());
//					productVarientMap.put("productVarientId", productVarientId);

					String key = productVarientLoop.getKey();
					int productVarientId;

					try {
					    if (key == null || key.equals("null")) {
					        // Handle the case where the key is "null" or actually null
					        productVarientId = -1; // Use a default value as appropriate
					    } else {
					        productVarientId = Integer.parseInt(key);
					    }
					} catch (NumberFormatException e) {
					    // Handle the case where the key is not a valid integer
					    productVarientId = -1; // Use a default value as appropriate
					}

					// Put the result into the map
					productVarientMap.put("productVarientId", productVarientId);
					List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
					for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientImagesLoop : productVarientLoop
							.getValue().entrySet()) {
						Map<String, Object> productVarientImageMap = new HashMap<>();
//						int productVarientImagesId = Integer.parseInt(productVarientImagesLoop.getKey());
//						productVarientImageMap.put("productVarientImagesId", productVarientImagesId);
						String key1 = productVarientImagesLoop.getKey();
						int productVarientImagesId;

						if (key1 == null || key1.equals("null")) {
						    // Handle the case where the key is "null" or actually null
						    productVarientImagesId = -1; // or any other default value you deem appropriate
						} else {
						    try {
						        productVarientImagesId = Integer.parseInt(key1);
						    } catch (NumberFormatException e) {
						        // Handle the case where the key is not a valid integer
						        productVarientImagesId = -1; // or any other default value you deem appropriate
						    }
						}

						productVarientImageMap.put("productVarientImagesId", productVarientImagesId);

						List<Map<String, Object>> productImagesList = new ArrayList<>();
						for (Entry<String, List<Map<String, Object>>> productImageLoop : productVarientImagesLoop
								.getValue().entrySet()) {
							Map<String, Object> productImagesMap = new HashMap<>();
							int productImagesId = Integer.parseInt(productImageLoop.getKey());
							productImagesMap.put("productImagesId", productImagesId);
							productMap.put("brandId", productImageLoop.getValue().get(0).get("brand_id"));
							productMap.put("description",productImageLoop.getValue().get(0).get("description"));
							productMap.put("brandName", productImageLoop.getValue().get(0).get("brand_name"));
							productMap.put("productName", productImageLoop.getValue().get(0).get("product_name"));
							productMap.put("categoryName", productImageLoop.getValue().get(0).get("category_name"));
							productMap.put("categoryName", productImageLoop.getValue().get(0).get("category_name"));
							productMap.put("categoryId", productImageLoop.getValue().get(0).get("category_id"));
							productListMap.put("mrp", productImageLoop.getValue().get(0).get("mrp"));
							productListMap.put("buyRate", productImageLoop.getValue().get(0).get("buy_rate"));
							productListMap.put("sellRate", productImageLoop.getValue().get(0).get("sell_rate"));
							productListMap.put("discountPercentage",
									productImageLoop.getValue().get(0).get("discount_percentage"));
							productListMap.put("discountAmount",
									productImageLoop.getValue().get(0).get("discount_amount"));
							productListMap.put("gst", productImageLoop.getValue().get(0).get("gst"));
							productListMap.put("weight", productImageLoop.getValue().get(0).get("weight"));
							productListMap.put("pieces", productImageLoop.getValue().get(0).get("pieces"));
							productListMap.put("gstTaxAmount",
									productImageLoop.getValue().get(0).get("gst_tax_amount"));
							productListMap.put("totalAmount",
									productImageLoop.getValue().get(0).get("total_amount"));
							productListMap.put("quantity", productImageLoop.getValue().get(0).get("quantity"));
							productListMap.put("varientName",
									productImageLoop.getValue().get(0).get("varient_name"));				
							productListMap.put("unit", productImageLoop.getValue().get(0).get("unit"));
							productListMap.put("alertQuantity",
									productImageLoop.getValue().get(0).get("alert_quantity"));

							productListMap.put("returnType", productImageLoop.getValue().get(0).get("return_type"));
							productListMap.put("returnCount",
									productImageLoop.getValue().get(0).get("return_count"));
							productListMap.put("returnStatus",
									productImageLoop.getValue().get(0).get("return_status"));
							productListMap.put("sizeId", productImageLoop.getValue().get(0).get("size_id"));							
							productListMap.put("sizeName", productImageLoop.getValue().get(0).get("size_name"));
//							productListMap.put("brandId", productImageLoop.getValue().get(0).get("brand_id"));							
//							productListMap.put("brandName", productImageLoop.getValue().get(0).get("brand_name"));
//							productListMap.put("description",
//									productImageLoop.getValue().get(0).get("description"));
							BigInteger categoryIdBigInt = (BigInteger) productImageLoop.getValue().get(0)
									.get("category_id");
							long categoryId = categoryIdBigInt.longValue();

							List<Map<String, Object>> productDetails = productImageLoop.getValue();

							int randomNumber = generateRandomNumber();
							String fileExtension = getFileExtensionForImage(productDetails);
							String imageUrl = "category/" + randomNumber + "/" + categoryId + "." + fileExtension;
							productMap.put("url", imageUrl);

							String productImageUrl = "product/" + randomNumber + "/" + productImagesId;

							String productVarientImageUrl = "" + "varient/" + randomNumber + "/"
									+ productVarientImagesId;

							productVarientImageMap.put("productVarientImageUrl", productVarientImageUrl);
							productImagesMap.put("productImagesUploadUrl", productImageUrl);

							productImagesList.add(productImagesMap);
							productMap.put("productImages", productImagesList);
						}

						productVarientImagesList.add(productVarientImageMap);
					}
//					productVarientList.add(productVarientMap);
//					productListMap.put("varientImages", productVarientImagesList);
				}
//				productListMap.put("varientList", productVarientList);
				productList.add(productListMap);
			}
			productMap.put("productList", productList);
//			mainProductList.add(productMap);
			return ResponseEntity.ok(productMap);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");

	}

	@GetMapping("/product/category/view")
	public ResponseEntity<Object> getAllRoleByEmployee(@RequestParam(required = true) String category,
			@RequestParam(required = false) Long userId) {
		if ("categoryDetails".equals(category)) {
			List<Map<String, Object>> mainCategoryList = new ArrayList<>();
			List<Map<String, Object>> categoryRole = productRepository.getAllCategoryWithProductDetails();
			Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = categoryRole
			        .stream()
			        .collect(Collectors.groupingBy(
			                action -> Objects.toString(action.get("category_id"), "UNKNOWN"),
			                Collectors.groupingBy(
			                        action -> Objects.toString(action.get("product_id"), "UNKNOWN"),
			                        Collectors.groupingBy(
			                                action -> Objects.toString(action.get("product_list_id"), "UNKNOWN"),
			                                Collectors.groupingBy(
			                                        action -> Objects.toString(action.get("product_varient_images_id"), "UNKNOWN"),
			                                        Collectors.groupingBy(
			                                                action -> Objects.toString(action.get("product_varient_id"), "UNKNOWN"),
			                                                Collectors.groupingBy(
			                                                        action -> Objects.toString(action.get("product_images_id"), "UNKNOWN")
			                                                )
			                                        )
			                                )
			                        )
			                )
			        ));


			for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
					.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();
				String categoryId1 = categoryLoop.getKey();

				categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

				List<Map<String, Object>> productList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
						.getValue().entrySet()) {
					Map<String, Object> productMap = new HashMap<>();

					int productId = Integer.parseInt(productLoop.getKey());
					productMap.put("productId", productId);

					List<Map<String, Object>> productSubList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						int productListId = Integer.parseInt(productListLoop.getKey());
						productListMap.put("productListId", productListId);

						List<Map<String, Object>> productImagesList = new ArrayList<>();
						for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> productImagesMap = new HashMap<>();
//							int productVarientImagesId = Integer.parseInt(productImagesLoop.getKey());
//							productImagesMap.put("productVarientImagesId", productVarientImagesId);
							
							String key = productImagesLoop.getKey();
							int productVarientImagesId;

							try {
							    if (key == null || key.equals("null")) {
							        // Handle the case where the key is "null" or actually null
							        productVarientImagesId = -1; // Use a default value as appropriate
							    } else {
							        productVarientImagesId = Integer.parseInt(key);
							    }
							} catch (NumberFormatException e) {
							    // Handle the case where the key is not a valid integer
							    productVarientImagesId = -1; // Use a default value as appropriate
							}

							// Put the result into the map
							productImagesMap.put("productVarientImagesId", productVarientImagesId);


							List<Map<String, Object>> productVarientList = new ArrayList<>();
							for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientMap = new HashMap<>();

//								int productVarientId = Integer.parseInt(productVarientLoop.getKey());
//								productVarientMap.put("productVarientId", productVarientId);
								String key1 = productVarientLoop.getKey();
								int productVarientId;
								try {							 
								    if (key1 == null || key1.equals("UNKNOWN")) {
								        productVarientId = -1;
								    } else {
								        productVarientId = Integer.parseInt(key1);
								    }
								} catch (NumberFormatException e) {							
								    productVarientId = -1; 
								}
								productVarientMap.put("productVarientId", productVarientId);
								List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
								for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
										.getValue().entrySet()) {
									Map<String, Object> productVarientImagesMap = new HashMap<>();

									int productImagesId1 = Integer.parseInt(productVarientImageLoop.getKey());
									productVarientImagesMap.put("productImagesId", productImagesId1);

									productListMap.put("productId",
											productVarientImageLoop.getValue().get(0).get("product_id"));
									productListMap.put("productName",
											productVarientImageLoop.getValue().get(0).get("product_name"));
									categoryMap.put("categoryName",
											productVarientImageLoop.getValue().get(0).get("category_name"));
									productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
									productListMap.put("buyRate",
											productVarientImageLoop.getValue().get(0).get("buy_rate"));
									productListMap.put("sellRate",
											productVarientImageLoop.getValue().get(0).get("sell_rate"));
									productListMap.put("discountPercentage",
											productVarientImageLoop.getValue().get(0).get("discount_percentage"));
									productListMap.put("sizeName",
											productVarientImageLoop.getValue().get(0).get("size_name"));
									productListMap.put("sizeId",
											productVarientImageLoop.getValue().get(0).get("size_id"));
									productListMap.put("weight",
											productVarientImageLoop.getValue().get(0).get("weight"));
									productListMap.put("pieces",
											productVarientImageLoop.getValue().get(0).get("pieces"));
									productListMap.put("description",
											productVarientImageLoop.getValue().get(0).get("description"));
									productListMap.put("alertQuantity",
											productVarientImageLoop.getValue().get(0).get("alert_quantity"));
									productListMap.put("discountAmount",
											productVarientImageLoop.getValue().get(0).get("discount_amount"));
									productListMap.put("gst", productVarientImageLoop.getValue().get(0).get("gst"));
									productListMap.put("gstTaxAmount",
											productVarientImageLoop.getValue().get(0).get("gst_tax_amount"));
									productListMap.put("totalAmount",
											productVarientImageLoop.getValue().get(0).get("total_amount"));

									productListMap.put("returnType",
											productVarientImageLoop.getValue().get(0).get("return_type"));
									productListMap.put("returnCount",
											productVarientImageLoop.getValue().get(0).get("return_count"));
									productListMap.put("returnStatus",
											productVarientImageLoop.getValue().get(0).get("return_status"));

									productListMap.put("quantity",
											productVarientImageLoop.getValue().get(0).get("quantity"));

									if (userId != null) {
										BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
										BigInteger userIdFromProductList = (BigInteger) productVarientImageLoop
												.getValue().get(0).get("user_id");

										if (userIdFromProductList != null
												&& userIdBigInteger.equals(userIdFromProductList)) {
											productListMap.put("wishListId",
													productVarientImageLoop.getValue().get(0).get("wish_list_id"));
											productListMap.put("userId", userIdFromProductList);
											productListMap.put("wishListStatus",
													productVarientImageLoop.getValue().get(0).get("wishListStatus"));
										} else {
											boolean status = false;
											productListMap.put("wishListStatus", status);
										}
									}

									productListMap.put("reviewCount",
											productVarientImageLoop.getValue().get(0).get("review_count"));
									productListMap.put("averageStarRate",
											productVarientImageLoop.getValue().get(0).get("average_star_rate"));
									productVarientMap.put("varientName",
											productVarientImageLoop.getValue().get(0).get("varient_name"));
									productListMap.put("unit", productVarientImageLoop.getValue().get(0).get("unit"));
									productVarientMap.put("varientValue",
											productVarientImageLoop.getValue().get(0).get("varient_value"));
									productMap.put("brandId",
											productVarientImageLoop.getValue().get(0).get("brand_id"));
									productMap.put("brandName",
											productVarientImageLoop.getValue().get(0).get("brand_name"));
//									productMap.put("sizeId",
//											productVarientImageLoop.getValue().get(0).get("size_id"));
//									productMap.put("sizeName",
//											productVarientImageLoop.getValue().get(0).get("size_name"));
									

									List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

									int randomNumber = generateRandomNumber();
									String fileExtension = getFileExtensionForImage(categoryDetails);
									String imageUrl = "category/" + randomNumber + "/" + categoryId1 + "."
											+ fileExtension;

									String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

									String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

									productMap.put("productImagesUploadUrl", productVarientImageUrl);
									productImagesMap.put("productVarientImageUrl", productImageUrl);

									categoryMap.put("url", imageUrl);

									productVarientImagesList.add(productVarientImagesMap);
								}
								productVarientList.add(productVarientMap);
//								productMap.put("productImages", productVarientImagesList);
							}

							productImagesList.add(productImagesMap);
//							productListMap.put("productVarientList", productVarientList);
//							productListMap.put("productVarientImagesList", productImagesList);
						}
						productSubList.add(productListMap);
					}
					productMap.put("productListDetails", productSubList);
					productList.add(productMap);

				}
				categoryMap.put("productDetails", productList);
				mainCategoryList.add(categoryMap);
			}

			return ResponseEntity.ok(mainCategoryList);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}

	@GetMapping("/product/category/view/{categoryId}")
	public ResponseEntity<Object> getAllRoleByEmployee1(
			@PathVariable(value = "categoryId", required = false) Long categoryId,
			@RequestParam(required = false) Long userId) {

		List<Map<String, Object>> categoryRole = productRepository
				.getAllCategoryWithProductDetailsWithCategoryById(categoryId);

		List<Map<String, Object>> categoryFinder = productRepository.getAllCtaegoryDetailsWithId(categoryId);

		if (categoryRole.isEmpty()) {
			Map<String, List<Map<String, Object>>> categoryFinderMap = categoryFinder.stream()
					.collect(Collectors.groupingBy(action -> action.get("category_id").toString()));

			for (Entry<String, List<Map<String, Object>>> categoryLoop : categoryFinderMap.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();
				String categoryIdStr = categoryLoop.getKey();
				categoryMap.put("categoryId", categoryIdStr);
				categoryMap.put("categoryName", categoryLoop.getValue().get(0).get("category_name"));

				int randomNumber = generateRandomNumber();
				String imageUrl = "category/" + randomNumber + "/" + categoryIdStr + ".jpg";
				categoryMap.put("url", imageUrl);

				categoryMap.put("productDetails", Collections.emptyList());

				return ResponseEntity.ok(categoryMap);
			}
		} else {

			Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = categoryRole
			        .stream()
			        .collect(Collectors.groupingBy(
			                action -> Objects.toString(action.get("category_id"), "UNKNOWN"),
			                Collectors.groupingBy(
			                        action -> Objects.toString(action.get("product_id"), "UNKNOWN"),
			                        Collectors.groupingBy(
			                                action -> Objects.toString(action.get("product_list_id"), "UNKNOWN"),
			                                Collectors.groupingBy(
			                                        action -> Objects.toString(action.get("product_varient_images_id"), "UNKNOWN"),
			                                        Collectors.groupingBy(
			                                                action -> Objects.toString(action.get("product_varient_id"), "UNKNOWN"),
			                                                Collectors.groupingBy(
			                                                        action -> Objects.toString(action.get("product_images_id"), "UNKNOWN")
			                                                )
			                                        )
			                                )
			                        )
			                )
			        ));


			for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
					.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();
				String categoryId1 = categoryLoop.getKey();

				categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

				List<Map<String, Object>> productList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
						.getValue().entrySet()) {
					Map<String, Object> productMap = new HashMap<>();

					int productId = Integer.parseInt(productLoop.getKey());
					productMap.put("productId", productId);

					List<Map<String, Object>> productSubList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						int productListId = Integer.parseInt(productListLoop.getKey());
						productListMap.put("productListId", productListId);

						List<Map<String, Object>> productImagesList = new ArrayList<>();
						for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> productImagesMap = new HashMap<>();
//							int productVarientImagesId = Integer.parseInt(productImagesLoop.getKey());
//							productImagesMap.put("productVarientImagesId", productVarientImagesId);

							String key = productImagesLoop.getKey();
							int productVarientImagesId;
							try {
							    if (key == null || key.equals("null")) {							      
							        productVarientImagesId = -1; 
							    } else {
							        productVarientImagesId = Integer.parseInt(key);
							    }
							} catch (NumberFormatException e) {
							    
							    productVarientImagesId = -1; 
							}						
							productImagesMap.put("productVarientImagesId", productVarientImagesId);

							List<Map<String, Object>> productVarientList = new ArrayList<>();
							for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientMap = new HashMap<>();

								int productVarientId = Integer.parseInt(productVarientLoop.getKey());
								productVarientMap.put("productVarientId", productVarientId);

								List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
								for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
										.getValue().entrySet()) {
									Map<String, Object> productVarientImagesMap = new HashMap<>();

									int productImagesId1 = Integer.parseInt(productVarientImageLoop.getKey());
									productVarientImagesMap.put("productImagesId", productImagesId1);

									productListMap.put("productId",
											productVarientImageLoop.getValue().get(0).get("product_id"));
									productListMap.put("productName",
											productVarientImageLoop.getValue().get(0).get("product_name"));
									categoryMap.put("categoryName",
											productVarientImageLoop.getValue().get(0).get("category_name"));
									productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
									productListMap.put("buyRate",
											productVarientImageLoop.getValue().get(0).get("buy_rate"));
									productListMap.put("sellRate",
											productVarientImageLoop.getValue().get(0).get("sell_rate"));
									productListMap.put("discountPercentage",
											productVarientImageLoop.getValue().get(0).get("discount_percentage"));
									productListMap.put("weight",
											productVarientImageLoop.getValue().get(0).get("weight"));
									productListMap.put("sizeName",
											productVarientImageLoop.getValue().get(0).get("size_name"));
									productListMap.put("sizeId",
											productVarientImageLoop.getValue().get(0).get("size_id"));
									productListMap.put("pieces",
											productVarientImageLoop.getValue().get(0).get("pieces"));
									productListMap.put("description",
											productVarientImageLoop.getValue().get(0).get("description"));
									productListMap.put("alertQuantity",
											productVarientImageLoop.getValue().get(0).get("alert_quantity"));
									productListMap.put("discountAmount",
											productVarientImageLoop.getValue().get(0).get("discount_amount"));
									productListMap.put("gst", productVarientImageLoop.getValue().get(0).get("gst"));
									productListMap.put("gstTaxAmount",
											productVarientImageLoop.getValue().get(0).get("gst_tax_amount"));
									productListMap.put("totalAmount",
											productVarientImageLoop.getValue().get(0).get("total_amount"));
									productListMap.put("quantity",
											productVarientImageLoop.getValue().get(0).get("quantity"));
									productListMap.put("returnType",
											productVarientImageLoop.getValue().get(0).get("return_type"));
									productListMap.put("returnCount",
											productVarientImageLoop.getValue().get(0).get("return_count"));
									productListMap.put("returnStatus",
											productVarientImageLoop.getValue().get(0).get("return_status"));

									if (userId != null) {
										BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
										BigInteger userIdFromProductList = (BigInteger) productVarientImageLoop
												.getValue().get(0).get("user_id");

										if (userIdFromProductList != null
												&& userIdBigInteger.equals(userIdFromProductList)) {
											productListMap.put("wishListId",
													productVarientImageLoop.getValue().get(0).get("wish_list_id"));
											productListMap.put("userId", userIdFromProductList);
											productListMap.put("wishListStatus",
													productVarientImageLoop.getValue().get(0).get("wishListStatus"));
										} else {
											boolean status = false;
											productListMap.put("wishListStatus", status);
										}
									}

									productListMap.put("reviewCount",
											productVarientImageLoop.getValue().get(0).get("review_count"));
									productListMap.put("averageStarRate",
											productVarientImageLoop.getValue().get(0).get("average_star_rate"));
									productVarientMap.put("varientName",
											productVarientImageLoop.getValue().get(0).get("varient_name"));
									productListMap.put("unit", productVarientImageLoop.getValue().get(0).get("unit"));
									productVarientMap.put("varientValue",
											productVarientImageLoop.getValue().get(0).get("varient_value"));
									productMap.put("brandId",
											productVarientImageLoop.getValue().get(0).get("brand_id"));
									productMap.put("brandName",
											productVarientImageLoop.getValue().get(0).get("brand_name"));

									List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

									int randomNumber = generateRandomNumber();
									String fileExtension = getFileExtensionForImage(categoryDetails);
									String imageUrl = "category/" + randomNumber + "/" + categoryId1 + "."
											+ fileExtension;

									String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

									String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

									productMap.put("productImagesUploadUrl", productVarientImageUrl);
									productImagesMap.put("productVarientImageUrl", productImageUrl);

									categoryMap.put("url", imageUrl);

									productVarientImagesList.add(productVarientImagesMap);
								}
								productVarientList.add(productVarientMap);
							}

							productImagesList.add(productImagesMap);
							productListMap.put("productVarientList", productVarientList);
							productListMap.put("productVarientImagesList", productImagesList);
						}
						productSubList.add(productListMap);
					}
					productMap.put("productListDetails", productSubList);
					productList.add(productMap);

				}
				if (productList.isEmpty()) {
					productList = new ArrayList<>();
				}
				categoryMap.put("productDetails", productList);
				return ResponseEntity.ok(categoryMap);
			}
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product d not found.");
	}

	@GetMapping("/product1/category/view/{categoryId}/{productId}")
	public ResponseEntity<Object> getAllCategoryWithProductDetailsWithCategoryByIdAndProductIdsss(
			@PathVariable(value = "categoryId", required = false) Long categoryId,
			@PathVariable(value = "productId", required = false) Long productId,
			@RequestParam(required = false) Long userId) {

		List<Map<String, Object>> categoryRole = productRepository
				.getAllCategoryWithProductDetailsWithCategoryByIdAndProductId(categoryId, productId);
		Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = categoryRole
				.stream()
				.collect(Collectors.groupingBy(action -> action.get("category_id").toString(),
						Collectors.groupingBy(action -> action.get("product_id").toString(), Collectors.groupingBy(

								action -> action.get("product_list_id").toString(),
								Collectors.groupingBy(action -> action.get("product_varient_images_id").toString(),
										Collectors.groupingBy(action -> action.get("product_varient_id").toString(),
												Collectors.groupingBy(
														action -> action.get("product_images_id").toString())))))));

		for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
				.entrySet()) {
			Map<String, Object> categoryMap = new HashMap<>();
			String categoryId1 = categoryLoop.getKey();

			categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

			List<Map<String, Object>> productList = new ArrayList<>();
			for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
					.getValue().entrySet()) {
				Map<String, Object> productMap = new HashMap<>();

				int productId1 = Integer.parseInt(productLoop.getKey());
				productMap.put("productId", productId1);

				List<Map<String, Object>> productSubList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
						.getValue().entrySet()) {
					Map<String, Object> productListMap = new HashMap<>();
					int productListId = Integer.parseInt(productListLoop.getKey());
					productListMap.put("productListId", productListId);

					List<Map<String, Object>> productImagesList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
							.getValue().entrySet()) {
						Map<String, Object> productImagesMap = new HashMap<>();
						int productVarientImagesId = Integer.parseInt(productImagesLoop.getKey());
						productImagesMap.put("productVarientImagesId", productVarientImagesId);

						List<Map<String, Object>> productVarientList = new ArrayList<>();
						for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
								.getValue().entrySet()) {
							Map<String, Object> productVarientMap = new HashMap<>();

							int productVarientId = Integer.parseInt(productVarientLoop.getKey());
							productVarientMap.put("productVarientId", productVarientId);

							List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientImagesMap = new HashMap<>();

								int productImagesId1 = Integer.parseInt(productVarientImageLoop.getKey());
								productVarientImagesMap.put("productImagesId", productImagesId1);

								productListMap.put("productId",
										productVarientImageLoop.getValue().get(0).get("product_id"));
								productListMap.put("productName",
										productVarientImageLoop.getValue().get(0).get("product_name"));
								categoryMap.put("categoryName",
										productVarientImageLoop.getValue().get(0).get("category_name"));
								productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
								productListMap.put("buyRate",
										productVarientImageLoop.getValue().get(0).get("buy_rate"));
								productListMap.put("sellRate",
										productVarientImageLoop.getValue().get(0).get("sell_rate"));
								productListMap.put("discountPercentage",
										productVarientImageLoop.getValue().get(0).get("discount_percentage"));
								productListMap.put("weight", productVarientImageLoop.getValue().get(0).get("weight"));
								productListMap.put("pieces", productVarientImageLoop.getValue().get(0).get("pieces"));
								productListMap.put("description",
										productVarientImageLoop.getValue().get(0).get("description"));
								productListMap.put("alertQuantity",
										productVarientImageLoop.getValue().get(0).get("alert_quantity"));
								productListMap.put("discountAmount",
										productVarientImageLoop.getValue().get(0).get("discount_amount"));
								productListMap.put("gst", productVarientImageLoop.getValue().get(0).get("gst"));
								productListMap.put("gstTaxAmount",
										productVarientImageLoop.getValue().get(0).get("gst_tax_amount"));
								productListMap.put("totalAmount",
										productVarientImageLoop.getValue().get(0).get("total_amount"));
								productListMap.put("quantity",
										productVarientImageLoop.getValue().get(0).get("quantity"));

								productListMap.put("returnType",
										productVarientImageLoop.getValue().get(0).get("return_type"));
								productListMap.put("returnCount",
										productVarientImageLoop.getValue().get(0).get("return_count"));
								productListMap.put("returnStatus",
										productVarientImageLoop.getValue().get(0).get("return_status"));

								if (userId != null) {
									BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
									BigInteger userIdFromProductList = (BigInteger) productVarientImageLoop.getValue()
											.get(0).get("user_id");

									if (userIdFromProductList != null
											&& userIdBigInteger.equals(userIdFromProductList)) {
										productListMap.put("wishListId",
												productVarientImageLoop.getValue().get(0).get("wish_list_id"));
										productListMap.put("userId", userIdFromProductList);
										productListMap.put("wishListStatus",
												productVarientImageLoop.getValue().get(0).get("wishListStatus"));
									} else {
										boolean status = false;
										productListMap.put("wishListStatus", status);
									}
								}

								productListMap.put("reviewCount",
										productVarientImageLoop.getValue().get(0).get("review_count"));
								productListMap.put("averageStarRate",
										productVarientImageLoop.getValue().get(0).get("average_star_rate"));
								productVarientMap.put("varientName",
										productVarientImageLoop.getValue().get(0).get("varient_name"));
								productListMap.put("unit", productVarientImageLoop.getValue().get(0).get("unit"));
								productVarientMap.put("varientValue",
										productVarientImageLoop.getValue().get(0).get("varient_value"));
								productMap.put("brandId", productVarientImageLoop.getValue().get(0).get("brand_id"));
								productMap.put("brandName",
										productVarientImageLoop.getValue().get(0).get("brand_name"));

								List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

								int randomNumber = generateRandomNumber();
								String fileExtension = getFileExtensionForImage(categoryDetails);
								String imageUrl = "category/" + randomNumber + "/" + categoryId1 + "." + fileExtension;

								String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

								String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

								productMap.put("productImagesUploadUrl", productVarientImageUrl);
								productImagesMap.put("productVarientImageUrl", productImageUrl);

								categoryMap.put("url", imageUrl);

								productVarientImagesList.add(productVarientImagesMap);
							}
							productVarientList.add(productVarientMap);
//								productMap.put("productImages", productVarientImagesList);
						}

						productImagesList.add(productImagesMap);
						productListMap.put("productVarientList", productVarientList);
						productListMap.put("productVarientImagesList", productImagesList);
					}
					productSubList.add(productListMap);
				}
				productMap.put("productListDetails", productSubList);
				productList.add(productMap);

			}
			categoryMap.put("productDetails", productList);
			return ResponseEntity.ok(categoryMap);
		}
		return ResponseEntity.ok(Collections.emptyList());
	}

	@GetMapping("/product/category/view/{categoryId}/{productId}")
	public ResponseEntity<Object> getAllCategoryWithProductDetailsWithCategoryByIdAndProductId(
			@PathVariable(value = "categoryId", required = false) Long categoryId,
			@PathVariable(value = "productId", required = false) Long productId,
			@RequestParam(required = false) Long userId) {

		List<Map<String, Object>> categoryRole = productRepository
				.getAllCategoryWithProductDetailsWithCategoryByIdAndProductId(categoryId, productId);

		List<Map<String, Object>> categoryFinder = productRepository.getAllCtaegoryDetailsWithId(categoryId);

		if (categoryRole != null) {
			Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = categoryRole
			        .stream()
			        .collect(Collectors.groupingBy(
			                action -> Objects.toString(action.get("category_id"), "UNKNOWN"),
			                Collectors.groupingBy(
			                        action -> Objects.toString(action.get("product_id"), "UNKNOWN"),
			                        Collectors.groupingBy(
			                                action -> Objects.toString(action.get("product_list_id"), "UNKNOWN"),
			                                Collectors.groupingBy(
			                                        action -> Objects.toString(action.get("product_varient_images_id"), "UNKNOWN"),
			                                        Collectors.groupingBy(
			                                                action -> Objects.toString(action.get("product_varient_id"), "UNKNOWN"),
			                                                Collectors.groupingBy(
			                                                        action -> Objects.toString(action.get("product_images_id"), "UNKNOWN")
			                                                )
			                                        )
			                                )
			                        )
			                )
			        ));

			for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
					.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();
				String categoryId1 = categoryLoop.getKey();

				categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

				List<Map<String, Object>> productList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
						.getValue().entrySet()) {
					Map<String, Object> productMap = new HashMap<>();

					int productId1 = Integer.parseInt(productLoop.getKey());
					productMap.put("productId", productId1);

					List<Map<String, Object>> productSubList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						int productListId = Integer.parseInt(productListLoop.getKey());
						productListMap.put("productListId", productListId);

						List<Map<String, Object>> productImagesList = new ArrayList<>();
						for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> productImagesMap = new HashMap<>();
//					/		int productVarientImagesId = Integer.parseInt(productImagesLoop.getKey());
//							productImagesMap.put("productVarientImagesId", productVarientImagesId);
							String key = productImagesLoop.getKey();
							int productVarientImagesId;
							try {
							    if (key == null || key.equals("null")) {							      
							        productVarientImagesId = -1; 
							    } else {
							        productVarientImagesId = Integer.parseInt(key);
							    }
							} catch (NumberFormatException e) {							    
							    productVarientImagesId = -1; 
							}						
							productImagesMap.put("productVarientImagesId", productVarientImagesId);
							List<Map<String, Object>> productVarientList = new ArrayList<>();
							for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientMap = new HashMap<>();

//								int productVarientId = Integer.parseInt(productVarientLoop.getKey());
//								productVarientMap.put("productVarientId", productVarientId);
								String key1 = productVarientLoop.getKey();
								int productVarientId;
								try {								   
								    if (key1 == null || key1.equals("UNKNOWN")) {
								        productVarientId = -1; 
								    } else {
								        productVarientId = Integer.parseInt(key1);
								    }
								} catch (NumberFormatException e) {								 
								    productVarientId = -1; 
								}
								productVarientMap.put("productVarientId", productVarientId);
								List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
								for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
										.getValue().entrySet()) {
									Map<String, Object> productVarientImagesMap = new HashMap<>();

									int productImagesId1 = Integer.parseInt(productVarientImageLoop.getKey());
									productVarientImagesMap.put("productImagesId", productImagesId1);

									productListMap.put("productId",
											productVarientImageLoop.getValue().get(0).get("product_id"));
									productListMap.put("productName",
											productVarientImageLoop.getValue().get(0).get("product_name"));
									categoryMap.put("categoryName",
											productVarientImageLoop.getValue().get(0).get("category_name"));
									productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
									productListMap.put("sizeName", productVarientImageLoop.getValue().get(0).get("size_name"));
									productListMap.put("sizeId", productVarientImageLoop.getValue().get(0).get("size_id"));
									productListMap.put("buyRate",
											productVarientImageLoop.getValue().get(0).get("buy_rate"));
									productListMap.put("sellRate",
											productVarientImageLoop.getValue().get(0).get("sell_rate"));
									productListMap.put("discountPercentage",
											productVarientImageLoop.getValue().get(0).get("discount_percentage"));
									productListMap.put("weight",
											productVarientImageLoop.getValue().get(0).get("weight"));
									productListMap.put("pieces",
											productVarientImageLoop.getValue().get(0).get("pieces"));
									productListMap.put("description",
											productVarientImageLoop.getValue().get(0).get("description"));
									productListMap.put("alertQuantity",
											productVarientImageLoop.getValue().get(0).get("alert_quantity"));
									productListMap.put("discountAmount",
											productVarientImageLoop.getValue().get(0).get("discount_amount"));
									productListMap.put("gst", productVarientImageLoop.getValue().get(0).get("gst"));
									productListMap.put("gstTaxAmount",
											productVarientImageLoop.getValue().get(0).get("gst_tax_amount"));
									productListMap.put("totalAmount",
											productVarientImageLoop.getValue().get(0).get("total_amount"));
									productListMap.put("quantity",
											productVarientImageLoop.getValue().get(0).get("quantity"));

									productListMap.put("returnType",
											productVarientImageLoop.getValue().get(0).get("return_type"));
									productListMap.put("returnCount",
											productVarientImageLoop.getValue().get(0).get("return_count"));
									productListMap.put("returnStatus",
											productVarientImageLoop.getValue().get(0).get("return_status"));

									if (userId != null) {
										BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
										BigInteger userIdFromProductList = (BigInteger) productVarientImageLoop
												.getValue().get(0).get("user_id");

										if (userIdFromProductList != null
												&& userIdBigInteger.equals(userIdFromProductList)) {
											productListMap.put("wishListId",
													productVarientImageLoop.getValue().get(0).get("wish_list_id"));
											productListMap.put("userId", userIdFromProductList);
											productListMap.put("wishListStatus",
													productVarientImageLoop.getValue().get(0).get("wishListStatus"));
										} else {
											boolean status = false;
											productListMap.put("wishListStatus", status);
										}
									}

									productListMap.put("reviewCount",
											productVarientImageLoop.getValue().get(0).get("review_count"));
									productListMap.put("averageStarRate",
											productVarientImageLoop.getValue().get(0).get("average_star_rate"));
									productVarientMap.put("varientName",
											productVarientImageLoop.getValue().get(0).get("varient_name"));
									productListMap.put("unit", productVarientImageLoop.getValue().get(0).get("unit"));
									productVarientMap.put("varientValue",
											productVarientImageLoop.getValue().get(0).get("varient_value"));
									productMap.put("brandId",
											productVarientImageLoop.getValue().get(0).get("brand_id"));
									productMap.put("brandName",
											productVarientImageLoop.getValue().get(0).get("brand_name"));

									List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

									int randomNumber = generateRandomNumber();
									String fileExtension = getFileExtensionForImage(categoryDetails);
									String imageUrl = "category/" + randomNumber + "/" + categoryId1 + "."
											+ fileExtension;

									String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

									String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

									productMap.put("productImagesUploadUrl", productVarientImageUrl);
									productImagesMap.put("productVarientImageUrl", productImageUrl);

									categoryMap.put("url", imageUrl);

									productVarientImagesList.add(productVarientImagesMap);
								}
								productVarientList.add(productVarientMap);
							}

							productImagesList.add(productImagesMap);
//							productListMap.put("productVarientList", productVarientList);
//							productListMap.put("productVarientImagesList", productImagesList);
						}
						productSubList.add(productListMap);
					}
					productMap.put("productListDetails", productSubList);
					productList.add(productMap);

				}
				categoryMap.put("productDetails", productList);
				return ResponseEntity.ok(categoryMap);
			}
		}
		List<Map<String, Object>> categories = new ArrayList<>();

		// Process categoryRole to populate category and product details
		if (categoryRole != null && !categoryRole.isEmpty()) {
			categories.addAll(processCategoryRole(categoryRole));
		}

		// If there's still no category or product data, process categoryFinder
		if (categories.isEmpty()) {
			Map<String, List<Map<String, Object>>> categoryFinderMap = categoryFinder.stream()
					.collect(Collectors.groupingBy(action -> action.get("category_id").toString()));

			for (Entry<String, List<Map<String, Object>>> categoryLoop : categoryFinderMap.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();
				String categoryIdStr = categoryLoop.getKey();
				categoryMap.put("categoryId", categoryIdStr);
				categoryMap.put("categoryName", categoryLoop.getValue().get(0).get("category_name"));

				// Generate a random image URL
				int randomNumber = generateRandomNumber();
				String imageUrl = "category/" + randomNumber + "/" + categoryIdStr + ".jpg";
				categoryMap.put("url", imageUrl);

				// Ensure productDetails is an empty list if no products
				categoryMap.put("productDetails", Collections.emptyList());

//	            categories.add(categoryMap);
				return ResponseEntity.ok(categoryMap);
			}
		}

		if (categories.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No categories or products found.");
		}
		return null;

	}

	private List<Map<String, Object>> processCategoryRole(List<Map<String, Object>> categoryRole) {
		List<Map<String, Object>> result = new ArrayList<>();

		// Group by category_id to process details
		Map<String, List<Map<String, Object>>> groupedByCategory = categoryRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("category_id").toString()));

		// Process each category group to create category maps
		for (Map.Entry<String, List<Map<String, Object>>> entry : groupedByCategory.entrySet()) {
			Map<String, Object> categoryMap = new HashMap<>();
			categoryMap.put("categoryId", entry.getKey());
			categoryMap.put("categoryName", entry.getValue().get(0).get("category_name"));

			// Extract product details from each group
			List<Map<String, Object>> productDetails = new ArrayList<>();
			for (Map<String, Object> product : entry.getValue()) {
				Map<String, Object> productDetail = new HashMap<>();
				productDetail.put("productId", product.get("product_id"));
				productDetail.put("productName", product.get("product_name"));
				productDetail.put("productListId", product.get("product_list_id")); // example additional details
				productDetails.add(productDetail);
			}

			// Set product details in the category map
			categoryMap.put("productDetails", productDetails);

			// Add the category with its product details to the result list
			result.add(categoryMap);
		}

		return result;
	}

	@GetMapping("/product/view/brand/{id}")
	public ResponseEntity<Object> getAllProductsWithCategoryAndBrandId(@PathVariable("id") Long brandId) {
		List<Map<String, Object>> mainProductList = new ArrayList<>();
		List<Map<String, Object>> productRole = productRepository.getAllCategoryWithProductDetailsWithBrandId(brandId);

		Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productGroupMap = productRole
				.stream()
				.collect(Collectors.groupingBy(action -> action.get("brand_id").toString(), Collectors.groupingBy(
						action -> action.get("product_list_id").toString(),
						Collectors.groupingBy(action -> action.get("product_varient_id").toString(),
								Collectors.groupingBy(action -> action.get("product_varient_images_id").toString(),
										Collectors
												.groupingBy(action -> action.get("product_images_id").toString()))))));

		for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : productGroupMap
				.entrySet()) {
			Map<String, Object> productMap = new HashMap<>();
			productMap.put("brandId", productLoop.getKey());

			List<Map<String, Object>> productList = new ArrayList<>();
			for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
					.getValue().entrySet()) {
				Map<String, Object> productListMap = new HashMap<>();
				productListMap.put("productListId", productListLoop.getKey());

				List<Map<String, Object>> productVarientList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productVarientLoop : productListLoop
						.getValue().entrySet()) {
					Map<String, Object> productVarientMap = new HashMap<>();
					productVarientMap.put("productVarientId", productVarientLoop.getKey());

					List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
					for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientImagesLoop : productVarientLoop
							.getValue().entrySet()) {
						Map<String, Object> productVarientImageMap = new HashMap<>();
						String productVarientImagesId = productVarientImagesLoop.getKey();
						productVarientImageMap.put("productVarientImagesId", productVarientImagesLoop.getKey());

						List<Map<String, Object>> productImagesList = new ArrayList<>();
						for (Entry<String, List<Map<String, Object>>> productImageLoop : productVarientImagesLoop
								.getValue().entrySet()) {
							Map<String, Object> productImagesMap = new HashMap<>();
							String productImagesId = productImageLoop.getKey();
							productImagesMap.put("productImagesId", productImageLoop.getKey());

							productListMap.put("productName", productImageLoop.getValue().get(0).get("product_name"));
							productListMap.put("productId", productImageLoop.getValue().get(0).get("product_id"));
							productListMap.put("categoryName", productImageLoop.getValue().get(0).get("category_name"));
							productListMap.put("mrp", productImageLoop.getValue().get(0).get("mrp"));
							productListMap.put("buyRate", productImageLoop.getValue().get(0).get("buy_rate"));
							productListMap.put("sellRate", productImageLoop.getValue().get(0).get("sell_rate"));
							productListMap.put("productName", productImageLoop.getValue().get(0).get("product_name"));
							productListMap.put("description", productImageLoop.getValue().get(0).get("description"));
							productListMap.put("discountPercentage",
									productImageLoop.getValue().get(0).get("discount_percentage"));
							productListMap.put("discountAmount",
									productImageLoop.getValue().get(0).get("discount_amount"));
							productListMap.put("gst", productImageLoop.getValue().get(0).get("gst"));
							productListMap.put("gstTaxAmount",
									productImageLoop.getValue().get(0).get("gst_tax_amount"));
							productListMap.put("totalAmount", productImageLoop.getValue().get(0).get("total_amount"));
							productListMap.put("quantity", productImageLoop.getValue().get(0).get("quantity"));
							productVarientMap.put("varientName",
									productImageLoop.getValue().get(0).get("varient_name"));
							productVarientMap.put("varientValue",
									productImageLoop.getValue().get(0).get("varient_value"));
							productListMap.put("unit", productImageLoop.getValue().get(0).get("unit"));
							productListMap.put("description", productImageLoop.getValue().get(0).get("description"));
							productListMap.put("alertQuantity",
									productImageLoop.getValue().get(0).get("alert_quantity"));
							productListMap.put("unit", productImageLoop.getValue().get(0).get("unit"));
							productListMap.put("brandName", productImageLoop.getValue().get(0).get("brand_name"));

							int randomNumber = generateRandomNumber();

							String productImageUrl = "product/" + randomNumber + "/" + productImagesId;

							String productVarientImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

							productVarientImageMap.put("productVarientImageUrl", productVarientImageUrl);
							productImagesMap.put("productImagesUploadUrl", productImageUrl);

							productImagesList.add(productImagesMap);
							productMap.put("productImages", productImagesList);
						}
						productVarientImagesList.add(productVarientImageMap);
					}
					productVarientList.add(productVarientMap);
					productListMap.put("varientImages", productVarientImagesList);
				}
				productListMap.put("varientList", productVarientList);
				productList.add(productListMap);
			}
			productMap.put("productDetails", productList);
			mainProductList.add(productMap);
		}

		return ResponseEntity.ok(mainProductList);

	}

	@GetMapping("/productList/views/{id}")
	public ResponseEntity<Object> getAllProductsListWithNewId(@PathVariable("id") Long productListId,
			@RequestParam(required = false) Long userId) {
		List<Map<String, Object>> productRole = productRepository
				.getAllCategoryWithProductListDetailsWithId(productListId);
		Map<String, Map<String, Map<String, List<Map<String, Object>>>>> productGroupMap = productRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("product_list_id").toString(),
						Collectors.groupingBy(action -> action.get("product_varient_id").toString(),
								Collectors.groupingBy(action -> action.get("product_varient_images_id").toString()))));

		List<Map<String, Object>> productList = new ArrayList<>();
		for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : productGroupMap
				.entrySet()) {
			Map<String, Object> productListMap = new HashMap<>();
			productListMap.put("productListId", productListLoop.getKey());

			List<Map<String, Object>> productVarientList = new ArrayList<>();
			for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productListLoop.getValue()
					.entrySet()) {
				Map<String, Object> productVarientMap = new HashMap<>();
				productVarientMap.put("productVarientId", productVarientLoop.getKey());

				List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
				for (Entry<String, List<Map<String, Object>>> productVarientImagesLoop : productVarientLoop.getValue()
						.entrySet()) {
					Map<String, Object> productVarientImageMap = new HashMap<>();
					String productVarientImagesId = productVarientImagesLoop.getKey();
					productVarientImageMap.put("productVarientImagesId", productVarientImagesLoop.getKey());
					productListMap.put("productListId",productVarientImagesLoop.getValue().get(0).get("product_list_id"));
					productVarientMap.put("productVarientId",productVarientImagesLoop.getValue().get(0).get("product_varient_id"));
					productVarientImageMap.put("productVarientImagesId",productVarientImagesLoop.getValue().get(0).get("product_varient_images_id"));
					productListMap.put("productName", productVarientImagesLoop.getValue().get(0).get("product_name"));
					productListMap.put("mrp", productVarientImagesLoop.getValue().get(0).get("mrp"));
					productListMap.put("buyRate", productVarientImagesLoop.getValue().get(0).get("buy_rate"));
					productListMap.put("sellRate", productVarientImagesLoop.getValue().get(0).get("sell_rate"));
					productListMap.put("categoryId", productVarientImagesLoop.getValue().get(0).get("category_id"));
					productListMap.put("categoryName", productVarientImagesLoop.getValue().get(0).get("category_name"));
					productListMap.put("discountPercentage",productVarientImagesLoop.getValue().get(0).get("discount_percentage"));
					productListMap.put("discountAmount",productVarientImagesLoop.getValue().get(0).get("discount_amount"));
					productListMap.put("description", productVarientImagesLoop.getValue().get(0).get("description"));
					productListMap.put("gst", productVarientImagesLoop.getValue().get(0).get("gst"));
					productListMap.put("gstTaxAmount",productVarientImagesLoop.getValue().get(0).get("gst_tax_amount"));
					productListMap.put("totalAmount", productVarientImagesLoop.getValue().get(0).get("total_amount"));
					productListMap.put("quantity", productVarientImagesLoop.getValue().get(0).get("quantity"));
					productVarientMap.put("varientName",productVarientImagesLoop.getValue().get(0).get("varient_name"));
					productVarientMap.put("varientValue",productVarientImagesLoop.getValue().get(0).get("varient_value"));
					productListMap.put("unit", productVarientImagesLoop.getValue().get(0).get("unit"));
					productListMap.put("alertQuantity",productVarientImagesLoop.getValue().get(0).get("alert_quantity"));
					productListMap.put("returnType", productVarientImagesLoop.getValue().get(0).get("return_type"));
					productListMap.put("returnCount", productVarientImagesLoop.getValue().get(0).get("return_count"));
					productListMap.put("returnStatus", productVarientImagesLoop.getValue().get(0).get("return_status"));

					if (userId != null) {
						BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
						BigInteger userIdFromProductList = (BigInteger) productVarientImagesLoop.getValue().get(0).get("user_id");

						if (userIdFromProductList != null && userIdBigInteger.equals(userIdFromProductList)) {
							productListMap.put("wishListId",
									productVarientImagesLoop.getValue().get(0).get("wish_list_id"));
							productListMap.put("userId", userIdFromProductList);
							productListMap.put("wishListStatus",
									productVarientImagesLoop.getValue().get(0).get("wishListStatus"));
						} else {
							productListMap.put("wishListStatus", "false");
						}
					}

					productListMap.put("categoryName", productVarientImagesLoop.getValue().get(0).get("category_name"));

					int randomNumber = generateRandomNumber();

					String productVarientImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

					productVarientImageMap.put("productVarientImageUrl", productVarientImageUrl);

					productVarientImagesList.add(productVarientImageMap);
					productListMap.put("varientImages", productVarientImagesList);
				}
				productVarientList.add(productVarientMap);
			}

			productListMap.put("varientList", productVarientList);
//			productList.add(productListMap);
			return ResponseEntity.ok(productListMap);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");

	}

	@GetMapping("/category/brand/{id}")
	public List<Map<String, Object>> getAllBrandDetails(@PathVariable("id") Long category_id) {
		return productRepository.getAllBrandDetails(category_id);
	}

	@GetMapping("/productDetails/view/{productName}")
	public ResponseEntity<?> getProductDetailsByProductNames(@PathVariable("productName") String productName) {
		try {
			List<Map<String, Object>> proDetails = productRepository.getAllDetailsByProductName1(productName);
			List<Map<String, Object>> proMapList = new ArrayList<>();

			for (Map<String, Object> product : proDetails) {
				Map<String, Object> productMap = new HashMap<>();
				productMap.put("productId", product.get("product_id"));
				productMap.put("productListId", product.get("product_list_id"));
				productMap.put("productName", product.get("product_name"));
				productMap.put("mrp", product.get("mrp"));
				productMap.put("buyRate", product.get("buy_rate"));
				productMap.put("sellRate", product.get("sell_rate"));
				productMap.put("discountPercentage", product.get("discount_percentage"));
				productMap.put("description", product.get("description"));
				productMap.put("alertQuantity", product.get("alert_quantity"));
				productMap.put("discountAmount", product.get("discount_amount"));
				productMap.put("gst", product.get("gst"));
				productMap.put("gstTaxAmount", product.get("gst_tax_amount"));
				productMap.put("totalAmount", product.get("total_amount"));
				productMap.put("quantity", product.get("quantity"));
				productMap.put("unit", product.get("unit"));
				productMap.put("productImagesId", product.get("product_images_id"));

				productMap.put("productImagesUploadUrl",
						"product/" + generateRandomNumber() + "/" + product.get("product_images_id"));

				proMapList.add(productMap);
			}

			return ResponseEntity.ok(proMapList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/highestMovingProduct/details")
	public ResponseEntity<?> highestMovingProduct(@RequestParam(required = true) String product) {
		try {
			if ("productDetails".equalsIgnoreCase(product)) {
				List<Map<String, Object>> proDetails = productRepository.getHighestMovingPoroduct();
				List<Map<String, Object>> proMapList = new ArrayList<>();
				for (Map<String, Object> productDetails : proDetails) {
					Map<String, Object> productMap = new HashMap<>();
					productMap.put("productImagesUploadUrl",
							"product/" + generateRandomNumber() + "/" + productDetails.get("product_images_id"));
					productMap.put("productId", productDetails.get("product_id"));
					productMap.put("productName", productDetails.get("product_name"));
					productMap.put("productListId", productDetails.get("product_list_id"));
					productMap.put("brandName", productDetails.get("brand_name"));
					productMap.put("categoryName", productDetails.get("category_name"));
					productMap.put("orderCount", productDetails.get("orderCount"));
					productMap.put("categoryId", productDetails.get("category_id"));
					productMap.put("productImagesId", productDetails.get("product_images_id"));
					proMapList.add(productMap);
				}
				return ResponseEntity.ok(proMapList);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getFileExtensionForImage(List<Map<String, Object>> categoryDetails) {
		if (categoryDetails == null || categoryDetails.isEmpty()) {
			return "jpg";
		}

		Map<String, Object> imageDetails = categoryDetails.get(0);
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
	
	

	@GetMapping("/product/search/{productName}")
	public ResponseEntity<Object> getAllDetailsByProductNameDetails(@PathVariable("productName") String productName,
			@RequestParam(required = false) Long userId) {
		List<Map<String, Object>> mainProductList = new ArrayList<>();
		List<Map<String, Object>> productRole = productRepository.getAllDetailsByProductNameDetails(productName);
		Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = productRole
		        .stream()
		        .collect(Collectors.groupingBy(
		                action -> Objects.toString(action.get("category_id"), "UNKNOWN"),
		                Collectors.groupingBy(
		                        action -> Objects.toString(action.get("product_id"), "UNKNOWN"),
		                        Collectors.groupingBy(
		                                action -> Objects.toString(action.get("product_list_id"), "UNKNOWN"),
		                                Collectors.groupingBy(
		                                        action -> Objects.toString(action.get("product_varient_images_id"), "UNKNOWN"),
		                                        Collectors.groupingBy(
		                                                action -> Objects.toString(action.get("product_varient_id"), "UNKNOWN"),
		                                                Collectors.groupingBy(
		                                                        action -> Objects.toString(action.get("product_images_id"), "UNKNOWN")
		                                                )
		                                        )
		                                )
		                        )
		                )
		        ));


		for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
				.entrySet()) {
			Map<String, Object> categoryMap = new HashMap<>();
			String categoryId1 = categoryLoop.getKey();

			categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

			List<Map<String, Object>> productList = new ArrayList<>();
			for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
					.getValue().entrySet()) {
				Map<String, Object> productMap = new HashMap<>();

				int productId = Integer.parseInt(productLoop.getKey());
				productMap.put("productId", productId);

				List<Map<String, Object>> productSubList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
						.getValue().entrySet()) {
					Map<String, Object> productListMap = new HashMap<>();
					int productListId = Integer.parseInt(productListLoop.getKey());
					productListMap.put("productListId", productListId);

					List<Map<String, Object>> productImagesList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
							.getValue().entrySet()) {
						Map<String, Object> productImagesMap = new HashMap<>();
//						int productVarientImagesId = Integer.parseInt(productImagesLoop.getKey());
//						productImagesMap.put("productVarientImagesId", productVarientImagesId);

						String key = productImagesLoop.getKey();
						int productVarientImagesId;
						try {
						    if (key == null || key.equals("null")) {							      
						        productVarientImagesId = -1; 
						    } else {
						        productVarientImagesId = Integer.parseInt(key);
						    }
						} catch (NumberFormatException e) {
						    
						    productVarientImagesId = -1; 
						}						
						productImagesMap.put("productVarientImagesId", productVarientImagesId);

						List<Map<String, Object>> productVarientList = new ArrayList<>();
						for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
								.getValue().entrySet()) {
							Map<String, Object> productVarientMap = new HashMap<>();

//							int productVarientId = Integer.parseInt(productVarientLoop.getKey());
//							productVarientMap.put("productVarientId", productVarientId);
							String key1 = productVarientLoop.getKey();
							int productVarientId;
							try {							 
							    if (key1 == null || key1.equals("UNKNOWN")) {
							        productVarientId = -1;
							    } else {
							        productVarientId = Integer.parseInt(key1);
							    }
							} catch (NumberFormatException e) {							
							    productVarientId = -1; 
							}
							productVarientMap.put("productVarientId", productVarientId);
							List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientImagesMap = new HashMap<>();

								int productImagesId1 = Integer.parseInt(productVarientImageLoop.getKey());
								productVarientImagesMap.put("productImagesId", productImagesId1);

								productListMap.put("productId",
										productVarientImageLoop.getValue().get(0).get("product_id"));
								productListMap.put("productName",
										productVarientImageLoop.getValue().get(0).get("product_name"));
								categoryMap.put("categoryName",
										productVarientImageLoop.getValue().get(0).get("category_name"));
								productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
								productListMap.put("sizeName", productVarientImageLoop.getValue().get(0).get("size_name"));
								productListMap.put("sizeId", productVarientImageLoop.getValue().get(0).get("size_id"));
								productListMap.put("buyRate",
										productVarientImageLoop.getValue().get(0).get("buy_rate"));
								productListMap.put("sellRate",
										productVarientImageLoop.getValue().get(0).get("sell_rate"));
								productListMap.put("discountPercentage",
										productVarientImageLoop.getValue().get(0).get("discount_percentage"));
								productListMap.put("weight", productVarientImageLoop.getValue().get(0).get("weight"));
								productListMap.put("pieces", productVarientImageLoop.getValue().get(0).get("pieces"));
								productListMap.put("description",
										productVarientImageLoop.getValue().get(0).get("description"));
								productListMap.put("alertQuantity",
										productVarientImageLoop.getValue().get(0).get("alert_quantity"));
								productListMap.put("discountAmount",
										productVarientImageLoop.getValue().get(0).get("discount_amount"));
								productListMap.put("gst", productVarientImageLoop.getValue().get(0).get("gst"));
								productListMap.put("gstTaxAmount",
										productVarientImageLoop.getValue().get(0).get("gst_tax_amount"));
								productListMap.put("totalAmount",
										productVarientImageLoop.getValue().get(0).get("total_amount"));
								productListMap.put("quantity",
										productVarientImageLoop.getValue().get(0).get("quantity"));
								productListMap.put("returnType",
										productVarientImageLoop.getValue().get(0).get("return_type"));
								productListMap.put("returnCount",
										productVarientImageLoop.getValue().get(0).get("return_count"));
								productListMap.put("returnStatus",
										productVarientImageLoop.getValue().get(0).get("return_status"));

								if (userId != null) {
									BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
									BigInteger userIdFromProductList = (BigInteger) productVarientImageLoop.getValue()
											.get(0).get("user_id");

									if (userIdFromProductList != null
											&& userIdBigInteger.equals(userIdFromProductList)) {
										productListMap.put("wishListId",
												productVarientImageLoop.getValue().get(0).get("wish_list_id"));
										productListMap.put("userId", userIdFromProductList);
										productListMap.put("wishListStatus",
												productVarientImageLoop.getValue().get(0).get("wishListStatus"));
									} else {
										boolean status = false;
										productListMap.put("wishListStatus", status);
									}
								}

								productListMap.put("reviewCount",
										productVarientImageLoop.getValue().get(0).get("review_count"));
								productListMap.put("averageStarRate",
										productVarientImageLoop.getValue().get(0).get("average_star_rate"));
								productVarientMap.put("varientName",
										productVarientImageLoop.getValue().get(0).get("varient_name"));
								productListMap.put("unit", productVarientImageLoop.getValue().get(0).get("unit"));
								productVarientMap.put("varientValue",
										productVarientImageLoop.getValue().get(0).get("varient_value"));
								productMap.put("brandId", productVarientImageLoop.getValue().get(0).get("brand_id"));
								productMap.put("brandName",
										productVarientImageLoop.getValue().get(0).get("brand_name"));

								List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

								int randomNumber = generateRandomNumber();
								String fileExtension = getFileExtensionForImage(categoryDetails);
								String imageUrl = "category/" + randomNumber + "/" + categoryId1 + "." + fileExtension;

								String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

								String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

								productMap.put("productImagesUploadUrl", productVarientImageUrl);
								productImagesMap.put("productVarientImageUrl", productImageUrl);

								categoryMap.put("url", imageUrl);

								productVarientImagesList.add(productVarientImagesMap);
							}
							productVarientList.add(productVarientMap);
//							productMap.put("productImages", productVarientImagesList);
						}

						productImagesList.add(productImagesMap);
//						productListMap.put("productVarientList", productVarientList);
//						productListMap.put("productVarientImagesList", productImagesList);
					}
					productSubList.add(productListMap);
				}
				productMap.put("productListDetails", productSubList);
				productList.add(productMap);

			}
			categoryMap.put("productDetails", productList);
			return ResponseEntity.ok(categoryMap);
		}
		return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());

	}
	
	@GetMapping("/product/search/list")
	public List<Map<String, Object>> getDetail() {
		return productRepository.getAllAddToCartDetails();
	}

}