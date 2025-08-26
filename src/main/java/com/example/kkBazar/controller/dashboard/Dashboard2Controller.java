package com.example.kkBazar.controller.dashboard;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.kkBazar.entity.dashboard.Dashboard2;
import com.example.kkBazar.repository.dashboard.Dashboard2Repository;
import com.example.kkBazar.service.dashboard.Dashboard2Service;

@RestController
@CrossOrigin
public class Dashboard2Controller {

	@Autowired
	private Dashboard2Service dashboard2Service;

	@Autowired
	private Dashboard2Repository dashboard2Repository;

	@PostMapping("/dashboard2/save")
	public ResponseEntity<String> saveDashboard(@RequestParam("title") String title,
			@RequestParam("description") String description, @RequestParam("categoryId") long categoryId,
			@RequestParam("fileUpload") MultipartFile fileUpload) {

		try {
			
			Optional<Dashboard2> existingCategory = dashboard2Repository.findByCategoryId(categoryId);

			if (existingCategory.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Dashboard2 entry for category ID " + categoryId + " already exists.");
			}

			Dashboard2 dashboard = new Dashboard2();
			dashboard.setTitle(title);
			dashboard.setDescription(description);
			dashboard.setCategoryId(categoryId);
			dashboard.setFileUpload(convertToBlob(fileUpload));
			dashboard.setCreatedAt(new Date(System.currentTimeMillis()));
			dashboard2Service.SaveDashboard(dashboard);

			return ResponseEntity.ok("Dashboard Details saved successfully.");
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while saving the dashboard: " + e.getMessage());
		}
	}

