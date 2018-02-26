package com.example.codyhammond.newshub;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by codyhammond on 1/9/18.
 */

public class RetroFitClient implements Callback<GSONCenter> {

    static final String BASE_URL = "https://newsapi.org/v2/";
    private static RetroFitClient retroFitClient=null;
    private ArticleListFragment.ArticleListAdapter articleListAdapter=null;
    private final String API_KEY="54f0598d97dd472f9cb439dbfd17a9a4";
    private final String TAG="RESPONSE";
    private Retrofit retrofit;
    private List<String>publishers;
    private SQLiteDatabase database;
    private NewsAPI newsAPI;

    private RetroFitClient(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

                retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

         newsAPI = retrofit.create(NewsAPI.class);


    }


    public static RetroFitClient getRetroFitClient() {

        if(retroFitClient==null) {
            retroFitClient=new RetroFitClient();
        }

        return retroFitClient;
    }


    public void setAdapter(ArticleListFragment.ArticleListAdapter adapter) {
        articleListAdapter=adapter;
    }

    public void getPublishers(SQLiteDatabase database) {
        this.database=database;
        Call<GSONCenter>call=newsAPI.getSources(API_KEY);
        call.enqueue(this);
    }

    public void getStoriesBySearch(String searchTerm) {
        Call<GSONCenter>call =newsAPI.getStoriesBySearch(searchTerm,20,API_KEY);
        call.enqueue(this);
    }
    public void getTopStoriesByCountry(String country) {

        Call<GSONCenter> call = newsAPI.getTopStoriesALL("us",20,API_KEY);
        call.enqueue(this);
    }

    public void getTopStoriesBySource(String source) {
        Call<GSONCenter>call=newsAPI.getTopStoriesSource(source,10,API_KEY);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GSONCenter> call, Response<GSONCenter> response) {


        if(response.isSuccessful() && response.body()!=null) {

            if(articleListAdapter==null) throw new IllegalStateException("Adapter not set");

            if(response.body().articles!=null) {

                articleListAdapter.setItems(response.body().articles);

                articleListAdapter.notifyDataSetChanged();


            }
            else if(response.body().sources!=null){
                ContentValues cv=new ContentValues();

                for( Source source : response.body().sources) {
                   // publishers.add(source.name);
                    cv.put(PublisherDatabase.PUBLISHER_COL,source.name);
                    cv.put(PublisherDatabase.PUBLISHER_ALT,source.id);
                    database.insert(PublisherDatabase.PUBLISHER_TBL,null,cv);
                }
            }
            Log.i(TAG,"success");
        }

        articleListAdapter.onSuccess();
    }

    @Override
    public void onFailure(Call<GSONCenter>call,Throwable t) {

        Log.i(TAG,"failure");
        articleListAdapter.onFailure();

    }



}
