package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentListener, FriendFragment.AddFragmentListener{
    private MyDBHelper myDBHelper;
    private BackPressCloseHandler backPressCloseHandler;
    private FriendFragment friendFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.backPressCloseHandler = new BackPressCloseHandler(MainActivity.this);

        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment.isVisible()) {
                if(fragment instanceof MainFragment)
                    this.backPressCloseHandler.onBackPressed();
                else
                    super.onBackPressed();
            }
        }
    }

    @Override
    public void goFriend(String type, String name, String phone, String birth) {
        friendFragment = new FriendFragment(type, name, phone, birth);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, friendFragment).addToBackStack(null).commit();
    }

    @Override
    public void refreshMainFragment() {
        getSupportFragmentManager().beginTransaction().remove(friendFragment).commit();
        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment).addToBackStack(null).commit();
    }

    @Override
    public void finishAddFragment() {
        getSupportFragmentManager().beginTransaction().remove(friendFragment).commit();
        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment).addToBackStack(null).commit();
    }

    public static class MyDBHelper extends SQLiteOpenHelper {
        public MyDBHelper(Context context) {
            super(context, "FRIEND", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE friendsDB (name CHAR(20) PRIMARY KEY, phone CHAR(30), birthday CHAR(30));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // DB Reset
            db.execSQL("DROP TABLE IF EXISTS friendsDB");
            onCreate(db);
        }
    }






}