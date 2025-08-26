package com.example.kkBazar.service.dashboard;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.dashboard.Dashboard1;
import com.example.kkBazar.repository.dashboard.Dashboard1Repository;

@Service
public class Dashboard1Service {

	@Autowired
	private Dashboard1Repository dashboard1Repository;
	
	public List<Dashboard1> listDashboard() {
		return this.dashboard1Repository.findAll();
	}

	// save
	public Dashboard1 SaveDashboard(Dashboard1 dashboard) {
		return dashboard1Repository.save(dashboard);
	}

	public Dashboard1 findDashboardById(Long id) {
		return dashboard1Repository.findById(id).get();
	}

	// delete
	public void deleteDashboardById(Long id) {
		dashboard1Repository.deleteById(id);
	}

	public Optional<Dashboard1> getById1(long id) {
        return Optional.of(dashboard1Repository.findById(id).get());
    }

	


	
}
