package org.TouchAPI.models;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

public class SentimentRequest {

    private final String remoteAddr;
    private final String userAgent;

    public SentimentRequest() {
        this.remoteAddr = null;
        this.userAgent = null;
    }

    public SentimentRequest(String text, HttpServletRequest request) {
        this.text = text;
        this.remoteAddr = request.getRemoteAddr();
        this.userAgent = request.getHeader("User-Agent");
    }


    @NotBlank
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
