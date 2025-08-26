package com.example.kkBazar.controller.user;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.user.WishList;
import com.example.kkBazar.repository.addProduct.ProductRepository;
import com.example.kkBazar.repository.user.WishListRepository;
import com.example.kkBazar.service.user.WishListService;
import java.util.Objects;

@RestController
@CrossOrigin
public class WishListController {

	@Autowired
	private WishListService wishListService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private WishListRepository wishListRepository;

	@GetMapping("/wishList")
	public ResponseEntity<?> getUserWishListDetails(@RequestParam(required = true) String wishList) {
		try {
			if ("wishListDetails".equals(wishList)) {
				Iterable<WishList> wishListDetails = wishListService.listAll();
				return new ResponseEntity<>(wishListDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided wishList is not supported.");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving wishList details: " + e.getMessage());
		}

	}

	@PostMapping("/wishList1/save")
	public ResponseEntity<?> saveUserWishListDetails(@RequestParam long userId, @RequestParam long productListId,
			@RequestParam long productVarientImagesId) {

		try {
			Optional<WishList> existingWishList = wishListService.findByUserIdAndProductListIdAndProductImagesId(
					userId, productListId, productVarientImagesId);

			if (existingWishList.isPresent()) {
				wishListService.deleteWishList(existingWishList.get().getWishListId());
				Map<String, Object> ob = new HashMap<>();
				ob.put("Message", "WishList details deleted successfully.");
				boolean data = false;
				ob.put("status", data);
				ob.put("userId", userId);
				return ResponseEntity.status(HttpStatus.OK).body(ob);
			} else {
				WishList wishList = new WishList();
				wishList.setProductImagesId(productVarientImagesId);
				wishList.setUserId(userId);
				wishList.setCreatedAt(new Date(System.currentTimeMillis()));
				wishList.setProductListId(productListId);
				wishList.setStatus(true);
				wishListService.SaveWishListDetails(wishList);

				Map<String, Object> ob = new HashMap<>();
				ob.put("Message", "WishList details saved successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(ob);
			}
		} catch (Exception e) {
			String errorMessage = "An error occurred while saving/deleting WishList details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PostMapping("/wishList/save")
	public ResponseEntity<?> saveUserWishListDetails(@RequestBody WishList request) {
		try {
			long userId = request.getUserId();
			long productListId = request.getProductListId();
			long productVarientImagesId = request.getProductImagesId();

			Optional<WishList> existingWishList = wishListService.findByUserIdAndProductListIdAndProductImagesId(
					userId, productListId, productVarientImagesId);

			if (existingWishList.isPresent()) {
				wishListService.deleteWishList(existingWishList.get().getWishListId());
				Map<String, Object> response = new HashMap<>();
				boolean data = false;
				response.put("Message", "WishList details deleted successfully.");
				response.put("status", data);
				response.put("userId", userId);
				return ResponseEntity.ok(response);
			} else {
				WishList wishList = new WishList();
				wishList.setUserId(userId);
				wishList.setProductListId(productListId);
				wishList.setProductImagesId(productVarientImagesId);
				wishList.setStatus(true);
				wishList.setCreatedAt(new Date(System.currentTimeMillis()));
				wishListService.SaveWishListDetails(wishList);

//	                Map<String, Object> response = new HashMap<>();
//	                response.put("Message", "WishList details saved successfully.");
				return ResponseEntity.ok(wishList);
			}
		} catch (Exception e) {
			String errorMessage = "An error occurred while saving/deleting WishList details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PostMapping("/wishList/app/save")
	public ResponseEntity<?> saveUserWishListDetailseeeeeee(@RequestBody WishList request) {
		try {
			long userId = request.getUserId();
			long productListId = request.getProductListId();
			long productImagesId = request.getProductImagesId();

			// Prepare the response map
			Map<String, Object> response = new HashMap<>();
			response.put("wishListDetails", new ArrayList<>()); // Default to empty list

			// Check if the item already exists in the wishlist
			Optional<WishList> existingWishList = wishListService.findByUserIdAndProductListIdAndProductImagesId(
					userId, productListId, productImagesId);

			if (existingWishList.isPresent()) {
				wishListService.deleteWishList(existingWishList.get().getWishListId());
				boolean data = false;
				response.put("userId", userId);
				response.put("status", data);
				response.put("message", "WishList item deleted successfully.");
			} else {
				// If it doesn't exist, add it
				WishList newWishList = new WishList();
				newWishList.setUserId(userId);
				newWishList.setProductListId(productListId);
				newWishList.setProductImagesId(productImagesId);
				newWishList.setStatus(true);
				newWishList.setCreatedAt(new Date(System.currentTimeMillis()));
				wishListService.SaveWishListDetails(newWishList);
				response.put("message", "WishList item added successfully.");
			}

			// Retrieve the wishlist details after adding or deleting
			List<Map<String, Object>> wishListDetails = wishListRepository.getAllWishListDetailByUserIdList(userId);

			if (wishListDetails != null && !wishListDetails.isEmpty()) {
				// If there's data, populate the response
				response.put("wishListDetails",
						wishListDetails.stream().map(this::formatWishListItem).collect(Collectors.toList()));
			}

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while saving/deleting WishList details.");
		}
	}

	private Map<String, Object> formatWishListItem(Map<String, Object> wishListItem) {
		Map<String, Object> result = new HashMap<>();
		result.put("wishListId", Objects.requireNonNullElse(wishListItem.get("wish_list_id"), 0));
		result.put("quantity", Objects.requireNonNullElse(wishListItem.get("quantity"), 0));
		result.put("totalAmount", Objects.requireNonNullElse(wishListItem.get("total_amount"), 0.0));
		result.put("productId", Objects.requireNonNullElse(wishListItem.get("product_id"), 0));
		result.put("productName", Objects.requireNonNullElse(wishListItem.get("product_name"), ""));
		result.put("productListId", Objects.requireNonNullElse(wishListItem.get("product_list_id"), 0));
		result.put("buyRate", Objects.requireNonNullElse(wishListItem.get("buy_rate"), 0.0));
		result.put("discountAmount", Objects.requireNonNullElse(wishListItem.get("discount_amount"), 0.0));
		result.put("discountPercentage", Objects.requireNonNullElse(wishListItem.get("discount_percentage"), 0.0));
		result.put("gst", Objects.requireNonNullElse(wishListItem.get("gst"), 0.0));
		result.put("gstTaxAmount", Objects.requireNonNullElse(wishListItem.get("gst_tax_amount"), 0.0));
		result.put("mrp", Objects.requireNonNullElse(wishListItem.get("mrp"), 0.0));
		result.put("sellRate", Objects.requireNonNullElse(wishListItem.get("sell_rate"), 0.0));
		result.put("alertQuantity", Objects.requireNonNullElse(wishListItem.get("alert_quantity"), 0));
		result.put("productImagesId", Objects.requireNonNullElse(wishListItem.get("product_images_id"), 0));
//		result.put("productVarientImagesId",
//				Objects.requireNonNullElse(wishListItem.get("product_varient_images_id"), 0));
		result.put("productImagesUploadUrl", "product/" + generateRandomNumber() + "/"
				+ Objects.requireNonNullElse(wishListItem.get("product_images_id"), ""));
		result.put("userId", Objects.requireNonNullElse(wishListItem.get("user_id"), 0));
		result.put("userName", Objects.requireNonNullElse(wishListItem.get("user_name"), ""));
		return result;
	}

	@DeleteMapping("/wishList/delete/{wishListId}")
	public ResponseEntity<Object> deletWishListDetail(@PathVariable("wishListId") Long wishListId) {
		try {
			wishListService.deleteWishListId(wishListId);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Item removed from the collection");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Item removal failed");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/wishListProductDetails")
	public List<Map<String, Object>> getWishListDetails() {
		return wishListRepository.getAllWishListProductDetails().stream().map(wishListItem -> {
			Map<String, Object> result = new HashMap<>();
			result.put("wishListId", wishListItem.get("wish_list_id"));
			result.put("quantity", wishListItem.get("quantity"));
			result.put("totalAmount", wishListItem.get("total_amount"));
			result.put("productId", wishListItem.get("product_id"));
			result.put("productName", wishListItem.get("product_name"));
			result.put("productListId", wishListItem.get("product_list_id"));
			result.put("buyRate", wishListItem.get("buy_rate"));
			result.put("discountAmount", wishListItem.get("discount_amount"));
			result.put("discountPercentage", wishListItem.get("discount_percentage"));
			result.put("gst", wishListItem.get("gst"));
			result.put("gstTaxAmount", wishListItem.get("gst_tax_amount"));
			result.put("mrp", wishListItem.get("mrp"));
			result.put("sellRate", wishListItem.get("sell_rate"));
			result.put("alertQuantity", wishListItem.get("alert_quantity"));
			result.put("productImagesId", wishListItem.get("product_images_id"));
			result.put("productImagesUploadUrl",
					"product/" + generateRandomNumber() + "/" + wishListItem.get("product_images_id"));
			result.put("productVarientId", wishListItem.get("product_varient_id"));
			result.put("varientName", wishListItem.get("varient_name"));
			result.put("varientValue", wishListItem.get("varient_value"));
			result.put("productVarientImagesId", wishListItem.get("product_varient_images_id"));
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + wishListItem.get("product_varient_images_id"));
			result.put("categoryId", wishListItem.get("category_id"));
			result.put("categoryName", wishListItem.get("category_name"));

			if (wishListItem != null && wishListItem.containsKey("category_id")) {
				result.put("url", "category/" + generateRandomNumber() + "/" + wishListItem.get("category_id"));
			}

			result.put("brandId", wishListItem.get("brand_id"));
			result.put("brandName", wishListItem.get("brand_name"));
			result.put("productDescriptionListId", wishListItem.get("product_description_list_id"));
			result.put("productDescriptionId", wishListItem.get("product_description_id"));
			result.put("name", wishListItem.get("name"));
			result.put("value", wishListItem.get("value"));
			result.put("userId", wishListItem.get("user_id"));
			result.put("userName", wishListItem.get("user_name"));
			return result;
		}).collect(Collectors.toList());

	}

	@GetMapping("/wishListProductDetails/{id}")
	public ResponseEntity<List<Map<String, Object>>> getWishListDetail(@PathVariable("id") Long wishListId) {
		List<Map<String, Object>> wishListDetails = wishListRepository.getAllWishListDetailsById(wishListId);

		if (wishListDetails.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<Map<String, Object>> response = wishListDetails.stream().map(wishListItem -> {
			Map<String, Object> result = new HashMap<>();

			result.put("wishListId", wishListItem.get("wish_list_id"));
			result.put("quantity", wishListItem.get("quantity"));
			result.put("totalAmount", wishListItem.get("total_amount"));
			result.put("productId", wishListItem.get("product_id"));
			result.put("productName", wishListItem.get("product_name"));
			result.put("productListId", wishListItem.get("product_list_id"));
			result.put("buyRate", wishListItem.get("buy_rate"));
			result.put("discountAmount", wishListItem.get("discount_amount"));
			result.put("discountPercentage", wishListItem.get("discount_percentage"));
			result.put("gst", wishListItem.get("gst"));
			result.put("gstTaxAmount", wishListItem.get("gst_tax_amount"));
			result.put("mrp", wishListItem.get("mrp"));
			result.put("sellRate", wishListItem.get("sell_rate"));
			result.put("alertQuantity", wishListItem.get("alert_quantity"));
			result.put("productImagesId", wishListItem.get("product_images_id"));
			result.put("productImagesUploadUrl",
					"product/" + generateRandomNumber() + "/" + wishListItem.get("product_images_id"));
			result.put("productVarientId", wishListItem.get("product_varient_id"));
			result.put("varientName", wishListItem.get("varient_name"));
			result.put("varientValue", wishListItem.get("varient_value"));
			result.put("productVarientImagesId", wishListItem.get("product_varient_images_id"));
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + wishListItem.get("product_varient_images_id"));
			result.put("categoryId", wishListItem.get("category_id"));
			result.put("categoryName", wishListItem.get("category_name"));
			result.put("url", "category/" + generateRandomNumber() + "/" + wishListItem.get("category_id"));
			result.put("brandId", wishListItem.get("brand_id"));
			result.put("brandName", wishListItem.get("brand_name"));
			result.put("userId", wishListItem.get("user_id"));
			result.put("userName", wishListItem.get("user_name"));
			return result;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(response);

	}

	@GetMapping("/wishListProductDetailsByUser/{id}")
	public ResponseEntity<List<Map<String, Object>>> getWishListDetailByUser(@PathVariable("id") Long userId) {
		List<Map<String, Object>> wishListDetailsList = wishListRepository.getAllWishListDetailsByUserId(userId);
		List<Map<String, Object>> resultList = new ArrayList<>();

		for (Map<String, Object> wishListDetails : wishListDetailsList) {
			Map<String, Object> result = new HashMap<>();
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + wishListDetails.get("productVarientImagesId"));
			result.putAll(wishListDetails);
			resultList.add(result);
		}

		return ResponseEntity.ok(resultList);
	}

//	

	@GetMapping("/wishListProductDetailByUser1/{id}")
	public ResponseEntity<List<Map<String, Object>>> getWishListDetailByUser1(@PathVariable("id") Long userId) {
		List<Map<String, Object>> wishListDetails = wishListRepository.getAllWishListDetailByUserId(userId);
		if (wishListDetails.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<Map<String, Object>> response = wishListDetails.stream().map(wishListItem -> {
			Map<String, Object> result = new HashMap<>();

			result.put("wishListId", wishListItem.get("wish_list_id"));
			result.put("quantity", wishListItem.get("quantity"));
			result.put("totalAmount", wishListItem.get("total_amount"));
			result.put("productId", wishListItem.get("product_id"));
			result.put("productName", wishListItem.get("product_name"));
			result.put("productListId", wishListItem.get("product_list_id"));
			result.put("buyRate", wishListItem.get("buy_rate"));
			result.put("discountAmount", wishListItem.get("discount_amount"));
			result.put("discountPercentage", wishListItem.get("discount_percentage"));
			result.put("gst", wishListItem.get("gst"));
			result.put("gstTaxAmount", wishListItem.get("gst_tax_amount"));
			result.put("mrp", wishListItem.get("mrp"));
			result.put("sellRate", wishListItem.get("sell_rate"));
			result.put("alertQuantity", wishListItem.get("alert_quantity"));
			result.put("productImagesId", wishListItem.get("product_images_id"));
			result.put("productImagesUploadUrl",
					"product/" + generateRandomNumber() + "/" + wishListItem.get("product_images_id"));
			result.put("userId", wishListItem.get("user_id"));
			result.put("userName", wishListItem.get("user_name"));
			return result;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/wishListProductDetailByUser/{id}")
	public ResponseEntity<List<Map<String, Object>>> getWishListDetailByUser11(@PathVariable("id") Long userId) {
		List<Map<String, Object>> wishListDetails = wishListRepository.getAllWishListDetailByUserId(userId);
		if (wishListDetails == null || wishListDetails.isEmpty()) {
			List<Map<String, Object>> emptyResponse = Collections.emptyList();
			return ResponseEntity.ok(emptyResponse);
		}
		List<Map<String, Object>> response = wishListDetails.stream().map(wishListItem -> {
			Map<String, Object> result = new HashMap<>();

			result.put("wishListId", Objects.requireNonNullElse(wishListItem.get("wish_list_id"), 0));
			result.put("quantity", Objects.requireNonNullElse(wishListItem.get("quantity"), 0));
			result.put("totalAmount", Objects.requireNonNullElse(wishListItem.get("total_amount"), 0.0));
			result.put("productId", Objects.requireNonNullElse(wishListItem.get("product_id"), 0));
			result.put("productName", Objects.requireNonNullElse(wishListItem.get("product_name"), ""));
			result.put("productListId", Objects.requireNonNullElse(wishListItem.get("product_list_id"), 0));
			result.put("buyRate", Objects.requireNonNullElse(wishListItem.get("buy_rate"), 0.0));
			result.put("discountAmount", Objects.requireNonNullElse(wishListItem.get("discount_amount"), 0.0));
			result.put("discountPercentage", Objects.requireNonNullElse(wishListItem.get("discount_percentage"), 0.0));
			result.put("gst", Objects.requireNonNullElse(wishListItem.get("gst"), 0.0));
			result.put("gstTaxAmount", Objects.requireNonNullElse(wishListItem.get("gst_tax_amount"), 0.0));
			result.put("mrp", Objects.requireNonNullElse(wishListItem.get("mrp"), 0.0));
			result.put("sellRate", Objects.requireNonNullElse(wishListItem.get("sell_rate"), 0.0));
			result.put("alertQuantity", Objects.requireNonNullElse(wishListItem.get("alert_quantity"), 0));
			result.put("productImagesId", Objects.requireNonNullElse(wishListItem.get("product_images_id"), 0));
			result.put("productVarientImagesId",
					Objects.requireNonNullElse(wishListItem.get("product_varient_images_id"), 0));
			result.put("productImagesUploadUrl", "product/" + generateRandomNumber() + "/"
					+ Objects.requireNonNullElse(wishListItem.get("product_images_id"), ""));
			result.put("userId", Objects.requireNonNullElse(wishListItem.get("user_id"), 0));
			result.put("userName", Objects.requireNonNullElse(wishListItem.get("user_name"), ""));

			return result;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(response); // Always return HTTP 200 with the data
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

}