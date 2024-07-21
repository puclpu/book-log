package com.mulight.dohgam.service;

import java.time.LocalDate;
import java.util.List;

import com.mulight.dohgam.domain.Calendar;

public interface CalendarService {

	List<Calendar> loadMonthlyCalendar(Long id, LocalDate firstDay, LocalDate lastDay);

	Calendar createCalendar(Calendar calendar);

	List<Calendar> loadDate(Long id, LocalDate readdate);

	void deleteCalendar(Long calendarid);

	Calendar loadCalendar(Long calendarid);

}
