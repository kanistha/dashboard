package org.user.dashboard.model;

import java.util.List;

public class User {

    private Profile profile;

    private List<Comment> comments;

    public User(Profile profile, List<Comment> comments) {
        this.profile = profile;
        this.comments = comments;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
