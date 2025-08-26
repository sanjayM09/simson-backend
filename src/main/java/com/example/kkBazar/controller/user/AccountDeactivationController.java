package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.admin.User;
import com.example.kkBazar.entity.user.AccountDeactivation;
import com.example.kkBazar.service.admin.UserService;
import com.example.kkBazar.service.user.AccountDeactivationService;
import com.example.kkBazar.service.user.UserAddressService;
import com.example.kkBazar.service.user.UserProfileService;

@RestController
@CrossOrigin
public class AccountDeactivationController {

	@Autowired
	private AccountDeactivationService accountDeactivationService;

	@Autowired
	private UserService userService;
	@Autowired
	private UserProfileService userProfileService;
	@Autowired
	private UserAddressService userAddressService;

	@GetMapping("/accountDeactivation/Details")
	public ResponseEntity<?> getUserAccountDeactivationDetails(
			@RequestParam(required = true) String accountDeactivation) {
		try {
			if ("accountDeactivationDetails".equals(accountDeactivation)) {
				Iterable<AccountDeactivation> accountDeactivationDetails = accountDeactivationService.listAll();
				return new ResponseEntity<>(accountDeactivationDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided accountDeactivation is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving accountDeactivation details: " + e.getMessage());
		}
	}
	/////////
	@PostMapping("/accountDeactivationDetails/save")
	public ResponseEntity<Object> saveAccountDeactivationDetails1(
			@RequestBody AccountDeactivation accountDeactivation) {
		try {
			accountDeactivation.setCreatedAt(new Date(System.currentTimeMillis()));
			accountDeactivation.setDate(LocalDate.now());
			String currentPassword = accountDeactivation.getCurrentPassword();
			String repeatPassword = accountDeactivation.getRepeatPassword();

			if (!currentPassword.equals(repeatPassword)) {
				return ResponseEntity.badRequest()
						.body(getErrorResponse("Validation Error", "CurrentPassword and repeatPassword do not match"));
			}

			long userId = accountDeactivation.getUserId();
			User user = userService.findById(userId);

			
			if (user != null && currentPassword.equals(user.getPassword())) {
				accountDeactivationService.SaveAccountDeactivation(accountDeactivation);
				userService.deleteUserId(userId);
				userProfileService.deleteUserProfileId(userId);
				userAddressService.deleteUserAddressById(userId);
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("message", "Your account has been deleted successfully.");
				return ResponseEntity.ok(successResponse);
			} else {
				Map<String, Object> errorMap = new HashMap<>();
				errorMap.put("message","Invalid user or incorrect password");
				return ResponseEntity.badRequest()
						.body(errorMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(getErrorResponse("Internal Server Error", "Failed to delete the account."));
		}
	}
	
	
	private Map<String, Object> getErrorResponse(String errorType, String errorMessage) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("errorMessage", errorMessage);
		return errorResponse;
	}
}



