package hk.pipgamestudio.dailybibleverse.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import hk.pipgamestudio.dailybibleverse.aspect.RequiresCaptcha;
import hk.pipgamestudio.dailybibleverse.repository.FirestoreEmailRepository;
import hk.pipgamestudio.dailybibleverse.service.EmailService;
import hk.pipgamestudio.dailybibleverse.service.JwtService;

@Controller
public class PipDbvController {
	
	public static final String NO_RESULT = "No result!";
	private static final String FRONT_END_MESSAGE = "message";
	
	@Value("${server.host}")
    private String serverHost;
	
	@Value("${bible.verse.website}")
    private String bibleVerseWebsite;
	
	@Value("${send.all.header.value}")
    private String sendAllHeaderValue;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	FirestoreEmailRepository emailRepository;
	
	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}
	
	@PostMapping("/next")
	@RequiresCaptcha
	public String next(HttpServletRequest request, HttpServletResponse response, Model model) {
		String result = "訊息: ";
		String email = request.getParameter("subscribe");
		if (null == email || email.length() < 5) {
			email = request.getParameter("unsubscribe");
			if (null == email || email.length() < 5) {
				result += "輸入電郵地址出錯， 請重新輸入。";
			} else {
				result += doUnsubscribe(email);
			}
		} else {
			result += doSubscribe(email);
		}
		
		model.addAttribute(FRONT_END_MESSAGE, result);
		
		return "index";
	}
	
	@GetMapping("/verify/{token}/{email}")
	public String verify(@PathVariable String email,
			@PathVariable String token, Model model) {
		String result = jwtService.verifyToken(token);
		String message = "訊息: ";
		
		boolean hasEmail = emailRepository.containsAddress(email);
		if (hasEmail) {
			model.addAttribute(FRONT_END_MESSAGE, message + "這個電郵地址已經登記了。");
		} else {
			if (null != result && result.equalsIgnoreCase(email)) {
				emailRepository.save(email);
				model.addAttribute(FRONT_END_MESSAGE, message + "登記成功! 或可用下方取消登記功能以取消登記。");
			} else {
				model.addAttribute(FRONT_END_MESSAGE, message + "登記失敗或超出有效時限! 請重新再試。");
			}
		}
		
		return "index";
	}
	
	@GetMapping("/sendAll")
	@ResponseStatus(value = HttpStatus.OK)
	public void sendAll(HttpServletRequest request, Model model) {
		// header security check
		String headerValue = request.getHeader("sendAllHeaderKey");
		if (null == headerValue || headerValue.length() < 1 || !headerValue.equals(sendAllHeaderValue)) {
			return;
		}
		
		List<String> addresses = emailRepository.findAll();
		String subject = "PIP 每日聖經金句";
		String content = "您好,<p/>今天的聖經金句是:<p/>";
		content += "\"" + getBibleVerse() + "\"";
		content += "<p><br/></p>PIP Games Studio 管理員";
		content += "<p/>* 若想取消推送， 請去 <a href=\""+ serverHost + "\"><b>這裏</b></a> 登記取消。";
		
		try {
			emailService.sendEmail(addresses, subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private String doSubscribe(String email) {
		if (emailRepository.containsAddress(email)) {
			return "這個電郵地址已經登記了。 或可用下方取消登記功能以取消登記。";
		}
		
		String res = "初步登記成功， 請檢查您的電子郵件以確認登記。";
		
		String token = jwtService.generateToken(email);
		
		String subject = "確認登記 PIP 每日聖經金句推送";
	    String content = "您好， 請按以下鏈結以確認登記 PIP 每日聖經金句推送 (有效時限一小時):<p/>"
	            + "<b><a href=\"" + serverHost + "/verify/" + token + "/" + email + "\">Verify</a></b><p><br/></p>"
	            + "謝謝，<p/>"
	            + "PIP Games Studio 管理員";
	    
		try {
			List<String> toAddresses = new ArrayList<String>();
			toAddresses.add(email);
			emailService.sendEmail(toAddresses, subject, content);
		} catch (MessagingException e) {
			return "出現錯誤， 請稍後重新再試。";
		}
		
		return res;
	}
	
	private String doUnsubscribe(String email) {
		if (!emailRepository.containsAddress(email)) {
			return "輸入的電郵地址沒有登記記錄， 請先登記。";
		}
		
		String res = "取消電子郵件登記成功。";
		
		emailRepository.deleteById(email);
		
		return res;
	}
	
	private String getBibleVerse() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("BIG5")));
		
		String html = restTemplate.getForObject(bibleVerseWebsite, String.class);
		
		if (null == html || html.length() < 1) return "No result!";
		
		return html;
	}
}
