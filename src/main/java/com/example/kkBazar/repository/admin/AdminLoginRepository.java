package com.example.kkBazar.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.admin.AdminLogin;

public interface AdminLoginRepository extends JpaRepository<AdminLogin, Long>{
	AdminLogin findByEmail(String email);
}
