package com.mulight.dohgam.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CalendarDTO {

	private String thumbnail;
	private Long userid;
	private LocalDate readdate;
	
}
