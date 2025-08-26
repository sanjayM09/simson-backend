package com.example.kkBazar.service.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.dashboard.Dashboard1;
import com.example.kkBazar.entity.dashboard.Dashboard2;
import com.example.kkBazar.entity.dashboard.Dashboard3;
import com.example.kkBazar.repository.dashboard.Dashboard2Repository;

@Service
public class Dashboard2Service {

	
	@Autowired
	private Dashboard2Repository dashboard2Repository;
	
	public List<Dashboard2> listDashboard() {
		return this.dashboard2Repository.findAll();
	}

	// save
	public Dashboard2 SaveDashboard(Dashboard2 dashboard) {
		return dashboard2Repository.save(dashboard);
	}

	public Dashboard2 findDashboardById(Long dashboardId) {
		return dashboard2Repository.findById(dashboardId).get();
	}

	// delete
	public void deleteDashboardById(Long id) {
		dashboard2Repository.deleteById(id);
	}

	public Optional<Dashboard2> getById1(long id) {
        return Optional.of(dashboard2Repository.findById(id).get());
    }
}
