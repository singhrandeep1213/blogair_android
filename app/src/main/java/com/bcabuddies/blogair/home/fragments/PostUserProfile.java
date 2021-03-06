package com.bcabuddies.blogair.home.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcabuddies.blogair.R;
import com.bcabuddies.blogair.adapter.ProfileRecyclerAdapter;
import com.bcabuddies.blogair.model.UserProfile;
import com.bcabuddies.blogair.retrofit.APIInterface;
import com.bcabuddies.blogair.retrofit.RetrofitManager;
import com.bcabuddies.blogair.utils.Constants;
import com.bcabuddies.blogair.utils.PreferenceManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostUserProfile extends Fragment {

    Bundle bundle;
    String postUid;
    List<UserProfile.Post> finallist;
    String token;
    CircleImageView thumbImageView;
    String profileType;
    Button followBtn, unFollowBtn, requestedBtn;
    String thumbImage, followersCount, followingCount, fullName, bio;
    TextView fullNameTv, followerCountTv, followingCountTv, bioTv, userBioMore, userBioLess;
    ProfileRecyclerAdapter profileRecyclerAdapter;
    RecyclerView postRecyclerview;
    PreferenceManager preferenceManager;

    private static final String TAG = "PostUserProfile";


    public PostUserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_post_user_profile, container, false);
    bundle=this.getArguments();
    postUid = bundle.get("post_uid").toString();
    thumbImage=bundle.getString("thumb_image");
    fullName=bundle.getString("full_name");

    preferenceManager=new PreferenceManager(getActivity());
    token = preferenceManager.getString(Constants.KEY_JWT_TOKEN);
    finallist=new ArrayList<>();
    followerCountTv=view.findViewById(R.id.userprof_followercount);
    followingCountTv=view.findViewById(R.id.userprof_followingcount);
    fullNameTv=view.findViewById(R.id.userprof_toplayouttv);
    thumbImageView=view.findViewById(R.id.userprof_userimage);
    followBtn=view.findViewById(R.id.userprof_followbtn);
    unFollowBtn=view.findViewById(R.id.userprof_unfollowbtn);
    bioTv=view.findViewById(R.id.userprof_biotv);
    userBioMore=view.findViewById(R.id.userprof_biomore);
        userBioLess = view.findViewById(R.id.userprof_bioless);
        requestedBtn = view.findViewById(R.id.userprof_requestedbtn);

    fullNameTv.setText(fullName);

        try {
            Glide.with(getActivity()).load(thumbImage).into(thumbImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.e(TAG, "onCreateView: postUid: " + postUid);

        callLoadAPi();
        recyclerviewInit(view);

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fid = UUID.randomUUID().toString();
                followBtn.setEnabled(false);
                if (postUid != null && profileType != null) {
                    callFollowApi(token, fid, postUid, profileType);
                }

            }

        });

        unFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFollowBtn.setEnabled(false);
                callUnfollowApi(token, postUid);
            }
        });

        return view;
    }

    private void callFollowApi(String token, String fid, String postUid, String profileType) {
        APIInterface userProfileApi = RetrofitManager.getRetrofit().create(APIInterface.class);
        Call<ResponseBody> listCall = userProfileApi.followUser("bearer " + token, fid, postUid, profileType);

        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {

                    Toast.makeText(getActivity(), "Some error occured1", Toast.LENGTH_SHORT).show();
                    followBtn.setEnabled(true);

                } else {
                    if (profileType.equals("public")) {
                        Toast.makeText(getActivity(), "followed successfully", Toast.LENGTH_SHORT).show();
                        followBtn.setVisibility(View.GONE);
                        unFollowBtn.setVisibility(View.VISIBLE);
                        unFollowBtn.setEnabled(true);
                    } else if (profileType.equals("private")) {
                        Toast.makeText(getActivity(), "Requested", Toast.LENGTH_SHORT).show();
                        followBtn.setVisibility(View.GONE);
                        requestedBtn.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Some error occured2", Toast.LENGTH_SHORT).show();
                followBtn.setEnabled(true);
            }
        });

    }

    private void callUnfollowApi(String token, String postUid) {

        APIInterface userProfileApi = RetrofitManager.getRetrofit().create(APIInterface.class);
        Call<ResponseBody> listCall = userProfileApi.unfollowUser("bearer " + token, postUid);

        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {

                    Toast.makeText(getActivity(), "Some error occured1", Toast.LENGTH_SHORT).show();
                    unFollowBtn.setEnabled(true);

                } else {
                    Toast.makeText(getActivity(), "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                    unFollowBtn.setVisibility(View.GONE);
                    followBtn.setVisibility(View.VISIBLE);
                    followBtn.setEnabled(true);
                    callLoadAPi();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Some error occured2", Toast.LENGTH_SHORT).show();
                unFollowBtn.setEnabled(true);
            }
        });
    }

    private void recyclerviewInit(View view) {

        profileRecyclerAdapter= new ProfileRecyclerAdapter(getActivity(),finallist,fullName,thumbImage);
        postRecyclerview= view.findViewById(R.id.userprof_recyclerview);
        postRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        postRecyclerview.setAdapter(profileRecyclerAdapter);

    }


    private void callLoadAPi() {

        Log.e(TAG, "callAPi: token: "+token );
        APIInterface userProfileApi= RetrofitManager.getRetrofit().create(APIInterface.class);
        Call<UserProfile> listCall= userProfileApi.getUserProfile("bearer " + token, postUid);
        listCall.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "onResponse: error: "+response.code() );
                    Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                }
                else{
                    finallist.clear();
                    List<UserProfile.Post> posts= response.body().getPost();
                    Log.e(TAG, "onResponse: posts: " +posts);
                    finallist.addAll(posts);
                    profileRecyclerAdapter.notifyDataSetChanged();
                    followersCount = response.body().getFollowers_count();
                    followingCount=response.body().getFollowing_count();
                    followerCountTv.setText(followersCount);
                    followingCountTv.setText(followingCount);

                    //set follow and unfollow buttons
                    profileType=response.body().getType();
                    if (profileType.equals("following")){
                        unFollowBtn.setVisibility(View.VISIBLE);
                        followBtn.setVisibility(View.GONE);
                        requestedBtn.setVisibility(View.GONE);
                    }else if(profileType.equals("public")){
                        followBtn.setVisibility(View.VISIBLE);
                        unFollowBtn.setVisibility(View.GONE);
                        requestedBtn.setVisibility(View.GONE);
                    }else if(profileType.equals("private")){
                        followBtn.setVisibility(View.VISIBLE);
                        unFollowBtn.setVisibility(View.GONE);
                        requestedBtn.setVisibility(View.GONE);
                    }else if(profileType.equals("requested")){
                        requestedBtn.setVisibility(View.VISIBLE);
                        followBtn.setVisibility(View.GONE);
                        unFollowBtn.setVisibility(View.GONE);
                    }

                    //set user bio
                    bio=response.body().getBio();
                    bioTv.setText(bio);
                    setUserBio();

                }

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

            }
        });


    }

    private void setUserBio() {

        bioTv.post(new Runnable() {
            @Override
            public void run() {
                int num = bioTv.getLineCount();
                if (num > 2) {
                    Log.e(TAG, "run: inside if" );
                    userBioMore.setVisibility(View.VISIBLE);
                }
                Log.e(TAG, "onCreateView: line count:  " + num);
            }
        });
        userBioMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userBioMore.setVisibility(View.GONE);
                userBioLess.setVisibility(View.VISIBLE);
                bioTv.setMaxLines(Integer.MAX_VALUE);
            }
        });
        userBioLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userBioLess.setVisibility(View.GONE);
                userBioMore.setVisibility(View.VISIBLE);
                bioTv.setMaxLines(2);
            }
        });
    }

    public static Fragment newInstance(){
        PostUserProfile fragment=new PostUserProfile();
        return  fragment;
    }
}