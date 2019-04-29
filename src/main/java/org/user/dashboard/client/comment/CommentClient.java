package org.user.dashboard.client.comment;

import org.user.dashboard.model.Comment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommentClient {
    CompletableFuture<List<Comment>> getComments(String userId);
}
