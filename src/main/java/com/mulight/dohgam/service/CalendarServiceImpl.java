package com.mulight.dohgam.service;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulight.dohgam.domain.Calendar;
import com.mulight.dohgam.repository.CalendarRepository;

@Service("calendarService")
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

	private final CalendarRepository calendarRepository;
	
	@Override
	public List<Calendar> loadMonthlyCalendar(Long userid, LocalDate firstday, LocalDate lastday) {
		return calendarRepository.findByUseridAndReaddateBetween(userid, firstday, lastday);
	}

	@Transactional
	@Override
	public Calendar createCalendar(Calendar calendar) {
		return calendarRepository.save(calendar);
	}

	@Override
	public List<Calendar> loadDate(Long userid, LocalDate readdate) {
		return calendarRepository.findByUseridAndReaddate(userid, readdate);
	}

	@Transactional
	@Override
	public void deleteCalendar(Long calendarid) {
		calendarRepository.deleteById(calendarid);
	}

	@Override
	public Calendar loadCalendar(Long calendarid) {
		return calendarRepository.findByCalendarid(calendarid);
	}

}
