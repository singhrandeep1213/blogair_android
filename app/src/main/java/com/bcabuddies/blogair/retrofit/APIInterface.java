package com.bcabuddies.blogair.retrofit;

import com.bcabuddies.blogair.model.BlockedUsers;
import com.bcabuddies.blogair.model.Bookmarks;
import com.bcabuddies.blogair.model.Comments;
import com.bcabuddies.blogair.model.FollowRequestUsers;
import com.bcabuddies.blogair.model.HomeFeed;
import com.bcabuddies.blogair.model.LoginToken;
import com.bcabuddies.blogair.model.SearchUser;
import com.bcabuddies.blogair.model.SinglePostData;
import com.bcabuddies.blogair.model.ThumbImageResponse;
import com.bcabuddies.blogair.model.UserProfile;
import com.bcabuddies.blogair.utils.Constants;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIInterface {

    //Register a user
    @Headers(Constants.KEY_HEADER)
    @POST("/user/register")
    @FormUrlEncoded
    Call<LoginToken> registerUser(@Field("full_name") String fullName, @Field("email_id") String emailId, @Field("password") String password, @Field("uid") String uid );

    //Get home feed of logged in user
    @Headers(Constants.KEY_HEADER)
    @GET("/user/homeFeed/{page_no}")
    Call<HomeFeed> getHomeFeed(@Header("authorization") String token, @Path("page_no") int page_no);

    //get jwt token with email_id and password
    @Headers(Constants.KEY_HEADER)
    @POST("/user/login")
    @FormUrlEncoded
    Call<LoginToken> getToken(@Field("email_id") String email_id, @Field("password") String password);

    //get all posts of the current logged in user (user profile)
    @Headers(Constants.KEY_HEADER)
    @GET("/user/current/profile/{uid}")
    Call<UserProfile> getUserPosts(@Header("authorization") String token, @Path("uid") String uid);

    //add a new post
    @Headers(Constants.KEY_HEADER)
    @Multipart
    @POST("/user/post/addnew")
    Call<ResponseBody> addNewPost(@Header("authorization") String token, @Part("pid") RequestBody pid , @Part("post_desc") RequestBody postDesc, @Part MultipartBody.Part file, @Part("post_heading") RequestBody postHeading, @Part("time_stamp") String timeStamp);

    //update user name and bio
    @Headers(Constants.KEY_HEADER)
    @POST("/user/update/nameandbio")
    @FormUrlEncoded
    Call<ResponseBody> updateNameAndBio(@Header("authorization") String token ,@Field("full_name") String full_name, @Field("bio") String bio);

    //update user profile image
    @Headers(Constants.KEY_HEADER)
    @Multipart
    @POST("/user/update/thumbimage")
    Call<ThumbImageResponse> updateThumbImage(@Header("authorization") String token, @Part MultipartBody.Part file);

    //change user password from app
    @Headers(Constants.KEY_HEADER)
    @FormUrlEncoded
    @POST("/user/update/password")
    Call<ResponseBody> changePassword(@Header("authorization") String token, @Field("old_password") String oldPassword, @Field("new_password") String newPassword);

    //get blocked users
    @Headers(Constants.KEY_HEADER)
    @GET("/user/blocked/users")
    Call<BlockedUsers> getBlockedUsers(@Header("authorization") String token );

    //get user profile (not current)
    @Headers(Constants.KEY_HEADER)
    @GET(("/user/post/profile/{puid}"))
    Call<UserProfile> getUserProfile (@Header("authorization") String token, @Path("puid") String puid );

    //like a post
    @Headers(Constants.KEY_HEADER)
    @POST("/user/like/post")
    @FormUrlEncoded
    Call<ResponseBody> likePost(@Header("authorization") String token, @Field("lid") String lid , @Field("pid") String pid);

    //unlike a post
    @Headers(Constants.KEY_HEADER)
    @POST("/user/post/unlike")
    @FormUrlEncoded
    Call<ResponseBody> unlikePost(@Header("authorization") String token,  @Field("pid") String pid);

    //bookmark a post
    @Headers(Constants.KEY_HEADER)
    @POST("/user/post/bookmark")
    @FormUrlEncoded
    Call<ResponseBody> bookmarkPost(@Header("authorization") String token, @Field("bid") String bid , @Field("pid") String pid);

    //remove bookmark
    @Headers(Constants.KEY_HEADER)
    @POST("/user/post/unbookmark")
    @FormUrlEncoded
    Call<ResponseBody> unBookmarkPost(@Header("authorization") String token,  @Field("pid") String pid);

    //get comments of a post
    @Headers(Constants.KEY_HEADER)
    @GET(("/user/post/comments/{pid}"))
    Call<Comments> getPostComments (@Header("authorization") String token, @Path("pid") String pid );

    //add a comment for a post
    @Headers(Constants.KEY_HEADER)
    @POST("/user/post/addcomment/")
    @FormUrlEncoded
    Call<ResponseBody> addComment(@Header("authorization") String token, @Field("cid") String cid , @Field("pid") String pid, @Field("comment_description") String commentDesc);

    //remove a comment
    @Headers(Constants.KEY_HEADER)
    @POST("/user/post/comments/remove")
    @FormUrlEncoded
    Call<ResponseBody> removeComment(@Header("authorization") String token, @Field("cid") String cid);

    //remove a post
    @Headers(Constants.KEY_HEADER)
    @GET(("/user/post/removepost/{pid}"))
    Call<ResponseBody> removePost(@Header("authorization") String token, @Path("pid") String pid );

    //user bookmarked posts
    @Headers(Constants.KEY_HEADER)
    @GET("/user/get/bookmarks/")
    Call<Bookmarks> getBookmarkedPosts(@Header("authorization") String token );

    //get single post data (i.e. heading and description only)
    @Headers(Constants.KEY_HEADER)
    @GET(("/user/post/singlepostdata/{pid}"))
    Call<SinglePostData> getSinglePostData(@Header("authorization") String token, @Path("pid") String pid);

    //Search users
    @Headers(Constants.KEY_HEADER)
    @GET("/user/search/{name}")
    Call<SearchUser> searchUser(@Header("authorization") String token, @Path("name") String name);

    //unfollow user
    @Headers(Constants.KEY_HEADER)
    @POST("/user/unfollow")
    @FormUrlEncoded
    Call<ResponseBody> unfollowUser(@Header(("authorization")) String token, @Field("unfollow_uid") String unfollowUid);

    //follow users
    @Headers(Constants.KEY_HEADER)
    @POST("/user/follow")
    @FormUrlEncoded
    Call<ResponseBody> followUser(@Header(("authorization")) String token,@Field("fid") String fid, @Field("follow_uid") String followUid, @Field("profile_type") String profileType);

    //Get follow requests users list
    @Headers(Constants.KEY_HEADER)
    @GET("/user/follow/requests")
    Call<FollowRequestUsers> followRequests(@Header("authorization") String token);

    //follow request response
    @Headers(Constants.KEY_HEADER)
    @POST("/user/follow/response")
    @FormUrlEncoded
    Call<ResponseBody> followResponse(@Header(("authorization")) String token,@Field("response_type") String responseType, @Field("follower_uid") String followerUid ,@Field("fid") String fid );


/*
    //test jwt auth token
    @Headers("key: Pz6WbvhZAQGsUtAxRJK3vtXCrJDW6kb3yMwtnGKu2kpfT9RahulGaurqFWfvFptqftcF87mBbV7pJWmPCPR5fZentc3qQVTtGLbqbjvGquT5B8UT2Kvjk7BCUm7hqtkqmJ3yR6fMFdWkWwvRGjrtSZjs52TdKC5Xazvp6b22pKNQSybvNb4mAwwuzXQFLKM7Pq5htpNNg8ZJ9dZJUF8gqc3aFXywYvaFLMXWdNUfErL8GEgUR3sEpNajEXbUcL22")
    @GET("/user/login/test")
    Call<LoginUser> getUser(@Header("authorization" ) String token);
*/


}
