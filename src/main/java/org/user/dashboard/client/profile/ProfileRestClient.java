package org.user.dashboard.client.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.Profile;

import java.util.concurrent.CompletableFuture;

@Component
public class ProfileRestClient implements ProfileClient {

    private final RestTemplate restTemplate;

    private final String profileApiUrl;

    public ProfileRestClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${user.profile-api-url}") String profileApiUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.profileApiUrl = profileApiUrl;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Profile> getProfile(String userId) throws UserNotFoundException {

        ResponseEntity<Profile> response;

        try {
            response = restTemplate.getForEntity(url(), Profile.class, userId);
            return CompletableFuture.completedFuture(response.getBody());

        } catch (RestClientException ex) {
            throw new UserNotFoundException("User profile not found");
        }

    }

    private String url() {
        return UriComponentsBuilder
                .fromUriString(profileApiUrl)
                .build()
                .toUriString();
    }
}
