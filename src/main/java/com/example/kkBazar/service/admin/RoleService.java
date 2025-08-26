package com.example.kkBazar.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.admin.Role;
import com.example.kkBazar.repository.admin.RoleRepository;

@Service
public class RoleService {
	@Autowired
	private RoleRepository roleRepository;

	public void addAdmin() {

		Role admin = new Role();
		admin.setRoleId(1);
		admin.setRoleName("Admin");
		roleRepository.save(admin);

		Role superAdmin = new Role();
		superAdmin.setRoleId(2);
		superAdmin.setRoleName("superAdmin");
		roleRepository.save(superAdmin);

		Role biller = new Role();
		biller.setRoleId(3);
		biller.setRoleName("biller");
		roleRepository.save(biller);
	}

}