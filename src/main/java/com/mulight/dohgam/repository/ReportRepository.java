package com.mulight.dohgam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mulight.dohgam.domain.Report;
public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findAllByUserid(Long userid);

	Page<Report> findAllByUserid(Long userid, Pageable pageable);

	Page<Report> findAllByUseridAndTitleContaining(Long userid, String searchKeyword, Pageable pageable);

	Report findByReportid(Long reportid);

	void deleteByReportid(Long reportid);

	@Query(value="select thumbnail from report r where userid = :userid and (startdate between :firstday and :lastday or enddate between :firstday and :lastday)", nativeQuery = true)
	List<String> findThumbnailByUseridAndStartdateBetweenOrEnddateBetween(@Param("userid") Long userid, @Param("firstday") LocalDate firstday, @Param("lastday") LocalDate lastday);

	@Query(value="select * from report r where userid = :userid and (startdate between :firstday and :lastday or enddate between :firstday and :lastday)", nativeQuery = true)
	List<Report> findByUseridAndStartdateBetweenOrEnddateBetween(@Param("userid") Long userid, @Param("firstday") LocalDate firstday, @Param("lastday") LocalDate lastday);

}
