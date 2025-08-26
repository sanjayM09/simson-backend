package com.example.kkBazar.controller.companyProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.companyProfile.InvoiceList;
import com.example.kkBazar.service.companyProfile.InvoiceListService;

@RestController
@CrossOrigin
public class InvoiceListController {

	@Autowired
	private InvoiceListService invoiceListService;
	
	@PutMapping("/invoiceListStatus/edit/{id}")
	public ResponseEntity<InvoiceList> updateInvoiceList(@PathVariable("id") Long invoiceListId, @RequestBody InvoiceList invoiceList) {
	    try {
	        InvoiceList existingInvoiceList = invoiceListService.findById(invoiceListId);

	        if (existingInvoiceList == null) {
	            return ResponseEntity.notFound().build();
	        }

	        if ("approved".equalsIgnoreCase(invoiceList.getStatusType())) {
	        	existingInvoiceList.setStatus(true);
	        }

	        existingInvoiceList.setStatusType(invoiceList.getStatusType());
	        existingInvoiceList.setInvoiceListDate(invoiceList.getInvoiceListDate());

	        invoiceListService.SaveInvoiceListDetails(existingInvoiceList);
	        return ResponseEntity.ok(existingInvoiceList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}
