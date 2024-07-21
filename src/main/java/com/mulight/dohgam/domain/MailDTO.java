package com.mulight.dohgam.domain;

import lombok.Data;

@Data
public class MailDTO {

	private String address;
	private String subject;
	private String text;
	
	
	public MailDTO(String address, String nickname, String newPassword) {
		this.address = address;
		this.subject = nickname + "님의 임시 비밀 번호를 발급했습니다.";
		this.text = nickname + "님의 임시 비밀 번호를 발급했습니다. 새로 발급한 임시 비밀 번호는 (" + newPassword + ") 입니다.";
	}
}