	private Blob convertToBlob(MultipartFile file) throws IOException, SQLException {
		if (file != null && !file.isEmpty()) {
			byte[] bytes = file.getBytes();
			return new SerialBlob(bytes);
		} else {
			return null;
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/dashboard2/{randomNumber}/{id}")
	public ResponseEntity<ByteArrayResource> serveFile(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") Long id) {
		Optional<Dashboard2> complaintsOptional = dashboard2Service.getById1(id);
		if (complaintsOptional.isPresent()) {
			Dashboard2 complaints = complaintsOptional.get();
			String filename = "file_" + randomNumber + "_" + id;
			byte[] fileBytes;
			try {
				fileBytes = complaints.getFileUpload().getBytes(1, (int) complaints.getFileUpload().length());
			} catch (SQLException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			String extension = determineFileExtension(fileBytes);
			MediaType mediaType = determineMediaType(extension);

			ByteArrayResource resource = new ByteArrayResource(fileBytes);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(mediaType);
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + "." + extension);
			return ResponseEntity.ok().headers(headers).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
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

	@PutMapping("/dashboard2/value/edit/{id}")
	public ResponseEntity<?> updateCategory22(@PathVariable long id,
			@RequestParam(value = "fileUpload", required = false) MultipartFile file,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "description", required = false) String description) {
		try {
			Dashboard2 dashboard = dashboard2Service.findDashboardById(id);

			if (dashboard == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
			}
			
			dashboard.setUpdatedAt(new Date(System.currentTimeMillis()));
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				dashboard.setFileUpload(blob);
			}
			if (categoryId != null) {
				dashboard.setCategoryId(categoryId);
			}
			if (description != null) {
				dashboard.setDescription(description);
			}
			if (title != null) {
				dashboard.setTitle(title);
			}
			dashboard2Service.SaveDashboard(dashboard);

			return ResponseEntity.ok(dashboard);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating category.");
		}
	}

	public ResponseEntity<Object> getAllRoleByEmployee(@RequestParam(required = true) String dashboard) {
		if ("dashboardDetails".equals(dashboard)) {
			List<Map<String, Object>> mainDashboardList = new ArrayList<>();
			List<Map<String, Object>> categoryRole = dashboard2Repository.getAllDashboard2Details();
			Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>>> categoryGroupMap = categoryRole
					.stream()
					.collect(Collectors.groupingBy(action -> action.get("dashboard2id").toString(),
							Collectors.groupingBy(action -> action.get("category_id").toString(), Collectors.groupingBy(
									action -> action.get("product_id").toString(),
									Collectors.groupingBy(action -> action.get("product_list_id").toString(),
											Collectors.groupingBy(
													action -> action.get("product_varient_images_id").toString(),
													Collectors.groupingBy(
															action -> action.get("product_varient_id").toString(),
															Collectors.groupingBy(action -> action
																	.get("product_images_id").toString()))))))));

			for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>>> dashboardLoop : categoryGroupMap
					.entrySet()) {
				Map<String, Object> dashboardMap = new HashMap<>();
				String dashboard2Id = dashboardLoop.getKey();
				dashboardMap.put("dashboard2id", dashboardLoop.getKey());

				List<Map<String, Object>> mainCategoryList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : dashboardLoop
						.getValue().entrySet()) {
					Map<String, Object> categoryMap = new HashMap<>();

					String categoryId = categoryLoop.getKey();

					categoryMap.put("categoryId", Long.parseLong(categoryLoop.getKey()));

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> productLoop : categoryLoop
							.getValue().entrySet()) {
						Map<String, Object> productMap = new HashMap<>();
						productMap.put("productId", productLoop.getKey());

						List<Map<String, Object>> productSubList = new ArrayList<>();
						for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> productListLoop : productLoop
								.getValue().entrySet()) {
							Map<String, Object> productListMap = new HashMap<>();
							productListMap.put("productListId", productListLoop.getKey());

							List<Map<String, Object>> productImagesList = new ArrayList<>();
							for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productImagesLoop : productListLoop
									.getValue().entrySet()) {
								Map<String, Object> productImagesMap = new HashMap<>();
								productImagesMap.put("productImagesId", productImagesLoop.getKey());

								String productImagesId = productImagesLoop.getKey();

								List<Map<String, Object>> productVarientList = new ArrayList<>();
								for (Entry<String, Map<String, List<Map<String, Object>>>> productVarientLoop : productImagesLoop
										.getValue().entrySet()) {
									Map<String, Object> productVarientMap = new HashMap<>();
									productVarientMap.put("productVarientId", productVarientLoop.getKey());

									List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
									for (Entry<String, List<Map<String, Object>>> productVarientImageLoop : productVarientLoop
											.getValue().entrySet()) {
										Map<String, Object> productVarientImagesMap = new HashMap<>();
										productVarientImagesMap.put("productVarientImagesId",
												productVarientImageLoop.getKey());

										String productVarientImagesId = productVarientImageLoop.getKey();

										productMap.put("productName",
												productVarientImageLoop.getValue().get(0).get("product_name"));
										categoryMap.put("categoryName",
												productVarientImageLoop.getValue().get(0).get("category_name"));
										productMap.put("categoryName",
												productVarientImageLoop.getValue().get(0).get("category_name"));
										productMap.put("categoryId",
												productVarientImageLoop.getValue().get(0).get("category_id"));
										productListMap.put("mrp", productVarientImageLoop.getValue().get(0).get("mrp"));
										productListMap.put("buyRate",
												productVarientImageLoop.getValue().get(0).get("buy_rate"));
										productListMap.put("sellRate",
												productVarientImageLoop.getValue().get(0).get("sell_rate"));
										productListMap.put("discountPercentage",
												productVarientImageLoop.getValue().get(0).get("discount_percentage"));
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
										productVarientMap.put("varientName",
												productVarientImageLoop.getValue().get(0).get("varient_name"));
										productListMap.put("unit",
												productVarientImageLoop.getValue().get(0).get("unit"));
										productListMap.put("listDescription",
												productVarientImageLoop.getValue().get(0).get("listDescription"));
										productVarientMap.put("varientValue",
												productVarientImageLoop.getValue().get(0).get("varient_value"));

										dashboardMap.put("title",
												productVarientImageLoop.getValue().get(0).get("title"));
										dashboardMap.put("description",
												productVarientImageLoop.getValue().get(0).get("description"));

										List<Map<String, Object>> categoryDetails = productVarientImageLoop.getValue();

										int randomNumber = generateRandomNumber();
										String fileExtension = getFileExtensionForImage(categoryDetails);
										String imageUrl = "category/" + randomNumber + "/" + categoryId + "."
												+ fileExtension;

										String productImageUrl = "product/" + randomNumber + "/" + productImagesId;
										String dashboardImageUrl = "dashboard2/" + randomNumber + "/" + dashboard2Id;

										dashboardMap.put("dashboardImageUrl", dashboardImageUrl);

										String productVarientImageUrl = "varient/" + randomNumber + "/"
												+ productVarientImagesId;

										productVarientImagesMap.put("productVarientImageUrl", productVarientImageUrl);
										productImagesMap.put("productImagesUploadUrl", productImageUrl);

										categoryMap.put("url", imageUrl);

										productVarientImagesList.add(productVarientImagesMap);
									}
									productVarientList.add(productVarientMap);
									productListMap.put("productVarientImagesList", productVarientImagesList);
								}

								productImagesList.add(productImagesMap);
								productListMap.put("productVarientList", productVarientList);
							}
							productSubList.add(productListMap);
							productMap.put("productImages", productImagesList);
						}
						productMap.put("productListDetails", productSubList);
						productList.add(productMap);

					}
					categoryMap.put("productDetails", productList);
					mainCategoryList.add(categoryMap);
				}

