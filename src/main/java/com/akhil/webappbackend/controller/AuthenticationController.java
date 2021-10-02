package com.akhil.webappbackend.controller;

import com.akhil.webappbackend.model.AccessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/authorization")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spotify.client.id}")
    private String clientID;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectURI;

    @Value("${spotify.access.api}")
    private String accessApi;

    @Value("${frontend.uri}")
    private String frontEndUri;

    @GetMapping("/access-token")
    public RedirectView getAuthorizationToken() throws URISyntaxException, MalformedURLException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost("accounts.spotify.com");
        uriBuilder.setPath("/authorize");
        uriBuilder.addParameter("client_id", clientID);
        uriBuilder.addParameter("response_type", "code");
        uriBuilder.addParameter("redirect_uri", redirectURI);
        uriBuilder.addParameter ("scope", "user-top-read");
        String url = uriBuilder.build().toURL().toString();
        logger.info("Encoded URI built :)");

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        logger.info("Redirecting to spotify authentication page...");
        return redirectView;
    }

    @GetMapping("/callback")
    public RedirectView getAccessToken(@RequestParam String code) throws URISyntaxException, MalformedURLException {
        logger.info("Retrieved OAuth Token: " + code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientID);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", redirectURI);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,headers);
        ResponseEntity<AccessResponse> response = restTemplate.exchange(accessApi, HttpMethod.POST, entity, AccessResponse.class);

        String accessToken = response.getBody().getAccess_token();
        String tokenType = response.getBody().getToken_type();
        String scope = response.getBody().getScope();
        String expiration = String.valueOf(response.getBody().getExpires_in());
        String refreshToken = response.getBody().getRefresh_token();

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http");
        uriBuilder.setHost(frontEndUri);
        uriBuilder.setPath("/home");
        uriBuilder.addParameter("access_token", accessToken);
        uriBuilder.addParameter("token_type", tokenType);
        uriBuilder.addParameter("scope", scope);
        uriBuilder.addParameter("expiration", expiration);
        uriBuilder.addParameter("refresh_token", refreshToken);
        String url = uriBuilder.build().toURL().toString();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }
}
