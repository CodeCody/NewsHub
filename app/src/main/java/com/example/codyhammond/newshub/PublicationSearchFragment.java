package com.example.codyhammond.newshub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by codyhammond on 2/5/18.
 */


public class PublicationSearchFragment extends Fragment {

    private SearchView searchView;
    private ListView resultsListView;
    private CountDownTimer countDownTimer;
    private List<String>publishers;
    private PublisherDatabase publisherDatabase;
    private ArrayAdapter<String>arrayAdapter;
    public static final String SEARCH_TAG="Search";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.pub_search_layout,viewGroup,false);

        searchView=(SearchView)view.findViewById(R.id.searchView);
        resultsListView=(ListView)view.findViewById(R.id.results);
        publishers=new LinkedList<>();
        searchView.setIconified(false);
        publisherDatabase=new PublisherDatabase(getContext());
        publisherDatabase.setPubList(publishers);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,publishers);
        resultsListView.setAdapter(arrayAdapter);

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).addtoFavoritesList(arrayAdapter.getItem(i));
                InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                getActivity().getSupportFragmentManager().popBackStack();


            }
        });
       // RetroFitClient.getRetroFitClient().getPublishers(da);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                publisherDatabase.getWordMatches(s,arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return view;
    }

}
