package com.example.codyhammond.newshub;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button addFavButton;
    private Button deleteFaveButton;
    private ListView favePubListView;
    private ArrayAdapter<String>faveListAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RadioButton prevRadioButton;
    private RadioButton showAllSourcesButton;
    private final String savedFaves="savedFaves";
    private LinkedList<String>faveLinkedList;
    private LinearLayout.LayoutParams buttonSection;
    private LinearLayout.LayoutParams faveListSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView=(NavigationView)findViewById(R.id.navigationView);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        addFavButton=(Button)findViewById(R.id.add_faves);
        deleteFaveButton=(Button)findViewById(R.id.delete_mode);
        showAllSourcesButton=(RadioButton)findViewById(R.id.show_all_button);
        buttonSection=(LinearLayout.LayoutParams)(((LinearLayout)findViewById(R.id.buttonOptionsLayout))).getLayoutParams();
        faveListSection=(LinearLayout.LayoutParams)(((LinearLayout)findViewById(R.id.listViewLayout))).getLayoutParams();
        favePubListView=(ListView)findViewById(R.id.news_favorites_list);
        favePubListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        //database=new PublisherDatabase(getApplicationContext());



        initActionBarDrawerToggle();
        setButtonListeners();
        initNavigationListener();
        faveListViewInit();
        openSourcesFile();
        setMenuSettings();
        startFragmentTransaction();
    }


    private void setButtonListeners() {

        addFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                PublicationSearchFragment publicationSearchFragment=new PublicationSearchFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.drawerLayout,publicationSearchFragment).addToBackStack(PublicationSearchFragment.SEARCH_TAG).commit();
            }
        });

        showAllSourcesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetroFitClient.getRetroFitClient().getTopStoriesByCountry("us");
                if(prevRadioButton!=null) {
                    prevRadioButton.setChecked(false);//deletion of all favorites results in this button being null
                }

                showAllSourcesButton.setChecked(true);
                prevRadioButton=showAllSourcesButton;
                drawerLayout.closeDrawers();
            }
        });

        deleteFaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeletionDialog.createDelectionDialog(faveLinkedList).show(getFragmentManager(),null);
            }
        });

    }
    private void setMenuSettings() {

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    public void faveListViewInit() {
        initFaveListAdapter();
        setListViewAdapter();
        initListViewClick();

        faveListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(faveListAdapter.getCount()==0) {
                    deleteFaveButton.setEnabled(false);
                    deleteFaveButton.setBackgroundColor(Color.GRAY);
                    showAllSourcesButton.performClick();
                }
                else if(faveListAdapter.getCount() > 10) {

                    buttonSection.weight=2;
                    faveListSection.weight=1;
                }
                else if(faveListAdapter.getCount() < 10) {
                    buttonSection.weight=0;
                    faveListSection.weight=0;
                }
                else{
                    deleteFaveButton.setEnabled(true);
                }
            }
        });


    }
    public void initFaveListAdapter() {
        faveLinkedList=new LinkedList<>();
        faveListAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.fav_pub_list_item,faveLinkedList);
    }

    public void setListViewAdapter() {
        favePubListView.setAdapter(faveListAdapter);
    }

    public void initListViewClick() {

       // favePubList.
        favePubListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name=faveListAdapter.getItem(i);
                String id=new PublisherDatabase(getApplicationContext()).getNameID(name);
                prevRadioButton=view.findViewById(R.id.radioButton);
                RetroFitClient.getRetroFitClient().getTopStoriesBySource(id);
                if(showAllSourcesButton.isChecked()) {
                    showAllSourcesButton.setChecked(false);
                }
                drawerLayout.closeDrawers();
            }
        });

        favePubListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        favePubListView.setScrollbarFadingEnabled(false);
    }

    private void fillFaveList(Set<String>set) {

        faveLinkedList.addAll(set);
        faveListAdapter.notifyDataSetChanged();

    }

    private void openSourcesFile() {
        sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
       Set<String>set= sharedPreferences.getStringSet(savedFaves,new TreeSet<String>());
       fillFaveList(set);
    }

    private void saveSources() {

        TreeSet<String> savedSortedSet=new TreeSet<>();

        if(faveListAdapter.getCount()!=0) {
            for(int i=0; i < faveListAdapter.getCount(); i++) {
                savedSortedSet.add(faveListAdapter.getItem(i));
            }

            editor.putStringSet(savedFaves,savedSortedSet);
            editor.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSources();
    }


    private void startFragmentTransaction() {

        Fragment articleList=new ArticleListFragment();

        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.activity_main,articleList).commit();
    }

    private void initNavigationListener() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }


    public void addtoFavoritesList( String fave) {

        for(int i=0; i < faveListAdapter.getCount(); i++) {
            if(faveListAdapter.getItem(i).equals(fave)) {
                  return;
            }
        }
        faveListAdapter.add(fave);
        faveListAdapter.notifyDataSetChanged();
    }

    private void initActionBarDrawerToggle() {

        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close) {
            @Override
            public void onDrawerOpened(View view) {
                // drawerLayout.openDrawer();
                Log.i("TEST","drawer opened");
                super.onDrawerOpened(view);
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }


    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.START);
    }
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
      //  Toast.makeText(this, "ATTACH", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment=getSupportFragmentManager().findFragmentByTag(WebViewFragment.ARTICLE_TAG);
        if(fragment!=null && fragment instanceof backPressInterface) {
            ((backPressInterface) fragment).onBackPress();
            return;
        }

        fragment=getSupportFragmentManager().findFragmentByTag(PublicationSearchFragment.SEARCH_TAG);
        if(fragment!=null && fragment instanceof backPressInterface) {
            ((backPressInterface) fragment).onBackPress();
            drawerLayout.openDrawer(Gravity.START);
            return;
        }


        super.onBackPressed();
    }

    public void notifyFaveListDataSetChanged(List<String>list) {
        faveListAdapter.clear();
        faveListAdapter.addAll(list);
        faveListAdapter.notifyDataSetChanged();

    }

}
