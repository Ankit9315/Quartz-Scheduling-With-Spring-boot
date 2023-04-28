package com.example.QuartzScheduling.Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TimeInterval {

	public List<TimeInterval> getDaysOfWeek() {
		// TODO Auto-generated method stub
		return null;
	}
	
    public List<String> getWeeklyDaysOff() {
		return weeklyDaysOff;
	}

	public void setWeeklyDaysOff(List<String> weeklyDaysOff) {
		this.weeklyDaysOff = weeklyDaysOff;
	}

	private List<String> weeklyDaysOff;
    
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	public LocalTime getStartTime() {
		// TODO Auto-generated method stub
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public LocalTime getEndTime() {
		// TODO Auto-generated method stub
		return endTime;
	}

}
