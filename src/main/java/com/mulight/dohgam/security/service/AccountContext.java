package com.mulight.dohgam.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.mulight.dohgam.domain.Account;


public class AccountContext extends User{

	private Account account;
	
	public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
		super(account.getUsername(), account.getPassword(), authorities);
		this.account = account;
	}
	
	@Override
	public String toString() {
		return "AccountContext [account=" + account + "]";
	}

	public Account getAccount() {
		return account;
	}
	
}
