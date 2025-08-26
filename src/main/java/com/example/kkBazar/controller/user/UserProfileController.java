package com.example.kkBazar.controller.user;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.kkBazar.entity.user.UserProfile;
import com.example.kkBazar.repository.UserProfileRepository;
import com.example.kkBazar.service.user.UserProfileService;

@RestController
@CrossOrigin
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@PostMapping("/userProfile/save")
	public ResponseEntity<?> addUserProfileImage(@RequestParam("profileImage") MultipartFile file,
			@RequestParam("userId") long userId) {

		try {

			byte[] bytes = file.getBytes();
			Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

			UserProfile user = new UserProfile();
			user.setUserProfile(blob);
			user.setUserId(userId);
			user.setCreatedAt(new Date(System.currentTimeMillis()));
			userProfileService.saveUserProfile(user);

			long id = user.getUserProfileId();
			return ResponseEntity.ok("UserProfile added successfully. UserProfile ID: " + id);

		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user profile.");
		}
	}

	@GetMapping("/UserProfile")
	public ResponseEntity<List<UserProfile>> displayAllUsers(@RequestParam(required = true) String userProfile) {
		try {
			if ("userProfileDetails".equals(userProfile)) {
				List<UserProfile> users = userProfileService.getUser();
				List<UserProfile> userObjects = new ArrayList<>();
				for (UserProfile user : users) {
					int randomNumber = generateRandomNumber();
					String fileExtension = getFileExtensionForImage(user);
					String imageUrl = "userProfile/" + randomNumber + "/" + user.getUserProfileId() + "."
							+ fileExtension;

					UserProfile userDetails = new UserProfile();
					userDetails.setUserProfileId(user.getUserProfileId());
					userDetails.setProfileUrl(imageUrl);
					userObjects.add(userDetails);
				}
				return ResponseEntity.ok().body(userObjects);
			} else {

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("userProfile/{randomNumber}/{id:.+}")
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
			return ResponseEntity.badRequest().build(); // Invalid image ID format
		}

		UserProfile image = userProfileService.findUserProfileById(imageId);
		if (image == null || image.getUserProfile() == null) {
			byte[] emptyArray = new byte[0];
			ByteArrayResource emptyResource = new ByteArrayResource(emptyArray);

			HttpHeaders emptyHeaders = new HttpHeaders();
			emptyHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			return ResponseEntity.ok().headers(emptyHeaders).body(emptyResource);
		}

		byte[] imageBytes;
		try {
			imageBytes = image.getUserProfile().getBytes(1, (int) image.getUserProfile().length());
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

	private String getFileExtensionForImage(UserProfile image) {
		if (image == null || image.getProfileUrl() == null || image.getProfileUrl().isEmpty()) {
			return "jpg";
		}
		String url = image.getProfileUrl();
		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}

//	@PutMapping("/userProfile/edit/{userProfileId}")
//	public ResponseEntity<String> updateProfile(@PathVariable long userProfileId,
//	        @RequestParam(value = "profile", required = false) MultipartFile file) {
//	    try {
//	        UserProfile userProfile = userProfileService.findUserProfileById(userProfileId);
//
//	        if (userProfile == null) {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserProfile not found.");
//	        }
//
//	        if (file != null && !file.isEmpty()) {
//	            byte[] bytes = file.getBytes();
//	            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
//	            userProfile.setUserProfile(blob);
//	        }
//
//	        if (file == null || file.isEmpty()) {
//	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image not provided.");
//	        }
//
//	        userProfileService.saveUserProfile(userProfile);
//
//	        return ResponseEntity.ok("UserProfile updated successfully. UserProfile ID: " + userProfileId);
//	    } catch (IOException | SQLException e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating userProfile.");
//	    }
//	}

	@PutMapping("/userProfile/edit/{userProfileId}")
	public ResponseEntity<String> updateProfile(@PathVariable long userProfileId,
			@RequestParam(value = "profile", required = false) MultipartFile file) {
		try {
			UserProfile userProfile = userProfileService.findUserProfileById(userProfileId);
			if (userProfile == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserProfile not found.");
			}
			if (file != null && !file.isEmpty()) {
				if (!file.getContentType().startsWith("image/")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Invalid file format. Please upload an image.");
				}
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				userProfile.setUserProfile(blob);
			}

			if (file == null || file.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image not provided.");
			}
			userProfileService.saveUserProfile(userProfile);
			return ResponseEntity.ok("UserProfile updated successfully. UserProfile ID: " + userProfileId);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating userProfile.");
		}
	}

	@DeleteMapping("/userProfile/delete/{userProfileId}")
	public ResponseEntity<Object> deleteTitle(@PathVariable("userProfileId") Long userProfileId) {
		try {
			userProfileService.deleteUserProfileId(userProfileId);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Profile deleted successfully");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Profile removal failed");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/userProfileDetails/{id}")
	public List<Map<String, Object>> getProfileDetail(@PathVariable("id") Long user_id) {
		return userProfileRepository.getProfileDetailById(user_id).stream().map(profile -> {
			Map<String, Object> result = new HashMap<>();
			result.put("userProfileId", profile.get("user_profile_id"));
			result.put("userId", profile.get("user_id"));
			result.put("userName", profile.get("user_name"));
			int randomNumber = generateRandomNumber();
			if (profile != null) {
				String imageUrl = "userProfile/" + randomNumber + "/" + profile.get("user_profile_id") + ".png";
				result.put("userProfileImageUrl", imageUrl);
			}
			return result;
		}).collect(Collectors.toList());
	}

}
