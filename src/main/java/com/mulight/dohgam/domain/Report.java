package com.mulight.dohgam.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

@Entity
@Data
public class Report {

	@jakarta.persistence.Id
	@GeneratedValue
	private Long reportid;
	private String title;
	private String author;
	private String publisher;
	private String thumbnail;
	private String isbn;
	private Long userid;
	private String genre;
	private Long page;
	private LocalDate startdate;
	private LocalDate enddate;
	private String grade;
	private String content;
	
}
