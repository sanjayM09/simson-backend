package com.example.kkBazar.controller.admin;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.admin.SuperAdmin;

@RestController
@CrossOrigin
public class SuperAdminController {
	@Value("${app.email}")
	private String email;

	@Value("${app.password}")
	private String password;

	@PostMapping("/superAdmin/login")
	public ResponseEntity<?> login(@RequestBody SuperAdmin superAdmin) {
		System.out.println("username from properties file: " + email);
		System.out.println("password from properties file: " + password);
		System.out.println("username from login request: " + superAdmin.getEmail());
		System.out.println("password from login request: " + superAdmin.getPassword());
		if (superAdmin.getEmail().equals(email) && superAdmin.getPassword().equals(password)) {
			Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("token",
					"2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys");
			responseData.put("username", email);
			responseData.put("login_status", "Success");

			return ResponseEntity.ok(responseData);
		} else {
			Map<String, Object> errorResponseData = new HashMap<String, Object>();
			errorResponseData.put("message", "username and password is incorrect");
			errorResponseData.put("login_status", "Failed");
			errorResponseData.put("token",
					"2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys");
			return ResponseEntity.ok(errorResponseData);

		}

	}
}
