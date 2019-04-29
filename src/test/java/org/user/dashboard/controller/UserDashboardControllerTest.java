package org.user.dashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.user.dashboard.exception.ErrorCode.USER_NOT_FOUND;

import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.Address;
import org.user.dashboard.model.Comment;
import org.user.dashboard.model.Company;
import org.user.dashboard.model.Geo;
import org.user.dashboard.model.Profile;
import org.user.dashboard.model.User;
import org.user.dashboard.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDashboardController.class)
public class UserDashboardControllerTest {

    private static final String URL = "/users/{id}";
    private static final String USER_ID = "1";
    private static final String USER_PROFILE_NOT_FOUND = "User profile not found";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUserDetails_withValidUserId_returns200() throws Exception {

        // given
        Profile profile = getProfile();
        List<Comment> comments = getComments();
        User user = new User(profile, comments);

        given(userService.getUser(any())).willReturn(user);

        // when
        ResultActions result = mockMvc.perform(get("/users/{id}", USER_ID));

        // then
        Comment comment = comments.get(0);
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

        given(userService.getUser(any()))
            .willThrow(new UserNotFoundException(USER_PROFILE_NOT_FOUND));

        mockMvc.perform(get(URL, USER_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.name()))
            .andExpect(jsonPath("$.message").value(USER_PROFILE_NOT_FOUND));
    }

    private Profile getProfile() {

        Address address = new Address("street", "suite", "city", "zipcode", new Geo("123", "234"));
        Company company = new Company("companyName", "catchPhrase", "bs");
        return new Profile(1, "foo", "username", "email", address, "phone", "website", company);
    }

    private List<Comment> getComments() {

        return Collections.singletonList(new Comment("id", USER_ID, "title", "body"));
    }

}
