package org.user.dashboard.client.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.user.dashboard.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(CommentClient.class)
public class CommentRestClientTest {

    private static final String USER_ID = "userId";
    private static final String INVALID_USER_ID = "invalid-userId";
    private static final String VALID_URL = "http://jsonplaceholder.typicode.com/posts?userId=userId";
    private static final String INVALID_URL = "http://jsonplaceholder.typicode.com/posts?userId=invalid-userId";

    @Autowired
    private CommentClient subject;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void getComments_forValidUserId() throws Exception {

        // given
        Comment expectedComment = getComment();

        String response = objectMapper
                .writeValueAsString(Collections.singletonList(expectedComment));

        this.server.expect(requestTo(VALID_URL))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        // when
        CompletableFuture<List<Comment>> comments = this.subject.getComments(USER_ID);

        // then
        Comment actualComment = comments.get().get(0);

        assertThat(actualComment).isEqualTo(expectedComment);
    }

    @Test
    public void getComments_returnsEmptyList_forInvalidUserId() throws Exception {

        // given
        String response = objectMapper.writeValueAsString(EMPTY_LIST);

        this.server.expect(requestTo(INVALID_URL))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        // when
        CompletableFuture<List<Comment>> comments = this.subject.getComments(INVALID_USER_ID);

        // then
        assertThat(comments.get()).isEmpty();
    }

    private Comment getComment() {
        return new Comment("id", USER_ID, "some title", "some text");
    }
}