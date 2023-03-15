package hk.pipgamestudio.dailybibleverse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import hk.pipgamestudio.dailybibleverse.entity.RecaptchaResponse;

@Service
public class CaptchaValidator {

    @Value("${recaptcha.server.url}")
    private String recaptchaUrl;
    
    @Value("${recaptcha.secret.key}")
    private String recaptchaSecret;

    public boolean validateCaptcha(String captchaResponse){
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", recaptchaSecret);
        requestMap.add("response", captchaResponse);

        RecaptchaResponse apiResponse = restTemplate.postForObject(recaptchaUrl, requestMap, RecaptchaResponse.class);
        if(apiResponse == null){
            return false;
        }

        return apiResponse.isSuccess();
    }

}
