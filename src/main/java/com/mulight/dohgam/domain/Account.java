package com.mulight.dohgam.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

@Entity
@Data
public class Account {

	@jakarta.persistence.Id
	@GeneratedValue
	private Long Id;
	private String username;
	private String password;
	private String nickname;
	private String email;
	private LocalDate joindate;
	private String role;
	
	
}
