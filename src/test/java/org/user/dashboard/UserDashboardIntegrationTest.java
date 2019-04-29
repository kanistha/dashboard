package org.user.dashboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.user.dashboard.client.comment.CommentClient;
import org.user.dashboard.client.profile.ProfileClient;
import org.user.dashboard.controller.UserDashboardController;
import org.user.dashboard.exception.GlobalControllerExceptionAdvice;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.*;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.user.dashboard.exception.ErrorCode.USER_NOT_FOUND;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DashboardApplication.class)
@AutoConfigureMockMvc
public class UserDashboardIntegrationTest {

    private static final String URL = "/users/{id}";
    private static final String USER_ID = "1";
    private static final String USER_PROFILE_NOT_FOUND = "User profile not found";

    @MockBean
    private ProfileClient profileClient;

    @MockBean
    private CommentClient commentClient;

    @Autowired
    private UserDashboardController subject;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new GlobalControllerExceptionAdvice())
                .build();
    }

    @Test
    public void getUserDetails_withValidUserId() throws Exception {

        // given
        Profile profile = getProfile();
        Comment comment = getComments();

        given(profileClient.getProfile(USER_ID))
                .willReturn(completedFuture(profile));

        given(commentClient.getComments(USER_ID))
                .willReturn(completedFuture(singletonList(comment)));

        // when
        ResultActions result = mockMvc.perform(get(URL, USER_ID));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.name").value(profile.getName()))
                .andExpect(jsonPath("$.profile.email").value(profile.getEmail()))
                .andExpect(jsonPath("$.profile.phone").value(profile.getPhone()))
                .andExpect(jsonPath("$.profile.address.city").value(profile.getAddress().getCity()))
                .andExpect(jsonPath("$.profile.company.name").value(profile.getCompany().getName()))
                .andExpect(jsonPath("$.comments.[0].id").value(comment.getId()))
                .andExpect(jsonPath("$.comments.[0].title").value(comment.getTitle()))
                .andExpect(jsonPath("$.comments.[0].body").value(comment.getBody()));
    }

    @Test
    public void getUserDetails_withInvalidUserId_returnsNotFound() throws Exception {

        // given
        given(profileClient.getProfile(USER_ID))
                .willThrow(new UserNotFoundException(USER_PROFILE_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(get(URL, USER_ID));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(USER_PROFILE_NOT_FOUND));

        verify(profileClient).getProfile(USER_ID);
        verifyZeroInteractions(commentClient);
    }

    private Profile getProfile() {

        Address address = new Address("street", "suite", "city", "zipcode", new Geo("123", "234"));
        Company company = new Company("companyName", "catchPhrase", "bs");
        return new Profile(1, "foo", "username", "email", address, "phone", "website", company);
    }

    private Comment getComments() {

        return new Comment("id", USER_ID, "title", "body");
    }

}
