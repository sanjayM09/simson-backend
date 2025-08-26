package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.repository.user.UserAddressRepository;
import com.example.kkBazar.service.user.UserAddressService;

@RestController
@CrossOrigin
public class UserAddressController {

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@GetMapping("/address/view")
	public ResponseEntity<?> getAddressDetails(@RequestParam(required = true) String address) {
		try {
			if ("addressDetails".equals(address)) {
				Iterable<UserAddress> addressDetails = userAddressService.listAll();
				return new ResponseEntity<>(addressDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided address is not supported.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving address details: " + e.getMessage());
		}
	}

	@GetMapping("/address/view/user/{userId}")
	public List<Map<String, Object>> getAllMemberDetailsByMemberId8(@PathVariable Long userId) {
		return userAddressRepository.UserDetailes(userId);
	}

	@PostMapping("/address/save")
	public ResponseEntity<?> saveDepartment(@RequestBody UserAddress address) {
		try {
			long userId = address.getUserId();
			boolean status = address.isStatus();

			address .setCreatedAt(new Date(System.currentTimeMillis()));
			if (status) {
				List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);

				if (existingAddresses != null) {
					for (UserAddress existingAddress : existingAddresses) {
						existingAddress.setStatus(false);
						userAddressService.saveAddressDetails(existingAddress);
					}
				}
			}

			userAddressService.saveAddressDetails(address);

			long id = address.getUserAddressId();
			return ResponseEntity.ok("UserAddress details saved successfully. ID: " + id);

		} catch (Exception e) {
			String errorMessage = "An error occurred while saving UserAddress details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}
//	  @PostMapping("/address/save")
//	    public ResponseEntity<?> saveDepartment1(@RequestBody UserAddress address) {
//	        try {
//	            long userId = address.getUserId();
//	            boolean status = address.isStatus();
//
//	            // Get the list of existing addresses for this user
//	            List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);
//
//	            // Update the status for all existing addresses based on the input status
//	            if (existingAddresses != null) {
//	                for (UserAddress existingAddress : existingAddresses) {
//	                    existingAddress.setStatus(status);
//	                    userAddressService.saveAddressDetails(existingAddress); // Save changes to the database
//	                }
//	            }
//	            userAddressService.saveAddressDetails(address);
//
//	            long id = address.getUserAddressId(); // Assuming user address has a method to get ID
//	            return ResponseEntity.ok("UserAddress details saved successfully. ID: " + id);
//
//	        } catch (Exception e) {
//	            String errorMessage = "An error occurred while saving UserAddress details.";
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//	        }
//	    }

//	@PutMapping("/address/{addressid}")
//	public ResponseEntity<?> updateDesignation(@PathVariable("addressid") Long addressid,
//			@RequestBody UserAddress address) {
//
//		try {
//			UserAddress existingaddress = userAddressService.findAddressById(addressid);
//
//			if (existingaddress == null) {
//				return ResponseEntity.notFound().build();
//			}
//
//			List<UserAddress> existingAddresses = userAddressRepository.findByUserId(addressid);
//
//			for (UserAddress userLoop : existingAddresses) {
//
//				Long userId = userLoop.getUserId();
//				if (userId != null && addressid == null) {
//					userLoop.setStatus(false);
//				}
//
//			}
//
//			Long userId = existingaddress.getUserId();
//			if (userId != null && addressid != null) {
//				existingaddress.setStatus(true);
//			}
//
//			userAddressService.saveAddressDetails(existingaddress);
//
//			return ResponseEntity.ok(existingaddress);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//	}

	
	 @PutMapping("/address/{addressid}")
	    public ResponseEntity<?> updateDesignation(
	        @PathVariable("addressid") Long addressid,
	        @RequestBody UserAddress address
	    ) {
	        try {
	            UserAddress existingAddress = userAddressService.findAddressById(addressid);
	            if (existingAddress == null) {
	                return ResponseEntity.notFound().build();
	            }
	            existingAddress.setUpdatedAt(new Date(System.currentTimeMillis()));
	            Long userId = existingAddress.getUserId();
	            List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);

	            for (UserAddress userAddress : userAddresses) {
	                if (!userAddress.getUserAddressId().equals(addressid)) {
	                    userAddress.setStatus(false);
	                    userAddressService.saveAddressDetails(userAddress);
	                }
	            }

	            existingAddress.setStatus(true);
	            userAddressService.saveAddressDetails(existingAddress);

	            return ResponseEntity.ok("Address details updated successfully ");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	
	@PutMapping("/address/edit/{id}")
	public ResponseEntity<Object> updateAddress(@PathVariable("id") Long userAddressId,
			@RequestBody UserAddress address) {
		try {
			UserAddress existingAddress = userAddressService.findAddressById(userAddressId);

			if (existingAddress == null) {
				return ResponseEntity.notFound().build();
			}
			existingAddress.setCreatedAt(new Date(System.currentTimeMillis()));
			existingAddress.setCity(address.getCity());
			existingAddress.setCountry(address.getCountry());
			existingAddress.setPostalCode(address.getPostalCode());
			existingAddress.setState(address.getState());
			existingAddress.setDistrict(address.getDistrict());
			existingAddress.setStreetAddress(address.getStreetAddress());
			existingAddress.setAddressType(address.getAddressType());
			existingAddress.setMobileNumber(address.getMobileNumber());
			userAddressService.saveAddressDetails(existingAddress);

//			Map<String, Object> successResponse = new HashMap<>();
//			successResponse.put("message", "Address details updated successfully.");

			return ResponseEntity.ok("Address details updated successfully.");
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			String errorMessage = "Failed to update address details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@DeleteMapping("/address/delete/{userAddressId}")
	public ResponseEntity<Object> deleteAddressId(@PathVariable("userAddressId") Long userAddressId) {
		try {
			userAddressService.deleteUserAddressById(userAddressId);
//			Map<String, Object> successResponse = new HashMap<>();
//			successResponse.put("message", "Address deleted successfully");
			return ResponseEntity.ok("Address deleted successfully");
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			String errorMessage =  "Failed to delete address";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@GetMapping("/userWithAddress/detail/{id}/{addressType}")
	public ResponseEntity<List<Map<String, Object>>> getUserWithAddressTypeDetails(@PathVariable("id") Long userId,
			@PathVariable("addressType") String addressType) {
		try {
			List<Map<String, Object>> result = userAddressRepository.getUserAddressTypeDetails(userId, addressType);

			if (result == null) {
				return ResponseEntity.ok(Collections.emptyList());
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/userWithAddress/{id}/{addressType}")
	public List<Map<String, Object>> getUserWithAddressTypeDetails1(@PathVariable("id") Long userId,
			@PathVariable("addressType") String addressType) {
		return userAddressRepository.getUserAddressTypeDetails(userId, addressType);
	}
	
	
}
