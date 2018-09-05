package com.example.codyhammond.newshub;

import android.content.Context;
import android.database.DataSetObserver;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by codyhammond on 1/3/18.
 */

public class ArticleListFragment extends Fragment {

    private RecyclerView articlesViewList;
    private ArticleListAdapter articleListAdapter=null;
    private Button refreshButton;
    private ViewSwitcher viewSwitcher;
    RetroFitClient retroFitClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {


        View view = layoutInflater.inflate(R.layout.article_listing, parent, false);
        articlesViewList = (RecyclerView) view.findViewById(R.id.articleViewList);
        viewSwitcher=(ViewSwitcher)view.findViewById(R.id.switcher);
        refreshButton=(Button)view.findViewById(R.id.refresh_button);


        retroFitClient = RetroFitClient.getRetroFitClient();


        if(articleListAdapter == null) {
            articlesViewList.setLayoutManager(new LinearLayoutManager(getContext()));
            articleListAdapter = new ArticleListAdapter(getContext(), getFragmentManager());
            retroFitClient.setAdapter(articleListAdapter);
            articlesViewList.setAdapter(articleListAdapter);
        }

        setHasOptionsMenu(true);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        connect();

        return view;
    }

    private void connect() {
        if(getArguments() == null || getArguments().getString(MainActivity.SOURCE_SELECTION) == null) {
            retroFitClient.getTopStoriesByCountry("us");
        }
        else {

            retroFitClient.getTopStoriesBySource(getArguments().getString(MainActivity.SOURCE_SELECTION));
        }
    }


    public void onBottomReached(int pos) {

    }

    @Override
    public void onResume() {
        super.onResume();
      //  RetroFitClient.getRetroFitClient().reconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("onStart","called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("onPause","called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("onStop","called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy","called");
    }




    private void reconnect() {

        RetroFitClient.getRetroFitClient().reconnect();

    }

    private void searchClick(SearchView searchView) {
        searchView.setQueryHint("Search Article");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RetroFitClient.getRetroFitClient().getStoriesBySearch(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.app_bar_search:
                searchClick((SearchView) menuItem.getActionView());

                break;

            case android.R.id.home:
                ((MainActivity) getActivity()).openDrawer();
        }
        return true;
    }


    class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

        private List<Article> articlesList;

        private LinearLayout ll;
        private Context context;
        private FragmentManager fragmentManager;



        public ArticleListAdapter(Context context, FragmentManager fragmentManager) {
            this.context = context;
            this.fragmentManager = fragmentManager;
            articlesList = new LinkedList<>();
        }

        public void setItems(List<Article> articlesList) {
            this.articlesList.clear();
            this.articlesList.addAll(articlesList);


           //notifyDataSetChanged();

        }

        public void onSuccess() {

           if(viewSwitcher.getNextView().getId() == R.id.articleViewList) {
                 viewSwitcher.showNext();
             }

        }

        public void onFailure() {
            if(viewSwitcher.getNextView().getId() == R.id.listViewEmpty) {
                viewSwitcher.showNext();
            }
        }



        public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private int count = 0;
            public ImageView thumbnail;
            public TextView articleTitle;
            public TextView summary;
            public TextView source;
            public View separator;

            public ArticleViewHolder(View v) {
                super(v);

                thumbnail = (ImageView) v.findViewById(R.id.article_thumbnail);
                articleTitle = (TextView) v.findViewById(R.id.article_title);
                summary = (TextView) v.findViewById(R.id.summary);
                source=(TextView)v.findViewById(R.id.source);
                separator=v.findViewById(R.id.separator);

                v.setOnClickListener(this);


            }


            @Override
            public void onClick(View view) {
                   setBundleArgs(articlesList.get(getAdapterPosition()));
            }

            private void setBundleArgs(Article article) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                setFragmentArgs(bundle);

            }

            private void setFragmentArgs(Bundle bundle) {
                WebViewFragment webViewFragment = new WebViewFragment();
                webViewFragment.setArguments(bundle);
                initTransition(webViewFragment);
            }

            private void initTransition(Fragment fragment) {
                fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in, R.animator.slide_in, R.animator.slide_in, R.animator.slide_out).add(R.id.frameLayout, fragment, WebViewFragment.ARTICLE_TAG).addToBackStack(null).commit();
                // fragmentManager.beginTransaction().set
            }
        }


        @Override
        public void onBindViewHolder(ArticleViewHolder articleViewHolder, int i) {

            articleViewHolder.articleTitle.setText(articlesList.get(i).title);
            articleViewHolder.summary.setText(articlesList.get(i).description);
            articleViewHolder.source.setText(articlesList.get(i).source.name);
          //x  articleViewHolder.separator.setMinimumWidth(articleViewHolder.source.getWidth());
           // articleViewHolder.separator.getLayoutParams().width=articleViewHolder.source.getWidth();
            if(articlesList.get(i).urlToImage!=null && articlesList.get(i).urlToImage.length() != 0) {
                Picasso.with(context).load(articlesList.get(i).urlToImage).fit().into(articleViewHolder.thumbnail);
            }
            else {

                articleViewHolder.thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
            }
          //  Log.i(" View id:", String.valueOf(thumbnail.getId()));
            Log.i("Test ITEM ID", String.valueOf(articleViewHolder.getLayoutPosition()));


        }



        @Override
        public int getItemCount() {

            return articlesList.size();

        }


        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_list_item, viewGroup, false);
            Log.i("COUNT", String.valueOf(++i));
            return new ArticleViewHolder(view);

        }


    }

}