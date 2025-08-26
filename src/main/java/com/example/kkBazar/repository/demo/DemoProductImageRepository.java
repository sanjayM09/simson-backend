package com.example.kkBazar.repository.demo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.demo.DemoProductImages;

public interface DemoProductImageRepository extends JpaRepository<DemoProductImages, Long> {

	Optional<DemoProductImages> findByDemoProImagesId(Long demoProImagesId);

	@Query(value = "select d.*,di.demo_pro_images_id,di.deleted from demo_pro as d"
			+ " join demo_pro_images as di on di.demo_id = d.demo_id"
			+ " where di.deleted = false", nativeQuery = true)
	List<Map<String, Object>> getAllDemoDetails();

}
