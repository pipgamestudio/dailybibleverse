package hk.pipgamestudio.dailybibleverse.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RecaptchaResponse {
	private boolean success;
    private String challenge_ts;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
