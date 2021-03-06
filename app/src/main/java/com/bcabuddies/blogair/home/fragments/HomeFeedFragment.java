package com.bcabuddies.blogair.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcabuddies.blogair.R;
import com.bcabuddies.blogair.adapter.homeRecyclerAdapter;
import com.bcabuddies.blogair.home.Notification;
import com.bcabuddies.blogair.model.HomeFeed;
import com.bcabuddies.blogair.retrofit.APIInterface;
import com.bcabuddies.blogair.retrofit.RetrofitManager;
import com.bcabuddies.blogair.utils.Constants;
import com.bcabuddies.blogair.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFeedFragment extends Fragment  {

    private RecyclerView recyclerView;
    private com.bcabuddies.blogair.adapter.homeRecyclerAdapter homeRecyclerAdapter;
    private static final String TAG = "HomeFeed";
    List<com.bcabuddies.blogair.model.HomeFeed.Post> finalList;
    PreferenceManager preferenceManager;
    ImageView notificationIcon;
    String token, uid, fullName;
    public  int pageCount = 1;
    public  boolean noMoreResults = false;
    boolean listEnd = false;
    View   view;


    public HomeFeedFragment() {
        //required empty constructure
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: homefeedcalled" );


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: called");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_feed, container, false);


        preferenceManager = new PreferenceManager(getActivity());
        notificationIcon =view.findViewById(R.id.homefeed_notificon);
        token = preferenceManager.getString(Constants.KEY_JWT_TOKEN);
        uid = preferenceManager.getString(Constants.KEY_UID);
        fullName = preferenceManager.getString(Constants.KEY_FUll_NAME);



        //test for values
        Log.e(TAG, "onCreate: full name:  " + fullName);
        Log.e(TAG, "onCreate: uid: " + uid);
        Log.e(TAG, "onCreate: token: " + token);

        finalList = new ArrayList<>();
        callApi(pageCount);
        // Log.e(TAG, "onResponse: finalList" + finalList);
        recyclerViewInit(view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    //Toast.makeText(MainActivity.this, "end of the list", Toast.LENGTH_SHORT).show();
                    pageCount = pageCount + 1;
                    if (!noMoreResults) {
                        callApi(pageCount);
                    } else {
                        if (!listEnd) {
                            Toast.makeText(getActivity().getApplicationContext(), "No more posts", Toast.LENGTH_SHORT).show();
                            listEnd = true;
                        }
                    }
                }
            }
        });

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), Notification.class));
            }
        });


        return view;
    }

    private void callApi(int pageNo) {

        APIInterface jsonHomeFeedApi = RetrofitManager.getRetrofit().create(APIInterface.class);

        Call<HomeFeed> listCall = jsonHomeFeedApi.getHomeFeed("bearer " + token, pageNo);

       listCall.enqueue(new Callback<HomeFeed>() {
           @Override
           public void onResponse(Call<HomeFeed> call, Response<HomeFeed> response) {
               if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: code " + response.code());
                   Log.e(TAG, "onResponse: errr:  " + response.body());
                   //Log.e(TAG, "onResponse: errr: + "+response.body() );
                   noMoreResults = true;
               }

               if  (response.message().equals("Posts_not_found")){
                   Log.e(TAG, "onResponse: Posts_not_found" );
               }
               else {
                   if (response.body().getPost()!=null){
                       List<com.bcabuddies.blogair.model.HomeFeed.Post> homeFeeds = response.body().getPost();
                       finalList.addAll(homeFeeds);

                       homeRecyclerAdapter.notifyDataSetChanged();
                       Log.e(TAG, "onResponse: final" + finalList);
                   }
                   else{
                       noMoreResults = true;
                   }
               }
           }

           @Override
           public void onFailure(Call<HomeFeed> call, Throwable t) {
               Log.e(TAG, "onFailure: "+t.getMessage() );
           }
       });



    }

    private void recyclerViewInit(View mView) {
        homeRecyclerAdapter = new homeRecyclerAdapter(getActivity(), finalList);
        recyclerView = mView.findViewById(R.id.homefrag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeRecyclerAdapter);
    }

    public static Fragment newInstance() {
        HomeFeedFragment fragment = new HomeFeedFragment();
        return fragment;
    }



}