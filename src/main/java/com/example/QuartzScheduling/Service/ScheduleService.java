package com.example.QuartzScheduling.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.Calendar;
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
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.QuartzScheduling.Calendar.MySchedulingCalendar;
import com.example.QuartzScheduling.CustomException.ErrorMessage;
import com.example.QuartzScheduling.CustomException.Message;
import com.example.QuartzScheduling.Entity.ScheduleInfo;
import com.example.QuartzScheduling.Entity.Status;
import com.example.QuartzScheduling.Job.JobData;
import com.example.QuartzScheduling.Job.ScheduleJob;
import com.example.QuartzScheduling.LoggingManagement.LoggingClass;
import com.example.QuartzScheduling.Repo.ScheduleInfoRepo;
import com.example.QuartzScheduling.Repo.StatusRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

@Service
public class ScheduleService {
	
	
	
	Logger logger =LogManager.getLogger(ScheduleService.class);
	@Autowired
	Scheduler quartzScheduler;
	
	@Autowired
	ScheduleInfoRepo scheduleInfoRepo;
	
	@Autowired
	StatusRepo statusRepo;

	public ResponseEntity schedule(JobData jobData) {
		
		 ObjectMapper Obj = new ObjectMapper(); 
		 String jsonStr = null;
         try {  
             // Converting the Java object into a JSON string  
            jsonStr = Obj.writerWithDefaultPrettyPrinter().writeValueAsString(jobData);
             // Displaying Java object into a JSON string  
             System.out.println();  
         }  
         catch (IOException e) {  
             e.printStackTrace();  
         }  
       
		
		ErrorMessage errorMessage= new ErrorMessage();
		String jobName = jobData.getJobName();
		if(jobName == null) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Name is null");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}else if(jobName == "" || jobName.isBlank()) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Name is Empty or Blank");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		String jobGroup = jobData.getJobGroup();
		if(jobGroup == null) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Group Name is null");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}else if(jobGroup == "" || jobGroup.isBlank()) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job Group is Empty or Blank");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
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
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
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
				logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
			}
		}

		if(jobStartTime.isBefore(LocalDateTime.now())) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Start date for task is in past");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
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
						logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
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
			logger.info("Job Detail successfully added with Job Name "+jobData.getJobName() +" and Job Group Name " + jobData.getJobGroup());
		}catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("job Name already exist");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		
		String sDate1="21/04/2023";  
	    Date date1 = null;
		try {
			date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
		MySchedulingCalendar calendar = new MySchedulingCalendar();
		HolidayCalendar holidayCalendar = new HolidayCalendar();
		try {
			if(jobData.getCalendarType().equalsIgnoreCase("Alternative")) {
				holidayCalendar = calendar.alternativeDays(jobEndTime, jobStartTime.toLocalDate());
			}
			else if(jobData.getCalendarType().equalsIgnoreCase("WeekOff")) {
				holidayCalendar = calendar.WeeklyOff(jobStartTime, jobEndTime, jobStartTime.toLocalDate());
			}
			else {
				errorMessage.setErrorCode(randomErrorCode());
				errorMessage.setErrorMessage("Calendar days type only as Alternative or WeekOff");
				logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
			}
		}
		catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Calendar not successfully created");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		if(holidayCalendar.getExcludedDates().isEmpty()) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("No day is exist between job Start Time "+jobStartTime.toLocalDate()+" and job End Time "+jobEndTime.toLocalDate() );
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		try {
			quartzScheduler.addCalendar(jobData.getCalendarName(), holidayCalendar, false, true);
			logger.info("Calendar added to modified the task" + jobData.getCalendarName() + "-" + jobData.getCalendarType());
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Calendar not been added because " +e1 );
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
		
		Trigger trigger =null;
		try {
			
			trigger =  TriggerBuilder.newTrigger()
					.withIdentity(jobName, jobGroup)
					.startAt(Date.from(zoneDateTime.toInstant()))
					.withSchedule(CronScheduleBuilder.cronSchedule(cornExpression)).modifiedByCalendar(jobData.getCalendarName()).endAt(Date.from(zoneDateTime2.toInstant()))	
					.build();
			
			logger.info("Trigger for job done" + "with trigger name "+ jobData.getJobName() +" and trigger group name "+ jobData.getJobGroup());
			
		}catch(Exception e) {
			errorMessage.setErrorCode(randomErrorCode());
			errorMessage.setErrorMessage("Job not successfully triggerd");
			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
		}
		
            try {
            	if(jobDetail != null) {
            		if(trigger != null) {
            			
            			
            				quartzScheduler.scheduleJob(jobDetail,trigger);
            				logger.info(" job successfully scheduled "+"with Job name "+ jobData.getJobName() +" and Job group name "+ jobData.getJobGroup());
//            				errorMessage.setErrorCode(randomErrorCode());
//                			errorMessage.setErrorMessage("Job not schedule on this"+LocalDateTime.now().getDayOfWeek()+" day of week ");
//                			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            			
            		}
            		else {
            			errorMessage.setErrorCode(randomErrorCode());
            			errorMessage.setErrorMessage("Job Trigger not successfully build ");
            			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
            			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            		}
            	}
            	else {
            		errorMessage.setErrorCode(randomErrorCode());
        			errorMessage.setErrorMessage("Job Details not successfully build ");
        			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
            		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            	}
			} catch (SchedulerException e) {
				errorMessage.setErrorCode(randomErrorCode());
    			errorMessage.setErrorMessage("Job not successfully scheduled");
    			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
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
			scheduleInfo.setCalendarName(jobData.getCalendarName());
			scheduleInfo.setCalendarType(jobData.getCalendarType());
	//		scheduleInfo.setStatus("New");
			try {
				Status status = statusRepo.findByStatusName("act");
		              
		                scheduleInfo.setStatus(status);
		                logger.info("Job is successfully added with "+ scheduleInfo.getJobUUID());
		                	scheduleInfoRepo.save(scheduleInfo);
		                return ResponseEntity.status(HttpStatus.CREATED).body(scheduleInfo);
		             
			}catch(Exception e) {
				errorMessage.setErrorCode(randomErrorCode());
    			errorMessage.setErrorMessage("Task not successfully created");
    			logger.error(errorMessage.getErrorMessage() + " \n " + jsonStr);
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
		return s;
		
	}
	
	ErrorMessage message;
	public ResponseEntity updateSchedulingTask(JobData jobData , String jobUUID) throws IOException, SchedulerException {
        
		
		
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobUUID(jobUUID);
		
		ObjectMapper Obj = new ObjectMapper(); 
		 String jsonStr = null;
        try {  
            // Converting the Java object into a JSON string  
           jsonStr = Obj.writerWithDefaultPrettyPrinter().writeValueAsString(jobData);
            // Displaying Java object into a JSON string  
            System.out.println();  
        }  
        catch (IOException e) {  
            e.printStackTrace();  
        }  
        
		 String jobName =  scheduleInfo.getJobName();
		 String jobGroup = scheduleInfo.getJobGroup();
		 message = new ErrorMessage();
		 Scheduler scheduler = quartzScheduler;
		 TriggerKey jobTriggerKey = new TriggerKey(jobName,jobGroup);
	
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
					logger.error(message.getErrorMessage() + " \n " + jsonStr);
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
					logger.error(message.getErrorMessage() + " \n " + jsonStr);
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
									logger.error(message.getErrorMessage() + " \n " + jsonStr);
									return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
								}
							}
						}
					}
					
					newTriggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cornExpression));
				}
				
				Trigger newTrigger = newTriggerBuilder.build();
				scheduler.rescheduleJob(jobTriggerKey, newTrigger);
				
				if(jobData.getJobType() != null)
				scheduleInfo.setJobType(jobData.getJobType());
				if(jobStartTime != null)
				scheduleInfo.setJobStartTime(jobStartTime);
				
				scheduleInfo.setModified(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
			//	scheduleInfo.setStatus("Modified");
				if(jobEndTime != null)
				scheduleInfo.setJobEndTime(jobEndTime);
				
				try {
					scheduleInfoRepo.save(scheduleInfo);
					return ResponseEntity.status(HttpStatus.OK).body(scheduleInfo);
				}catch(Exception e) {
					message.setErrorCode(randomErrorCode());
	 			message.setErrorMessage("Task not successfully created");
	 			logger.error(message.getErrorMessage() + " \n " + jsonStr);
					return ResponseEntity.badRequest().body(message);
				}
		 }catch(Exception e) {
			 message.setErrorCode(randomErrorCode());
			 message.setErrorMessage("Non existing jobDetail");
			 logger.error(message.getErrorMessage() + " \n " + jsonStr);
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
		 }
		
	}
	
	public ResponseEntity getAllJobs(){
		try {
			return ResponseEntity.status(HttpStatus.OK).body(scheduleInfoRepo.findAll());
		}catch(Exception e) {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("Something went wrong while getting the data from database");
			logger.error(message.getErrorMessage());
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
			logger.error(message.getErrorMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}
	
	
	public ResponseEntity deleteJob(String jobUUID) {
		message = new ErrorMessage();
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobUUID(jobUUID);
		
	//	Message successMessage = new Message();
		List<Status> statusList = new ArrayList<>();
		if(scheduleInfo != null) {
			JobKey jobKeyObj = new JobKey(scheduleInfo.getJobName(),scheduleInfo.getJobGroup());
			TriggerKey triggerKey = new TriggerKey(scheduleInfo.getJobName(),scheduleInfo.getJobGroup());
			try {
				quartzScheduler.unscheduleJob(triggerKey);
				Status status = statusRepo.findByStatusName("del");
                scheduleInfo.setStatus(status);
                scheduleInfoRepo.save(scheduleInfo);
//				for(Status statusMod:statusList) {
//					if(statusMod == null) {
//						statusMod.setActive(false);
//						statusMod.setModifiedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
//						statusRepo.save(statusMod);
//					}
//				}
//				quartzScheduler.deleteJob(jobKeyObj);
//				successMessage.setJobUUID(jobUUID);
//				successMessage.setJobName(scheduleInfo.getJobName());
//				successMessage.setJobGroup(scheduleInfo.getJobGroup());
//				successMessage.setMessage("Job successfully deleted and no longer available");
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(scheduleInfo);
				
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				message.setErrorCode(randomErrorCode());
				message.setErrorMessage("Something went wrong while deleting the job ");
				logger.error(message.getErrorMessage());
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		}
		else {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("Job not found for this Job UUID : "+jobUUID);
			logger.error(message.getErrorMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}
	
	public ResponseEntity getJobsUsingStatus(String jobStatus) {
		
		message = new ErrorMessage();
	//	List<ScheduleInfo> scheduleInfoList = scheduleInfoRepo.findAllByStatus(jobStatus);
		Status status = statusRepo.findByStatusName(jobStatus);
		List<ScheduleInfo> scheduleInfoList= scheduleInfoRepo.findAllByStatus(status) ;
		
		
		if(scheduleInfoList.size() > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(scheduleInfoList);
		}
		else {
			message.setErrorCode(randomErrorCode());
			message.setErrorMessage("There is no "+ jobStatus + " job");
			logger.error(message.getErrorMessage());
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		
	}
}
