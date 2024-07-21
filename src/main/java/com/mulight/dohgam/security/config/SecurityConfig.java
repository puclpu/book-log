package com.mulight.dohgam.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mulight.dohgam.security.provider.CustomAuthenticationProvider;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// WebSecurityConfigurerAdapter가 Deprecate 됨
	// WebSecurityConfigurerAdpater를 상속받아 
	// 메소드 오버라이드를 활용하여 구현하는 방식에서
	// -> @Bean 메소드를 생성하여 결과값을 리턴하는 방식으로 변경
	
//	@Autowired
//	private UserDetailsService userDetailsService;
	
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        		.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                		.requestMatchers(
                				new AntPathRequestMatcher("/"),
                				new AntPathRequestMatcher("home"),
                				new AntPathRequestMatcher("layouts/**"),
                				new AntPathRequestMatcher("body"),
                				new AntPathRequestMatcher("/users"),
                				new AntPathRequestMatcher("/signup/**"),
                				new AntPathRequestMatcher("user/login/**"),
                				new AntPathRequestMatcher("/login*"),
                				new AntPathRequestMatcher("/img/**"),
                				new AntPathRequestMatcher("/search/**"),
                				new AntPathRequestMatcher("search/**"),
                				new AntPathRequestMatcher("/find/**"),
                				new AntPathRequestMatcher("user/find/**")
                				
                		).permitAll()
                		.requestMatchers(
                				new AntPathRequestMatcher("user/myaccount/**"),
                				new AntPathRequestMatcher("/profile"),
                				new AntPathRequestMatcher("/profile/**"),
                				new AntPathRequestMatcher("/report/**"),
                				new AntPathRequestMatcher("/bookshelf"),
                				new AntPathRequestMatcher("/calendar"),
                				new AntPathRequestMatcher("/calendar/**"),
                				new AntPathRequestMatcher("/statistics"),
                				new AntPathRequestMatcher("/statistics/**")
                		).hasAnyAuthority("USER")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login_proc")
                        .defaultSuccessUrl("/")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .permitAll())
		;
		
		return http.build();
	}
	
	@Bean
	protected WebSecurityCustomizer webSecurityCustomizer() {
		// 정적 리소스 spring security 대상에서 제외
		return (web) ->
				web
					.ignoring()
					.requestMatchers(
							PathRequest.toStaticResources().atCommonLocations()
					);
	}
	
	@Bean
	protected PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() throws Exception{
		
		return new CustomAuthenticationProvider();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		ProviderManager authenticationManager = (ProviderManager)authenticationConfiguration.getAuthenticationManager();
		authenticationManager.getProviders().add(authenticationProvider());
		return authenticationManager;
	}
	
}
