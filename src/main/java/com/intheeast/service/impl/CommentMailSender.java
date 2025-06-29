package com.intheeast.service.impl;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.intheeast.entity.Comment;

@Component
public class CommentMailSender {
	@Inject private MailSender mailSender;
	@Inject private SimpleMailMessage commentMailMessage;

	public MailSender getMailSender() { return mailSender; }
	
	public void setMailSender(MailSender sender) { this.mailSender = sender; }
	
	public SimpleMailMessage getCommentMailMessage() { return commentMailMessage; }
	
	public void setCommentMailMessage(SimpleMailMessage message) { this.commentMailMessage = message; }
	
	/**
	 * Sends a notification e-mail to the system administrator indicating that the comment has been posted.
	 */
	@Async
	public void sendNotificationEmail(Comment comment) {
		SimpleMailMessage message = new SimpleMailMessage(commentMailMessage);
		message.setSentDate(new Date());
		message.setText(comment.getText());
		
		message.setTo(comment.getEmail());
		message.setSubject("New Comment Notification");
		mailSender.send(message);
	}
}