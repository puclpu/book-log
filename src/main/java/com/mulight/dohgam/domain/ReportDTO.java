package com.mulight.dohgam.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReportDTO {

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
