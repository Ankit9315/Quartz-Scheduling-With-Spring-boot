package com.example.QuartzScheduling.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuartzScheduling.Entity.ScheduleInfo;
import com.example.QuartzScheduling.Entity.Status;

@Repository
public interface StatusRepo extends JpaRepository<Status,Integer>{

	public Status findByStatusName(String statusName);
}
