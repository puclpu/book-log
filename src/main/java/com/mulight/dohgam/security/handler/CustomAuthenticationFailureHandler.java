package com.mulight.dohgam.security.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		// 인증 검증에 실패해 예외가 발생하면 인증 필터가 FailureHandler를 호출해 후속 처리 진행 == 예외 처리
		// 예외 발생 메시지를 클라이언트에게 응답
		String errorMessage = "Invalid Username or Password";
		
		// 예외 타입에 따라 에러 메시지 설정
		if (exception instanceof BadCredentialsException) {
//			errorMessage = "Invalid Username or Password";
			errorMessage = "아이디와 비밀번호를 다시 입력해주세요";
		} 
//		else if (exception instanceof InsufficientAuthenticationException) {
//			errorMessage = "Invalid Secret";
//		}
		
		// login 화면으로 이동하되
		// error parameter를 던지도록 함
		// error message 전달
		errorMessage = URLEncoder.encode(errorMessage,"UTF-8"); // 한글 인코딩 깨짐 방지
		setDefaultFailureUrl("/login?error=true&exception="+errorMessage);
//		setDefaultFailureUrl("/login?error=true&exception="+exception.getMessage());
		
		// response
		// 부모 클래스에게 onAuthenticationFailure 핸들러로 처리를 위임
		super.onAuthenticationFailure(request, response, exception);
		// 클라이언트에게 응답되어 사용자가 인증이 실패한 원인을 알 수 있게 함
	}

	
	
}
