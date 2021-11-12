package com.educandoweb.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.educandoweb.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);

}