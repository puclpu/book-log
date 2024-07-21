package com.mulight.dohgam.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountFindDTO {

	private String username;
	private LocalDate joindate;

	public AccountFindDTO(String username, LocalDate joindate) {
		this.username = username;
		this.joindate = joindate;
	}
}
