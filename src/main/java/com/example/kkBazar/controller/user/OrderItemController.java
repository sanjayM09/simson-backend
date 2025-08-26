package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.admin.User;
import com.example.kkBazar.entity.product.Product;
import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.user.AddToCart;
import com.example.kkBazar.entity.user.OrderItem;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.entity.user.Review;
import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.entity.user.UserProfile;
import com.example.kkBazar.repository.addProduct.ProductListRepository;
import com.example.kkBazar.repository.admin.UserRepository;
import com.example.kkBazar.repository.user.CartRepository;
import com.example.kkBazar.repository.user.OrderItemRepository;
import com.example.kkBazar.repository.user.ReviewRepository;
import com.example.kkBazar.repository.user.UserAddressRepository;
import com.example.kkBazar.service.admin.UserService;
import com.example.kkBazar.service.user.CartService;
//import com.example.kkBazar.service.user.FirebaseNotificationService;
import com.example.kkBazar.service.user.OrderItemListService;
import com.example.kkBazar.service.user.OrderItemService;

@RestController
@CrossOrigin

public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ProductListRepository productListRepository;
	@Autowired
	private OrderItemListService orderItemListService;
//	@Autowired
//	private FirebaseNotificationService service;

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userservice;
	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@Autowired
	private JavaMailSender emailSender;

	@Value("${spring.mail.username}")
	private String from;

	@GetMapping("/orderItems/view")
	public ResponseEntity<?> getOrderItemDetails(@RequestParam(required = true) String orderItem) {
		try {
			if ("orderItemDetails".equals(orderItem)) {
				Iterable<OrderItem> orderItemDetails = orderItemService.listAll();
				return new ResponseEntity<>(orderItemDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided orderItem is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderItem details: " + e.getMessage());
		}
	}

	@PostMapping("/orderItems/save")
	public ResponseEntity<?> saveOrderItemsDetails(@RequestBody OrderItem orderItem) {
		try {
			
			orderItem.setCreatedAt(new Date(System.currentTimeMillis()));
			LocalDate currentDate = LocalDate.now();
			orderItem.setId(1);
			orderItem.getOrderItemList().forEach(item -> item.setDate(currentDate));
			orderItem.setDate(currentDate);
			long id1 = orderItem.getId();
			List<Long> orderItemIdList = orderItem.getOrderItemList().stream().map(OrderItemList::getProductListId)
					.collect(Collectors.toList());

			List<ProductList> productList = productListRepository.findAllById(orderItemIdList);

			for (ProductList product : productList) {
				for (OrderItemList orderItemItem : orderItem.getOrderItemList()) {
					if (product.getProductListId().equals(orderItemItem.getProductListId())) {
						List<Map<String, Object>> getName = productListRepository
								.getProductName(orderItemItem.getProductListId());
						if (Objects.nonNull(product.getQuantity())
								&& product.getQuantity() < orderItemItem.getQuantity()) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
									" Insufficient quantity for product Name: " + getName.get(0).get("product_name"));
						}
						double newQuantity = Objects.nonNull(product.getQuantity())
								? product.getQuantity() - orderItemItem.getQuantity()
								: orderItemItem.getQuantity();
						newQuantity = Math.max(0, newQuantity);
						product.setQuantity(newQuantity);
					}
				}
			}

			long userId = orderItem.getUserId();
			List<UserAddress> userDetailsList = userAddressRepository.findByUserId(userId);

			if (userDetailsList.isEmpty()
					|| userDetailsList.stream().noneMatch(user -> user.getStreetAddress() != null)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Address is not present");
			}
			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				orderItemLoop.setPending(true);
				long productListId = orderItemLoop.getProductListId();

				Optional<AddToCart> existingCart = cartRepository.findByUserIdAndProductListId(userId, productListId);
				if (existingCart.isPresent()) {
					try {
						cartService.deleteCartId(existingCart.get().getAddToCartId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Debug: No cart item found for productListId=" + productListId);
				}
			}
			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				orderItemLoop.setOrderStatus("pending");
			}

			productListRepository.saveAll(productList);
			orderItemService.SaveOrderItemDetails(orderItem);
			//sendEmailToCandidate(orderItem);

			long id = orderItem.getUserId();
			long userOrderId = orderItem.getOrderItemId();

			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				long orderId = orderItemLoop.getOrderItemListId();
				orderItemLoop.setOrderId("OD98745632100" + orderId);
				orderItemLoop.setUserOrderId(userOrderId);
//
//				Review review = new Review();
//				review.setUserId(id);
//				review.setProductListId(orderItemLoop.getProductListId());
//				review.setDate(currentDate);
//
//				reviewRepository.save(review);
			}
			orderItemService.SaveOrderItemDetails(orderItem);
			sendEmailToCandidate(orderItem);
			List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);
			Map<String, Object> response = new HashMap<>();
			response.put("cartDetails", cartDetails.stream().map(this::formatCartItem) // Changed the method name to
																						// reflect cart context
					.collect(Collectors.toList()));
			response.put("message", "Order confirmed successfully");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product order details.");
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
		result.put("productVarientImagesId", cartItem.get("product_varient_images_id"));
		result.put("productQuantity", cartItem.get("productQuantity"));
		int randomNumber = generateRandomNumber();
		result.put("productVarientImageUrl",
				"varient/" + randomNumber + "/" + cartItem.get("product_varient_images_id"));

		result.put("userId", cartItem.get("user_id"));
		result.put("userName", cartItem.get("user_name"));

		return result;
	}

	@PostMapping("/orderItems1/save")
	public ResponseEntity<?> saveOrderItemsDetails1(@RequestBody OrderItem orderItem) {
		try {
			
			orderItem.setCreatedAt(new Date(System.currentTimeMillis()));
			LocalDate currentDate = LocalDate.now();
			orderItem.setId(1);
			if (orderItem.getOrderItemList() != null) {
				orderItem.getOrderItemList().forEach(item -> item.setDate(currentDate));
			} else {

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" Order item list cannot be null");
			}

			orderItem.setDate(currentDate);

			List<Long> orderItemIdList = orderItem.getOrderItemList().stream().map(OrderItemList::getProductListId)
					.collect(Collectors.toList());

			List<ProductList> productList = productListRepository.findAllById(orderItemIdList);

			for (ProductList product : productList) {
				for (OrderItemList orderItemItem : orderItem.getOrderItemList()) {
					if (product.getProductListId().equals(orderItemItem.getProductListId())) {
						List<Map<String, Object>> getName = productListRepository
								.getProductName(orderItemItem.getProductListId());
						if (Objects.nonNull(product.getQuantity())
								&& product.getQuantity() < orderItemItem.getQuantity()) {
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
									" Insufficient quantity for product Name: " + getName.get(0).get("product_name"));
						}
						double newQuantity = Objects.nonNull(product.getQuantity())
								? product.getQuantity() - orderItemItem.getQuantity()
								: orderItemItem.getQuantity();
						newQuantity = Math.max(0, newQuantity);
						product.setQuantity(newQuantity);
					}
				}
			}

			long userId = orderItem.getUserId();
			User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

			if (orderItem.getOrderItemList().stream().noneMatch(item -> item.getProductListId() != 0)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No products in order");
			}

			orderItemService.SaveOrderItemDetails(orderItem); // Ensure initial save to generate IDs

			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				orderItemLoop.setOrderStatus("pending");
				orderItemLoop.setPending(true);
				long productListId = orderItemLoop.getProductListId();
				Optional<AddToCart> existingCart = cartRepository.findByUserIdAndProductListId(userId, productListId);
				existingCart.ifPresent(cart -> {
					try {
						cartService.deleteCartId(cart.getAddToCartId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				orderItemListService.SaveOrderItemListDetails(orderItemLoop);
				orderItemLoop.setOrderId("OD98745632100" + orderItemLoop.getOrderItemListId());
				orderItemLoop.setUserOrderId(orderItem.getOrderItemId());
				orderItemListService.SaveOrderItemListDetails(orderItemLoop);
			}

//			FirebaseNotificationService notificationService = new FirebaseNotificationService();
//			String adminToken = getAdminFirebaseToken();
//			if (adminToken != null) {
//				String title = "New Order Placed";
//				String body = "User " + orderItem.getUserId() + " placed a new order.";
//				notificationService.sendNotificationToUser(adminToken, title, body); 
//			}

			orderItemService.SaveOrderItemDetails(orderItem);
			sendEmailToCandidate(orderItem);
			return ResponseEntity.ok("Order confirmed successfully");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving order details.");
		}
	}

	private void sendEmailToCandidate(OrderItem orderItem) {
		try {
			long userId = orderItem.getUserId();
			User candidate = userservice.findById(userId);
			String email = candidate.getEmailId();
			String name = candidate.getUserName();

			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(email);
			helper.setSubject("Order Confirmation");

			String emailContent = String.format(
					"Dear %s,%n%n" + "Your order has been confirmed with the following details:%n%n"
							+ "Order Date: %s%n" + "Order Time: %s%n%n" + "Thank you for shopping with us!%n%n"
							+ "Regards,%n" + "Customer Support Team%n" + "Contact: 9442428832",
					name, orderItem.getDate(), orderItem.getTime());

			helper.setText(emailContent);

			emailSender.send(message);
		} catch (Exception e) {
			// Handle email sending failure
			e.printStackTrace();
		}
	}

	private String getAdminFirebaseToken() {
		return "fHav8UNHRdisIuoG5ewSpL:APA91bH1jJtdCJR2bSJ3308ep6u9JXgCP3kLhEBP4qCf6GwEkjhW1aKxDGqBIcao5nk7Dih0oEbGSbwRqi4iE2hWww3RFmf5DgtoxLc85f8F8ttWB9th3ZxeRwjYmfEm3IvL8jLRdPsS";

	}

	@GetMapping("/orderItems/detail/view/{id}")
	public ResponseEntity<Object> getOrderItemDetail(@PathVariable(value = "id") Long userId) {
		try {
			List<Map<String, Object>> mainDashboardList = new ArrayList<>();
			List<Map<String, Object>> orderItemRole = orderItemRepository.getOrderItemDetails(userId);

			Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderItemGroupMap = orderItemRole
					.stream()
					.collect(Collectors.groupingBy(action -> getString(action, "order_Item_id"), Collectors.groupingBy(
							action -> getString(action, "order_Item_List_Id"),
							Collectors.groupingBy(action -> getString(action, "product_List_Id"),
									Collectors.groupingBy(action -> getString(action, "product_Varient_Id"), Collectors
											.groupingBy(action -> getString(action, "product_Varient_Images_Id")))))));
			for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderLoop : orderItemGroupMap
					.entrySet()) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderItemId", orderLoop.getKey());
				List<Map<String, Object>> orderItemList = new ArrayList<>();
				for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> orderItemListLoop : orderLoop
						.getValue().entrySet()) {
					Map<String, Object> orderLoopProductListMap = new HashMap<>();
					orderLoopProductListMap.put("orderItemListId", orderItemListLoop.getKey());

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : orderItemListLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						productListMap.put("productListId", productListLoop.getKey());

						List<Map<String, Object>> variantList = new ArrayList<>();
						for (Map.Entry<String, Map<String, List<Map<String, Object>>>> variantLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> variantMap = new HashMap<>();
							variantMap.put("productVarientId", variantLoop.getKey());

							List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> productVarientImagesLoop : variantLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientImageMap = new HashMap<>();
								String productVarientImagesId = productVarientImagesLoop.getKey();
								productVarientImageMap.put("productVarientImagesId", productVarientImagesLoop.getKey());
								Map<String, Object> firstItem = productVarientImagesLoop.getValue().get(0);

								orderMap.put("userId", firstItem.get("user_id"));
								orderMap.put("orderDate", firstItem.get("orderDate"));
								orderMap.put("totalPrice", firstItem.get("total_price"));
								orderMap.put("totalItems", firstItem.get("total_items"));
								orderLoopProductListMap.put("mrp",
										productVarientImagesLoop.getValue().get(0).get("mrp"));
								orderLoopProductListMap.put("buyRate",
										productVarientImagesLoop.getValue().get(0).get("buy_rate"));
								orderLoopProductListMap.put("sellRate",
										productVarientImagesLoop.getValue().get(0).get("sell_rate"));
								orderLoopProductListMap.put("discountPercentage",
										productVarientImagesLoop.getValue().get(0).get("discount_percentage"));
								orderLoopProductListMap.put("alertQuantity",
										productVarientImagesLoop.getValue().get(0).get("alert_quantity"));
								orderLoopProductListMap.put("gst",
										productVarientImagesLoop.getValue().get(0).get("gst"));
								orderLoopProductListMap.put("gstTaxAmount",
										productVarientImagesLoop.getValue().get(0).get("gst_tax_amount"));
								orderLoopProductListMap.put("totalAmount",
										productVarientImagesLoop.getValue().get(0).get("total_amount"));
								orderLoopProductListMap.put("deliveredDate",
										productVarientImagesLoop.getValue().get(0).get("deliveredDate"));
								orderLoopProductListMap.put("productName",
										productVarientImagesLoop.getValue().get(0).get("product_name"));

								orderLoopProductListMap.put("unit",
										productVarientImagesLoop.getValue().get(0).get("unit"));
								orderLoopProductListMap.put("listDescription",
										productVarientImagesLoop.getValue().get(0).get("listDescription"));
								orderLoopProductListMap.put("productId",
										productVarientImagesLoop.getValue().get(0).get("product_id"));
								orderLoopProductListMap.put("orderItemQuantity",
										productVarientImagesLoop.getValue().get(0).get("orderItemQuantity"));
								orderLoopProductListMap.put("orderStatus",
										productVarientImagesLoop.getValue().get(0).get("order_status"));
								orderLoopProductListMap.put("returnCount",
										productVarientImagesLoop.getValue().get(0).get("return_count"));
								orderLoopProductListMap.put("returnStatus",
										productVarientImagesLoop.getValue().get(0).get("return_status"));
								orderLoopProductListMap.put("returnType",
										productVarientImagesLoop.getValue().get(0).get("return_type"));
								orderLoopProductListMap.put("productListId",
										productVarientImagesLoop.getValue().get(0).get("product_list_id"));

								int randomNumber = generateRandomNumber();
								String productVarientImageUrl = "varient/" + randomNumber + "/"
										+ productVarientImagesId;

								orderLoopProductListMap.put("productVarientImageUrl", productVarientImageUrl);

								productVarientImagesList.add(productVarientImageMap);
							}
							variantList.add(variantMap);
						}
						productList.add(productListMap);
					}
					orderItemList.add(orderLoopProductListMap);
				}
				orderMap.put("orderItemDetails", orderItemList);
				mainDashboardList.add(orderMap);
			}
			return ResponseEntity.ok(mainDashboardList);
		} catch (Exception e) {
			String errorMessage = "Error processing order item details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@GetMapping("/orderItemListDetails/{id}/{orderItemListId}")
	public List<Map<String, Object>> getAllOrderItemListDetails(@PathVariable("id") Long userId,
			@PathVariable("orderItemListId") Long orderItemListId) {
		List<Map<String, Object>> orderList = new ArrayList<>();
		List<Map<String, Object>> getOrderDetails = orderItemRepository.getAllOrderItemListDetails(userId,
				orderItemListId);
		Map<String, Object> orderMap = new HashMap<>();
		int randomNumber = generateRandomNumber();
		for (Map<String, Object> orderLoop : getOrderDetails) {
			String productVarientImageUrl = "varient/" + randomNumber + "/" + orderLoop.get("productVarientImagesId");
			orderMap.put("productVarientImageUrl", productVarientImageUrl);
			orderMap.putAll(orderLoop);

			orderList.add(orderMap);
		}
		return orderList;
	}
//	@GetMapping("/order/detail/{id}")
//	public List<Map<String, Object>> getOrderItemDetailss(@PathVariable("id") Long userId)
//			 {
//		List<Map<String, Object>> orderList = new ArrayList<>();
//		List<Map<String, Object>> getOrderDetails = orderItemRepository.getOrderItemDetails(userId);
//		Map<String, Object> orderMap = new HashMap<>();
//		int randomNumber = generateRandomNumber();
//		for (Map<String, Object> orderLoop : getOrderDetails) {
//			String productVarientImageUrl = "varient/" + randomNumber + "/" + orderLoop.get("productVarientImagesId");
//			orderMap.put("productVarientImageUrl", productVarientImageUrl);
//			orderMap.putAll(orderLoop);
//
//			orderList.add(orderMap);
//		}
//		return orderList;
//	}

	@GetMapping("/order/detail/{id}")
	public ResponseEntity<Object> getOrderItemDetails1(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> mainOrderList = new ArrayList<>();
			List<Map<String, Object>> userRole = orderItemRepository.getOrderItemDetails(userId);
			Map<String, List<Map<String, Object>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
			List<Map.Entry<String, List<Map<String, Object>>>> sortedList = new ArrayList<>(userGroupMap.entrySet());
			sortedList.sort(
					(entry1, entry2) -> Long.compare(Long.parseLong(entry2.getKey()), Long.parseLong(entry1.getKey())));
			for (Map.Entry<String, List<Map<String, Object>>> orderLoop : sortedList) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderItemListId", orderLoop.getKey());
				List<Map<String, Object>> orderList = new ArrayList<>();
				for (Map<String, Object> orderItemMap : orderLoop.getValue()) {
					Map<String, Object> ordersMap = new HashMap<>();
					orderMap.put("orderItemId", orderItemMap.get("orderItemId"));
					orderMap.put("orderItemListId", orderItemMap.get("orderItemListId"));
					orderMap.put("cancelled", orderItemMap.get("cancelled"));
					orderMap.put("orderId", orderItemMap.get("order_id"));
					orderMap.put("delivered", orderItemMap.get("delivered"));
					orderMap.put("orderStatus", orderItemMap.get("orderStatus"));
					orderMap.put("productListId", orderItemMap.get("productListId"));
					orderMap.put("quantity", orderItemMap.get("quantity"));
					orderMap.put("totalPrice", orderItemMap.get("totalPrice"));
					orderMap.put("productVarientImagesId", orderItemMap.get("productVarientImagesId"));
					orderMap.put("date", orderItemMap.get("date"));
					orderMap.put("totalItems", orderItemMap.get("totalItems"));
					orderMap.put("orderTotalPrice", orderItemMap.get("orderTotalPrice"));
					orderMap.put("userId", orderItemMap.get("userId"));
					orderMap.put("productId", orderItemMap.get("productId"));
					orderMap.put("totalAmount", orderItemMap.get("totalAmount"));
					orderMap.put("productName", orderItemMap.get("productName"));
					orderMap.put("description", orderItemMap.get("description"));
					orderMap.put("userAddressId", orderItemMap.get("user_address_id"));
					orderMap.put("deliveredDate", orderItemMap.get("deliveredDate"));
					orderMap.put("returnType", orderItemMap.get("return_type"));
					orderMap.put("returnCount", orderItemMap.get("return_count"));
					orderMap.put("returnStatus", orderItemMap.get("return_status"));
					orderMap.put("returnCancelled", orderItemMap.get("return_cancelled"));
					orderMap.put("starRate", orderItemMap.get("starRate"));
					orderMap.put("orderReturnId", orderItemMap.get("order_return_id"));
					orderMap.put("gst", orderItemMap.get("gst"));
					orderMap.put("mrp", orderItemMap.get("mrp"));
					orderMap.put("alertQuantity", orderItemMap.get("alertQuantity"));
					orderMap.put("discountPercentage", orderItemMap.get("discountPercentage"));
					orderMap.put("sellRate", orderItemMap.get("sellRate"));
					orderMap.put("unit", orderItemMap.get("unit"));
					orderMap.put("gstTaxAmount", orderItemMap.get("gstTaxAmount"));
					orderMap.put("buyRate", orderItemMap.get("buyRate"));
					orderMap.put("streetAddress", orderItemMap.get("street_address"));
					orderMap.put("state", orderItemMap.get("state"));
					orderMap.put("postalCode", orderItemMap.get("postal_code"));
					orderMap.put("city", orderItemMap.get("city"));
					orderMap.put("country", orderItemMap.get("country"));
					orderMap.put("addressType", orderItemMap.get("address_type"));
					orderMap.put("status", orderItemMap.get("addressStatus"));
					orderMap.put("mobileNumber", orderItemMap.get("mobile_number"));
					orderMap.put("paymentType", orderItemMap.get("payment_type"));

					boolean demo1 = false;
					boolean demo2 = true;
					Object reviewid = orderItemMap.get("reviewId");
					if (orderItemMap.get("reviewId") == null) {
						orderMap.put("reviewStatus", demo1);
					} else if (reviewid != null && orderItemMap.get("reviewId").equals(reviewid)) {
						orderMap.put("reviewStatus", demo2);
						orderMap.put("reviewId", orderItemMap.get("reviewId"));
						orderMap.put("message", orderItemMap.get("message"));
						orderMap.put("starRate", orderItemMap.get("starRate"));
					}
					orderMap.put("productVarientImageUrl",
							"varient/" + generateRandomNumber() + "/" + orderItemMap.get("productVarientImagesId"));
					orderList.add(ordersMap);
				}
				mainOrderList.add(orderMap);
			}
			return ResponseEntity.ok(mainOrderList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user purchase details.");
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

	private String getString(Map<String, Object> map, String key) {
		Object value = map.get(key);
		return value != null ? value.toString() : "";
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/getDashboardPageDetails")
	public ResponseEntity<?> getIncomeDetails1(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getDashboardPageDetails();
				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					String metric = (String) entry.get("metric");
					Object value = entry.get("value");
					if (shouldFormatAsDouble(metric)) {
						value = formatAsDouble(value);
					}
					result.put(metric, value);
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

	private boolean shouldFormatAsDouble(String metric) {
		return metric.equals("totalOrdersCurrentDay") || metric.equals("totalCustomers")
				|| metric.equals("totalOrdersCurrentYear") || metric.equals("totalOrdersCurrentMonth")
				|| metric.equals("totalCancelledOrders") || metric.equals("totalOrders")
				|| metric.equals("totalDeliveredOrders");
	}

	private Object formatAsDouble(Object value) {
		if (value instanceof Double && (Double) value % 1 == 0) {
			return ((Double) value).intValue();
		}
		return value;
	}

	@DeleteMapping("/order/delete/{orderItemId}")
	public ResponseEntity<String> deleteOrderDetail(@PathVariable("orderItemId") Long orderItemId) {
		orderItemService.deleteOrderItemId(orderItemId);
		return ResponseEntity.ok("orderItem details deleted successfully");
	}

	@GetMapping("/dashboardPageDetail")
	public ResponseEntity<?> getDashboardPageDetails(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getDashboardDetails();

				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					result.put((String) entry.get("metric"), entry.get("value"));
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

	@GetMapping("/incomeDetails")
	public ResponseEntity<?> getIncomeDetails(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getIncomeDetails();

				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					result.put((String) entry.get("metric"), entry.get("value"));
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

// Find OrderList Details By CurrentDate
	@GetMapping("/orderListByCurrentDate")
	public ResponseEntity<?> findOrderListByCurrentDate(@RequestParam String order) {
		try {
			if ("orderListDetails".equals(order)) {
				List<Map<String, Object>> orderListDetails = orderItemRepository.findOrderListByCurrentDate();

				List<Map<String, Object>> modifiedOrderDetails = orderListDetails.stream().map(orderListDetail -> {
					Map<String, Object> result = new HashMap<>();
					result.put("orderItemId", orderListDetail.get("order_item_id"));
					result.put("date", orderListDetail.get("date"));
					result.put("userId", orderListDetail.get("user_id"));
					result.put("userName", orderListDetail.get("user_name"));
					result.put("orderItemListId", orderListDetail.get("order_item_list_id"));
					result.put("cancelled", orderListDetail.get("cancelled"));
					result.put("delivered", orderListDetail.get("delivered"));
					result.put("orderStatus", orderListDetail.get("order_status"));
					result.put("quantity", orderListDetail.get("quantity"));
					result.put("totalPrice", orderListDetail.get("total_price"));
					result.put("alertQuantity", orderListDetail.get("alert_quantity"));
					result.put("discountAmount", orderListDetail.get("discount_amount"));
					result.put("discountPercentage", orderListDetail.get("discount_percentage"));
					result.put("stockIn", orderListDetail.get("stock_in"));
					result.put("description", orderListDetail.get("description"));
					result.put("productQuantity", orderListDetail.get("product_quantity"));
					result.put("buyRate", orderListDetail.get("buy_rate"));
					result.put("unit", orderListDetail.get("unit"));
					result.put("gst", orderListDetail.get("gst"));
					result.put("gstTaxAmount", orderListDetail.get("gst_tax_amount"));
					result.put("mrp", orderListDetail.get("mrp"));
					result.put("sellRate", orderListDetail.get("sell_rate"));
					result.put("totalAmount", orderListDetail.get("total_amount"));
					result.put("productId", orderListDetail.get("product_id"));
					result.put("productName", orderListDetail.get("product_name"));
					result.put("productListId", orderListDetail.get("product_list_id"));
					result.put("productVarientId", orderListDetail.get("product_varient_id"));
					result.put("varientName", orderListDetail.get("varient_name"));
					result.put("varientValue", orderListDetail.get("varient_value"));

					result.put("productVarientImagesId", orderListDetail.get("product_varient_images_id"));
					result.put("productVarientImageUrl", "varient/" + generateRandomNumber() + "/"
							+ orderListDetail.get("product_varient_images_id"));
					return result;
				}).collect(Collectors.toList());

				return new ResponseEntity<>(modifiedOrderDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided orderList is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderList details: " + e.getMessage());
		}
	}

//	@PostMapping("/findOrderListByDateRange")
//	public ResponseEntity<List<Map<String, Object>>> findOrderListByDateRange1(
//			@RequestBody Map<String, Object> requestBody) {
//		if (!requestBody.containsKey("choose")) {
//			return ResponseEntity.badRequest().build();
//		}
//		String choose = requestBody.get("choose").toString();
//		switch (choose) {
//		case "date":
//			if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
//				LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(),
//						DateTimeFormatter.ISO_DATE);
//				LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
//				List<Map<String, Object>> orderList = orderItemRepository.findOrderListBetweenDate(startDate, endDate);
//				List<Map<String, Object>> modifiedOrderDetails = new ArrayList<>();
//				for (Map<String, Object> orderDetail : orderList) {
//					int randomNumber = generateRandomNumber();
//					String imageUrl = "varient/" + randomNumber + "/" + orderDetail.get("productVarientImagesId");
//					Map<String, Object> modifiedDetail = new HashMap<>(orderDetail);
//					modifiedDetail.put("productVarientImageUrl", imageUrl);
//					modifiedOrderDetails.add(modifiedDetail);
//				}
//				return ResponseEntity.ok(modifiedOrderDetails);
//			}
//			break;
//		case "month":
//			if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
//				String month = requestBody.get("monthName").toString();
//				String year = requestBody.get("year").toString();
//				List<Map<String, Object>> orderList = orderItemRepository.findOrderListByMonthYear(month, year);
//				List<Map<String, Object>> modifiedOrderDetails = new ArrayList<>();
//				for (Map<String, Object> orderDetail : orderList) {
//					int randomNumber = generateRandomNumber();
//					String imageUrl = "varient/" + randomNumber + "/" + orderDetail.get("productVarientImagesId");
//					Map<String, Object> modifiedDetail = new HashMap<>(orderDetail);
//					modifiedDetail.put("productVarientImageUrl", imageUrl);
//					modifiedOrderDetails.add(modifiedDetail);
//				}
//				return ResponseEntity.ok(modifiedOrderDetails);
//			}
//
//			break;
//		default:
//			return ResponseEntity.badRequest().build();
//		}
//		return ResponseEntity.badRequest().build();
//
//	}

	@PostMapping("/findOrderListByDateRange")
	public ResponseEntity<List<Map<String, Object>>> findStockListByDateRange1(
			@RequestBody Map<String, Object> requestBody) {
		if (!requestBody.containsKey("choose")) {
			return ResponseEntity.badRequest().build();
		}

		String choose = requestBody.get("choose").toString();

		switch (choose) {
		case "date":
			if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
				LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(),
						DateTimeFormatter.ISO_DATE);
				LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
				List<Map<String, Object>> stockList = orderItemRepository.findOrderListBetweenDate(startDate, endDate);
				return buildStockResponse(stockList);
			}
			break;
		case "month":
			if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
				String month = requestBody.get("monthName").toString();
				String year = requestBody.get("year").toString();
				List<Map<String, Object>> stockList = orderItemRepository.findOrderListByMonthYear(month, year);
				return buildStockResponse(stockList);
			}
			break;
		default:
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.badRequest().build();
	}

	private ResponseEntity<List<Map<String, Object>>> buildStockResponse(List<Map<String, Object>> stockList) {
		Set<String> uniqueStockIds = new HashSet<>();
		List<Map<String, Object>> uniqueStockList = new ArrayList<>();

		for (Map<String, Object> item : stockList) {
			String stockListId = item.get("orderItemListId").toString();
			if (!uniqueStockIds.contains(stockListId)) {
				uniqueStockIds.add(stockListId);
				Map<String, Object> result = new HashMap<>();
				result.put("productVarientImageUrl",
						"varient/" + generateRandomNumber() + "/" + item.get("productVarientImagesId"));
				result.putAll(item);
				uniqueStockList.add(result);
			}
		}

		return ResponseEntity.ok(uniqueStockList);
	}

	@GetMapping("/orderDetails/view/{userId}/{productName}")
	public ResponseEntity<List<Map<String, Object>>> getOrderDetailByProductName(@PathVariable("userId") Long userId,
			@PathVariable("productName") String productName) {
		List<Map<String, Object>> orderDetails = orderItemRepository.getOrderDetailsByProductName(userId, productName);

		if (orderDetails.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<Map<String, Object>> response = orderDetails.stream().map(order -> {
			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("userId", order.get("user_id"));
			orderMap.put("orderItemId", order.get("order_item_id"));
			orderMap.put("totalPrice", order.get("totalPrice"));
			orderMap.put("totalItems", order.get("total_items"));
			orderMap.put("orderItemListId", order.get("order_item_list_id"));
			orderMap.put("listDescription", order.get("listDescription"));
			orderMap.put("orderItemQuantity", order.get("orderItemQuantity"));
			orderMap.put("orderStatus", order.get("order_status"));
			orderMap.put("date", order.get("date"));
			orderMap.put("productId", order.get("product_id"));
			orderMap.put("productName", order.get("product_name"));
			orderMap.put("productListId", order.get("product_list_id"));
			orderMap.put("buyRate", order.get("buy_rate"));
			orderMap.put("discountAmount", order.get("discount_amount"));
			orderMap.put("discountPercentage", order.get("discount_percentage"));
			orderMap.put("gst", order.get("gst"));
			orderMap.put("gstTaxAmount", order.get("gst_tax_amount"));
			orderMap.put("mrp", order.get("mrp"));
			orderMap.put("sellRate", order.get("sell_rate"));
			orderMap.put("alertQuantity", order.get("alert_quantity"));
			orderMap.put("productVarientImagesId", order.get("product_varient_images_id"));
			orderMap.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + order.get("product_varient_images_id"));

			return orderMap;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/sales/count/year")
	public List<Map<String, Object>> getAllCount(@RequestParam(required = true) String order) {
		try {
			if ("count".equalsIgnoreCase(order)) {
				return orderItemRepository.getOrderCounts();
			} else {
				throw new IllegalArgumentException("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@GetMapping("/orderCount/details")
	public List<Map<String, Object>> getAllOrderCounts(@RequestParam(required = true) String order) {
		try {
			if ("countDetails".equalsIgnoreCase(order)) {
				return orderItemRepository.getOrderDetails();
			} else {
				throw new IllegalArgumentException("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@GetMapping("/userPurchaseDetails")
	public ResponseEntity<Object> getAllPurchaseDetails(@RequestParam(required = true) String orderItem) {
		if ("orderItemDetails".equals(orderItem)) {
			try {
				List<Map<String, Object>> mainOrderList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetails();

				Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> userGroupMap = userRole
						.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> getString(action, "order_item_list_id"),
										Collectors.groupingBy(action -> getString(action, "product_varient_id"),
												Collectors.groupingBy(
														action -> getString(action, "product_varient_images_id"))))));

				for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> orderItemListList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> orderItemListLoop : userLoop
							.getValue().entrySet()) {
						Map<String, Object> orderItemListMap = new HashMap<>();
						orderItemListMap.put("orderItemListId", orderItemListLoop.getKey());

						List<Map<String, Object>> varientList = new ArrayList<>();
						List<Map<String, Object>> orderList = new ArrayList<>();
						for (Map.Entry<String, Map<String, List<Map<String, Object>>>> varientLoop : orderItemListLoop
								.getValue().entrySet()) {
							Map<String, Object> varientMap = new HashMap<>();
							varientMap.put("productVarientId", varientLoop.getKey());

							List<Map<String, Object>> varientImageList = new ArrayList<>();
							for (Map.Entry<String, List<Map<String, Object>>> varientImageLoop : varientLoop.getValue()
									.entrySet()) {
								Map<String, Object> varientImageMap = new HashMap<>();
								String productVarientImagesId = varientImageLoop.getKey();
								varientImageMap.put("productVarientImagesId", productVarientImagesId);

								for (Map<String, Object> orderItemMap : varientImageLoop.getValue()) {
									Map<String, Object> orderMap = new HashMap<>();

									orderMap.put("productName", orderItemMap.get("product_name"));
									orderItemListMap.put("orderItemId", orderItemMap.get("order_item_id"));
									userMap.put("orderTotalAmount", orderItemMap.get("orderTotalAmount"));
									orderItemListMap.put("orderItemListId", orderItemMap.get("order_item_list_id"));
									orderItemListMap.put("orderStatus", orderItemMap.get("order_status"));
									orderItemListMap.put("confirmed", orderItemMap.get("confirmed"));
									orderItemListMap.put("delivered", orderItemMap.get("delivered"));
									orderItemListMap.put("cancelled", orderItemMap.get("cancelled"));
									orderItemListMap.put("productName", orderItemMap.get("product_name"));
									orderItemListMap.put("productId", orderItemMap.get("product_id"));
									orderItemListMap.put("quantity", orderItemMap.get("quantity"));
									orderItemListMap.put("productListId", orderItemMap.get("product_list_id"));
									orderItemListMap.put("totalPrice", orderItemMap.get("total_price"));
									orderItemListMap.put("totalAmount", orderItemMap.get("total_amount"));
									varientMap.put("varientName", orderItemMap.get("varient_name"));
									varientMap.put("varientValue", orderItemMap.get("varient_value"));
									userMap.put("paymentType", orderItemMap.get("payment_type"));
									userMap.put("paymentStatus", orderItemMap.get("payment_status"));

									userMap.put("date", orderItemMap.get("date"));
									userMap.put("userName", orderItemMap.get("user_name"));
									userMap.put("mobileNumber", orderItemMap.get("mobile_number"));
									int randomNumber = generateRandomNumber();
									String productVarientImageUrl = "varient/" + randomNumber + "/"
											+ productVarientImagesId;
									varientImageMap.put("productVarientImageUrl", productVarientImageUrl);

									orderList.add(orderMap);
								}

								varientImageList.add(varientImageMap);
							}
							orderItemListMap.put("varientImages", varientImageList);
							varientList.add(varientMap);
						}
						orderItemListMap.put("varientValues", varientList);
						orderItemListList.add(orderItemListMap);
					}
					userMap.put("orderItemList", orderItemListList);
					mainOrderList.add(userMap);
				}

				return ResponseEntity.ok(mainOrderList);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user purchase details.");
			}
		} else {
			String errorMessage = "Invalid value for 'orderItem'. Expected 'orderItemDetails'.";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
	}
	
	
	

	@GetMapping("/userPurchaseDetails/demo")
	public ResponseEntity<Object> getAllPurchaseDetails6(@RequestParam(required = true) String orderItem) {
	    if (!"orderItemDetails".equals(orderItem)) {
	        String errorMessage = "Invalid value for 'orderItem'. Expected 'orderItemDetails'.";
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	    }

	    try {
	        List<Map<String, Object>> mainOrderList = new ArrayList<>();
	        List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetails();

	        // Grouping the userRole list by different IDs
	        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole
	                .stream()
	                .collect(Collectors.groupingBy(
	                        action -> getString1(action, "order_item_id"),
	                        TreeMap::new,
	                        Collectors.groupingBy(
	                                action -> getString1(action, "order_item_list_id"),
	                                Collectors.groupingBy(
	                                        action -> getString1(action, "product_images_id")
	                                )
	                        )
	                ));

	        // Reversing the order of userGroupMap
	        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> reversedUserGroupMap = new LinkedHashMap<>();
	        userGroupMap.entrySet()
	                .stream()
	                .sorted(Map.Entry.<String, Map<String, Map<String, List<Map<String, Object>>>>>comparingByKey().reversed())
	                .forEachOrdered(entry -> reversedUserGroupMap.put(entry.getKey(), entry.getValue()));

	        // Loop through the grouped and reversed map to structure the response
	        for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> orderGroupEntry : reversedUserGroupMap.entrySet()) {
	            Map<String, Object> orderItemMap = new HashMap<>();
	            String orderItemId = orderGroupEntry.getKey();
	            orderItemMap.put("orderItemId", orderItemId);

	            List<Map<String, Object>> orderItemList = new ArrayList<>();
	            for (Map.Entry<String, Map<String, List<Map<String, Object>>>> orderItemGroupEntry : orderGroupEntry.getValue().entrySet()) {
	                Map<String, Object> orderItemListMap = new HashMap<>();
	                orderItemListMap.put("orderItemListId", orderItemGroupEntry.getKey());

	                List<Map<String, Object>> varientImagesList = new ArrayList<>();
	                for (Map.Entry<String, List<Map<String, Object>>> varientImageEntry : orderItemGroupEntry.getValue().entrySet()) {
	                    for (Map<String, Object> detail : varientImageEntry.getValue()) {
	                        if (varientImagesList.isEmpty()) {
	                            orderItemMap.put("date", detail.get("date"));
	                            orderItemMap.put("invoiceFlag", detail.get("invoice_flag"));
	                            orderItemMap.put("orderTotalAmount", detail.get("orderTotalAmount"));
	                            orderItemMap.put("mobileNumber", detail.get("mobile_number"));
	                            orderItemMap.put("invoiceStatus", detail.get("invoice_status"));
	                            orderItemMap.put("userName", detail.get("user_name"));
	                            orderItemMap.put("productName", detail.get("product_name"));
	                            orderItemMap.put("paymentStatus", detail.get("payment_status"));
	                            orderItemMap.put("paymentType", detail.get("payment_type"));
	                        }
	                        orderItemListMap.put("quantity", detail.get("quantity"));
	                        orderItemListMap.put("productId", detail.get("product_id"));
	                        orderItemListMap.put("totalPrice", detail.get("total_price"));
	                        orderItemListMap.put("orderStatus", detail.get("order_status"));
	                        orderItemListMap.put("delivered", detail.get("delivered"));
	                        orderItemListMap.put("productListId", detail.get("product_list_id"));
	                        orderItemListMap.put("confirmed", detail.get("confirmed"));
	                        orderItemListMap.put("productName", detail.get("product_name"));
	                        orderItemListMap.put("totalAmount", detail.get("total_amount"));
	                        orderItemListMap.put("cancelled", detail.get("cancelled"));
	                        orderItemListMap.put("url", detail.get("url"));
	                        orderItemListMap.put("invoicePdf", detail.get("invoice_pdf"));
	                      
	                        
	                        Map<String, Object> varientImagesMap = new HashMap<>();
	                        varientImagesMap.put("productImagesId", varientImageEntry.getKey());
	                        int randomNumber = generateRandomNumber();
	                        String productVarientImageUrl = "product/" + randomNumber + "/" + varientImageEntry.getKey();
	                        varientImagesMap.put("productImagesUploadUrl", productVarientImageUrl);
	                        varientImagesList.add(varientImagesMap);
	                    }
	                }
	                orderItemListMap.put("productImages", varientImagesList);
	                orderItemList.add(orderItemListMap);
	            }
	            orderItemMap.put("orderItemListDetails", orderItemList);
	            mainOrderList.add(orderItemMap);
	        }
	        return ResponseEntity.ok(mainOrderList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user purchase details.");
	    }
	}

	// Assuming getString1 and generateRandomNumber are defined elsewhere in your code

	private static String getString1(Map<String, Object> map, String key) {
	    return map.get(key) != null ? map.get(key).toString() : null;
	}

	@GetMapping("/count1/{userId}")
	public ResponseEntity<Map<String, List<Map<String, Object>>>> getAllDataBetweenDates11(@PathVariable long userId) {
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();

		resultMap.put("orderPending", orderItemRepository.getAllFamilyInformations(userId));
		resultMap.put("orderConfirmed", orderItemRepository.getQualifications(userId));

		return ResponseEntity.ok(resultMap);
	}

	@GetMapping("/userPurchaseDetail/{id}")
	public ResponseEntity<Object> getUserPurchaseDetails(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetailsByUserId(userId);

			Map<String, Map<String, List<Map<String, Object>>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
							Collectors.groupingBy(action -> action.get("date").toString())));

			for (Map.Entry<String, Map<String, List<Map<String, Object>>>> userLoop : userGroupMap.entrySet()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userLoop.getKey());

				List<Map<String, Object>> dateList = new ArrayList<>();
				for (Map.Entry<String, List<Map<String, Object>>> dateLoop : userLoop.getValue().entrySet()) {
					Map<String, Object> dateMap = new HashMap<>();
					dateMap.put("date", dateLoop.getKey());

					List<Map<String, Object>> orderList = new ArrayList<>();
					for (Map<String, Object> orderItemMap : dateLoop.getValue()) {
						Map<String, Object> orderMap = new HashMap<>();
						orderMap.put("orderItemId", orderItemMap.get("order_item_id"));
						orderMap.put("orderItemListId", orderItemMap.get("order_item_list_id"));
						orderMap.put("cancelled", orderItemMap.get("cancelled"));
						orderMap.put("delivered", orderItemMap.get("delivered"));
						orderMap.put("orderStatus", orderItemMap.get("order_status"));
						orderMap.put("productListId", orderItemMap.get("product_list_id"));
						orderMap.put("quantity", orderItemMap.get("quantity"));
						orderMap.put("totalPrice", orderItemMap.get("total_price"));

						orderList.add(orderMap);
					}

					dateMap.put("ListOfPurchaseOrder", orderList);
					dateList.add(dateMap);
				}

				userMap.put("userName", userRole.get(0).get("user_name"));
				userMap.put("userDetails", dateList);
//				mainOrderList.add(userMap);
				return ResponseEntity.ok(userMap);
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user purchase details.");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User  not found.");
	}

	@GetMapping("/order/item/{userId}")
	public ResponseEntity<?> getAllDataBetweenDates(@PathVariable long userId) {
		Map<String, Object> ob = new HashMap<>();
////////////// Pending ////////////
		List<Map<String, Object>> pendingRole = orderItemRepository.getQualifications(userId);

		List<Map<String, Object>> pendingList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Pending ////////////

//////////////Delivered ////////////
		List<Map<String, Object>> deliveredRole = orderItemRepository.getAllFamilyInformations(userId);

		List<Map<String, Object>> deliveredList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> deliveredGroupMap = deliveredRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Delivered ////////////

//////////////Confirmed ////////////
		List<Map<String, Object>> confirmedRole = orderItemRepository.getAllConfirmedInformations(userId);

		List<Map<String, Object>> confirmedList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> confirmedGroupMap = confirmedRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Confirmed ////////////

		for (Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> pendingMap = new HashMap<>();
			pendingMap.put("orderItemListId", pendingLoop.getKey());
			pendingMap.put("orderStatus", pendingLoop.getValue().get(0).get("orderStatus"));
			pendingMap.put("orderItemId", pendingLoop.getValue().get(0).get("orderItemId"));
			pendingMap.put("date", pendingLoop.getValue().get(0).get("date"));
			pendingMap.put("totalItems", pendingLoop.getValue().get(0).get("totalItems"));
			pendingMap.put("orderTotalPrice", pendingLoop.getValue().get(0).get("orderTotalPrice"));
			pendingMap.put("userId", pendingLoop.getValue().get(0).get("userId"));
			pendingMap.put("productId", pendingLoop.getValue().get(0).get("productId"));
			pendingMap.put("productListId", pendingLoop.getValue().get(0).get("productListId"));
			pendingMap.put("totalAmount", pendingLoop.getValue().get(0).get("totalAmount"));
			pendingMap.put("totalPrice", pendingLoop.getValue().get(0).get("totalPrice"));
			pendingMap.put("productName", pendingLoop.getValue().get(0).get("productName"));
			pendingMap.put("productImagesId", pendingLoop.getValue().get(0).get("product_images_id"));
			pendingMap.put("description", pendingLoop.getValue().get(0).get("description"));
			pendingMap.put("reviewId", pendingLoop.getValue().get(0).get("reviewId"));
			pendingMap.put("starRate", pendingLoop.getValue().get(0).get("starRate"));
			pendingMap.put("gst", pendingLoop.getValue().get(0).get("gst"));
			pendingMap.put("mrp", pendingLoop.getValue().get(0).get("mrp"));
			pendingMap.put("orderId", pendingLoop.getValue().get(0).get("order_id"));
			pendingMap.put("discountPercentage", pendingLoop.getValue().get(0).get("discountPercentage"));
			pendingMap.put("unit", pendingLoop.getValue().get(0).get("unit"));
			pendingMap.put("gstTaxAmount", pendingLoop.getValue().get(0).get("gstTaxAmount"));
			pendingMap.put("buyRate", pendingLoop.getValue().get(0).get("buyRate"));
			pendingMap.put("quantity", pendingLoop.getValue().get(0).get("quantity"));

			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ pendingLoop.getValue().get(0).get("product_images_id");
			pendingMap.put("productImageUrl", productVarientImageUrl);

			pendingList.add(pendingMap);
		}

		for (Entry<String, List<Map<String, Object>>> deliveredLoop : deliveredGroupMap.entrySet()) {
			Map<String, Object> deliveredMap = new HashMap<>();
			deliveredMap.put("orderItemListId", deliveredLoop.getKey());
			deliveredMap.put("orderStatus", deliveredLoop.getValue().get(0).get("orderStatus"));
			deliveredMap.put("orderItemId", deliveredLoop.getValue().get(0).get("orderItemId"));
			deliveredMap.put("date", deliveredLoop.getValue().get(0).get("date"));
			deliveredMap.put("totalItems", deliveredLoop.getValue().get(0).get("totalItems"));
			deliveredMap.put("orderTotalPrice", deliveredLoop.getValue().get(0).get("orderTotalPrice"));
			deliveredMap.put("returnCancelled", deliveredLoop.getValue().get(0).get("returnCancelled"));
			deliveredMap.put("userId", deliveredLoop.getValue().get(0).get("userId"));
			deliveredMap.put("productId", deliveredLoop.getValue().get(0).get("productId"));
			deliveredMap.put("productListId", deliveredLoop.getValue().get(0).get("productListId"));
			deliveredMap.put("totalAmount", deliveredLoop.getValue().get(0).get("totalAmount"));
			deliveredMap.put("totalPrice", deliveredLoop.getValue().get(0).get("totalPrice"));
			deliveredMap.put("productName", deliveredLoop.getValue().get(0).get("productName"));
			deliveredMap.put("productImagesId", deliveredLoop.getValue().get(0).get("product_images_id"));
			deliveredMap.put("description", deliveredLoop.getValue().get(0).get("description"));
			deliveredMap.put("reviewId", deliveredLoop.getValue().get(0).get("reviewId"));
			deliveredMap.put("starRate", deliveredLoop.getValue().get(0).get("starRate"));
			deliveredMap.put("gst", deliveredLoop.getValue().get(0).get("gst"));
			deliveredMap.put("mrp", deliveredLoop.getValue().get(0).get("mrp"));
			deliveredMap.put("orderId", deliveredLoop.getValue().get(0).get("order_id"));
			deliveredMap.put("discountPercentage", deliveredLoop.getValue().get(0).get("discountPercentage"));
			deliveredMap.put("unit", deliveredLoop.getValue().get(0).get("unit"));
			deliveredMap.put("gstTaxAmount", deliveredLoop.getValue().get(0).get("gstTaxAmount"));
			deliveredMap.put("buyRate", deliveredLoop.getValue().get(0).get("buyRate"));
			deliveredMap.put("quantity", deliveredLoop.getValue().get(0).get("quantity"));
			deliveredMap.put("deliveredDate", deliveredLoop.getValue().get(0).get("deliveredDate"));
			deliveredMap.put("pdfUrl", deliveredLoop.getValue().get(0).get("pdf_url"));
			deliveredMap.put("returnType", deliveredLoop.getValue().get(0).get("return_type"));
			deliveredMap.put("returnStatus", deliveredLoop.getValue().get(0).get("return_status"));
			deliveredMap.put("returnCount", deliveredLoop.getValue().get(0).get("return_count"));
			
			
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ deliveredLoop.getValue().get(0).get("product_images_id");
			deliveredMap.put("productImageUrl", productVarientImageUrl);

			deliveredList.add(deliveredMap);
		}

		for (Entry<String, List<Map<String, Object>>> confirmedLoop : confirmedGroupMap.entrySet()) {
			Map<String, Object> confirmedMap = new HashMap<>();
			confirmedMap.put("orderItemListId", confirmedLoop.getKey());
			confirmedMap.put("orderStatus", confirmedLoop.getValue().get(0).get("orderStatus"));
			confirmedMap.put("orderItemId", confirmedLoop.getValue().get(0).get("orderItemId"));
			confirmedMap.put("date", confirmedLoop.getValue().get(0).get("date"));
			confirmedMap.put("totalItems", confirmedLoop.getValue().get(0).get("totalItems"));
			confirmedMap.put("orderTotalPrice", confirmedLoop.getValue().get(0).get("orderTotalPrice"));
			confirmedMap.put("userId", confirmedLoop.getValue().get(0).get("userId"));
			confirmedMap.put("productId", confirmedLoop.getValue().get(0).get("productId"));
			confirmedMap.put("productListId", confirmedLoop.getValue().get(0).get("productListId"));
			confirmedMap.put("totalAmount", confirmedLoop.getValue().get(0).get("totalAmount"));
			confirmedMap.put("totalPrice", confirmedLoop.getValue().get(0).get("totalPrice"));
			confirmedMap.put("productName", confirmedLoop.getValue().get(0).get("productName"));
			confirmedMap.put("productImagesId", confirmedLoop.getValue().get(0).get("product_images_id"));
			confirmedMap.put("description", confirmedLoop.getValue().get(0).get("description"));
			confirmedMap.put("reviewId", confirmedLoop.getValue().get(0).get("reviewId"));
			confirmedMap.put("starRate", confirmedLoop.getValue().get(0).get("starRate"));
			confirmedMap.put("gst", confirmedLoop.getValue().get(0).get("gst"));
			confirmedMap.put("mrp", confirmedLoop.getValue().get(0).get("mrp"));
			confirmedMap.put("orderId", confirmedLoop.getValue().get(0).get("order_id"));
			confirmedMap.put("discountPercentage", confirmedLoop.getValue().get(0).get("discountPercentage"));
			confirmedMap.put("unit", confirmedLoop.getValue().get(0).get("unit"));
			confirmedMap.put("gstTaxAmount", confirmedLoop.getValue().get(0).get("gstTaxAmount"));
			confirmedMap.put("buyRate", confirmedLoop.getValue().get(0).get("buyRate"));
			confirmedMap.put("quantity", confirmedLoop.getValue().get(0).get("quantity"));
			confirmedMap.put("deliveredDate", confirmedLoop.getValue().get(0).get("deliveredDate"));
			confirmedMap.put("pdfUrl", confirmedLoop.getValue().get(0).get("pdf_url"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ confirmedLoop.getValue().get(0).get("product_images_id");
			confirmedMap.put("productImageUrl", productVarientImageUrl);

			confirmedList.add(confirmedMap);
		}

		ob.put("pending", pendingList);
		ob.put("delivered", deliveredList);
		ob.put("confirmed", confirmedList);
//	    mainList.add(ob);

		return ResponseEntity.ok(ob);
	}

	
	
	@GetMapping("/order/item2/{userId}")
	public ResponseEntity<?> getAllDataBetweenDates2(@PathVariable long userId) {
		Map<String, Object> ob = new HashMap<>();
////////////// Pending ////////////
		List<Map<String, Object>> pendingRole = orderItemRepository.getQualifications1(userId);

		List<Map<String, Object>> pendingList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Pending ////////////

//////////////Delivered ////////////
		List<Map<String, Object>> deliveredRole = orderItemRepository.getAllFamilyInformations(userId);

		List<Map<String, Object>> deliveredList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> deliveredGroupMap = deliveredRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Delivered ////////////

//////////////Confirmed ////////////
		List<Map<String, Object>> confirmedRole = orderItemRepository.getAllConfirmedInformations(userId);

		List<Map<String, Object>> confirmedList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> confirmedGroupMap = confirmedRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
//////////////Confirmed ////////////

		for (Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> pendingMap = new HashMap<>();
			pendingMap.put("orderItemListId", pendingLoop.getValue().get(0).get("orderItemListId"));
			pendingMap.put("orderStatus", pendingLoop.getValue().get(0).get("orderStatus"));
			pendingMap.put("orderItemId", pendingLoop.getValue().get(0).get("orderItemId"));
			pendingMap.put("date", pendingLoop.getValue().get(0).get("date"));
			pendingMap.put("totalItems", pendingLoop.getValue().get(0).get("totalItems"));
			pendingMap.put("orderTotalPrice", pendingLoop.getValue().get(0).get("orderTotalPrice"));
			pendingMap.put("userId", pendingLoop.getValue().get(0).get("userId"));
			pendingMap.put("productId", pendingLoop.getValue().get(0).get("productId"));
			pendingMap.put("productListId", pendingLoop.getValue().get(0).get("productListId"));
			pendingMap.put("totalAmount", pendingLoop.getValue().get(0).get("totalAmount"));
			pendingMap.put("totalPrice", pendingLoop.getValue().get(0).get("totalPrice"));
			pendingMap.put("productName", pendingLoop.getValue().get(0).get("productName"));
			pendingMap.put("productImagesId", pendingLoop.getValue().get(0).get("product_images_id"));
			pendingMap.put("description", pendingLoop.getValue().get(0).get("description"));
			pendingMap.put("reviewId", pendingLoop.getValue().get(0).get("reviewId"));
			pendingMap.put("starRate", pendingLoop.getValue().get(0).get("starRate"));
			pendingMap.put("gst", pendingLoop.getValue().get(0).get("gst"));
			pendingMap.put("mrp", pendingLoop.getValue().get(0).get("mrp"));
			pendingMap.put("orderId", pendingLoop.getValue().get(0).get("order_id"));
			pendingMap.put("discountPercentage", pendingLoop.getValue().get(0).get("discountPercentage"));
			pendingMap.put("unit", pendingLoop.getValue().get(0).get("unit"));
			pendingMap.put("gstTaxAmount", pendingLoop.getValue().get(0).get("gstTaxAmount"));
			pendingMap.put("buyRate", pendingLoop.getValue().get(0).get("buyRate"));
			pendingMap.put("quantity", pendingLoop.getValue().get(0).get("quantity"));
			pendingMap.put("userAddressId", pendingLoop.getValue().get(0).get("user_address_id"));
			pendingMap.put("addressType", pendingLoop.getValue().get(0).get("address_type"));
			pendingMap.put("city", pendingLoop.getValue().get(0).get("city"));
			pendingMap.put("country", pendingLoop.getValue().get(0).get("country"));
			pendingMap.put("mobileNumber", pendingLoop.getValue().get(0).get("mobile_number"));
			pendingMap.put("postalCode", pendingLoop.getValue().get(0).get("postal_code"));
			pendingMap.put("state", pendingLoop.getValue().get(0).get("state"));
			pendingMap.put("streetAddress", pendingLoop.getValue().get(0).get("street_address"));
			pendingMap.put("district", pendingLoop.getValue().get(0).get("district"));
			pendingMap.put("returnType", pendingLoop.getValue().get(0).get("return_type"));
			pendingMap.put("returnStatus", pendingLoop.getValue().get(0).get("return_status"));
			pendingMap.put("returnCount", pendingLoop.getValue().get(0).get("return_count"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ pendingLoop.getValue().get(0).get("product_images_id");
			pendingMap.put("productImageUrl", productVarientImageUrl);

			pendingList.add(pendingMap);
		}

		for (Entry<String, List<Map<String, Object>>> deliveredLoop : deliveredGroupMap.entrySet()) {
			Map<String, Object> deliveredMap = new HashMap<>();
			deliveredMap.put("orderItemListId", deliveredLoop.getValue().get(0).get("orderItemListId"));
			deliveredMap.put("orderStatus", deliveredLoop.getValue().get(0).get("orderStatus"));
			deliveredMap.put("orderItemId", deliveredLoop.getValue().get(0).get("orderItemId"));
			deliveredMap.put("date", deliveredLoop.getValue().get(0).get("date"));
			deliveredMap.put("totalItems", deliveredLoop.getValue().get(0).get("totalItems"));
			deliveredMap.put("orderTotalPrice", deliveredLoop.getValue().get(0).get("orderTotalPrice"));
			deliveredMap.put("returnCancelled", deliveredLoop.getValue().get(0).get("returnCancelled"));
			deliveredMap.put("userId", deliveredLoop.getValue().get(0).get("userId"));
			deliveredMap.put("productId", deliveredLoop.getValue().get(0).get("productId"));
			deliveredMap.put("productListId", deliveredLoop.getValue().get(0).get("productListId"));
			deliveredMap.put("totalAmount", deliveredLoop.getValue().get(0).get("totalAmount"));
			deliveredMap.put("totalPrice", deliveredLoop.getValue().get(0).get("totalPrice"));
			deliveredMap.put("productName", deliveredLoop.getValue().get(0).get("productName"));
			deliveredMap.put("productImagesId", deliveredLoop.getValue().get(0).get("product_images_id"));
			deliveredMap.put("description", deliveredLoop.getValue().get(0).get("description"));
			deliveredMap.put("reviewId", deliveredLoop.getValue().get(0).get("reviewId"));
			deliveredMap.put("starRate", deliveredLoop.getValue().get(0).get("starRate"));
			deliveredMap.put("gst", deliveredLoop.getValue().get(0).get("gst"));
			deliveredMap.put("mrp", deliveredLoop.getValue().get(0).get("mrp"));
			deliveredMap.put("orderId", deliveredLoop.getValue().get(0).get("order_id"));
			deliveredMap.put("discountPercentage", deliveredLoop.getValue().get(0).get("discountPercentage"));
			deliveredMap.put("unit", deliveredLoop.getValue().get(0).get("unit"));
			deliveredMap.put("gstTaxAmount", deliveredLoop.getValue().get(0).get("gstTaxAmount"));
			deliveredMap.put("buyRate", deliveredLoop.getValue().get(0).get("buyRate"));
			deliveredMap.put("quantity", deliveredLoop.getValue().get(0).get("quantity"));
			deliveredMap.put("deliveredDate", deliveredLoop.getValue().get(0).get("deliveredDate"));
			deliveredMap.put("pdfUrl", deliveredLoop.getValue().get(0).get("pdf_url"));
			deliveredMap.put("userAddressId", deliveredLoop.getValue().get(0).get("user_address_id"));
			deliveredMap.put("addressType", deliveredLoop.getValue().get(0).get("address_type"));
			deliveredMap.put("city", deliveredLoop.getValue().get(0).get("city"));
			deliveredMap.put("country", deliveredLoop.getValue().get(0).get("country"));
			deliveredMap.put("mobileNumber", deliveredLoop.getValue().get(0).get("mobile_number"));
			deliveredMap.put("postalCode", deliveredLoop.getValue().get(0).get("postal_code"));
			deliveredMap.put("state", deliveredLoop.getValue().get(0).get("state"));
			deliveredMap.put("streetAddress", deliveredLoop.getValue().get(0).get("street_address"));
			deliveredMap.put("district", deliveredLoop.getValue().get(0).get("district"));
			deliveredMap.put("pdfBlob", deliveredLoop.getValue().get(0).get("pdf_url"));
			deliveredMap.put("returnType", deliveredLoop.getValue().get(0).get("return_type"));
			deliveredMap.put("returnStatus", deliveredLoop.getValue().get(0).get("return_status"));
			deliveredMap.put("returnCount", deliveredLoop.getValue().get(0).get("return_count"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ deliveredLoop.getValue().get(0).get("product_images_id");
			deliveredMap.put("productImageUrl", productVarientImageUrl);

			deliveredList.add(deliveredMap);
		}

		for (Entry<String, List<Map<String, Object>>> confirmedLoop : confirmedGroupMap.entrySet()) {
			Map<String, Object> confirmedMap = new HashMap<>();
			confirmedMap.put("orderItemListId", confirmedLoop.getValue().get(0).get("orderItemListId"));
			confirmedMap.put("orderStatus", confirmedLoop.getValue().get(0).get("orderStatus"));
			confirmedMap.put("orderItemId", confirmedLoop.getValue().get(0).get("orderItemId"));
			confirmedMap.put("date", confirmedLoop.getValue().get(0).get("date"));
			confirmedMap.put("totalItems", confirmedLoop.getValue().get(0).get("totalItems"));
			confirmedMap.put("orderTotalPrice", confirmedLoop.getValue().get(0).get("orderTotalPrice"));
			confirmedMap.put("userId", confirmedLoop.getValue().get(0).get("userId"));
			confirmedMap.put("productId", confirmedLoop.getValue().get(0).get("productId"));
			confirmedMap.put("productListId", confirmedLoop.getValue().get(0).get("productListId"));
			confirmedMap.put("totalAmount", confirmedLoop.getValue().get(0).get("totalAmount"));
			confirmedMap.put("totalPrice", confirmedLoop.getValue().get(0).get("totalPrice"));
			confirmedMap.put("productName", confirmedLoop.getValue().get(0).get("productName"));
			confirmedMap.put("productImagesId", confirmedLoop.getValue().get(0).get("product_images_id"));
			confirmedMap.put("description", confirmedLoop.getValue().get(0).get("description"));
			confirmedMap.put("reviewId", confirmedLoop.getValue().get(0).get("reviewId"));
			confirmedMap.put("starRate", confirmedLoop.getValue().get(0).get("starRate"));
			confirmedMap.put("gst", confirmedLoop.getValue().get(0).get("gst"));
			confirmedMap.put("mrp", confirmedLoop.getValue().get(0).get("mrp"));
			confirmedMap.put("orderId", confirmedLoop.getValue().get(0).get("order_id"));
			confirmedMap.put("discountPercentage", confirmedLoop.getValue().get(0).get("discountPercentage"));
			confirmedMap.put("unit", confirmedLoop.getValue().get(0).get("unit"));
			confirmedMap.put("gstTaxAmount", confirmedLoop.getValue().get(0).get("gstTaxAmount"));
			confirmedMap.put("buyRate", confirmedLoop.getValue().get(0).get("buyRate"));
			confirmedMap.put("quantity", confirmedLoop.getValue().get(0).get("quantity"));
			confirmedMap.put("deliveredDate", confirmedLoop.getValue().get(0).get("deliveredDate"));
			confirmedMap.put("pdfUrl", confirmedLoop.getValue().get(0).get("pdf_url"));
			confirmedMap.put("userAddressId", confirmedLoop.getValue().get(0).get("user_address_id"));
			confirmedMap.put("addressType", confirmedLoop.getValue().get(0).get("address_type"));
			confirmedMap.put("city", confirmedLoop.getValue().get(0).get("city"));
			confirmedMap.put("country", confirmedLoop.getValue().get(0).get("country"));
			confirmedMap.put("mobileNumber", confirmedLoop.getValue().get(0).get("mobile_number"));
			confirmedMap.put("postalCode", confirmedLoop.getValue().get(0).get("postal_code"));
			confirmedMap.put("state", confirmedLoop.getValue().get(0).get("state"));
			confirmedMap.put("streetAddress", confirmedLoop.getValue().get(0).get("street_address"));
			confirmedMap.put("district", confirmedLoop.getValue().get(0).get("district"));
			confirmedMap.put("returnType", confirmedLoop.getValue().get(0).get("return_type"));
			confirmedMap.put("returnStatus", confirmedLoop.getValue().get(0).get("return_status"));
			confirmedMap.put("returnCount", confirmedLoop.getValue().get(0).get("return_count"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ confirmedLoop.getValue().get(0).get("product_images_id");
			confirmedMap.put("productImageUrl", productVarientImageUrl);

			confirmedList.add(confirmedMap);
		}

		ob.put("pendingAndConfirmed", pendingList);
		ob.put("delivered", deliveredList);
//		ob.put("confirmed", confirmedList);
//	    mainList.add(ob);

		return ResponseEntity.ok(ob);
	}
	
	@GetMapping("/orderList/item/{userId}/{orderItemId}/{orderItemListId}")
	public ResponseEntity<?> getAllDataBetweenDates2222(@PathVariable long userId, @PathVariable long orderItemId,
			@PathVariable long orderItemListId) {
		try {
			List<Map<String, Object>> pendingRole = orderItemRepository.getQualificationsOrderiteamList(userId,
					orderItemId, orderItemListId);

			if (pendingRole.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order item details not found.");
			}

			Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));

			for (Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
				Map<String, Object> pendingMap = new HashMap<>();
				List<Map<String, Object>> values = pendingLoop.getValue();
				Map<String, Object> firstValue = values.get(0);

				pendingMap.put("orderItemListId", pendingLoop.getKey());
				pendingMap.put("orderStatus", firstValue.get("orderStatus"));
				pendingMap.put("orderItemId", firstValue.get("orderItemId"));
				pendingMap.put("date", firstValue.get("date"));
				pendingMap.put("totalItems", firstValue.get("totalItems"));
				pendingMap.put("orderTotalPrice", firstValue.get("orderTotalPrice"));
				pendingMap.put("userId", firstValue.get("userId"));
				pendingMap.put("productId", firstValue.get("productId"));
				pendingMap.put("productListId", firstValue.get("productListId"));
				pendingMap.put("totalAmount", firstValue.get("totalAmount"));
				pendingMap.put("totalPrice", firstValue.get("totalPrice"));
				pendingMap.put("productName", firstValue.get("productName"));
				pendingMap.put("productVarientImagesId", firstValue.get("productVarientImagesId"));
				pendingMap.put("description", firstValue.get("description"));
				pendingMap.put("reviewId", firstValue.get("reviewId"));
				pendingMap.put("starRate", firstValue.get("starRate"));
				pendingMap.put("pdfUrl", firstValue.get("pdf_url"));
				pendingMap.put("gst", firstValue.get("gst"));
				pendingMap.put("mrp", firstValue.get("mrp"));
				pendingMap.put("discountPercentage", firstValue.get("discountPercentage"));
				pendingMap.put("unit", firstValue.get("unit"));
				pendingMap.put("gstTaxAmount", firstValue.get("gstTaxAmount"));
				pendingMap.put("buyRate", firstValue.get("buyRate"));
				pendingMap.put("quantity", firstValue.get("quantity"));
				pendingMap.put("userName", firstValue.get("userName"));
				pendingMap.put("mobileNumber", firstValue.get("mobileNumber"));
				pendingMap.put("userAddressId", firstValue.get("userAddressId"));
				pendingMap.put("addressType", firstValue.get("addressType"));
				pendingMap.put("city", firstValue.get("city"));
				pendingMap.put("postalCode", firstValue.get("postalCode"));
				pendingMap.put("orderId", firstValue.get("order_id"));
				pendingMap.put("state", firstValue.get("state"));
				pendingMap.put("status", firstValue.get("status"));
				pendingMap.put("streetAddress", firstValue.get("street_address"));
				pendingMap.put("country", firstValue.get("country"));
				pendingMap.put("companyId", firstValue.get("company_id"));
				pendingMap.put("companyAddress", firstValue.get("companyAddress"));
				pendingMap.put("companyName", firstValue.get("company_name"));
				pendingMap.put("companyCountry", firstValue.get("companyCountry"));
				pendingMap.put("companyEmail", firstValue.get("companyEmail"));
				pendingMap.put("companyPhoneNumber", firstValue.get("companyPhoneNumber"));
				pendingMap.put("location", firstValue.get("location"));
				pendingMap.put("companyPincode", firstValue.get("companyPincode"));
				pendingMap.put("companyState", firstValue.get("companyState"));

				int randomNumber = generateRandomNumber();
				String fileExtension = getFileExtensionForImage(values);
				String productVarientImageUrl = "varient/" + randomNumber + "/"
						+ firstValue.get("productVarientImagesId");
				pendingMap.put("productVarientImageUrl", productVarientImageUrl);
				String url = "company/" + randomNumber + "/" + firstValue.get("company_id") + "." + fileExtension;
				pendingMap.put("companyImage", url);
				String url1 = "signature/" + randomNumber + "/" + firstValue.get("company_id") + "." + fileExtension;
				pendingMap.put("signature", url1);

				return ResponseEntity.ok(pendingMap);
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}

	@GetMapping("/orderitem/cancelled/{userId}")
	public ResponseEntity<?> getCancelledOrderItems(@PathVariable long userId) {
		List<Map<String, Object>> mainList = new ArrayList<>(); // This list will hold the grouped orders

		List<Map<String, Object>> pendingRole = orderItemRepository.getQualifications2(userId);
		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
		for (Map.Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> pendingMap = new HashMap<>();
			List<Map<String, Object>> group = pendingLoop.getValue();
			Map<String, Object> firstItem = group.get(0);
			pendingMap.put("orderItemListId", pendingLoop.getKey());
			pendingMap.put("orderStatus", firstItem.get("orderStatus"));
			pendingMap.put("orderItemId", firstItem.get("orderItemId"));
			pendingMap.put("date", firstItem.get("date"));
			pendingMap.put("totalItems", firstItem.get("totalItems"));
			pendingMap.put("orderTotalPrice", firstItem.get("orderTotalPrice"));
			pendingMap.put("userId", firstItem.get("userId"));
			pendingMap.put("productId", firstItem.get("productId"));
			pendingMap.put("productListId", firstItem.get("productListId"));
			pendingMap.put("totalAmount", firstItem.get("totalAmount"));
			pendingMap.put("totalPrice", firstItem.get("totalPrice"));
			pendingMap.put("productName", firstItem.get("productName"));
			pendingMap.put("productImagesId", firstItem.get("productImagesId"));
			pendingMap.put("description", firstItem.get("description"));
			pendingMap.put("reviewId", firstItem.get("reviewId"));
			pendingMap.put("starRate", firstItem.get("starRate"));
			pendingMap.put("gst", firstItem.get("gst"));
			pendingMap.put("mrp", firstItem.get("mrp"));
			pendingMap.put("orderId", firstItem.get("order_id"));
			pendingMap.put("discountPercentage", firstItem.get("discountPercentage"));
			pendingMap.put("unit", firstItem.get("unit"));
			pendingMap.put("gstTaxAmount", firstItem.get("gstTaxAmount"));
			pendingMap.put("buyRate", firstItem.get("buyRate"));
			pendingMap.put("quantity", firstItem.get("quantity"));

			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/" + firstItem.get("productImagesId");
			pendingMap.put("productImageUrl", productVarientImageUrl);

			mainList.add(pendingMap);
		}

		return ResponseEntity.ok(mainList);
	}

	@GetMapping("/orderitem/delivered/{order_item_list_id}")
	public ResponseEntity<?> getCancelledOrderItemsde(@PathVariable long order_item_list_id) {
		List<Map<String, Object>> mainList = new ArrayList<>(); // This list will hold the grouped orders

		List<Map<String, Object>> pendingRole = orderItemRepository.getAllFamilyInformations0000(order_item_list_id);
		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
		for (Entry<String, List<Map<String, Object>>> deliveredLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> deliveredMap = new HashMap<>();
			deliveredMap.put("orderItemListId", deliveredLoop.getKey());
			deliveredMap.put("orderStatus", deliveredLoop.getValue().get(0).get("orderStatus"));
			deliveredMap.put("orderItemId", deliveredLoop.getValue().get(0).get("orderItemId"));
			deliveredMap.put("date", deliveredLoop.getValue().get(0).get("date"));
			deliveredMap.put("totalItems", deliveredLoop.getValue().get(0).get("totalItems"));
			deliveredMap.put("orderTotalPrice", deliveredLoop.getValue().get(0).get("orderTotalPrice"));
			deliveredMap.put("returnCancelled", deliveredLoop.getValue().get(0).get("returnCancelled"));
			deliveredMap.put("userId", deliveredLoop.getValue().get(0).get("userId"));
			deliveredMap.put("productId", deliveredLoop.getValue().get(0).get("productId"));
			deliveredMap.put("productListId", deliveredLoop.getValue().get(0).get("productListId"));
			deliveredMap.put("totalAmount", deliveredLoop.getValue().get(0).get("totalAmount"));
			deliveredMap.put("totalPrice", deliveredLoop.getValue().get(0).get("totalPrice"));
			deliveredMap.put("productName", deliveredLoop.getValue().get(0).get("productName"));
			deliveredMap.put("productVarientImagesId", deliveredLoop.getValue().get(0).get("productVarientImagesId"));
			deliveredMap.put("description", deliveredLoop.getValue().get(0).get("description"));
			deliveredMap.put("reviewId", deliveredLoop.getValue().get(0).get("reviewId"));
			deliveredMap.put("starRate", deliveredLoop.getValue().get(0).get("starRate"));
			deliveredMap.put("gst", deliveredLoop.getValue().get(0).get("gst"));
			deliveredMap.put("mrp", deliveredLoop.getValue().get(0).get("mrp"));
			deliveredMap.put("orderId", deliveredLoop.getValue().get(0).get("order_id"));
			deliveredMap.put("discountPercentage", deliveredLoop.getValue().get(0).get("discountPercentage"));
			deliveredMap.put("unit", deliveredLoop.getValue().get(0).get("unit"));
			deliveredMap.put("gstTaxAmount", deliveredLoop.getValue().get(0).get("gstTaxAmount"));
			deliveredMap.put("buyRate", deliveredLoop.getValue().get(0).get("buyRate"));
			deliveredMap.put("quantity", deliveredLoop.getValue().get(0).get("quantity"));
			deliveredMap.put("deliveredDate", deliveredLoop.getValue().get(0).get("deliveredDate"));
			deliveredMap.put("pdfUrl", deliveredLoop.getValue().get(0).get("pdf_url"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "varient/" + randomNumber + "/"
					+ deliveredLoop.getValue().get(0).get("productVarientImagesId");
			deliveredMap.put("productVarientImageUrl", productVarientImageUrl);

//			mainList.add(deliveredMap);
			return ResponseEntity.ok(deliveredMap);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided orderList Id is not supported.");

//		return ResponseEntity.ok(mainList);
	}

	private String getFileExtensionForImage(Entry<String, List<Map<String, Object>>> pendingLoop) {
		if (pendingLoop == null || ((CharSequence) pendingLoop).isEmpty()) {
			return "jpg";
		}

		Map<String, Object> imageDetails = pendingLoop.getValue().get(0);
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

}