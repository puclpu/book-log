package com.mulight.dohgam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mulight.dohgam.domain.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

	List<Calendar> findByUseridAndReaddateBetween(Long userid, LocalDate firstday, LocalDate lastday);

	List<Calendar> findByUseridAndReaddate(Long userid, LocalDate readdate);

	Calendar findByCalendarid(Long calendarid);

}
