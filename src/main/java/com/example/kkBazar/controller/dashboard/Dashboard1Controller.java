package com.example.kkBazar.controller.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.dashboard.Dashboard1;
import com.example.kkBazar.repository.addProduct.ProductRepository;
import com.example.kkBazar.repository.dashboard.Dashboard1Repository;
import com.example.kkBazar.service.dashboard.Dashboard1Service;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@RestController
@CrossOrigin
public class Dashboard1Controller {

	@Autowired
	private Dashboard1Service dashboard1Service;

	@Autowired
	private Dashboard1Repository dashboard1Repository;
	@Autowired
	private ProductRepository productRepository;

	@PostMapping("/dashboard1/save")
	public ResponseEntity<String> saveAdminDetails(@RequestBody Dashboard1 dashboard) {
		try {
			
			dashboard.setCreatedAt(new Date(System.currentTimeMillis()));
			dashboard.setStatus(true);

			long productId = dashboard.getProductId();

			Optional<Dashboard1> existingProducts = dashboard1Repository.findByProductId(dashboard.getProductListId());

			if (!existingProducts.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Dashboard1 entry for product ID " + productId + " already exists.");
			}

			List<Map<String, Object>> productRoleData = productRepository
					.getAllCategoryWithProductDetailsWithId(productId);

			if (!productRoleData.isEmpty()) {
				Map<String, Object> firstResult = productRoleData.get(0);

				long productImagesId = ((Number) firstResult.get("product_images_id")).longValue();

				dashboard.setProductImagesId(productImagesId);
			}
			

			dashboard1Service.SaveDashboard(dashboard);

			long id = dashboard.getDashboard1Id();
			return ResponseEntity.ok("Dashboard Details saved successfully. Dashboard ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving variant: " + e.getMessage());
		}
	}

	
//	 @PostConstruct
//	    public void initializeFirebase() {
//	        try {
//	            InputStream serviceAccount = new ClassPathResource("kk-bazar-firebase-adminsdk-n9ml6-0c28bddac3.json").getInputStream();
//
//	            FirebaseOptions options = new FirebaseOptions.Builder()
//	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//	                .build();
//
//	            FirebaseApp.initializeApp(options);
//	        } catch (IOException e) {
//	            System.err.println("Error initializing Firebase: " + e.getMessage());
//	            e.printStackTrace();
//	        }
//	    }
//
//	    @PostMapping("/dashboard444/save")
//	    public ResponseEntity<String> saveAdminDetails222(@RequestBody Dashboard1 dashboard) {
//	        try {
//	            dashboard.setStatus(true);
//
//	            long productId = dashboard.getProductId();
//
//	            Optional<Dashboard1> existingProducts = dashboard1Repository.findByProductId(dashboard.getProductListId());
//
//	            if (existingProducts.isPresent()) {
//	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//	                        .body("Dashboard1 entry for product ID " + productId + " already exists.");
//	            }
//
//	            List<Map<String, Object>> productRoleData = productRepository
//	                    .getAllCategoryWithProductDetailsWithId(productId);
//
//	            if (!productRoleData.isEmpty()) {
//	                Map<String, Object> firstResult = productRoleData.get(0);
//
//	                long productImagesId = ((Number) firstResult.get("product_images_id")).longValue();
//
//	                dashboard.setProductImagesId(productImagesId);
//	            }
//
//	            dashboard1Service.SaveDashboard(dashboard);
//
//	            long id = dashboard.getDashboard1Id();
//
//	            // Send notification to all users
//	            String title = "New Product Launched!";
//	            String body = "Check out our new product with ID: " + productId;
//	            sendNotificationToAllUsers(title, body);
//
//	            return ResponseEntity.ok("Dashboard Details saved successfully. Dashboard ID: " + id);
//	        } catch (Exception e) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                    .body("Error saving variant: " + e.getMessage());
//	        }
//	    }
//
//	    private void sendNotificationToAllUsers(String title, String body) {
//	        try {
//	            Notification notification = Notification.builder()
//	                .setTitle(title)
//	                .setBody(body)
//	                .build();
//
//	            Message message = Message.builder()
//	                .setNotification(notification)
//	                .setTopic("allUsers")
//	                .build();
//
//	            String response = FirebaseMessaging.getInstance().send(message);
//	            System.out.println("Successfully sent message: " + response);
//
//	        } catch (Exception e) {
//	            System.err.println("Error sending notification: " + e.getMessage());
//	            e.printStackTrace();
//	        }
//	    }
	@GetMapping("/dashboard1/view")
	public ResponseEntity<?> getAllDashboardDetails(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardActiveDetails".equals(dashboard)) {
				Iterable<Map<String, Object>> dashboardDetails = dashboard1Repository.getAllDashboard1ActiveDetails();
				List<Map<String, Object>> dashboardList = new ArrayList<>();
				for (Map<String, Object> dashLoop : dashboardDetails) {
					Map<String, Object> ob = new HashMap<>();
					int randomNumber = generateRandomNumber();
					Object productImagesId = dashLoop.get("productVarientImagesId");
					String productImageUrl = "varient/" + randomNumber + "/" + productImagesId;
					ob.put("url", productImageUrl);
					ob.putAll(dashLoop);
					dashboardList.add(ob);

				}

				return new ResponseEntity<>(dashboardList, HttpStatus.OK);
			} else if ("dashboardDetails".equals(dashboard)) {
				Iterable<Map<String, Object>> dashboardDetails = dashboard1Repository.getAllDashboard1Details();
				List<Map<String, Object>> dashboardList = new ArrayList<>();
				for (Map<String, Object> dashLoop : dashboardDetails) {
					Map<String, Object> ob = new HashMap<>();
					int randomNumber = generateRandomNumber();
					Object productImagesId = dashLoop.get("productVarientImagesId");
					String productImageUrl = "varient/" + randomNumber + "/" + productImagesId;
					ob.put("url", productImageUrl);
					ob.putAll(dashLoop);
					dashboardList.add(ob);
				}
				return new ResponseEntity<>(dashboardList, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided dashboard is not supported.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving dashboard details: " + e.getMessage());
		}
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

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@PutMapping("/dashboard1/edit/{id}")
	public ResponseEntity<Dashboard1> updateDashboard1(@PathVariable("id") Long dashboard1Id,
			@RequestBody Dashboard1 dashboard) {
		try {

			Dashboard1 existingDashboard1 = dashboard1Service.findDashboardById(dashboard1Id);

			if (existingDashboard1 == null) {
				return ResponseEntity.notFound().build();
			}
			existingDashboard1.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingDashboard1.setProductListId(dashboard.getProductListId());
			existingDashboard1.setDescription(dashboard.getDescription());
			existingDashboard1.setProductId(dashboard.getProductId());
			existingDashboard1.setDescription(dashboard.getDescription());
			existingDashboard1.setProductImagesId(dashboard.getProductImagesId());

			dashboard1Service.SaveDashboard(existingDashboard1);
			return ResponseEntity.ok(existingDashboard1);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	
	@PutMapping("/dashboard1/status/{id}")
	public ResponseEntity<Dashboard1> updateDashboard1Status(@PathVariable("id") Long dashboard1Id,
			@RequestBody Dashboard1 dashboard) {
		try {
			Dashboard1 existingDashboard1 = dashboard1Service.findDashboardById(dashboard1Id);
			if (existingDashboard1 == null) {
				return ResponseEntity.notFound().build();
			}
			existingDashboard1.setDashboardStatus(dashboard.getDashboardStatus());
			if (dashboard.getDashboardStatus().equals("inactive")) {
				existingDashboard1.setStatus(false);
			} else {
				existingDashboard1.setStatus(true);
			}
			dashboard1Service.SaveDashboard(existingDashboard1);
			return ResponseEntity.ok(existingDashboard1);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
