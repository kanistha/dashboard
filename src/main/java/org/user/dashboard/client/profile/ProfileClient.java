package org.user.dashboard.client.profile;

import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.Profile;

import java.util.concurrent.CompletableFuture;

public interface ProfileClient {

    CompletableFuture<Profile> getProfile(String userId) throws UserNotFoundException;
}
