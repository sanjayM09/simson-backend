package com.example.kkBazar.repository.dashboard;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.dashboard.Dashboard3;

public interface Dashboard3Repository extends JpaRepository<Dashboard3, Long> {

	@Query(value = "select d3.*,c.category_name from dashboard3 as d3"
			+ " join category as c on c.category_id = d3.category_id"
			+ " where d3.status = true", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard3Details();
	
	
	@Query(value = "select d3.*,c.category_name from dashboard3 as d3"
			+ " join category as c on c.category_id = d3.category_id"
			+ " ", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard3DetailsAdmin();
}
