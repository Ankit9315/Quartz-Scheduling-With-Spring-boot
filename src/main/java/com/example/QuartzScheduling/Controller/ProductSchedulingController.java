package com.example.QuartzScheduling.Controller;


import java.io.IOException;
import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.QuartzScheduling.Entity.ScheduleInfo;
import com.example.QuartzScheduling.Job.JobData;
import com.example.QuartzScheduling.Service.ScheduleService;

@RestController
public class ProductSchedulingController {
	 
	 @Autowired
	 ScheduleService scheduleService;
	 
	 @PostMapping("/schedule")
	 public ResponseEntity<ScheduleInfo> scheduleTask(@RequestBody JobData jobData) {
		 System.out.println(jobData.toString());
		 return scheduleService.schedule(jobData);
	 }
	 
	 @PatchMapping("/schedule/{jobUUID}")
	 public ResponseEntity<ScheduleInfo> updateScheduleTask(@PathVariable String jobUUID , @RequestBody JobData jobData) throws IOException, SchedulerException{
		 
//		 scheduleService.updateSchedulingTask(jobKey,triggerKey,jobData);
		return scheduleService.updateSchedulingTask(jobData,jobUUID);
		 
	 }
	 
	 @GetMapping("/schedules")
	 public ResponseEntity<List> getAllJobs(){
		return scheduleService.getAllJobs();
		 
	 }
	 
	 @GetMapping("/schedule/{jobUUID}")
	 public ResponseEntity<ScheduleInfo> getJob(@PathVariable String jobUUID){
			return scheduleService.getSpecificJob(jobUUID);
			 
		 }
	 
	 @DeleteMapping("/schedule/{jobUUID}")
	 public ResponseEntity<Object> deleteJob(@PathVariable String jobUUID){
			return scheduleService.deleteJob(jobUUID);
		 }
	 
	 @GetMapping("/schedule")
	 public ResponseEntity getJobsByStatus(@RequestParam("status") String status) {
		return scheduleService.getJobsUsingStatus(status);
		 
	 }
}
