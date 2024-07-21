package com.mulight.dohgam.service;

import java.util.List;

import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.domain.AccountDTO;
import com.mulight.dohgam.domain.AccountFindDTO;

public interface UserService {

	void createUser (Account account);

	Account findAccountByUsername(String username);

	void updateUser(Account account);

	Account findAccountByNickname(String nickname);

	void deleteUser(Account account);

	List<AccountFindDTO> findAccountFindDTOByNicknameAndEmail(String nickname, String email);

	List<Account> findAccountByAccountDTO(AccountDTO accountDTO);

	String getRandomPassword();

}
