package com.algebratech.pulse_wellness.api;

public class Api {

    //BASE URL
   // public static final String protocol = "https://";
    public static final String protocol = "http://";
//    public static final String baseurl = "pulse-wellness.tech/dev/app";
    public static final String baseurl = "pulse.eshagi.com";
//    public static final String baseurl = "jedaidevbed.in/pluse-wellness/dev/app";
//    public static final String baseurl = "3.7.53.246/pulsehealth";
    //public static final String baseurl = "trysoftpulse.com";
    //Route
    public static final String api = protocol + baseurl + "/api/";
    //Login
    public static final String login = api + "login";
    //Register
    public static final String register = api + "registeruser";
    //  public static final String register =  "https://demo1.pulse-wellness.tech/api/registeruser";
    //Create Profile
    public static final String createprofile = api + "createprofile";
    //Add Mac
    public static final String addmac = api + "addmac";
    //Get Rewards
    public static final String allrewards = api + "allrewards";

    public static final String getcoins = api + "getcoins";
    //Update Points
    public static final String updatepoints = api + "updatepoints";
    //Add FCM Tokken
    public static final String addfcmtokken = api + "addfcmtokken";
    //Get News Feed
    public static final String allfeeds = api + "allfeeds";
    //Get My News Feed
    public static final String myfeeds = api + "myfeeds";
    //  public static final String allfeeds = "https://demo1.pulse-wellness.tech/api/allfeeds";
    //Get All User
    public static final String allusers = api + "allusers";
    //Add Friend
    public static final String addfriend = api + "addfriend";
    //Friend Request
    public static final String friendreq = api + "friendreq";
    //Accept Friend Request
    public static final String friendaccept = api + "friendreqaccept";
    //Reject Friend Request
    public static final String friendreject = api + "friendreqreject";
    //Get Friend List
    public static final String myfriends = api + "friendlists";
    //Change Password
    public static final String change_password =api + "change_passi";
    //Add Profile Pic
    public static final String add_profile_pic = api + "isa_pic";
    //Add News Feed
    public static final String add_feed = api + "addfeed";
    //Daily Reads Sync
    public static final String dailysync = api + "dailysync";
    //Daily Reads Sync
    public static final String actsync = api + "actsync";
    //Profile Sync
    public static final String profsync = api + "profsync";
    //Test File Upload
    public static final String testaws = api + "testaws";
    //forgot pass via sms
    public static final String forgotpass_phone = api + "check_sms_forgot";
    //forgot pass via email
    public static final String forgotpass_email = api + "check_email_forgot";
    //otp verification
    public static final String verify_otp = api + "otp_verify";
    //News feed like comment
    public static final String add_feed_activities = api + "add_feed_activities";
    //API for get news feed details
    public static final String getfeeddetails = api + "getfeeddetails";
    //API for subscription plan
    public static final String subscribe = api + "subscribe";
    //API for get subscription plans
    public static final String getsubscriptionplans = api + "getsubscriptionplans";
    //API for user subscription
    public static final String getusersubscriptionplans = api + "getusersubscriptionplans";
    //API for get all corporation lists
    public static final String allcorporates = api + "allcorporates";
    //API for get all Disease lists
    public static final String getDisease = api + "getDisease";
    //API for generate wellness plan
    public static final String generateWellnessPlan = api + "generateWellnessPlan";
    //API for get wellness plan
    public static final String getMyWellnessPlan = api + "getMyWellnessPlan";
    //Api for add card
    public static final String addcard = api + "addcard";
    //Api for delete post
    public static final String deleteFeed = api + "deleteFeed";
    //Api for get transaction history
    public static final String transaction_history = api + "transaction_history";
    //Api for redeem product
    public static final String redeem = api + "redeem";
    //Api for unfriend
    public static final String unfriend = api + "unfriend";
    //APi for set user goal
    public static final String setgoal = api + "setgoal";
    //APi for fetch user goal
    public static final String fetchgoal = api + "fetchgoal";
    //Api for add activity data
    public static final String addActivityDetails = api + "addActivityDetails";
    //Api for get activity data
    public static final String getActivityDetailsLists = api + "getActivityDetailsLists";
    //Api for leaderboard
    public static final String getLeaderBoard = api + "getMyFriendLists";
    //APi for report screen
    public static final String getReport = api + "getDashboardReport";
    //Api for dashboard
    public static final String getDashboardData = api + "getDashboardData";
    //Api for friend detail
    public static final String friendDetails = api + "friendDetails";
    //Api for user detail
    public static final String getUserDetails = api + "getUserDetails";
    //Api for cancel subscription
    public static final String cancelSubscribe = api + "cancelSubscribe";
    //Api for daily walk report
    public static final String getDailyWalkReport  = api + "getDailyWalkReport";
}
