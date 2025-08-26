package com.example.kkBazar.service.whatsapp.pdf;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PdfRepository extends JpaRepository<Pdf, Long>{

	
@Query(value="select p.pdf_id as pdfId, p.url,p.order_item_id as orderItemId,o.user_id as userId from pdf as p"
		+ " join order_item as o on o.order_item_id=p.order_item_id"
		+ " join user as u on u.user_id =o.user_id where o.user_id=:user_id and p.order_item_id=:order_item_id", nativeQuery = true)
		List<Map<String, Object>>getInvoicePdf(Long user_id, Long order_item_id);
		 
}
