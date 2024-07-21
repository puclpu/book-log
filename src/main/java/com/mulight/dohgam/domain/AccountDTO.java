package com.mulight.dohgam.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountDTO {

	private String username;
	private String password;
	private String nickname;
	private String email;
	private LocalDate joindate;
	private String role;
	
}
