package com.example.QuartzScheduling.Job;

public class JobData {

	private String jobName;
	private String jobGroup;
	private String jobType;
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private String jobStartTime;
	
	private String jobEndTime;
	
	public String getJobEndTime() {
		return jobEndTime;
	}
	public void setJobEndTime(String jobEndTime) {
		this.jobEndTime = jobEndTime;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getJobStartTime() {
		return jobStartTime;
	}
	public void setJobStartTime(String jobStartTime) {
		this.jobStartTime = jobStartTime;
	}
	
	@Override
	public String toString() {
		return "JobData [jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobType=" + jobType + ", jobStartTime="
				+ jobStartTime + ", cronExpression=" +  "]";
	}
	
}
