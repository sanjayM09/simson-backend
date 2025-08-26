package com.example.kkBazar.repository.addProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.addProduct.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	Optional<Category> findByCategoryName(String categoryname);

}
