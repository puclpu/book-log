package com.mulight.dohgam.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	private RequestCache requestCache = new HttpSessionRequestCache();
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 기본적으로 이동할 페이지 설정
		setDefaultTargetUrl("/");
		// 사용자가 인증 과정 이전에 가고자 했던 url로 이동할 수 있도록 처리
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) { // 이전 과정에서 예외가 발생하면 saveRequest가 null일 가능성이 있다
			String targetUrl = savedRequest.getRedirectUrl();
			redirectStrategy.sendRedirect(request, response, targetUrl);
		} else { // null일 경우
			redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

	
	
}
