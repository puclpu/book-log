package com.mulight.dohgam.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.domain.AccountDTO;
import com.mulight.dohgam.domain.AccountFindDTO;
import com.mulight.dohgam.repository.UserRepository;


@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	@Override
	public void createUser(Account account) {
		
		userRepository.save(account);
		
	}

	@Override
	public Account findAccountByUsername(String username) {
		Account account = userRepository.findByUsername(username);
		
		return account;
	}
	
	@Transactional
	@Override
	public void updateUser(Account account) {
		userRepository.save(account);
	}

	@Override
	public Account findAccountByNickname(String nickname) {
		Account account = userRepository.findByNickname(nickname);
		
		return account;
	}

	@Transactional
	@Override
	public void deleteUser(Account account) {
		userRepository.delete(account);
	}

	@Override
	public List<AccountFindDTO> findAccountFindDTOByNicknameAndEmail(String nickname, String email) {
		List<Account> list = userRepository.findByNicknameAndEmail(nickname, email);
		
		List<AccountFindDTO> findDTO = new ArrayList<>();
		for(Account account : list) {
			AccountFindDTO dto = new AccountFindDTO(account.getUsername(), account.getJoindate());
			findDTO.add(dto);
		}
		
		return findDTO;
	}

	@Override
	public List<Account> findAccountByAccountDTO(AccountDTO accountDTO) {
		return userRepository.findByUsernameAndNicknameAndEmail(accountDTO.getUsername(), accountDTO.getNickname(), accountDTO.getEmail());
	}

	@Override
	public String getRandomPassword() {
		
		char[] charSet = new char[] {
				'0', '1', '2', '3', '4', '5', '6' ,'7', '8', '9', '~', '!', '@',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' , 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' , 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' 
		};
		
		Random random = new Random();
		int length = random.nextInt(9) + 8; // 임시 비밀번호 길이 생성 : 8~16
		
		StringBuffer sb = new StringBuffer();
		SecureRandom sr = new SecureRandom();
		sr.setSeed(new Date().getTime());
		
		int idx = 0;
		int len = charSet.length;
		for(int i = 0; i < length; i++) {
			idx = sr.nextInt(len); // 난수 발생
			sb.append(charSet[idx]);
		}
		
		return sb.toString();
	}

}
