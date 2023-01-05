package com.algebratech.pulse_wellness.models;

public class NewsFeedModel {


    private String id, category, headline, post, imageurl, postedby, partnerid, created_at, user_name, profile_pic, type, total_likes, like_status,tot_comment_count,points;
    private boolean isMyNewsFeed;
    public NewsFeedModel(String id, String category, String headline, String post,
                         String imageurl, String postedby, String partnerid, String created_at,
                         String user_name, String profile_pic, String type, String total_likes,
                         String like_status,boolean isMyNewsFeed,String tot_comment_count,String points) {

        this.id = id;
        this.category = category;
        this.headline = headline;
        this.post = post;
        this.imageurl = imageurl;
        this.postedby = postedby;
        this.partnerid = partnerid;
        this.created_at = created_at;
        this.user_name = user_name;
        this.profile_pic = profile_pic;
        this.type = type;
        this.total_likes = total_likes;
        this.like_status = like_status;
        this.isMyNewsFeed = isMyNewsFeed;
        this.tot_comment_count = tot_comment_count;
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTot_comment_count() {
        return tot_comment_count;
    }

    public void setTot_comment_count(String tot_comment_count) {
        this.tot_comment_count = tot_comment_count;
    }

    public boolean isMyNewsFeed() {
        return isMyNewsFeed;
    }

    public void setMyNewsFeed(boolean myNewsFeed) {
        isMyNewsFeed = myNewsFeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String id) {
        this.partnerid = partnerid;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getUserName() {
        return user_name;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(String total_likes) {
        this.total_likes = total_likes;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }
}
