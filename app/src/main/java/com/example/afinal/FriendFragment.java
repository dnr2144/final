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

    EditText name, phone, birth;
    Button btnSave;
    Context mContext;
    String type;
    String focusName, focusPhone, focusBirth;

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

                if(type.equals("ADD")) {
                    if(!name.getText().toString().equals(" ") && !phone.getText().toString().equals(" ") && !birth.getText().toString().equals(" ")) {
                        try {
                            MainActivity.MyDBHelper myDBHelper = new MainActivity.MyDBHelper((MainActivity)mContext);
                            SQLiteDatabase sqLiteDatabase = myDBHelper.getWritableDatabase();
                            sqLiteDatabase.execSQL("INSERT INTO friendsDB VALUES ('" + name.getText().toString() + "', '" + phone.getText().toString() + "', '"  + birth.getText().toString() + "');");
                            sqLiteDatabase.close();
                            Toast.makeText(getActivity(),name.getText() + ", " + phone.getText() + ", " + birth.getText() + "가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            ((AddFragmentListener) mContext).finishAddFragment();
                            Log.i("과연 Type은??", type);
                        } catch (SQLiteConstraintException ex) {
                            Toast.makeText(getActivity(),"이름이 중복됩니다.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getActivity(),"내용을 전부 채워주세요", Toast.LENGTH_LONG).show();
                    }
                } else if(type.equals("EDIT")) {

                }

            }
        });

        return view;
    }
}
