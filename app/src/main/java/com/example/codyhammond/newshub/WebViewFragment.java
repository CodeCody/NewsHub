package com.example.codyhammond.newshub;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by codyhammond on 1/16/18.
 */

public class WebViewFragment extends Fragment implements backPressInterface{

    private WebView webView;
    private ProgressBar progressBar;
    public static final String ARTICLE_TAG="Article";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup parent, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.webview_layout,parent,false );
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);

        webView=(WebView)view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView,String url) {
                progressBar.setVisibility(View.GONE);

            }
        });
        Article article=(Article)getArguments().getSerializable("article");

        if(article!=null) {
            webView.loadUrl(article.url);
            Log.i("INFO","webview");

        }



        return view;
    }

    @Override
    public void onBackPress() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}

interface backPressInterface {
   void onBackPress();
}