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
import android.widget.LinearLayout;
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
    private List<TextView> nameList;
    private LinearLayout mainLinearLayout;
    private List<LinearLayout> linearLayoutList;
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

        this.mainLinearLayout = (LinearLayout) view.findViewById(R.id.mainLinearLayout);
        this.linearLayoutList = new ArrayList<LinearLayout>();
        this.nameList = new ArrayList<TextView>();

        myDBHelper = new MainActivity.MyDBHelper((MainActivity)mContext);
        SQLiteDatabase sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT name, phone, birthday FROM friendsDB;", null);

        String name;

        while(cursor.moveToNext()) {
            name = cursor.getString(0);
            createTextView(name);
        }

        cursor.close();
        sqLiteDatabase.close();
        return view;
    }

    public void createTextView(String name) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        TextView nameTV = new TextView(getActivity());

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        nameTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130));

        nameTV.setTextSize(15);

        nameTV.setGravity(Gravity.CENTER);

        nameTV.setTypeface(null, Typeface.BOLD_ITALIC);

        nameTV.setText(name);

        this.nameList.add(nameTV);

        linearLayout.addView(nameTV);

        this.linearLayoutList.add(linearLayout);
        this.mainLinearLayout.addView(linearLayout);
        registerForContextMenu(linearLayout);
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

        this.focusName = ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString();

        SQLiteDatabase sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT phone, birthday FROM friendsDB WHERE name = '" + this.focusName + "';", null);
        cursor.moveToNext();

        this.focusPhone = cursor.getString(0);
        this.focusBirth = cursor.getString(1);

        Log.i("View group", focusName + ", " + focusPhone + ", " + focusBirth);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.details:
                mainFragmentListener.goFriend("DETAILS", this.focusName, this.focusBirth, this.focusPhone);
                Log.i("Long Click", item.toString());
                return true;
            case R.id.edit:
                mainFragmentListener.goFriend("EDIT", this.focusName.toString(), this.focusPhone, this.focusBirth);
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
