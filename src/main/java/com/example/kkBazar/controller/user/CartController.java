package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
import com.example.kkBazar.entity.user.AddToCart;
import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.repository.user.CartRepository;
import com.example.kkBazar.service.user.CartService;

@RestController
@CrossOrigin
public class CartController {
	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@GetMapping("/cartDetails")
	public ResponseEntity<?> getUserCartDetails(@RequestParam(required = true) String addToCart) {
		try {
			if ("addToCartDetails".equals(addToCart)) {
				Iterable<AddToCart> cartDetails = cartService.listAll();
				return new ResponseEntity<>(cartDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided addToCart is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving cart details: " + e.getMessage());
		}
	}

//	@PostMapping("/cartDetails/save")
//	public ResponseEntity<?> saveUsercartDetails(@RequestBody AddToCart addToCart) {
//		try {
//			cartService.SaveCartDetails(addToCart);
//			long id = addToCart.getAddToCartId();
//			return ResponseEntity.status(HttpStatus.OK).body("Cart details saved successfully." + id);
//		} catch (Exception e) {
//			e.printStackTrace();
//			String errorMessage = "An error occurred while saving Cart details.";
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//		}
//	}

	@PostMapping("/cartDetails/save")
	public ResponseEntity<?> saveUsercartDetails(@RequestBody AddToCart addToCart) {
		try {
			addToCart.setCreatedAt(new Date(System.currentTimeMillis()));
			long productListId = addToCart.getProductListId();
			long userId = addToCart.getUserId();
			long productVarientImagesId = addToCart.getProductImagesId();
			int quantity = addToCart.getQuantity();

			// Debug log for extracted values
			System.out.println("Debug: Received - productListId=" + productListId + ", userId=" + userId
					+ ", productVarientImagesId=" + productVarientImagesId + ", quantity=" + quantity);

			// Check if the item is already in the cart
			Optional<AddToCart> existingCart = cartService.findByUserIdAndProductListId(userId, productListId,
					productVarientImagesId);

			if (existingCart.isPresent()) {
				System.out.println("Debug: Item already in cart."); // Log for additional information
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item is already in Cart");
			} else {
				// Ensure data is correct before saving
				if (productListId == 0 || userId == 0 || productVarientImagesId == 0 || quantity <= 0) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data provided");
				}

				// Save the new cart item
				addToCart.setUserId(userId);
				addToCart.setProductListId(productListId);
				addToCart.setProductImagesId(productVarientImagesId);
				addToCart.setQuantity(quantity);
				cartService.SaveCartDetails(addToCart); // Ensure method name matches exact case

				System.out.println("Debug: Item saved to cart successfully."); // Log success message
				return ResponseEntity.ok("Item saved to the cart successfully");
			}
		} catch (Exception e) {
			e.printStackTrace(); // Print stack trace for debugging
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to save item to the cart due to an error.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@DeleteMapping("/cart/web/delete/{addToCartId}")
	public ResponseEntity<Object> deleteCartDetaileee(@PathVariable("addToCartId") Long addToCartId) {
		try {
			cartService.deleteCartId(addToCartId);
			return ResponseEntity.ok("Successfully removed item from your cart");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Item removal from the cart failed");
		}
	}

	@PutMapping("/cart/web/edit/{cartId}")
	public ResponseEntity<?> updateDepartmentIdwwwww(@PathVariable("cartId") Long cartId,
			@RequestBody AddToCart DepartmentIdDetails) {
		try {
			AddToCart existingDepartment = cartService.findById(cartId);
			if (existingDepartment == null) {
				return ResponseEntity.notFound().build();
			}
			existingDepartment.setCreatedAt(new Date(System.currentTimeMillis()));
			existingDepartment.setQuantity(DepartmentIdDetails.getQuantity());
			cartService.SaveCartDetails(existingDepartment);
			return ResponseEntity.ok(existingDepartment);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/cartDetails/app/save")
	public ResponseEntity<?> saveUserCartDetailsList(@RequestBody AddToCart addToCart) {
		try {
			addToCart.setCreatedAt(new Date(System.currentTimeMillis()));
			long productListId = addToCart.getProductListId();
			long userId = addToCart.getUserId();
			long productImagesId = addToCart.getProductImagesId();
			int quantity = addToCart.getQuantity();

			// Check if the item is already in the cart
			Optional<AddToCart> existingCart = cartService.findByUserIdAndProductListId(userId, productListId,
					productImagesId);

			if (existingCart.isPresent()) {
				// Return a response indicating the item is already in the cart
				Map<String, Object> response = new HashMap<>();
				response.put("message", "Item is already in Cart");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Save the new cart item
			addToCart.setQuantity(quantity);
			cartService.SaveCartDetails(addToCart); // Changed from "SaveCartDetails" to "saveCartDetails"

			// Retrieve all cart details for this user
			List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);

			// Prepare the response map with cart details
			Map<String, Object> response = new HashMap<>();
			response.put("cartDetails", cartDetails.stream().map(this::formatCartItem) // Changed the method name to
																						// reflect cart context
					.collect(Collectors.toList()));
			response.put("message", "Item saved to the cart successfully");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "An error occurred while saving the item to the cart.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	private Map<String, Object> formatCartItem(Map<String, Object> cartItem) {
		Map<String, Object> result = new HashMap<>();

		result.put("addToCartId", Objects.requireNonNullElse(cartItem.get("add_to_cart_id"), 0)); // Fixed key name
		result.put("totalAmount", Objects.requireNonNullElse(cartItem.get("total_amount"), 0.0)); // Added null checks
		result.put("productId", cartItem.get("product_id"));
		result.put("productName", cartItem.get("product_name"));
		result.put("productListId", cartItem.get("product_list_id"));
		result.put("buyRate", cartItem.get("buy_rate"));
		result.put("discountAmount", cartItem.get("discount_amount"));
		result.put("discountPercentage", cartItem.get("discount_percentage"));
		result.put("gst", cartItem.get("gst"));
		result.put("alertQuantity", cartItem.get("alert_quantity"));
		result.put("quantity", cartItem.get("cartQuantity"));
		result.put("gstTaxAmount", cartItem.get("gst_tax_amount"));
		result.put("mrp", cartItem.get("mrp"));
		result.put("description", cartItem.get("description"));
		result.put("sellRate", cartItem.get("sell_rate"));
		result.put("productImagesId", cartItem.get("product_images_id"));
		result.put("productQuantity", cartItem.get("productQuantity"));
		int randomNumber = generateRandomNumber();
		result.put("productImageUrl", "product/" + randomNumber + "/" + cartItem.get("product_images_id"));

		result.put("userId", cartItem.get("user_id"));
		result.put("userName", cartItem.get("user_name"));

		return result;
	}

	@PostMapping("/cartDetails1/save")
	public ResponseEntity<?> saveUserCartDetails(@RequestBody AddToCart addToCart) {
		try {
			addToCart.setCreatedAt(new Date(System.currentTimeMillis()));
			long productListId = addToCart.getProductListId();
			long userId = addToCart.getUserId();
			long productVarientImagesId = addToCart.getProductImagesId();
			int quantity = addToCart.getQuantity();

			Optional<AddToCart> existingCart = cartService.findByUserIdAndProductListId(userId, productListId,
					productVarientImagesId);
			if (existingCart.isPresent()) {
				// Return existing cart details
				Map<String, Object> cartDetails = new HashMap<>();
				AddToCart existingItem = existingCart.get();

				cartDetails.put("cartId", existingItem.getAddToCartId());
				cartDetails.put("userId", existingItem.getUserId());
				cartDetails.put("productListId", existingItem.getProductListId());
				cartDetails.put("productVarientImagesId", existingItem.getProductImagesId());
				cartDetails.put("quantity", existingItem.getQuantity());
				cartDetails.put("message", "Item is already in Cart");

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cartDetails);
			} else {
				// Save new item
				addToCart.setUserId(userId);
				addToCart.setProductListId(productListId);
				addToCart.setProductImagesId(productVarientImagesId);
				addToCart.setQuantity(quantity);

				AddToCart savedCart = cartService.SaveCartDetails(addToCart);

				// Retrieve the saved item details
				Map<String, Object> cartDetails = new HashMap<>();
				cartDetails.put("cartId", savedCart.getAddToCartId());
				cartDetails.put("userId", savedCart.getUserId());
				cartDetails.put("productListId", savedCart.getProductListId());
				cartDetails.put("productVarientImagesId", savedCart.getProductImagesId());
				cartDetails.put("quantity", savedCart.getQuantity());
				cartDetails.put("message", "Item saved to the cart successfully");

				return ResponseEntity.ok(cartDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to save item to the cart.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@DeleteMapping("/cart/delete/{addToCartId}")
	public ResponseEntity<Object> deleteCartDetail(@PathVariable("addToCartId") Long addToCartId) {
		try {

			AddToCart addToCart = cartService.findById(addToCartId);
			if (addToCart == null) {
				return ResponseEntity.notFound().build();
			}

			long userId = addToCart.getUserId();

			cartService.deleteCartId(addToCartId);
			List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);

			// Prepare the response map with cart details
			Map<String, Object> response = new HashMap<>();
			response.put("cartDetails", cartDetails.stream().map(this::formatCartItem) // Changed the method name to
																						// reflect cart context
					.collect(Collectors.toList()));
			response.put("message", "Item removal to the cart successfully");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Prepare an error response
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("cartDetails", Collections.emptyList()); // Keep consistent structure
			errorResponse.put("message", "Item removal from the cart failed");

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@PutMapping("/cart/edit/{cartId}")
	public ResponseEntity<?> updateDepartmentId(@PathVariable("cartId") Long cartId,
			@RequestBody AddToCart DepartmentIdDetails) {
		try {
			AddToCart existingDepartment = cartService.findById(cartId);
			if (existingDepartment == null) {
				return ResponseEntity.notFound().build();
			}
			long userId = existingDepartment.getUserId();

			existingDepartment.setQuantity(DepartmentIdDetails.getQuantity());
			existingDepartment.setCreatedAt(new Date(System.currentTimeMillis()));
			cartService.SaveCartDetails(existingDepartment);

			List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);

			// Prepare the response map with cart details
			Map<String, Object> response = new HashMap<>();
			response.put("cartDetails", cartDetails.stream().map(this::formatCartItem) // Changed the method name to
																						// reflect cart context
					.collect(Collectors.toList()));
			response.put("message", "Item Update to the cart successfully");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/cart")
	public List<Map<String, Object>> getDetail() {
		return cartRepository.getAllAddToCartDetails();
	}

	@GetMapping("/userAddToCartDetails")
	public List<Map<String, Object>> getCartDetails() {
		return cartRepository.getAllAddToCartDetails().stream().map(cartItem -> {
			Map<String, Object> result = new HashMap<>();

			result.put("addToCartId", cartItem.get("add_to_cart_id"));
			result.put("quantity", cartItem.get("quantity"));
			result.put("totalAmount", cartItem.get("total_amount"));
			result.put("productId", cartItem.get("product_id"));
			result.put("productName", cartItem.get("product_name"));
			result.put("productListId", cartItem.get("product_list_id"));
			result.put("buyRate", cartItem.get("buy_rate"));
			result.put("discountAmount", cartItem.get("discount_amount"));
			result.put("discountPercentage", cartItem.get("discount_percentage"));
			result.put("gst", cartItem.get("gst"));
			result.put("gstTaxAmount", cartItem.get("gst_tax_amount"));
			result.put("mrp", cartItem.get("mrp"));
			result.put("sellRate", cartItem.get("sell_rate"));
			result.put("alertQuantity", cartItem.get("alert_quantity"));
			result.put("productImagesId", cartItem.get("product_images_id"));
			result.put("productImagesUploadUrl",
					"product/" + generateRandomNumber() + "/" + cartItem.get("product_images_id"));
			result.put("productVarientId", cartItem.get("product_varient_id"));
			result.put("varientName", cartItem.get("varient_name"));
			result.put("varientValue", cartItem.get("varient_value"));
			result.put("productVarientImagesId", cartItem.get("product_varient_images_id"));
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + cartItem.get("product_varient_images_id"));
			result.put("categoryId", cartItem.get("category_id"));
			result.put("categoryName", cartItem.get("category_name"));
			result.put("url", "category/" + generateRandomNumber() + "/" + cartItem.get("category_id"));
			result.put("brandId", cartItem.get("brand_id"));
			result.put("brandName", cartItem.get("brand_name"));
			result.put("productDescriptionId", cartItem.get("product_description_id"));
			result.put("descriptionName", cartItem.get("description_name"));
			result.put("productDescriptionListId", cartItem.get("product_description_list_id"));
			result.put("name", cartItem.get("name"));
			result.put("value", cartItem.get("value"));
			result.put("userId", cartItem.get("user_id"));
			result.put("userName", cartItem.get("user_name"));
			result.put("totalPrice", cartItem.get("cartPrice"));
			return result;
		}).collect(Collectors.toList());
	}

	@GetMapping("/getAllCartDetailsByUserId/{id}")
	public ResponseEntity<List<Map<String, Object>>> getAllCartDetailsByUserId(@PathVariable("id") Long userId) {
		List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);
		Map<String, List<Map<String, Object>>> cartGroupMap = cartDetails.stream()
				.collect(Collectors.groupingBy(action -> action.get("add_to_cart_id").toString()));
		List<Map<String, Object>> cartList = new ArrayList<>();
		for (Entry<String, List<Map<String, Object>>> cartItem : cartGroupMap.entrySet()) {
			Map<String, Object> result = new HashMap<>();
			result.put("addToCartId", cartItem.getValue().get(0).get("add_to_cart_id"));
			result.put("totalAmount", cartItem.getValue().get(0).get("total_amount"));
			result.put("productId", cartItem.getValue().get(0).get("product_id"));
			result.put("productName", cartItem.getValue().get(0).get("product_name"));
			result.put("productListId", cartItem.getValue().get(0).get("product_list_id"));
			result.put("buyRate", cartItem.getValue().get(0).get("buy_rate"));
			result.put("discountAmount", cartItem.getValue().get(0).get("discount_amount"));
			result.put("discountPercentage", cartItem.getValue().get(0).get("discount_percentage"));
			result.put("gst", cartItem.getValue().get(0).get("gst"));
			result.put("quantity", cartItem.getValue().get(0).get("cartQuantity"));
			result.put("gstTaxAmount", cartItem.getValue().get(0).get("gst_tax_amount"));
			result.put("mrp", cartItem.getValue().get(0).get("mrp"));
			result.put("listDescription", cartItem.getValue().get(0).get("description"));
			result.put("sellRate", cartItem.getValue().get(0).get("sell_rate"));
			result.put("alertQuantity", cartItem.getValue().get(0).get("alert_quantity"));
			result.put("productVarientImagesId", cartItem.getValue().get(0).get("product_varient_images_id"));
			result.put("productImageUrl", "product/" + generateRandomNumber() + "/"
					+ cartItem.getValue().get(0).get("product_images_id"));
			result.put("userId", cartItem.getValue().get(0).get("user_id"));
			result.put("userName", cartItem.getValue().get(0).get("user_name"));
			result.put("productQuantity", cartItem.getValue().get(0).get("productQuantity"));
			result.put("totalPrice", cartItem.getValue().get(0).get("cartPrice"));
			cartList.add(result);
		}
		return ResponseEntity.ok(cartList);
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

}
