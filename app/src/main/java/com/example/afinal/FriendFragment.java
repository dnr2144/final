package com.example.afinal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FriendFragment extends Fragment {

    private EditText name, phone, birth;
    private Button btnSave;
    private Context mContext;
    private String type;
    private String focusName, focusPhone, focusBirth;
    private MainActivity.MyDBHelper myDBHelper;

    public FriendFragment() { ; }

    // 1: add, 2: update, 3:delete, 4: detail
    public FriendFragment(String type, String name, String phone, String birth) {
        this.type = type;
        this.focusName = name;
        this.focusPhone = phone;
        this.focusBirth = birth;
    }

    interface AddFragmentListener {
        void finishAddFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend, container, false);

        this.name = (EditText) view.findViewById(R.id.addName);
        this.phone = (EditText) view.findViewById(R.id.addphone);
        this.birth = (EditText) view.findViewById(R.id.addBirth);
        this.btnSave = (Button) view.findViewById(R.id.btnSave);

        if(!this.type.equals("ADD")) {
            this.name.setText(this.focusName);
            this.phone.setText(this.focusPhone);
            this.birth.setText(this.focusBirth);
        }

        if(this.type.equals("DETAILS")) this.btnSave.setVisibility(View.GONE);

        Log.i("TYPE", this.type);

        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDBHelper = new MainActivity.MyDBHelper((MainActivity)mContext);
                SQLiteDatabase sqLiteDatabase = myDBHelper.getWritableDatabase();

                if(type.equals("ADD")) {
                    if(!name.getText().toString().equals("") && !phone.getText().toString().equals("") && !birth.getText().toString().equals("")) {
                        try {
                            Log.i("FriendFragment In ADD", name.getText() + ", " + phone.getText() + ", " + birth.getText());
                            sqLiteDatabase.execSQL("INSERT INTO friendsDB VALUES ('" + name.getText().toString() + "', '" + phone.getText().toString() + "', '"  + birth.getText().toString() + "');");
                            Toast.makeText(getActivity(),name.getText() + ", " + phone.getText() + ", " + birth.getText() + "가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            ((AddFragmentListener) mContext).finishAddFragment();
                        } catch (SQLiteConstraintException ex) {
                            Log.i("FriendFragment In ADD", name.getText() + ", " + phone.getText() + ", " + birth.getText());
                            Toast.makeText(getActivity(),"이름이 중복됩니다.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getActivity(),"내용을 전부 채워주세요", Toast.LENGTH_LONG).show();
                    }
                } else if(type.equals("EDIT")) {
                    try{
                        sqLiteDatabase.execSQL("UPDATE friendsDB set name = '" + name.getText() + "', phone = '" + phone.getText() + "', birthday = '" + birth.getText() + "' WHERE name = '" + focusName + "';");
                        Toast.makeText(getActivity(),name.getText() + ", " + phone.getText() + ", " + birth.getText() + "가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        ((AddFragmentListener) mContext).finishAddFragment();

                    } catch (SQLiteConstraintException ex) {
                        Log.i("FriendFragment In ADD", name.getText() + ", " + phone.getText() + ", " + birth.getText());
                        Toast.makeText(getActivity(),"이름이 중복됩니다.", Toast.LENGTH_LONG).show();
                    }
                }


                sqLiteDatabase.close();
            }
        });

        return view;
    }
}
