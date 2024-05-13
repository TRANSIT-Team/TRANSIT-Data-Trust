package com.transit.backend.datalayers.controller.dto;

import lombok.Data;

import java.util.Date;


@Data
public class LoggerDTO {
	
	private java.util.UUID id;
	private java.util.Set Properties;
	private Date createDate;
	private Date modifyDate;
}
