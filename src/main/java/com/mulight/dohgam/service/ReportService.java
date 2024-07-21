package com.mulight.dohgam.service;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mulight.dohgam.domain.Report;

public interface ReportService {

	void createReport(Report report);

	Page<Report> readReportList(Long id, Pageable pageable);

	Page<Report> searchReport(Long id, String searchKeyword, Pageable pageable);

	Report readReport(Long reportid);

	void deleteReport(Long reportid);

	Report updateReport(Report report);

	List<Report> loadMonthlyReport(LocalDate firstDay, LocalDate lastDay, Long id);

	List<String> getUploadImageUrl(String content);

}
