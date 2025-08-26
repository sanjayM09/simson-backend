package com.example.kkBazar.service.companyProfile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.companyProfile.Company;
import com.example.kkBazar.repository.companyProfile.CompanyRepository;

@Service
public class CompanyService {
@Autowired
private CompanyRepository companyRepository;

//view
	public List<Company> listAll() {
		return this.companyRepository.findAll();
	}

//save
	public Company SaveCompanyDetails(Company company) {
		return companyRepository.save(company);
	}
	
	public Company findById(Long companyId) {
		return companyRepository.findById(companyId).get();
	}

	//delete
		public void deleteCompanyId(Long id) {
			companyRepository.deleteById(id);
		}
}
