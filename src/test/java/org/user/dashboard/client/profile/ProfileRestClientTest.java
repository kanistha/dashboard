package org.user.dashboard.client.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.Address;
import org.user.dashboard.model.Company;
import org.user.dashboard.model.Geo;
import org.user.dashboard.model.Profile;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RunWith(SpringRunner.class)
@RestClientTest(ProfileClient.class)
public class ProfileRestClientTest {

    private static final String USER_ID = "userId";
    private static final String INVALID_USER_ID = "invalid-userId";
    private static final String VALID_URL = "http://jsonplaceholder.typicode.com/users/userId";
    private static final String INVALID_URL = "http://jsonplaceholder.typicode.com/users/invalid-userId";

    @Autowired
    private ProfileClient subject;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getProfile_forValidUserId() throws Exception {

        // given
        Profile expectedProfile = getProfile();

        String response = objectMapper
                .writeValueAsString(expectedProfile);

        this.server.expect(requestTo(VALID_URL))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        // when
        CompletableFuture<Profile> profileFuture = this.subject.getProfile(USER_ID);

        // then
        Profile actualProfile = profileFuture.get();

        assertThat(actualProfile).isEqualTo(expectedProfile);
    }

    @Test
    public void getProfile_forInvalidUserId() {

        // given
        this.server.expect(requestTo(INVALID_URL))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> subject.getProfile(INVALID_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User profile not found");

    }

    private Profile getProfile() {
        Address address = new Address("street", "suite", "city", "zipcode", new Geo("123", "234"));
        Company company = new Company("companyName", "catchPhrase", "bs");
        return new Profile(1, "name", "username", "email", address, "phone", "website", company);
    }

}