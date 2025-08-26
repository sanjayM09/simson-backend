package com.example.kkBazar.service.companyProfile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.companyProfile.Invoice;
import com.example.kkBazar.repository.companyProfile.InvoiceRepository;

@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;
	
	//view
		public List<Invoice> listAll() {
			return this.invoiceRepository.findAll();
		}

	//save
		public Invoice SaveInvoiceDetails(Invoice invoice) {
			return invoiceRepository.save(invoice);
		}
		
		public Invoice findById(Long invoiceId) {
			return invoiceRepository.findById(invoiceId).get();
		}

		//delete
			public void deleteInvoiceId(Long id) {
				invoiceRepository.deleteById(id);
			}
}
