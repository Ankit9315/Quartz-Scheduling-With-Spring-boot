package com.example.QuartzScheduling.Job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.example.QuartzScheduling.Entity.ScheduleInfo;
import com.example.QuartzScheduling.Repo.ScheduleInfoRepo;

@Component
public class ScheduleJob extends QuartzJobBean{
	
	@Autowired
	ScheduleInfoRepo scheduleInfoRepo;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println(context.getJobDetail().getKey().getName());
		ScheduleInfo scheduleInfo = scheduleInfoRepo.findByJobName(context.getJobDetail().getKey().getName());
		scheduleInfo.setStatus("Active");
		scheduleInfoRepo.save(scheduleInfo);
		JobDataMap mergedJobDataMap= context.getMergedJobDataMap();
		
		for(String key : mergedJobDataMap.getKeys())
		{
			System.out.println(" from scheduled job :: "+mergedJobDataMap.get(key));
		}
		context.setResult("Complete");
	}

}
