package com.example.QuartzScheduling.Entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Status {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int statusCode;
	
    private String statusName;
	
	private boolean isActive;
	
	private LocalDateTime createdOn;
	
	private LocalDateTime modifiedOn;
	
//	@OneToOne(cascade=CascadeType.ALL,targetEntity=ScheduleInfo.class)
//	@JoinColumn(name="status_code",referencedColumnName = "status_code")
//	private ScheduleInfo scheduleInfo;
//	
//	public ScheduleInfo getScheduleInfo() {
//		return scheduleInfo;
//	}
//
//	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
//		this.scheduleInfo = scheduleInfo;
//	}

	

	@Override
	public String toString() {
		return "Status [statusCode=" + statusCode + ", statusName=" + statusName + ", isActive=" + isActive
				+ ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", scheduleInfo="  + "]";
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	
}
