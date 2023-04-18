package com.example.QuartzScheduling.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.QuartzScheduling.CustomException.ErrorMessage;
import com.example.QuartzScheduling.CustomException.Message;
import com.example.QuartzScheduling.Entity.ScheduleInfo;
import com.example.QuartzScheduling.Job.JobData;
import com.example.QuartzScheduling.Job.ScheduleJob;
import com.example.QuartzScheduling.Repo.ScheduleInfoRepo;

@Service
public class ScheduleService {
	
	@Autowired
	Scheduler quartzScheduler;
	
	@Autowired
	ScheduleInfoRepo scheduleInfoRepo;

	public ResponseEntity schedule(JobData jobData) {
		
		ErrorMessage errorMessage= new ErrorMessage();
		String jobName = jobData.getJobName();
		if(jobName == null) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Name cannot be null");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}else if(jobName == "" || jobName.isBlank()) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Name cannot be Empty or Blank");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		String jobGroup = jobData.getJobGroup();
		if(jobGroup == null) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Group cannot be null");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}else if(jobGroup == "" || jobGroup.isBlank()) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Group cannot be Empty or Blank");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		String startTime = jobData.getJobStartTime();
		LocalDateTime jobStartTime = null;
		try {
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
			jobStartTime = LocalDateTime.parse(startTime, formatter);
		}catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Start Date format must be in  "+"yyyy-MM-dd HH:mm");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		String endTime = jobData.getJobEndTime();
		LocalDateTime jobEndTime = null;
		if(endTime !=null) {
			try {
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
				jobEndTime = LocalDateTime.parse(endTime, formatter);
			}catch(Exception e) {
				errorMessage.setErrorCode(randomErrorCode());
				errorMessage.setErrorMessage("End Date format must be in  "+"yyyy-MM-dd HH:mm");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
			}
		}

		String cornExpression = null;
		
		if(jobData.getJobType().equalsIgnoreCase("monthly")) {
			cornExpression = MonthlyScheduleJob(jobStartTime,jobEndTime);
		}else {
			if(jobData.getJobType().equalsIgnoreCase("daily")) {
				cornExpression = DailyScheduleJob(jobStartTime,jobEndTime);
			}else {
				if(jobData.getJobType().equalsIgnoreCase("hourly")) {
					cornExpression = HourlyScheduleJob(jobStartTime,jobEndTime);
				}else {
					if(jobData.getJobType().equalsIgnoreCase("minutely")) {
						cornExpression = MinutelyScheduleJob(jobStartTime,jobEndTime);
					}
					else {
						errorMessage.setErrorCode(randomErrorCode());
						errorMessage.setErrorMessage(jobData.getJobType()+" is not valid job type. "+" The correct value for Job Type are monthly,daily,hourly,minutely.");
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
					}
				}
			}
		}
		
		
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("test","this is for demo");
		JobDetail jobDetail =null;
		ZonedDateTime zoneDateTime =ZonedDateTime.of(jobStartTime, ZoneId.of("Asia/Kolkata"));
		ZonedDateTime zoneDateTime2 =ZonedDateTime.of(jobEndTime, ZoneId.of("Asia/Kolkata"));
		try {
			jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(jobName,jobGroup)
					.usingJobData(jobDataMap).build();
		}catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("job Name already exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		Trigger trigger =null;
		try {
			trigger =  TriggerBuilder.newTrigger()
					.withIdentity(jobName, jobGroup)
					.startAt(Date.from(zoneDateTime.toInstant()))
					.withSchedule(CronScheduleBuilder.cronSchedule(cornExpression)).endAt(Date.from(zoneDateTime2.toInstant()))	
					.build();
		}catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job not successfully triggerd");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
		}
		
		
            try {
            	if(jobDetail != null) {
            		if(trigger != null) {
            			System.out.println(quartzScheduler.getSchedulerName());
            			quartzScheduler.scheduleJob(jobDetail,trigger);
            			
            			System.out.println(quartzScheduler.getSchedulerName());
            			System.out.println(jobDetail.getKey());
            			System.out.println(trigger.getKey());
            		}
            		else {
            			errorMessage.setErrorCode(randomErrorCode());
            			errorMessage.setErrorMessage("Job Trigger not successfully build ");
            			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            		}
            	}
            	else {
            		errorMessage.setErrorCode(randomErrorCode());
        			errorMessage.setErrorMessage("Job Details not successfully build ");
            		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            	}
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				errorMessage.setErrorCode(randomErrorCode());
    			errorMessage.setErrorMessage("Job not successfully scheduled");
        		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
			}
			
			ScheduleInfo scheduleInfo = new ScheduleInfo();
			scheduleInfo.setJobName(jobName);
			scheduleInfo.setJobGroup(jobGroup);
			scheduleInfo.setJobType(jobData.getJobType());
			scheduleInfo.setJobStartTime(jobStartTime);
			scheduleInfo.setCreated(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
			scheduleInfo.setModified(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
			scheduleInfo.setJobEndTime(jobEndTime);
			scheduleInfo.setStatus("New");
			System.out.println(scheduleInfo.toString());
			try {
				scheduleInfoRepo.save(scheduleInfo);
				return ResponseEntity.status(HttpStatus.CREATED).body(scheduleInfo);
			}catch(Exception e) {
				errorMessage.setErrorCode(randomErrorCode());
    			errorMessage.setErrorMessage("Task not successfully created");
				return ResponseEntity.badRequest().body(errorMessage);
			}
		
	}
	
	public String DailyScheduleJob(LocalDateTime startTime,LocalDateTime jobEndTime) {
		if(jobEndTime == null) {
			return "0 " + startTime.getMinute() + " "+ startTime.getHour() + " 1/1 " + "* " + "?";
		}
		return "0 " + startTime.getMinute()+"-"+ jobEndTime.getMinute() + " "+ startTime.getHour() + " 1/1 " + "* " + "?";
		
	}
	
	public String HourlyScheduleJob(LocalDateTime startTime,LocalDateTime jobEndTime) {
		if(jobEndTime == null) {
			return "0 " +startTime.getMinute() +" "+ startTime.getHour()+"/1 " + "* "+"* " +"? ";
		}
		return "0 " +startTime.getMinute() + " "+ startTime.getHour()+"-"+ jobEndTime.getHour() +"/1 " + "* "+"* " +"? ";
		
	}
	
	public String MonthlyScheduleJob(LocalDateTime startTime,LocalDateTime jobEndTime) {
		if(jobEndTime == null) {
			return "0 " + startTime.getMinute() +" "+ startTime.getHour() +" "+ startTime.getDayOfMonth()
			+" " +startTime.getMonthValue()+"/1 "+"?";
			
	}
		return "0 " + startTime.getMinute() +" "+ startTime.getHour() +" "+ startTime.getDayOfMonth()
		+" " +startTime.getMonthValue()+"-"+jobEndTime.getMonthValue()+"/1 "+"?";
	}
	
	public String MinutelyScheduleJob(LocalDateTime startTime,LocalDateTime jobEndTime) {
		return "0 " +startTime.getMinute()+"-"+jobEndTime.getMinute() +"/1 "+ startTime.getHour() + " * "+"* " +"?";
		
	}
	
	public String randomErrorCode() {
		
		String limitedString= "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String s= "";
		
		Random random = new Random();
		while(s.length() <= 6) {
			s = s + limitedString.charAt(random.nextInt(limitedString.length()));
		}
		System.out.println(s);
		return s;
		
	}
	
	ErrorMessage message;
	public ResponseEntity updateSchedulingTask(JobData jobData , String jobUUID) throws IOException, SchedulerException {
        
		
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobUUID(jobUUID);
		
		 String jobName =  scheduleInfo.getJobName();
		 String jobGroup = scheduleInfo.getJobGroup();
		 message = new ErrorMessage();
		 Scheduler scheduler = quartzScheduler;
		// System.out.println(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("bbb")));
		 TriggerKey jobTriggerKey = new TriggerKey(jobName,jobGroup);
		 System.out.println(jobTriggerKey);
		 JobKey jobKeyObj = new JobKey(jobName,jobGroup);
		 Trigger oldTrigger = scheduler.getTrigger(jobTriggerKey);
		 JobDetail jobDetail = scheduler.getJobDetail(jobKeyObj);
		 try {
			 
			 TriggerBuilder newTriggerBuilder = oldTrigger.getTriggerBuilder();
			 String startTime = jobData.getJobStartTime();
			
				LocalDateTime jobStartTime = null;
				if(startTime != null) {
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
					jobStartTime = LocalDateTime.parse(startTime, formatter);
				}catch(Exception e) {
					message.setErrorCode(randomErrorCode());
					message.setErrorMessage("Start Date format must be in  "+"yyyy-MM-dd HH:mm");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				}
				}
				if(jobStartTime == null) {
					jobStartTime = scheduleInfo.getJobStartTime();
				}
				
				String endTime = jobData.getJobEndTime();
				LocalDateTime jobEndTime = null;
				if(endTime != null) {
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
					jobEndTime = LocalDateTime.parse(endTime, formatter);
				}catch(Exception e) {
					message.setErrorCode(randomErrorCode());
					message.setErrorMessage("End Date format must be in  "+"yyyy-MM-dd HH:mm");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
				}
				}
				if(jobEndTime == null) {
					jobEndTime = scheduleInfo.getJobEndTime();
				}
				if(jobData.getJobType() == null) {
					jobData.setJobType(scheduleInfo.getJobType());
				}
				if(jobData.getJobType() != null) {
					String cornExpression = null;
					
					if(jobData.getJobType().equalsIgnoreCase("monthly")) {
						cornExpression = MonthlyScheduleJob(jobStartTime,jobEndTime);
					}else {
						if(jobData.getJobType().equalsIgnoreCase("daily")) {
							cornExpression = DailyScheduleJob(jobStartTime,jobEndTime);
						}else {
							if(jobData.getJobType().equalsIgnoreCase("hourly")) {
								cornExpression = HourlyScheduleJob(jobStartTime,jobEndTime);
							}else {
								if(jobData.getJobType().equalsIgnoreCase("minutely")) {
									cornExpression = MinutelyScheduleJob(jobStartTime,jobEndTime);
								}
								else {
									message.setErrorCode(randomErrorCode());
									message.setErrorMessage(jobData.getJobType()+" is not valid job type. "+" The correct value for Job Type are monthly,daily,hourly,minutely.");
									return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
								}
							}
						}
					}
					
					System.out.println(cornExpression);
					newTriggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cornExpression));
					System.out.println(newTriggerBuilder);
				}
				
				Trigger newTrigger = newTriggerBuilder.build();
				scheduler.rescheduleJob(jobTriggerKey, newTrigger);
				
				if(jobData.getJobType() != null)
				scheduleInfo.setJobType(jobData.getJobType());
				if(jobStartTime != null)
				scheduleInfo.setJobStartTime(jobStartTime);
				
				scheduleInfo.setModified(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
				scheduleInfo.setStatus("Modified");
				if(jobEndTime != null)
				scheduleInfo.setJobEndTime(jobEndTime);
				
				try {
					scheduleInfoRepo.save(scheduleInfo);
					return ResponseEntity.status(HttpStatus.OK).body(scheduleInfo);
				}catch(Exception e) {
					message.setErrorCode(randomErrorCode());
	 			message.setErrorMessage("Task not successfully created");
					return ResponseEntity.badRequest().body(message);
				}
		 }catch(Exception e) {
			 message.setErrorCode(randomErrorCode());
			 message.setErrorMessage("Non existing jobDetail");
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
		 }
		
	}
	
	public ResponseEntity getAllJobs(){
		try {
			return ResponseEntity.status(HttpStatus.OK).body(scheduleInfoRepo.findAll());
		}catch(Exception e) {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("Something went wrong while getting the data from database");
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		
	}
	
	public ResponseEntity getSpecificJob(String jobUUID){
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobUUID(jobUUID);
		if(scheduleInfo != null) {
			return ResponseEntity.status(HttpStatus.OK).body(scheduleInfo);
		}
		else {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("Job not found for this Job UUID : "+jobUUID);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}
	
	
	public ResponseEntity deleteJob(String jobUUID) {
		message = new ErrorMessage();
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobUUID(jobUUID);
	//	Message successMessage = new Message();
		if(scheduleInfo != null) {
			JobKey jobKeyObj = new JobKey(scheduleInfo.getJobName(),scheduleInfo.getJobGroup());
			TriggerKey triggerKey = new TriggerKey(scheduleInfo.getJobName(),scheduleInfo.getJobGroup());
			try {
			
//				quartzScheduler.deleteJob(jobKeyObj);
				quartzScheduler.unscheduleJob(triggerKey);
				scheduleInfo.setStatus("InActive");
				scheduleInfoRepo.save(scheduleInfo);
//				successMessage.setJobUUID(jobUUID);
//				successMessage.setJobName(scheduleInfo.getJobName());
//				successMessage.setJobGroup(scheduleInfo.getJobGroup());
//				successMessage.setMessage("Job successfully deleted and no longer available");
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(scheduleInfo);
				
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				message.setErrorCode(randomErrorCode());
				message.setErrorMessage("Something went wrong while deleting the job ");
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		}
		else {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("Job not found for this Job UUID : "+jobUUID);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}
	
	public ResponseEntity getJobsUsingStatus(String jobStatus) {
		message = new ErrorMessage();
		List<ScheduleInfo> scheduleInfoList = scheduleInfoRepo.findAllByStatus(jobStatus);
		if(scheduleInfoList.size() > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(scheduleInfoList);
		}
		else {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("There is no "+ jobStatus + " job");
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		
	}
}
