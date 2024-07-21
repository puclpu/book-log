package com.mulight.dohgam.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulight.dohgam.domain.Report;
import com.mulight.dohgam.repository.ReportRepository;

@Service("reportService")
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	
	@Transactional
	@Override
	public void createReport(Report report) {
		reportRepository.save(report);
	}

	@Override
	public Page<Report> readReportList(Long userid, Pageable pageable) {

		return reportRepository.findAllByUserid(userid, pageable);
	}

	@Override
	public Page<Report> searchReport(Long userid, String searchKeyword, Pageable pageable) {
		
		return reportRepository.findAllByUseridAndTitleContaining(userid, searchKeyword, pageable);
	}

	@Override
	public Report readReport(Long reportid) {
		
		return reportRepository.findByReportid(reportid);
	}

	@Transactional
	@Override
	public void deleteReport(Long reportid) {
		reportRepository.deleteByReportid(reportid);
	}

	@Transactional
	@Override
	public Report updateReport(Report report) {
		return reportRepository.save(report);
	}

	@Override
	public List<Report> loadMonthlyReport(LocalDate firstday, LocalDate lastday, Long userid) {
		return reportRepository.findByUseridAndStartdateBetweenOrEnddateBetween(userid, firstday, lastday);
	}

	@Override
	public List<String> getUploadImageUrl(String content) {
		List<String> urls = new ArrayList<>();
		
		String prefix = "src=\"";
		String suffix = "\"";
		int len = prefix.length();
		int idx1 = 0;
		int idx2 = 0;
		while(idx1 != -1) {
			idx1 = content.indexOf(prefix, idx2);
			if (idx1 != -1) {
				idx2 = content.indexOf(suffix, idx1 + len);
				if (idx2 != -1) {
					idx2++;
					String url = content.substring(idx1 + len, idx2-1);
					urls.add(url);
				} else {
					break;
				}
			} else {
				break;
			}
		}
		
		return urls;
	}

}