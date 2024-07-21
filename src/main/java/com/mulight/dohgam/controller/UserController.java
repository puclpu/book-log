package com.mulight.dohgam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mulight.dohgam.domain.Account;
import com.mulight.dohgam.domain.AccountDTO;
import com.mulight.dohgam.domain.AccountFindDTO;
import com.mulight.dohgam.domain.MailDTO;
import com.mulight.dohgam.service.MailService;
import com.mulight.dohgam.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	
	@GetMapping("/body")
	public String body(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account authenticatedAccount = (Account) authentication.getPrincipal();
		Account account = userService.findAccountByUsername(authenticatedAccount.getUsername());
		
		if (authentication.getPrincipal() instanceof UserDetails) {
			model.addAttribute("nickname", account.getNickname());
		}
		
		return "layouts/body";
	}
	
	
	@GetMapping("/login")
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="exception", required=false) String exception, Model model) {
		
		// CustomAuthenticationFailureHandler에서
		// error 발생 시 url (/login?error=true&exception)을 error와 exception 파라미터를 받음
		// url에서 넘기는 param의 value를 error라고 설정
		// error는 필수값은 아니기 때문에 required는 false (일반적 로그인에서는 parameter 없음)
		
		// parameter를 Model에 담아
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
		
		return "user/login/login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login";
	}
	
	@GetMapping("/users")
	public String createUser() {
		return "user/login/register";
	}
	
	@PostMapping("/users")
	public String createUser(AccountDTO accountDTO) {
		
		ModelMapper modelMapper = new ModelMapper();
		Account account = modelMapper.map(accountDTO, Account.class);
		
		account.setPassword(passwordEncoder.encode(account.getPassword())); // 사용자가 입력한 password를 encode
		
		userService.createUser(account); // 사용자가 입력한 정보로 계정 생성 시도
		
		return "redirect:user/login/login"; // 회원가입 이후 login 화면으로 이동
	}
	
	@ResponseBody
	@PostMapping("/signup/idcheck")
	public ResponseEntity<Map<String, Integer>> idCheck(String username) {
		Map<String, Integer> response = new HashMap<>();
		
		Account account = userService.findAccountByUsername(username);
		System.out.println(account);
		if(account == null) {
			response.put("data", -1);
		} else {
			response.put("data", 1);
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/signup/nicknamecheck")
	public ResponseEntity<Map<String, Integer>> nicknameCheck(String nickname) {
		Map<String, Integer> response = new HashMap<>();
		
		Account account = userService.findAccountByNickname(nickname);
		System.out.println(account);
		if(account == null) {
			response.put("data", -1);
		} else {
			response.put("data", 1);
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/profile")
	public String profile(Model model, Authentication authentication) {
		
		authentication = SecurityContextHolder.getContext().getAuthentication();
		Account authenticatedAccount = (Account)authentication.getPrincipal();
		Account account = userService.findAccountByUsername(authenticatedAccount.getUsername());
		
		model.addAttribute("account", account);
		
		return "user/myaccount/profile";
	}
	
	@GetMapping("/profile/modify")
	public String modify(Model model, Authentication authentication) {
		
		authentication = SecurityContextHolder.getContext().getAuthentication();
		Account authenticatedAccount = (Account)authentication.getPrincipal();
		Account account = userService.findAccountByUsername(authenticatedAccount.getUsername());
		
		model.addAttribute("account", account);
		
		return "user/myaccount/modify";
	}
	
	@PostMapping("/profile/modify")
	public String updateUser(Account account, Model model) {
		// 가입된 사용자인지 판별
		Account authenticatedAccount = userService.findAccountByUsername(account.getUsername());
		
		// 사용자 정보 DB 업데이트
		if (account != null) {
			account.setId(authenticatedAccount.getId());
			account.setPassword(passwordEncoder.encode(account.getPassword()));
			userService.updateUser(account);
		}
		
		model.addAttribute("account", userService.findAccountByUsername(authenticatedAccount.getUsername()));

		return "redirect:/profile";
	}
	
	@GetMapping("/withdraw")
	public String withdraw(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		model.addAttribute("account", account);
		
		return "user/withdraw/withdraw";
	}
	
	@PostMapping("/withdraw")
	public String deleteUser(AccountDTO accountDTO, Model model) {
		
		Account account = userService.findAccountByUsername(accountDTO.getUsername());
		String exception = "";
		
		if (account != null) {
			if(passwordEncoder.matches(accountDTO.getPassword(), account.getPassword())) {

				userService.deleteUser(account);
				return "redirect:/";
				
			} else {
				exception = "비밀번호가 일치하지 않습니다.";
			}
		} else {
			exception = "존재하지 않는 사용자입니다.";
		}
		
		System.out.println(exception);
		
		return "redirect:/withdraw";
	}
	
	@GetMapping("/find/find_id")
	public String findID () {
		
		return "user/find/find_id";
	}

	@PostMapping("/find/find_id")
	public String findUsername (AccountDTO accountDTO, Model model) {
		
		List<AccountFindDTO> findDTO = userService.findAccountFindDTOByNicknameAndEmail(accountDTO.getNickname(), accountDTO.getEmail());
		
		model.addAttribute("keyword", accountDTO);
		model.addAttribute("list", findDTO);
		model.addAttribute("count", findDTO.size());
		
		return "user/find/find_id_result";
	}

	@GetMapping("/find/find_password")
	public String findPassword () {
		
		return "user/find/find_password";
	}

	@PostMapping("/find/find_password")
	public String findPasswordResult (AccountDTO accountDTO, Model model) {
		
		List<Account> list = userService.findAccountByAccountDTO(accountDTO);
		
		int count = list.size();
		model.addAttribute("count", count);
		model.addAttribute("keyword", accountDTO);
		
		if (count == 1) {
			Account account = list.get(0);
			String newPassword = userService.getRandomPassword(); // 임시 비밀번호 생성
			System.out.println(newPassword + " >> New Password!!");
			account.setPassword(passwordEncoder.encode(newPassword)); // 임시 비밀번호 암호화
			userService.updateUser(account); // 임시 비밀번호로 정보 변경
			
			MailDTO mailDTO = new MailDTO(account.getEmail(), account.getNickname(), newPassword);
			mailService.sendMail(mailDTO);
		} 
		
		return "user/find/find_password_result";
	}
}
