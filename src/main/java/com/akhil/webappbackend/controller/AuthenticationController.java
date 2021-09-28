package com.akhil.webappbackend.controller;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/authorization")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Value("${spotify.client.id}")
    private String clientID;

    @Value("${spotify.redirect.uri}")
    private String redirectURI;

    @GetMapping("/access-token")
    public RedirectView getAccessToken() throws URISyntaxException, MalformedURLException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost("accounts.spotify.com");
        uriBuilder.setPath("/authorize");
        uriBuilder.addParameter("client_id", clientID);
        uriBuilder.addParameter("response_type", "code");
        uriBuilder.addParameter("redirect_uri", redirectURI);
        String url = uriBuilder.build().toURL().toString();
        logger.info("Encoded URI built :)");

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        logger.info("Redirecting to spotify authentication page...");
        return redirectView;
    }

    @PostMapping("/callback")
    public void storeAccessToken() {

    }
}
