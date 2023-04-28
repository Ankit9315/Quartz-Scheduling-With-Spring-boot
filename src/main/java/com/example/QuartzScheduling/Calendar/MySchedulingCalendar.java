package com.example.QuartzScheduling.Calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.impl.calendar.HolidayCalendar;

public class MySchedulingCalendar{
	
	static HolidayCalendar holiday = new HolidayCalendar();
	public HolidayCalendar alternativeDays( LocalDateTime endTime,LocalDate date) {
		if(!date.plusDays(1).isBefore(endTime.toLocalDate())) {
			return holiday;
		}
		else {
			date = date.plusDays(1);
			System.out.println(date);
			holiday.addExcludedDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			System.out.println(holiday.getExcludedDates());
			return alternativeDays(endTime,date.plusDays(1));
		}
	}
	
	public HolidayCalendar WeeklyOff(LocalDateTime startTime ,LocalDateTime endTime ,LocalDate date) {
		if(!date.plusDays(1).isAfter(startTime.toLocalDate()) && !date.plusDays(1).isBefore(endTime.toLocalDate())) {
			return holiday;
		}
		else {
			date = date.plusDays(1);
			System.out.println(date);
			holiday.addExcludedDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			return WeeklyOff(startTime,endTime,date.plusDays(1));
		}
		
	}
	
//	public HolidayCalendar holidays(LocalDateTime startTime ,LocalDateTime endTime ,LocalDate date) {
//		HolidayCalendar holiday = new HolidayCalendar();
//		if(!date.plusDays(1).isAfter(startTime.toLocalDate()) && !date.plusDays(1).isBefore(endTime.toLocalDate())) {
//			return holiday;
//		}
//		else {
//			date = date.plusDays(1);
//			System.out.println(date);
//			holiday.addExcludedDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//			return WeeklyOff(startTime,endTime,date.plusDays(1));
//		}
//	}
	
}