				dashboardMap.put("categoryDetails", mainCategoryList);
				mainDashboardList.add(dashboardMap);
			}

			return ResponseEntity.ok(mainDashboardList);
		} else {
			String errorMessage = "Invalid value for 'department'. Expected 'Department'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@GetMapping("/dashboard2/view")
	public ResponseEntity<?> getAllRoleByEmployee2(@RequestParam(required = true) String dashboard,
			@RequestParam(required = false) Long userId) {
		if ("dashboardDetails".equals(dashboard)) {
			List<Map<String, Object>> mainDashboardList = new ArrayList<>();
			List<Map<String, Object>> dashboardRole = dashboard2Repository.getAllDashboard2Details();
			Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> dashboardGroupMap = dashboardRole
					.stream().collect(Collectors.groupingBy(action -> action.get("dashboard2id").toString(),

							Collectors.groupingBy(action -> action.get("product_list_id").toString(),
									Collectors.groupingBy(action -> action.get("product_varient_id").toString(),
											Collectors.groupingBy(
													action -> action.get("product_varient_images_id").toString())))));

			for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> dashboardLoop : dashboardGroupMap
					.entrySet()) {
				Map<String, Object> dashboardMap = new HashMap<>();

				int dashboard2Id = Integer.parseInt(dashboardLoop.getKey());
				dashboardMap.put("dashboard2Id", dashboard2Id);

				List<Map<String, Object>> productList = new ArrayList<>();
				for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : dashboardLoop
						.getValue().entrySet()) {
					Map<String, Object> productListMap = new HashMap<>();

					int productListId = Integer.parseInt(productListLoop.getKey());
					productListMap.put("productListId", productListId);

					List<Map<String, Object>> varientList = new ArrayList<>();
					for (Entry<String, Map<String, List<Map<String, Object>>>> varientLoop : productListLoop.getValue()
							.entrySet()) {
						Map<String, Object> varientMap = new HashMap<>();

						int productVarientId = Integer.parseInt(varientLoop.getKey());
						varientMap.put("productVarientId", productVarientId);

						List<Map<String, Object>> varientImageList = new ArrayList<>();
						for (Entry<String, List<Map<String, Object>>> varientImageLoop : varientLoop.getValue()
								.entrySet()) {
							Map<String, Object> varientImageMap = new HashMap<>();
							int productVarientImagesId = Integer.parseInt(varientImageLoop.getKey());
							productListMap.put("productVarientImagesId", productVarientImagesId);

//	                        varientImageMap.put("productVarientImagesId", varientImageLoop.getKey());
//	                       String productVarientImagesId = varientImageLoop.getKey();
//	                        productListMap.put("productVarientImagesId", varientImageLoop.getKey());

							dashboardMap.put("title", varientImageLoop.getValue().get(0).get("title"));
							dashboardMap.put("description", varientImageLoop.getValue().get(0).get("description"));
							dashboardMap.put("categoryId", varientImageLoop.getValue().get(0).get("category_id"));
							dashboardMap.put("categoryName", varientImageLoop.getValue().get(0).get("category_name"));
							Object categoryId = varientImageLoop.getValue().get(0).get("category_id");

							productListMap.put("mrp", varientImageLoop.getValue().get(0).get("mrp"));
							productListMap.put("buyRate", varientImageLoop.getValue().get(0).get("buy_rate"));
							productListMap.put("sellRate", varientImageLoop.getValue().get(0).get("sell_rate"));
							productListMap.put("discountPercentage",
									varientImageLoop.getValue().get(0).get("discount_percentage"));
							productListMap.put("alertQuantity",
									varientImageLoop.getValue().get(0).get("alert_quantity"));
							productListMap.put("discountAmount",
									varientImageLoop.getValue().get(0).get("discount_amount"));
							if (userId != null) {
								BigInteger userIdBigInteger = BigInteger.valueOf(userId.longValue());
								BigInteger userIdFromProductList = (BigInteger) varientImageLoop.getValue().get(0)
										.get("user_id");

								if (userIdFromProductList != null && userIdBigInteger.equals(userIdFromProductList)) {
									productListMap.put("wishListId",
											varientImageLoop.getValue().get(0).get("wish_list_id"));
									productListMap.put("userId", userIdFromProductList);
									productListMap.put("wishListStatus",
											varientImageLoop.getValue().get(0).get("wishListStatus"));
								} else {
									boolean status = false;
									productListMap.put("wishListStatus", status);
								}
							}
							productListMap.put("reviewCount", varientImageLoop.getValue().get(0).get("review_count"));
							productListMap.put("averageStarRate",
									varientImageLoop.getValue().get(0).get("average_star_rate"));
							productListMap.put("gst", varientImageLoop.getValue().get(0).get("gst"));
							productListMap.put("gstTaxAmount",
									varientImageLoop.getValue().get(0).get("gst_tax_amount"));
							productListMap.put("productName", varientImageLoop.getValue().get(0).get("product_name"));
							productListMap.put("totalAmount", varientImageLoop.getValue().get(0).get("total_amount"));
							productListMap.put("quantity", varientImageLoop.getValue().get(0).get("quantity"));
							Object status = varientImageLoop.getValue().get(0).get("status");
							productListMap.put("status", (status != null) ? status : false);
							varientMap.put("varientName", varientImageLoop.getValue().get(0).get("varient_name"));
							varientMap.put("varientValue", varientImageLoop.getValue().get(0).get("varient_value"));
							productListMap.put("unit", varientImageLoop.getValue().get(0).get("unit"));
							productListMap.put("listDescription",
									varientImageLoop.getValue().get(0).get("description"));
							productListMap.put("productId", varientImageLoop.getValue().get(0).get("product_id"));

							List<Map<String, Object>> categoryDetails = varientImageLoop.getValue();

							int randomNumber = generateRandomNumber();
							String fileExtension = getFileExtensionForImage(categoryDetails);

							String imageUrl = "category/" + randomNumber + "/" + categoryId + "." + fileExtension;

							String dashboardImageUrl = "dashboard2/" + randomNumber + "/" + dashboard2Id;

							dashboardMap.put("dashboardImageUrl", dashboardImageUrl);

							String productVarientImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;

							productListMap.put("productVarientImageUrl", productVarientImageUrl);

							dashboardMap.put("url", imageUrl);

							varientImageList.add(varientImageMap);
						}
						varientList.add(varientMap);

					}
					productList.add(productListMap);
				}
				dashboardMap.put("productDetails", productList);
//	            mainDashboardList.add(dashboardMap);
				return ResponseEntity.ok(dashboardMap);
			}

//	        if (!mainDashboardList.isEmpty()) {
//	            return ResponseEntity.ok(mainDashboardList);
//	        }
//	    else {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//	        }
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");

	}

	private String getFileExtensionForImage(List<Map<String, Object>> categoryDetails) {
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

	@PutMapping("/dashboard2/edit/{dashboard2Id}")
	public ResponseEntity<String> updateDashboard(@PathVariable long dashboard2Id,
			@RequestParam(value = "fileUpload", required = false) MultipartFile file,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "categoryId", required = false) long categoryId,
			@RequestParam(value = "description", required = false) String description) {
		try {
			Dashboard2 dashboard = dashboard2Service.findDashboardById(dashboard2Id);

			if (dashboard == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
			}
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				dashboard.setFileUpload(blob);
			}
			if (title != null) {
				dashboard.setTitle(title);
			}
			if (categoryId != 0) {
				dashboard.setCategoryId(categoryId);
			}
			if (description != null) {

				dashboard.setDescription(description);
			}
			dashboard2Service.SaveDashboard(dashboard);

			return ResponseEntity.ok("Dashboard updated successfully. Dashboard1 ID: " + dashboard2Id);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating dashboard1.");
		}

	}
	
	@GetMapping("/dashboard2/category/view")
	public ResponseEntity<Object> getAllRoleByEmployee(@RequestParam(required = true) String category,
			@RequestParam(required = false) Long userId) {
		if ("categoryDetails".equals(category)) {
			Map<String, Object> mainCategoryList = new HashMap<>();
			List<Map<String, Object>> categoryRole = dashboard2Repository.getAllDashboard2Details();
			Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryGroupMap = categoryRole
					.stream()
					.collect(Collectors.groupingBy(action -> action.get("dashboard2id").toString(),
							Collectors.groupingBy(action -> action.get("product_id").toString(), Collectors.groupingBy(
					
									
									action -> action.get("product_list_id").toString(),
									Collectors.groupingBy(action -> action.get("product_varient_images_id").toString(),
											Collectors.groupingBy(action -> action.get("product_varient_id").toString(),
													Collectors.groupingBy(
															action -> action.get("product_images_id").toString())))))));

			for (Entry<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>>> categoryLoop : categoryGroupMap
					.entrySet()) {
				Map<String, Object> categoryMap = new HashMap<>();

				categoryMap.put("dashboard2Id", Long.parseLong(categoryLoop.getKey()));
				
				int dashboard2Id = Integer.parseInt(categoryLoop.getKey());

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
									categoryMap.put("title", productVarientImageLoop.getValue().get(0).get("title"));
									categoryMap.put("description", productVarientImageLoop.getValue().get(0).get("description"));
									categoryMap.put("categoryId", productVarientImageLoop.getValue().get(0).get("category_id"));
									
									

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
										}else {
											boolean status = false;
											productListMap.put("wishListStatus",status);
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
									String imageUrl = "category/" + randomNumber + "/" + productVarientImageLoop.getValue().get(0).get("category_id") + "."
											+ fileExtension;

									String productVarientImageUrl = "product/" + randomNumber + "/" + productImagesId1;

									String productImageUrl = "varient/" + randomNumber + "/" + productVarientImagesId;
									
									
									String dashboardImageUrl = "dashboard2/" + randomNumber + "/" + dashboard2Id;
									categoryMap.put("dashboardImageUrl", dashboardImageUrl);

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
				mainCategoryList.putAll(categoryMap);
			}

			return ResponseEntity.ok(mainCategoryList);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}

}
