package com.mulight.dohgam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.mulight.dohgam.domain.MailDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service("mailService")
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

	private final JavaMailSender javaMailSender;
	
	@Override
	public void sendMail(MailDTO mailDTO) {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(mailDTO.getAddress());
			mimeMessageHelper.setSubject(mailDTO.getSubject());
			mimeMessageHelper.setText(mailDTO.getText());
			
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}

}
