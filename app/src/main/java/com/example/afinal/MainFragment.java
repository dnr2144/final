package com.example.afinal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private Context mContext;
    private MainFragmentListener mainFragmentListener;
    //private ArrayList<TextView> nameList;
    private List<String> nameList;
    private ListView listView;
    private ArrayAdapter adapter;
    private String focusName;
    private String focusPhone;
    private String focusBirth;
    private MainActivity.MyDBHelper myDBHelper;

    public interface MainFragmentListener {
        void goFriend(String type, String name, String phone, String birth);
        void refreshMainFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MainFragmentListener)
            mainFragmentListener = (MainFragmentListener)context;
        else
            throw new RuntimeException((context.toString() + "must be implement MainFragmentListener."));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_friend, container, false);

        this.nameList = new ArrayList<String>();

        this.adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, nameList);
        this.listView = (ListView) view.findViewById(R.id.listView1);
        this.listView.setAdapter(adapter);
        registerForContextMenu(listView);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        myDBHelper = new MainActivity.MyDBHelper((MainActivity)mContext);
        SQLiteDatabase sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT name, phone, birthday FROM friendsDB;", null);

        String name;

        while(cursor.moveToNext()) {
            nameList.add(cursor.getString(0));
            //createTextView(name);
        }

        cursor.close();
        sqLiteDatabase.close();
        return view;
    }

    public void createTextView(String name) {
        TextView nameTV = new TextView(getActivity());

        nameTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130));
        nameTV.setTextSize(15);
        nameTV.setGravity(Gravity.CENTER);
        nameTV.setTypeface(null, Typeface.BOLD_ITALIC);
        nameTV.setText(name);

        //this.nameList.add(nameTV);

        //registerForContextMenu(this.listView);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.option, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_friend:
                mainFragmentListener.goFriend("ADD", null, null, null);
                return true;
            default:
                break;
        }
        return false;
    }



    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context, menu);

        focusName = (String) nameList.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);

        SQLiteDatabase sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT phone, birthday FROM friendsDB WHERE name = '" + focusName + "';", null);
        cursor.moveToNext();

        focusPhone = cursor.getString(0);
        focusBirth = cursor.getString(1);

        Log.i("View group", focusName + ", " + focusPhone + ", " + focusBirth);

        Log.i("ListView Index", nameList.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position));


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.details:
                mainFragmentListener.goFriend("DETAILS", this.focusName, this.focusBirth, this.focusPhone);
                Log.i("Long Click", item.toString());
                return true;
            case R.id.edit:
                mainFragmentListener.goFriend("EDIT", this.focusName, this.focusPhone, this.focusBirth);
                return true;
            case R.id.delete:
                SQLiteDatabase sqLiteDatabase = myDBHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("Delete FROM friendsDB WHERE name = '" + this.focusName + "';");
                sqLiteDatabase.close();
                this.mainFragmentListener.refreshMainFragment();
                return true;
            default:

        }

        return false;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // This Fragment can have option menus.
    }






}
