package com.mulight.dohgam.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mulight.dohgam.bucket.service.S3Uploader;
import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.domain.Calendar;
import com.mulight.dohgam.service.CalendarService;

@Controller
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;
	private final S3Uploader s3Uploader;
	
	@GetMapping("/calender")
	public String calendar() {
		return "calendar/calendar";
	}
	
	@ResponseBody
	@GetMapping("/calendar/loadMonthlyCalendar")
	public ResponseEntity<List<Calendar>> loadMonthlyCalendar(String firstDayString, String lastDayString) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();

		LocalDate firstDay = LocalDate.parse(firstDayString);
		LocalDate lastDay = LocalDate.parse(lastDayString);
		
		List<Calendar> list = calendarService.loadMonthlyCalendar(account.getId(), firstDay, lastDay);
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@PostMapping("/calendar/createCalendar")
	public ResponseEntity<List<Calendar>> createCalendar(@RequestParam(value="thumbnail", required=false) MultipartFile imgFile,@RequestParam(value="imgUrl",required=false)String imgUrl , @RequestParam(value="readdate",required=true)String readDateString) throws IllegalStateException, IOException {
		Calendar calendar = new Calendar();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		calendar.setUserid(account.getId());
		
		LocalDate readdate = LocalDate.parse(readDateString);
		calendar.setReaddate(readdate);

		if (imgFile == null && imgUrl.compareTo("") != 0) {
			calendar.setThumbnail(imgUrl);
		} else if (!imgFile.isEmpty()) {
			String dirName = "image/user/" + account.getId() +"/calendar";
			String uploadImageUrl = s3Uploader.upload(imgFile, dirName);
			calendar.setThumbnail(uploadImageUrl);
		}
		
		Calendar newCalendar = calendarService.createCalendar(calendar);
		List<Calendar> list = new ArrayList<>();
		list.add(newCalendar);
		
		return new ResponseEntity<> (list, HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping("/calendar/loadDate")
	public ResponseEntity<List<Calendar>> loadDate(String readdateString) {
		
		LocalDate readdate = LocalDate.parse(readdateString);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		List<Calendar> list = calendarService.loadDate(account.getId(), readdate);
		
		return new ResponseEntity<> (list, HttpStatus.OK);
	}
	
	@PostMapping("/calendar/deleteCalendar")
	public ResponseEntity<String> deleteCalendar (Long calendarid) {
		
		Calendar calendar = calendarService.loadCalendar(calendarid);
		if (calendar == null) {
			return ResponseEntity.ok().body("Empty Calendar");
		}
		
		s3Uploader.deleteFile(calendar.getThumbnail());
		calendarService.deleteCalendar(calendarid);
		
		return ResponseEntity.ok().body("Deleted Calendar");
	}
	
}
