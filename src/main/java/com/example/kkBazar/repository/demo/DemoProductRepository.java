package com.example.kkBazar.repository.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.demo.DemoProduct;

public interface DemoProductRepository extends JpaRepository<DemoProduct, Long>{

}
