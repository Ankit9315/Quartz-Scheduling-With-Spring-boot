package com.example.QuartzScheduling.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ScheduleInfo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(unique=true)
	private String jobUUID=UUID.randomUUID().toString();

	private String jobName;
	
	private String jobGroup;
	
    private LocalDateTime created;
    
    private LocalDateTime modified;

    @OneToOne(cascade=CascadeType.ALL,targetEntity=Status.class)
	@JoinColumn(name="status_code",referencedColumnName = "statusCode")
	private Status status;
    
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

	public String getJobUUID() {
		return jobUUID;
	}

	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}

	private String jobType;
	
	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
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

	public LocalDateTime getJobStartTime() {
		return jobStartTime;
	}

	@Override
	public String toString() {
		return "ScheduleInfo [id=" + id + ", jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobType=" + jobType
				+ ", jobStartTime=" + jobStartTime + "]";
	}

	public void setJobStartTime(LocalDateTime jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm")
	private LocalDateTime jobStartTime;
	
	private LocalDateTime jobEndTime;
	
	private String calendarName;

	public String getCalendarName() {
		return calendarName;
	}
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}
	
	private String calendarType;

	public String getCalendarType() {
		return calendarType;
	}
	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public LocalDateTime getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(LocalDateTime jobEndTime) {
		this.jobEndTime = jobEndTime;
	}
}
