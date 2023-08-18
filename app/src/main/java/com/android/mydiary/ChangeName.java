package com.android.mydiary;

import static com.android.mydiary.MainActivity.helper;
import static com.android.mydiary.MenuActivity.ID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeName extends AppCompatActivity {
    Button changeNameButton;
    EditText newNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        changeNameButton = findViewById(R.id.changeNameButton);
        newNameInput = findViewById(R.id.newNameInput);

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newNameInput.getText().toString();
                helper.UpdateUserName(ID, name);
                Intent i = new Intent();
                i.putExtra("name", name);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

    }
}