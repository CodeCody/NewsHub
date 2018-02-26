package com.example.codyhammond.newshub;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by codyhammond on 2/20/18.
 */

public class DeletionDialog extends DialogFragment{


    private RadioButton radioButton;
    private ListView sourcesListView;
    private Button deleteButton;
    private ArrayAdapter<String>arrayAdapter;
    private LinkedList<String>removal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removal=new LinkedList<>();
        setStyle(DialogFragment.STYLE_NORMAL,R.style.customTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.delete_faves_layout,viewGroup,false);
        sourcesListView=(ListView)view.findViewById(R.id.deletion_list);
        deleteButton=(Button)view.findViewById(R.id.delete_from_fave_list);

        sourcesListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        sourcesListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
         final ArrayList<String>sources=getArguments().getStringArrayList("sources");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    for (String name : removal) {

                        for(int j=0; j < arrayAdapter.getCount(); j++) {
                            if(name.equals(arrayAdapter.getItem(j))) {
                                arrayAdapter.remove(name);
                                j=0;
                            }
                        }
                       arrayAdapter.notifyDataSetChanged();
                    }
                }catch (NullPointerException NPE) {
                    Log.e("NPE",NPE.getLocalizedMessage());
                }
                finally {

                    dismiss();
                    ((MainActivity)getActivity()).notifyFaveListDataSetChanged(new LinkedList<String>(sources));
                }
            }
        });


        if(sources!=null) {
           arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.delete_fave_list_item, sources);
            sourcesListView.setAdapter(arrayAdapter);

        }
        else {
            sourcesListView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.delete_fave_list_item,new LinkedList<String>()));
        }



        sourcesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RadioButton radioButton=(RadioButton)view.findViewById(R.id.source);
                Log.i("NAME",radioButton.getText().toString()+" : "+String.valueOf(i));
                if(radioButton.isChecked()) {

                    removal.add(sources.get(i));

                }
                else {
                    radioButton.setChecked(false);
                    int result=removal.indexOf(i);

                    if(result >= 0) {
                        removal.remove(result);
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getDialog().setTitle("SELECT SOURCES TO DELETE");


    }
    public static DeletionDialog createDelectionDialog(List<String>sources) {
        DeletionDialog deletionDialog=new DeletionDialog();
        Bundle bundle=new Bundle();
        ArrayList<String>arrayList=new ArrayList<>();
        arrayList.addAll(sources);
        bundle.putStringArrayList("sources",arrayList);
        deletionDialog.setArguments(bundle);

        return deletionDialog;
    }
}
