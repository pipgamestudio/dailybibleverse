package hk.pipgamestudio.dailybibleverse.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	@Value("${spring.mail.username}")
    private String mailUsername;
	
	@Autowired
	JavaMailSender mailSender;

	public void sendEmail(List<String> toAddresses, String subject, String body) throws MessagingException {

		MimeMessage emailMessage = mailSender.createMimeMessage();

		emailMessage.setContent(body, "text/html");
		emailMessage.setFrom(mailUsername);
		
		String addresses = "";
	    if (null != toAddresses) {
	    	if (toAddresses.size() == 1) addresses =  toAddresses.get(0);
	    	else if (toAddresses.size() > 1) {
	    		for (String address : toAddresses) {
	    			addresses += address + ",";
	    		}
	    	}
	    }
	    emailMessage.setRecipients(Message.RecipientType.BCC, addresses);
		
		emailMessage.setSubject(subject);
		emailMessage.setText(body, null, "html");

		// Sending the email
		mailSender.send(emailMessage);
	}
}
