package com.akhil.webappbackend.model;

import lombok.Data;

@Data
public class AccessResponse {
    private String access_token;
    private String token_type;
    private String scope;
    private int expires_in;
    private String refresh_token;
}
