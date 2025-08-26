package com.example.kkBazar.service.companyProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.companyProfile.InvoiceList;
import com.example.kkBazar.repository.companyProfile.InvoiceListRepository;

@Service
public class InvoiceListService {

	@Autowired
	private InvoiceListRepository invoiceListRepository;
	
	
	//save
			public InvoiceList SaveInvoiceListDetails(InvoiceList invoiceList) {
				return invoiceListRepository.save(invoiceList);
			}
	public InvoiceList findById(Long invoiceListId) {
		return invoiceListRepository.findById(invoiceListId).get();
	}
	
}
