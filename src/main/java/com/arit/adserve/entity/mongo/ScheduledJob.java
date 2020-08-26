/**
 * 
 */
package com.arit.adserve.entity.mongo;

import java.util.Date;

import lombok.Data;

/**
 * 
 * @author Alex Ryjoukhine
 * @since Aug 4, 2020
 */
@Data
public class ScheduledJob {

	private String name;
	private String status;
	private Date lastExecutionTime;
	
}
