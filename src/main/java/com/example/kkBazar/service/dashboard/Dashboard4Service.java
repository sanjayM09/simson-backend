package com.example.kkBazar.service.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.dashboard.Dashboard4;
import com.example.kkBazar.repository.dashboard.Dashboard4Repository;

@Service

public class Dashboard4Service {

	@Autowired
	private Dashboard4Repository dashboard4Repository;
	
	public List<Dashboard4> listDashboard() {
		return this.dashboard4Repository.findAll();
	}

	// save
	public Dashboard4 SaveDashboard(Dashboard4 dashboard) {
		return dashboard4Repository.save(dashboard);
	}

	public Dashboard4 findDashboardById(Long dashboardId) {
		return dashboard4Repository.findById(dashboardId).get();
	}

	// delete
	public void deleteDashboardById(Long id) {
		dashboard4Repository.deleteById(id);
	}
	
	public Optional<Dashboard4> getById1(long id) {
        return Optional.of(dashboard4Repository.findById(id).get());
    }
}
