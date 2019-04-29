package org.user.dashboard.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.user.dashboard.client.comment.CommentClient;
import org.user.dashboard.client.profile.ProfileClient;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.*;

import java.util.Collections;
import java.util.List;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String USER_ID = "userId";
    private static final String INVALID_USER_ID = "invalid-userId";
    private static final String USER_PROFILE_NOT_FOUND = "User profile not found";

    @Mock
    private ProfileClient profileClient;

    @Mock
    private CommentClient commentClient;

    @InjectMocks
    private UserService subject;

    @Test
    public void getUser_forValidUserId() throws Exception {

        // given
        Profile expectedProfile = getProfile();
        given(profileClient.getProfile(any()))
                .willReturn(completedFuture(expectedProfile));

        List<Comment> expectedComments = getComments();
        given(commentClient.getComments(any()))
                .willReturn(completedFuture(expectedComments));

        // when
        User user = subject.getUser(USER_ID);

        // then
        assertThat(user.getProfile()).isEqualTo(expectedProfile);
        assertThat(user.getComments()).isEqualTo(expectedComments);

        verify(profileClient).getProfile(USER_ID);
        verify(commentClient).getComments(USER_ID);
    }

    @Test
    public void getUser_forInvalidUserId() throws Exception {

        // given
        given(profileClient.getProfile(any()))
                .willThrow(new UserNotFoundException(USER_PROFILE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> subject.getUser(INVALID_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_PROFILE_NOT_FOUND);


        verify(profileClient).getProfile(INVALID_USER_ID);
        verifyZeroInteractions(commentClient);

    }

    private Profile getProfile() {
        Address address = new Address("street", "suite", "city", "zipcode", new Geo("123", "234"));
        Company company = new Company("companyName", "catchPhrase", "bs");
        return new Profile(1, "name", "username", "email", address, "phone", "website", company);
    }

    private List<Comment> getComments() {
        return Collections.singletonList(new Comment("id", "userId", "title", "body"));
    }
}