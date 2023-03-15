package hk.pipgamestudio.dailybibleverse.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import hk.pipgamestudio.dailybibleverse.service.CaptchaValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class CaptchaAspect {

    @Autowired
    private CaptchaValidator captchaValidator;

    private static final String CAPTCHA_PARAMETER_NAME = "captcha-response";

    @Around("@annotation(RequiresCaptcha)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        
        String captchaResponse = request.getParameter(CAPTCHA_PARAMETER_NAME);
        boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
        if(null == captchaResponse || !isValidCaptcha){
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return joinPoint.proceed();
    }

}
