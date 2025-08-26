package com.example.kkBazar.repository.dashboard;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.dashboard.Dashboard4;

public interface Dashboard4Repository extends JpaRepository<Dashboard4, Long> {

	@Query(value = " select d4.*, c.category_name from dashboard4 as d4"
			+ "			 join category as c on c.category_id=d4.category_id"
			+ "			 where d4.status = true", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard4Details();
}
