package org.user.dashboard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.user.dashboard.client.comment.CommentClient;
import org.user.dashboard.client.profile.ProfileClient;
import org.user.dashboard.exception.UserDashboardException;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.Comment;
import org.user.dashboard.model.Profile;
import org.user.dashboard.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final ProfileClient profileClient;

    private final CommentClient commentClient;

    public UserService(ProfileClient profileClient, CommentClient commentClient) {
        this.profileClient = profileClient;
        this.commentClient = commentClient;
    }

    public User getUser(String id) throws UserDashboardException, UserNotFoundException {

        CompletableFuture<Profile> futureProfile = profileClient.getProfile(id);
        CompletableFuture<List<Comment>> futureComments = commentClient.getComments(id);

        try {
            CompletableFuture.allOf(futureComments, futureProfile).join();
            return new User(futureProfile.get(), futureComments.get());

        } catch (InterruptedException | ExecutionException e) {

            LOGGER.error("Error while retrieving user details from apis for user {} ", id, e);
            throw new UserDashboardException("Error while retrieving user details from apis for user " + id);
        }
    }
}
