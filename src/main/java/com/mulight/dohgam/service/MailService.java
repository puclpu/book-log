package com.mulight.dohgam.service;

import com.mulight.dohgam.domain.MailDTO;

public interface MailService {

	void sendMail(MailDTO mailDTO);
	
}
