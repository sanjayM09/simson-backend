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

import com.example.kkBazar.entity.user.Contact;
import com.example.kkBazar.repository.user.ContactRepository;
import com.example.kkBazar.service.user.ContactService;



@RestController
@CrossOrigin
public class ContactController {

	@Autowired
	private ContactService departmentService;
	


	@GetMapping("/contact")
	public ResponseEntity<?> getDetails(@RequestParam(required = true) String contactParam) {
	    try {
	        if ("contact".equals(contactParam)) {
	            Iterable<Contact> departmentDetails = departmentService.listAll();
	            return new ResponseEntity<>(departmentDetails, HttpStatus.OK);
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided Contact is not supported.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error retrieving department details: " + e.getMessage());
	    }
	}


	@PostMapping("/contact/save")
	public ResponseEntity<?> saveDepartment(@RequestBody Contact department) {
		try {
			department.setCreatedAt(new Date(System.currentTimeMillis()));
			departmentService.SaveorUpdate(department);
			long id = department.getContactId();
			return ResponseEntity.status(HttpStatus.OK).body("Contact details saved successfully."  + id);
		} catch (Exception e) {
			String errorMessage = "An error occurred while saving Contact details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}


	@PutMapping("/contact/edit/{ContactId}")
	public ResponseEntity<?> updateDepartmentId(@PathVariable("ContactId") Long contactId, @RequestBody Contact DepartmentIdDetails) {
		try {
			Contact existingDepartment = departmentService.findById(contactId);
			if (existingDepartment == null) {
				return ResponseEntity.notFound().build();
			}
			existingDepartment.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingDepartment.setAddress(DepartmentIdDetails.getAddress());
			existingDepartment.setMessage(DepartmentIdDetails.getMessage());
			existingDepartment.setName(DepartmentIdDetails.getName());
			departmentService.SaveorUpdate(existingDepartment);
			return ResponseEntity.ok(existingDepartment);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/contact/delete/{contactId}")
	public ResponseEntity<String> deletDepartmentName(@PathVariable("contactId") Long contactId) {
		departmentService.deleteById(contactId);
		return ResponseEntity.ok("contact deleted successfully");
}
	
	
	

}
