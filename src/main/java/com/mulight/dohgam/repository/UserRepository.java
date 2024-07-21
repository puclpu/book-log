package com.mulight.dohgam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mulight.dohgam.domain.Account;

public interface UserRepository extends JpaRepository<Account, Long> {

	Account findByUsername(String username);

	Account findByNickname(String nickname);

	List<Account> findByNicknameAndEmail(String nickname, String email);

	List<Account> findByUsernameAndNicknameAndEmail(String username, String nickname, String email);
	
}
