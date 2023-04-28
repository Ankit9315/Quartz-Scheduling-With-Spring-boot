package com.example.QuartzScheduling.LoggingManagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingClass {

	private static Logger logger = LogManager.getLogger(LoggingClass.class);
	
	public void logData() {
		logger.info("Info level log message");
		logger.debug("Debug level log message");
        logger.error("Error level log message");
        
        
//		logger.error("This is an error message",new Exception("Sample Execption"));
}
}
