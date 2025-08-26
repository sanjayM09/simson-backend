package com.example.kkBazar.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.JwtUtils;
import com.example.kkBazar.entity.addProduct.Varient;
import com.example.kkBazar.entity.admin.ChangeUserPasswordRequest;
import com.example.kkBazar.entity.admin.EmailOtpService;
import com.example.kkBazar.entity.admin.LoginRequest;
import com.example.kkBazar.entity.admin.OtpRequest;
import com.example.kkBazar.entity.admin.OtpStore;
import com.example.kkBazar.entity.admin.OtpUtils;
import com.example.kkBazar.entity.admin.User;
import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.entity.user.UserProfile;
import com.example.kkBazar.repository.UserProfileRepository;
import com.example.kkBazar.repository.admin.UserRepository;
import com.example.kkBazar.repository.user.UserAddressRepository;
import com.example.kkBazar.service.admin.UserService;

@RestController
@CrossOrigin
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private UserAddressRepository userAddressRepository;

	@GetMapping("/user/detail/view")
	public ResponseEntity<?> getUserDetails(@RequestParam(required = true) String user) {
		try {
			if ("userDetails".equals(user)) {
				Iterable<User> userDetails = userService.listAll();
				Map<String, Object> imageObjects = new HashMap<>();
				return new ResponseEntity<>(userDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided user is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving user details: " + e.getMessage());
		}
	}
	
	@GetMapping("/user/detail/view/{userId}")
	public Map<String, Object> getAllMemberDetailsByMemberId8(@PathVariable Long userId) {
		return userRepository.UserDetailes(userId);
	}

	@PostMapping("/user/save")
	public ResponseEntity<?> saveUserDetails(@RequestBody User user) {
		try {
//			user.setCreatedAt(new Date(System.currentTimeMillis()));
			LocalDate currentDate = LocalDate.now();
			user.setDate(currentDate);
	        user.setDate(currentDate);
			String email = user.getEmailId();

			String password = user.getPassword();
			String confirmPassword = user.getConfirmPassword();
			Long phoneNumber = user.getMobileNumber();
			Long alternateMobileNumber = user.getAlternateMobileNumber();
			String name = user.getUserName();
			java.sql.Date dateOfBirth = user.getDateOfBirth();

			if (email == null || email.isEmpty()) {	
				return ResponseEntity.badRequest().body( "Email cannot be empty");		
			}

			if (!isValidEmail(email)) {
				return ResponseEntity.badRequest().body("Email is already in use. Please use a different email.");
			}
			  if (!isValidPassword(password)) {
			  return ResponseEntity.badRequest().body( "Invalid password format. Password must contain at least one uppercase letter, one lowercase letter, and one special character.");			
				}
		            
			if (!password.equals(confirmPassword)) {
				return ResponseEntity.badRequest().body( "Password and Confirm Password doesn't match");
			}

			if (userService.isEmailExists(email)) {
				return ResponseEntity.badRequest().body( "E-Mail already exists");
			}

			if (isNullOrEmpty(user.getEmailId())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Email.");
			}
			if (isNullOrEmpty(user.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Password.");
			}
			if (isNullOrEmpty(user.getConfirmPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your ConfirmPassword.");
			}
			if (isNullOrEmpty(user.getUserName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your UserName.");
			}
			if (user.getMobileNumber() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your MobileNumber.");
			}
			if (userService.findByEmailId(user.getEmailId()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Email '" + user.getEmailId() + "' already exists.");
			}


			if (phoneNumber == null || phoneNumber == 0 || String.valueOf(phoneNumber).length() < 10) {

				return ResponseEntity.badRequest().body("PhoneNumber is either empty or less than 10 digits");
			}
			if (name == null || name.isEmpty()) {
				return ResponseEntity.badRequest().body("UserName cannot be empty");
			}

			if (password == null || password.isEmpty()) {
				return ResponseEntity.badRequest().body("Password cannot be empty");
			}
			
			userService.saveUserDetails(user);
					
		
			 			     
		        return ResponseEntity.ok("Registration Successful!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user: " + e.getMessage());
		}
	}
	private boolean isValidPassword(String password) {
	    if (password == null || password.length() < 2) {
	        return false;
	    }

	    boolean hasUppercase = false;
	    boolean hasLowercase = false;
	    boolean hasSpecialChar = false;

	    for (char ch : password.toCharArray()) {
	        if (Character.isUpperCase(ch)) {
	            hasUppercase = true;
	        } else if (Character.isLowerCase(ch)) {
	            hasLowercase = true;
	        } else if (isSpecialChar(ch)) {
	            hasSpecialChar = true;
	        }
	    }

	    return hasUppercase && hasLowercase && hasSpecialChar;
	}

	private boolean isSpecialChar(char ch) {
	    String specialCharacters = "!@#$%^&*()-_=+\\|[{]};:'\",<.>/?`~";
	    return specialCharacters.indexOf(ch) != -1;
	}
	
	@PostMapping("/user/changepassword")
	public ResponseEntity<String> processChangePassword(@RequestBody ChangeUserPasswordRequest changePasswordRequest) {
		String email = changePasswordRequest.getEmail();
		String oldPassword = changePasswordRequest.getOldPassword();
		String newPassword = changePasswordRequest.getNewPassword();

		if (isNullOrEmpty(email)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Email.");
		}

		if (isNullOrEmpty(oldPassword)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your OldPassword.");
		}

		if (isNullOrEmpty(newPassword)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your NewPassword.");
		}

		User user = userRepository.findByEmailId(email);
		 if (user == null) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the provided email.");
		    }
		
		 if (!user.getPassword().equals(oldPassword)) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
		        
		    }
		 
		 
		if (user != null && user.getPassword().equals(oldPassword)) {
			user.setPassword(newPassword);
			user.setConfirmPassword(newPassword);
			userRepository.save(user);

			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	@PutMapping("/user/edit/{id}")
	public ResponseEntity<?> updateValue1(@PathVariable("id") Long id, @RequestBody User user) {
		try {

			User existingUser = userService.findById(id);

			if (existingUser == null) {
				return ResponseEntity.notFound().build();
			}

//			existingUser.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingUser.setDateOfBirth(user.getDateOfBirth());
			existingUser.setGender(user.getGender());
			existingUser.setMobileNumber(user.getMobileNumber());
			existingUser.setAlternateMobileNumber(user.getAlternateMobileNumber());
			existingUser.setUserName(user.getUserName());
			existingUser.setPassword(user.getPassword());
			existingUser.setConfirmPassword(user.getConfirmPassword());
			userService.saveUserDetails(existingUser);
			return ResponseEntity.ok(existingUser);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/user/delete/{id}")
	public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long userId) {
		userService.deleteUserId(userId);
		return ResponseEntity.ok("User deleted successfully");
	}


	@PostMapping("/user/login")
	public ResponseEntity<?> processLogin(@RequestBody LoginRequest loginRequest) {
	    String email = loginRequest.getEmail();
	    String password = loginRequest.getPassword();
	    if (!isValidEmail(email)) {
	        return ResponseEntity.badRequest().body("Invalid email format. Please provide a valid email address.");
	    }

	    User user = userRepository.findByEmailId(email);
	    if (user != null) {
	        String storedPassword = user.getPassword();
	        if (storedPassword != null && storedPassword.equals(password)) {
	            Map<String, Object> userDetails = getUserDetails(user);
	            String token = JwtUtils.generateUserToken(user);
	            Long id = user.getUserId();
	            String name = user.getUserName();
	            userDetails.put("token", token);
	            userDetails.put("id", id);
	            userDetails.put("name", name);	            
	            return ResponseEntity.ok(userDetails);
	        }
	    }
	    return ResponseEntity.badRequest().body("E-Mail and Password do not match");
	}

	private Map<String, Object> getUserDetails(User user) {
	    List<Map<String, Object>> userDetails = userRepository.getDetailsById(user.getUserId());
	    Map<String, List<Map<String, Object>>> userGroupMap = userDetails.stream()
	            .collect(Collectors.groupingBy(action -> action.get("userId").toString()));

	    Map<String, Object> userDetailsMap = new HashMap<>();

	    for (Entry<String, List<Map<String, Object>>> userLoop : userGroupMap.entrySet()) {
	        Map<String, Object> userMap = new HashMap<>();
	        userMap.put("name", userLoop.getValue().get(0).get("userName"));
	        userMap.put("id", userLoop.getKey());
	        userDetailsMap = userMap;
	    }

	    return userDetailsMap;
	}



	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	@PostMapping("/validate-token")
	public ResponseEntity<String> validateToken(@RequestBody Map<String, String> request) {
		String token = request.get("token");

		System.out.println("Received Token: " + token);

		try {
			User user = getUserFromToken(token);

			if (user != null && JwtUtils.validateUserToken(token, user)) {
				Date expirationDate = JwtUtils.getExpirationDate(token);
				Date currentDate = new Date();

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				calendar.add(Calendar.YEAR, 1);
				Date oneYearLater = calendar.getTime();

				if (!expirationDate.after(oneYearLater)) {
					return ResponseEntity.ok("Token is valid for one year.");
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

	private User getUserFromToken(String token) {
		try {
			String email = JwtUtils.extractUsername(token);

			User user = userRepository.findByEmailId(email);

			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

////////////////////////////

	@GetMapping("/userDetails/{id}")
	public ResponseEntity<?> getUserDetail(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> userDetails = userRepository.getUserDetails(userId);

			List<Map<String, Object>> userMainList = new ArrayList<>();
			Map<String, Map<String, List<Map<String, Object>>>> userGroupMap = userDetails.stream()
					.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
							Collectors.groupingBy(action -> action.get("user_address_id").toString())));

			for (Entry<String, Map<String, List<Map<String, Object>>>> userLoop : userGroupMap.entrySet()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userLoop.getKey());

				List<Map<String, Object>> addressList = new ArrayList<>();
				for (Entry<String, List<Map<String, Object>>> addressListLoop : userLoop.getValue().entrySet()) {
					Map<String, Object> addressListMap = new HashMap<>();

					addressListMap.put("userAddressId", addressListLoop.getKey());
					userMap.put("alternateMobileNumber",
							addressListLoop.getValue().get(0).get("alternate_mobile_number"));
					userMap.put("dateOfBirth", addressListLoop.getValue().get(0).get("date_of_birth"));
					userMap.put("emailId", addressListLoop.getValue().get(0).get("email_id"));
					userMap.put("gender", addressListLoop.getValue().get(0).get("gender"));
					userMap.put("mobileNumber", addressListLoop.getValue().get(0).get("mobile_number"));
					userMap.put("userName", addressListLoop.getValue().get(0).get("user_name"));
					addressListMap.put("postalCode", addressListLoop.getValue().get(0).get("postal_code"));
					addressListMap.put("city", addressListLoop.getValue().get(0).get("city"));
					addressListMap.put("country", addressListLoop.getValue().get(0).get("country"));
					addressListMap.put("streetAddress", addressListLoop.getValue().get(0).get("street_address"));
					addressListMap.put("state", addressListLoop.getValue().get(0).get("state"));
					addressList.add(addressListMap);
				}

				userMap.put("addressDetails", addressList);
				userMainList.add(userMap);
			}

			return ResponseEntity.ok(userMainList);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping("/user/view")
	public ResponseEntity<?> getAllUserDetails(@RequestParam(required = true) String user) {
	    try {
	        if ("userDetails".equals(user)) {
	            List<Map<String, Object>> userDetails = userRepository.getAllUserDetails();

	            List<Map<String, Object>> userMainList = new ArrayList<>();
	            Map<Object, Map<Object, List<Map<String, Object>>>> userGroupMap = userDetails.stream()
	                    .collect(Collectors.groupingBy(
	                            action -> Objects.toString(action.get("user_id"), ""),
	                            Collectors.groupingBy(
	                                    action -> Objects.toString(action.get("user_address_id"), "")
	                            )
	                    ));
	            for (Entry<Object, Map<Object, List<Map<String, Object>>>> userLoop : userGroupMap.entrySet()) {
	                Map<String, Object> userMap = new HashMap<>();
	                userMap.put("userId", userLoop.getKey());

	                List<Map<String, Object>> addressList = new ArrayList<>();

	                for (Entry<Object, List<Map<String, Object>>> addressListLoop : userLoop.getValue().entrySet()) {
	                    Map<String, Object> addressListMap = new HashMap<>();

	                    // Setting user details, ensuring non-null default values
	                    Map<String, Object> firstEntry = addressListLoop.getValue().get(0);
	                    userMap.put("gender", Objects.toString(firstEntry.get("gender"), ""));
	                    userMap.put("mobileNumber", Objects.toString(firstEntry.get("mobile_number"), ""));
	                    userMap.put("alternateMobileNumber", Objects.toString(firstEntry.get("alternate_mobile_number"), ""));
	                    userMap.put("dateOfBirth", Objects.toString(firstEntry.get("date_of_birth"), ""));
	                    userMap.put("emailId", Objects.toString(firstEntry.get("email_id"), ""));
	                    userMap.put("userName", Objects.toString(firstEntry.get("user_name"), ""));

	                    // Checking address details
	                    String postalCode = Objects.toString(firstEntry.get("postal_code"), "");
	                    String district = Objects.toString(firstEntry.get("city"), "");
	                    String country = Objects.toString(firstEntry.get("country"), "");
	                    String streetAddress = Objects.toString(firstEntry.get("street_address"), "");
	                    String addressType = Objects.toString(firstEntry.get("address_type"), "");
	                    String state = Objects.toString(firstEntry.get("state"), "");
	                    String id = Objects.toString(firstEntry.get("user_address_id"), "");

	                    // If any address detail is an empty string, skip adding the address
	                    if (!postalCode.isEmpty() && !district.isEmpty() && !country.isEmpty() && !streetAddress.isEmpty() && !addressType.isEmpty() && !state.isEmpty() && !id.isEmpty()) {
	                        addressListMap.put("postalCode", postalCode);
	                        addressListMap.put("district", district);
	                        addressListMap.put("country", country);
	                        addressListMap.put("streetAddress", streetAddress);
	                        addressListMap.put("addressType", addressType);
	                        addressListMap.put("state", state); 
	                        addressListMap.put("userAddressId", id);
	                        
	                        addressList.add(addressListMap);
	                    }
	                }

	                userMap.put("addressDetails", addressList.isEmpty() ? new ArrayList<>() : addressList);
	                userMainList.add(userMap);
	            }

	            return ResponseEntity.ok(userMainList);

	        } else {
	            return ResponseEntity.badRequest().build();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}




	@GetMapping("/userWithAddress/{id}")
	public List<Map<String, Object>> getUserWithAddressDetails(@PathVariable("id") Long userId) {
		return userRepository.getUserAddressDetails(userId);
	}

	@GetMapping("/userDetailsById/{id}")
	public List<Map<String, Object>> getUserDetails1(@PathVariable("id") Long userId) {
		return userRepository.getUserById(userId);
	}

	@GetMapping("/customerCount")
	public List<Map<String, Object>> getAllCustomerCount(@RequestParam(required = true) String customer) {
		try {
			if ("count".equalsIgnoreCase(customer)) {
				return userRepository.getYearWishCustomerCount();
			} else {
				throw new IllegalArgumentException("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();

		}
	}
	
	
	
    @Autowired
    private EmailOtpService emailService;
        
    @Autowired
    private OtpStore otpStore22;

    @PostMapping("/user/login/otp")
    public ResponseEntity<?> processLoginProcess(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();

        // Validate email format
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Invalid email format. Please provide a valid email address.");
        }
        User user = userRepository.findByEmailId(email);
        if (user != null) {
            String otp = OtpUtils.generateOtp();
            otpStore22.storeOtp(email, otp);
            emailService.sendOtp(email, otp);
            return ResponseEntity.ok("OTP sent to your email address.");
        }
        return ResponseEntity.badRequest().body("Email does not exist in the database.");
    }



    @PostMapping("/user/validateOtp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpRequest otpRequest) {
        String email = otpRequest.getEmail();
        String otp = otpRequest.getOtp();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email cannot be empty");
        }

        User existingUser = userRepository.findByEmailId(email);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Email does not exist in the database");
        }

        OtpStore.OtpDetails otpDetails = otpStore22.getOtpDetails(email);
        if (otpDetails == null) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime otpTimestamp = otpDetails.getTimestamp();
        if (otpTimestamp.plusMinutes(5).isBefore(now)) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        if (!otpDetails.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        Map<String, Object> userDetails = getUserDetails(existingUser);
        String token = JwtUtils.generateUserToken(existingUser);
        Long id = existingUser.getUserId();
        String name = existingUser.getUserName();
        userDetails.put("token", token);
        userDetails.put("id", id);
        userDetails.put("name", name);
        otpStore22.removeOtp(email); // Remove OTP after successful validation
        return ResponseEntity.ok(userDetails);
    }


}
