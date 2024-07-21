package com.mulight.dohgam.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mulight.dohgam.bucket.service.S3Uploader;
import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.domain.Report;
import com.mulight.dohgam.domain.ReportDTO;
import com.mulight.dohgam.service.ReportService;

@Controller
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	private final S3Uploader s3Uploader;
	
	@GetMapping("/report/swrite")
	public String searchWrite(ReportDTO reportDTO, Model model) {

		model.addAttribute("book", reportDTO);
		
		return "report/swrite";
	}

	@GetMapping("/report/nwrite")
	public String newWrite() {
		
		return "report/nwrite";
	}
	
	@ResponseBody
	@PostMapping("/report/imgupload")
	public ResponseEntity<List<String>> imgUpload(MultipartFile[] uploadFile) throws IOException {
		List<String> list = new ArrayList<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		for(MultipartFile multipartFile : uploadFile) {
			String dirName = "image/user/" + account.getId() +"/report";
			String uploadImageUrl = s3Uploader.upload(multipartFile, dirName);
			
			list.add(uploadImageUrl);
		}
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/report/imgdelete")
	public ResponseEntity<Void> imgDelete(String imgUrl) {
		
		System.out.println(imgUrl);
		s3Uploader.deleteFile(imgUrl);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PostMapping("/report/write")
	public String createReport(ReportDTO reportDTO) {
		
		ModelMapper modelMapper = new ModelMapper();
		Report report = modelMapper.map(reportDTO, Report.class);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		report.setUserid(account.getId());
		
		reportService.createReport(report);
		
		return "redirect:/report/read/"+report.getReportid();
	}
	
	@GetMapping("/bookshelf/{pageNo}")
	public String readReportList(Model model, @PathVariable("pageNo")int pageNo,
			String searchKeyword) {
		
		Pageable pageable = PageRequest.of(pageNo-1, 20, Sort.by("enddate").descending());
		
		Page<Report> list = null;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		if (searchKeyword == null) {
			list = reportService.readReportList(account.getId(), pageable);
		} else {
			list = reportService.searchReport(account.getId(), searchKeyword, pageable);
		}
		
		System.out.println(list.getTotalPages());
		
		int nowPage = list.getPageable().getPageNumber() + 1;
		int startPage = Math.max(nowPage-2, 1);
//		int endPage = Math.min(nowPage+2, list.getTotalPages());
		int endPage = 0;
		
		if (nowPage < 3) {
			endPage = Math.min(5, list.getTotalPages());
		} else {
			endPage = Math.min(nowPage+2, list.getTotalPages());
		}
		
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		model.addAttribute("list", list);
		
		return "report/bookshelf";
	}
//	@GetMapping("/bookshelf")
//	public String readReportList(Model model, @PageableDefault(page=0, size=20, sort="reportid", direction=Sort.Direction.DESC)Pageable pageable,
//			String searchKeyword) {
//		
//		Page<Report> list = null;
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Account account = (Account) authentication.getPrincipal();
//		
//		if (searchKeyword == null) {
//			list = reportService.readReportList(account.getId(), pageable);
//		} else {
//			list = reportService.searchReport(account.getId(), searchKeyword, pageable);
//		}
//		
//		int nowPage = list.getPageable().getPageNumber() + 1;
//		int startPage = Math.max(nowPage-2, 1);
//		int endPage = Math.min(nowPage+2, list.getTotalPages());
//		
//		model.addAttribute("nowPage", nowPage);
//		model.addAttribute("startPage", startPage);
//		model.addAttribute("endPage", endPage);
//		
//		model.addAttribute("list", list);
//		
//		return "report/bookshelf";
//	}
	
	@GetMapping("/report/read/{reportid}")
	public String readReport(Model model, @PathVariable("reportid") Long reportid) {
		
		Report report = reportService.readReport(reportid);
		
		model.addAttribute("report", report);
		
		return "report/read";
	}
	
	@GetMapping("/report/modify/{reportid}")
	public String updateReport(Model model, @PathVariable("reportid") Long reportid) {
		
		Report report = reportService.readReport(reportid);
		
		model.addAttribute("report", report);
		
		return "report/modify";
	}
	
	@PostMapping("/report/modify")
	public String updateReport(Report report) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		report.setUserid(account.getId());
		
		Report newReport = reportService.updateReport(report);
		
		return "redirect:/report/read/"+newReport.getReportid();
	}
	
	@GetMapping("/report/delete/{reportid}")
	public String deleteReport(@PathVariable("reportid") Long reportid) {
		
		Report report = reportService.readReport(reportid);
		// content 내의 uplaodImageUrl 추출
		List<String> uploadImageUrls = reportService.getUploadImageUrl(report.getContent());
		for(String uploadImageUrl : uploadImageUrls) {
			System.out.println("uploadImageUrl >> " + uploadImageUrl);
			s3Uploader.deleteFile(uploadImageUrl);
		}
		
		reportService.deleteReport(reportid);
		
		return "redirect:/bookshelf";
	}
	
	@ResponseBody
	@GetMapping("/report/loadMonthlyReport")
	public ResponseEntity<List<Report>> loadMonthlyReport(String firstDayString, String lastDayString) {
	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		LocalDate firstDay = LocalDate.parse(firstDayString);
		LocalDate lastDay = LocalDate.parse(lastDayString);
		
		System.out.println(firstDay +" "+lastDay);
		
		List<Report> list = reportService.loadMonthlyReport(firstDay, lastDay, account.getId());
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping("/statistics")
	public String statistics () {
		
		return "report/statistics";
	}
	
}
