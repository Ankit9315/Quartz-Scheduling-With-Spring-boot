package com.example.QuartzScheduling.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.QuartzScheduling.Entity.ScheduleInfo;

@Repository
public interface ScheduleInfoRepo extends JpaRepository<ScheduleInfo,Long>{

	public ScheduleInfo findByJobName(String string);
	
	public ScheduleInfo findByJobUUID(String string);
	
	@Transactional
	public String deleteByJobUUID(String string);
	
	public List findAllByStatus(String status);
}
