package com.example.kkBazar.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.kkBazar.JwtUtils;
import com.example.kkBazar.entity.admin.AdminLogin;
import com.example.kkBazar.entity.admin.ChangePasswordRequest;
import com.example.kkBazar.entity.admin.LoginRequest;
import com.example.kkBazar.repository.admin.AdminLoginRepository;
import com.example.kkBazar.repository.admin.UserRepository;
import com.example.kkBazar.service.admin.AdminLoginService;

@RestController
@CrossOrigin
public class AdminLoginController {

	@Autowired
	private AdminLoginRepository adminLoginRepository;

	@Autowired
	private AdminLoginService adminLoginService;
	@Autowired
	private UserRepository userRepository;


	@PostMapping("/admin/changepassword")
	public ResponseEntity<String> processChangePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
		String email = changePasswordRequest.getEmail();
		String oldPassword = changePasswordRequest.getOldPassword();
		String newPassword = changePasswordRequest.getNewPassword();

		if (isNullOrEmpty(email)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Email.");
		}

		if (isNullOrEmpty(oldPassword)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Old password.");
		}

		if (isNullOrEmpty(newPassword)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your New password.");
		}

		AdminLogin admin = adminLoginRepository.findByEmail(email);

		if (admin != null && admin.getPassword().equals(oldPassword)) {
			admin.setPassword(newPassword);
			adminLoginRepository.save(admin);

			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
		}
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	@GetMapping("/admin/view")
	public ResponseEntity<Object> getAdminDetails(@RequestParam(required = true) String admin) {
		if ("adminDetails".equals(admin)) {
			return ResponseEntity.ok(adminLoginService.listAll());
		} else {
			String errorMessage = "Invalid value for 'admin'. Expected 'adminDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}

	}

	@PostMapping("/admin/save")
	public ResponseEntity<String> saveAdminDetails(@RequestBody AdminLogin admin) {
		try {
			if (adminLoginService.findByEmail(admin.getEmail()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Admin with the email '" + admin.getEmail() + "' already exists.");
			}

			if (isNullOrEmpty(admin.getEmail())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Email.");
			}

			if (isNullOrEmpty(admin.getName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Name.");
			}

			if (isNullOrEmpty(admin.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Password.");
			}

			adminLoginService.SaveAdminDetails(admin);
			long id = admin.getId();
			return ResponseEntity.ok("Admin Details saved successfully. Admin ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving admin: " + e.getMessage());
		}
	}

	@PutMapping("/admin/edit/{id}")
	public ResponseEntity<String> updateAdmin(@PathVariable("id") Long id, @RequestBody AdminLogin admin) {
		try {

			if (isNullOrEmpty(admin.getEmail())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Email.");
			}

			if (isNullOrEmpty(admin.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Password.");
			}

			AdminLogin existingAdmin = adminLoginService.findById(id);
			if (existingAdmin == null) {
				return ResponseEntity.notFound().build();
			}

			existingAdmin.setEmail(admin.getEmail());
			existingAdmin.setPassword(admin.getPassword());

			adminLoginService.SaveAdminDetails(existingAdmin);

			return ResponseEntity.ok("Admin details updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/admin/login")
	public ResponseEntity<?> processLoginn(@RequestBody LoginRequest loginRequest) {
		String email = loginRequest.getEmail();
		String password = loginRequest.getPassword();

		AdminLogin admin = adminLoginRepository.findByEmail(email);

		if (admin != null && admin.getPassword().equals(password)) {
			Map<String, Object> adminDetails = getAdminDetails(admin);
			String token = JwtUtils.generateToken(admin);
			adminDetails.put("token", token);
			return ResponseEntity.ok(adminDetails);
		} else {
			Map<String, Object> loginFailedResponse = createLoginFailedResponse();
			return ResponseEntity.badRequest().body(loginFailedResponse);
		}
	}

	private Map<String, Object> getAdminDetails(AdminLogin admin) {
		List<Map<String, Object>> adminDetailsList = userRepository.getAllAdminDetailsById(admin.getId());
		Map<String, List<Map<String, Object>>> adminGroupMap = adminDetailsList.stream()
				.collect(Collectors.groupingBy(action -> action.get("id").toString()));

		Map<String, Object> adminDetailsMap = new HashMap<>();

		for (Entry<String, List<Map<String, Object>>> adminLoop : adminGroupMap.entrySet()) {
			Map<String, Object> adminMap = new HashMap<>();

			adminMap.put("id", adminLoop.getKey());
			adminMap.put("name", adminLoop.getValue().get(0).get("name"));
			adminMap.put("roleId", adminLoop.getValue().get(0).get("role_id"));
			adminMap.put("roleName", adminLoop.getValue().get(0).get("role_name"));
			adminMap.put("roleType", adminLoop.getValue().get(0).get("role_type"));
			adminDetailsMap.putAll(adminMap);
		}

		return adminDetailsMap;
	}

	private Map<String, Object> createLoginFailedResponse() {
		Map<String, Object> response = new HashMap<>();
		response.put("result", "Login Failed");
		return response;
	}

	@PostMapping("/validateAdmin-token")
	public ResponseEntity<String> validateAdminToken(@RequestBody Map<String, String> request) {
		String token = request.get("token");

		System.out.println("Received Token: " + token);

		try {
			AdminLogin admin = getAdminFromToken(token);

			if (admin != null && JwtUtils.validateToken(token, admin)) {
				Date expirationDate = JwtUtils.getExpirationDate(token);
				Date currentDate = new Date();

				if (!expirationDate.before(currentDate)) {
					return ResponseEntity.ok("Token is valid.");
				} else {
					return ResponseEntity.badRequest().body("Token has expired.");
				}
			} else {
				return ResponseEntity.badRequest().body("Token is not valid.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error during token validation.");
		}
	}

	private AdminLogin getAdminFromToken(String token) {
		try {
			String email = JwtUtils.extractUsername(token);

			AdminLogin admin = adminLoginRepository.findByEmail(email);

			return admin;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/////////////////////

	@PostMapping("/validateAdmin-token/year")
	public ResponseEntity<String> validateAdminToken1(@RequestBody Map<String, String> request) {
		String token = request.get("token");

		System.out.println("Received Token: " + token);

		try {
			AdminLogin admin = getAdminFromToken1(token);

			if (admin != null && JwtUtils.validateToken(token, admin)) {
				Date expirationDate = JwtUtils.getExpirationDate(token);
				Date currentDate = new Date();

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				calendar.add(Calendar.YEAR, 1);
				Date oneYearLater = calendar.getTime();

				if (!expirationDate.after(oneYearLater)) {
					return ResponseEntity.ok("Token is valid.");
				} else {
					return ResponseEntity.badRequest().body("Token has expired.");
				}
			} else {
				return ResponseEntity.badRequest().body("Token is not valid.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error during token validation.");
		}
	}

	private AdminLogin getAdminFromToken1(String token) {
		try {
			String email = JwtUtils.extractUsername(token);

			AdminLogin admin = adminLoginRepository.findByEmail(email);

			return admin;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
