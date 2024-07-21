package com.mulight.dohgam.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.repository.UserRepository;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// UserDetails를 구현해 사용자 객체를 생성
		Account account = userRepository.findByUsername(username);
		
		if (account == null) { // account가 null이면 예외 발생
			throw new UsernameNotFoundException("UsernameNotFoundException");
		}
		
		List<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority(account.getRole()));
		
		// null이 아니면
		AccountContext accountContext = new AccountContext(account, roles);
		System.out.println(accountContext.toString());
		
		return accountContext;
	}

}
