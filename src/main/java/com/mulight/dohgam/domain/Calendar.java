package com.mulight.dohgam.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

@Entity
@Data
public class Calendar {

	@jakarta.persistence.Id
	@GeneratedValue
	private Long calendarid;
	private String thumbnail;
	private Long userid;
	private LocalDate readdate;
	
}
