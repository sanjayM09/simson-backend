package com.example.kkBazar.service.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.dashboard.Dashboard1;
import com.example.kkBazar.entity.dashboard.Dashboard3;
import com.example.kkBazar.repository.dashboard.Dashboard3Repository;

@Service
public class Dashboard3Service {

	
	@Autowired
	private Dashboard3Repository dashboard3Repository;
	
	public List<Dashboard3> listDashboard() {
		return this.dashboard3Repository.findAll();
	}

	// save
	public Dashboard3 SaveDashboard(Dashboard3 dashboard) {
		return dashboard3Repository.save(dashboard);
	}

	public Dashboard3 findDashboardById(Long dashboardId) {
		return dashboard3Repository.findById(dashboardId).get();
	}

	// delete
	public void deleteDashboardById(Long id) {
		dashboard3Repository.deleteById(id);
	}
	
	public Optional<Dashboard3> getById1(long id) {
        return Optional.of(dashboard3Repository.findById(id).get());
    }
}
