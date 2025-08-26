package com.example.kkBazar.controller.user;

import java.sql.Date;

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
import com.example.kkBazar.entity.user.BankDetails;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.entity.user.OrderRefund;
import com.example.kkBazar.repository.user.OrderIemListRepository;
import com.example.kkBazar.service.user.BankDetailsService;
import com.example.kkBazar.service.user.OrderRefundService;

@RestController
@CrossOrigin
public class BankDetailsController {

	@Autowired
	private BankDetailsService bankService;
	
	@Autowired
	private OrderIemListRepository orderItemListRepository;
	
	@Autowired
	private OrderRefundService orderRefundService;

	@GetMapping("/bankDetails")
	public ResponseEntity<?> getUserBankDetails(@RequestParam(required = true) String bank) {
		try {
			if ("bankDetails".equals(bank)) {
				Iterable<BankDetails> bankDetails = bankService.listAll();
				return new ResponseEntity<>(bankDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided bank is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving bank details: " + e.getMessage());
		}
	}

	@PostMapping("/bank/save")
	public ResponseEntity<?> saveUserBankDetails(@RequestBody BankDetails bank) {
		try {
			bank.setCreatedAt(new Date(System.currentTimeMillis()));
		long orderId=bank.getOrderItemListId();
		long returnId =bank.getOrderReturnId();
		long userId = bank.getUserId();
			
//			if (bank.getUserId() == null || isNullOrEmpty(bank.getHolderName()) || bank.getAccountNumber() == 0
//					|| isNullOrEmpty(bank.getBankName()) || isNullOrEmpty(bank.getBranchName())
//					|| isNullOrEmpty(bank.getIfscCode()) || isNullOrEmpty(bank.getPanNumber())) {
//
//				String errorMessage = "All fields are required.";
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
//			}

			bankService.SaveBankDetails(bank);
			long id = bank.getBankId();
			OrderRefund orderRefund =new OrderRefund();
			orderRefund.setOrderItemListId(orderId);
			orderRefund.setOrderReturnId(returnId);
			orderRefund.setUserId(userId);
			orderRefund.setBankId(id);
			orderRefund.setCreatedAt(new Date(System.currentTimeMillis()));
			orderRefund.setReturnStatus("pending");
			orderRefund.setPending(true);
			orderRefundService.SaveOrderReturnDetails(orderRefund);
			OrderItemList orderItemList = orderItemListRepository.findById(orderRefund.getOrderItemListId())
					.orElse(null);
			if (orderItemList != null) {
				orderItemList.setOrderStatus("refundRequestPending");
//				orderItemList.setReturnPending(true);
//				orderItemList.setDelivered(false);
				orderItemListRepository.save(orderItemList);
			}
			
			return ResponseEntity.status(HttpStatus.OK).body("Bank details saved successfully. " + id);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = "An error occurred while saving Bank details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PutMapping("/bankDetails/edit/{bankId}")
	public ResponseEntity<String> updateBankId(@PathVariable("bankId") Long bankId, @RequestBody BankDetails bank) {
		try {
			if (bank.getUserId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is required.");
			}

			if (isNullOrEmpty(bank.getHolderName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Holder Name is required.");
			}

			if (bank.getAccountNumber() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account Number is required.");
			}

			if (isNullOrEmpty(bank.getBankName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bank Name is required.");
			}

			if (isNullOrEmpty(bank.getBranchName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Branch Name is required.");
			}

			if (isNullOrEmpty(bank.getIfscCode())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IFSC Code is required.");
			}

			if (isNullOrEmpty(bank.getPanNumber())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PAN Number is required.");
			}

			BankDetails existingBank = bankService.findById(bankId);

			if (existingBank == null) {
				return ResponseEntity.notFound().build();
			}
			existingBank.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingBank.setBankName(bank.getBankName());
			existingBank.setAccountNumber(bank.getAccountNumber());
			existingBank.setBankAddress(bank.getBankAddress());
			existingBank.setBranchName(bank.getBranchName());
			existingBank.setHolderName(bank.getHolderName());
			existingBank.setIfscCode(bank.getIfscCode());
			existingBank.setPanNumber(bank.getPanNumber());
			existingBank.setUserId(bank.getUserId());

			bankService.SaveBankDetails(existingBank);

			return ResponseEntity.ok("Bank details updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	@DeleteMapping("/bank/delete/{bankId}")
	public ResponseEntity<String> deleteBankDetail(@PathVariable("bankId") Long bankId) {
		bankService.deleteBankId(bankId);
		return ResponseEntity.ok("Bank details deleted successfully");
	}

}
