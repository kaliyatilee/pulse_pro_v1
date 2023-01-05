package com.algebratech.pulse_wellness.models;

import java.util.List;

public class commentModel {
    public commentModel(String id, String comments, String user_id, String username, String comment_id, String tot_likes, String tot_comments, String userImage, String created_at, String like_status, List<commentModel> comments_arr) {
        this.id = id;
        this.comments = comments;
        this.user_id = user_id;
        this.username = username;
        this.comment_id = comment_id;
        this.tot_likes = tot_likes;
        this.tot_comments = tot_comments;
        this.userImage = userImage;
        this.comments_arr = comments_arr;
        this.created_at = created_at;
        this.like_status = like_status;
    }

    private String id;
    private String like_status;
    private String comments;
    private String user_id;
    private String username;
    private String comment_id;
    private String tot_likes;
    private String tot_comments;
    private String userImage;
    private String created_at;
    private List<commentModel> comments_arr;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getTot_likes() {
        return tot_likes;
    }

    public List<commentModel> getComments_arr() {
        return comments_arr;
    }

    public void setComments_arr(List<commentModel> comments_arr) {
        this.comments_arr = comments_arr;
    }

    public void setTot_likes(String tot_likes) {
        this.tot_likes = tot_likes;
    }

    public String getTot_comments() {
        return tot_comments;
    }

    public void setTot_comments(String tot_comments) {
        this.tot_comments = tot_comments;
    }
}
