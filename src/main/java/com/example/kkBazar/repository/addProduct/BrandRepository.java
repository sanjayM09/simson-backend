package com.example.kkBazar.repository.addProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.addProduct.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long>{

	Optional<Brand> findByBrandName(String brandname);

}
