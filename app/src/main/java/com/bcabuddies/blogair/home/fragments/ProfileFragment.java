package com.bcabuddies.blogair.home.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcabuddies.blogair.APIInterface;
import com.bcabuddies.blogair.R;
import com.bcabuddies.blogair.adapter.ProfileRecyclerAdapter;
import com.bcabuddies.blogair.model.UserPosts;
import com.bcabuddies.blogair.utils.Constants;
import com.bcabuddies.blogair.utils.PreferenceManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileFragment extends Fragment {

    TextView userBio;
    TextView userBioMore, userBioLess;
    private static final String TAG = "ProfileFragment";
    RecyclerView postRecyclerView;
    List<UserPosts> postList;
    ProfileRecyclerAdapter profileRecyclerAdapter;
    PreferenceManager preferenceManager;
    String token, uid, thumb_image;
    CircleImageView userImage;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferenceManager = new PreferenceManager(getActivity());

        token = preferenceManager.getString(Constants.KEY_JWT_TOKEN);
        uid = preferenceManager.getString(Constants.KEY_UID);
        thumb_image = preferenceManager.getString(Constants.KEY_THUMB_IMAGE);

        userBio = view.findViewById(R.id.account_biotv);
        userBioMore = view.findViewById(R.id.account_biomore);
        userBioLess = view.findViewById(R.id.account_bioless);
        userImage = view.findViewById(R.id.account_userimage);

        userBio.post(new Runnable() {
            @Override
            public void run() {
                int num = userBio.getLineCount();
                if (num > 4) {
                    userBioMore.setVisibility(View.VISIBLE);
                }
                Log.e(TAG, "onCreateView: line count:  " + num);
            }
        });

        Glide.with(getActivity()).load(thumb_image).into(userImage);

        userBioMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userBioMore.setVisibility(View.GONE);
                userBioLess.setVisibility(View.VISIBLE);
                userBio.setMaxLines(Integer.MAX_VALUE);
            }
        });
        userBioLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userBioLess.setVisibility(View.GONE);
                userBioMore.setVisibility(View.VISIBLE);
                userBio.setMaxLines(4);
            }
        });

        postList = new ArrayList<>();
        ;

        callApi();
        recyclerViewInit(view);


        return view;
    }

    private void callApi() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        APIInterface userPostsApi = retrofit.create(APIInterface.class);
        Call<List<UserPosts>> listCall = userPostsApi.getUserPosts("bearer " + token, uid);

        listCall.enqueue(new Callback<List<UserPosts>>() {
            @Override
            public void onResponse(Call<List<UserPosts>> call, Response<List<UserPosts>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: prpfile error:  " + response.code());
                } else {
                    List<UserPosts> posts = response.body();
                    postList.addAll(posts);
                    profileRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<UserPosts>> call, Throwable t) {

            }
        });


    }

    private void recyclerViewInit(View view) {

        profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity(), postList);
        postRecyclerView = view.findViewById(R.id.account_recyclerview);
        postRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        postRecyclerView.setAdapter(profileRecyclerAdapter);

    }

    public static Fragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }


}