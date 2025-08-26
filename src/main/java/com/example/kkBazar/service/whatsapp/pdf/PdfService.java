package com.example.kkBazar.service.whatsapp.pdf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class PdfService {

	@Autowired
	private PdfRepository pdfRepository;
	
	public List<Pdf> listPdf() {
		return this.pdfRepository.findAll();
	}

	public Pdf savePdf(Pdf cart) {
		return pdfRepository.save(cart);
	}

	public Pdf findById(Long pdfId) {
		return pdfRepository.findById(pdfId).get();
	}


	public void deletePdfById(long addToCartId) {
		pdfRepository.deleteById(addToCartId);
	}
}
