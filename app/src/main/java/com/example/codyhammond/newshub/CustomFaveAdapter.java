package com.example.codyhammond.newshub;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by codyhammond on 2/19/18.
 */

public class CustomFaveAdapter extends ArrayAdapter<String> {

    public CustomFaveAdapter(Context context,int id,List<String> list) {
        super(context,id,list);
    }
}
