package com.example.kkBazar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.lang.Override;
import com.example.kkBazar.service.admin.AdminLoginService;
import com.example.kkBazar.service.admin.RoleService;



@Component
public class ApplicationRunner implements CommandLineRunner {

	@Autowired
	private RoleService roleService;

	@Autowired
	private AdminLoginService adminLoginService;

	@Override
	public void run(String... args) {
		roleService.addAdmin();
		adminLoginService.addAdminLoginService();
	}
	
}
