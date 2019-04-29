package org.user.dashboard.client.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.user.dashboard.model.Comment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentRestClient implements CommentClient {

    private final RestTemplate restTemplate;

    private final String commentApiUrl;

    public CommentRestClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${user.comment-api-url}") String commentApiUrl) {

        this.restTemplate = restTemplateBuilder.build();
        this.commentApiUrl = commentApiUrl;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<Comment>> getComments(String userId) {

        ResponseEntity<List<Comment>> response = restTemplate.exchange(url(userId),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Comment>>() {});

        List<Comment> comments =  response.getBody();
        return CompletableFuture.completedFuture(comments);
    }

    private String url(String userId) {
        return UriComponentsBuilder
                .fromUriString(commentApiUrl)
                .queryParam("userId", userId)
                .build()
                .toUriString();
    }
}
