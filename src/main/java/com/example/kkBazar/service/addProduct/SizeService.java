package com.example.kkBazar.service.addProduct;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.addProduct.Size;
import com.example.kkBazar.repository.addProduct.SizeRepository;

@Service
public class SizeService {
	
	@Autowired
	private SizeRepository sizerepo;

	// view
	public List<Size> listSize() {
		return this.sizerepo.findAll();
	}

	// save
	public Size SaveSizeDetails(Size size) {
		return sizerepo.save(size);
	}

	public Size findSizeById(Long id) {
		return sizerepo.findById(id).get();
	}

	// delete
	public void deleteSizeById(Long id) {
		sizerepo.deleteById(id);
	}

}
