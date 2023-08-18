package com.android.mydiary;

import static android.content.ContentValues.TAG;
import static com.android.mydiary.MainActivity.helper;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    ImageView profileImage;
    EditText IDInput;
    EditText passwordInput;
    EditText nameInput;
    Button signupButton;
    Boolean isSame = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        profileImage = findViewById(R.id.profileImage);
        IDInput = findViewById(R.id.IDInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput = findViewById(R.id.nameInput);
        signupButton = findViewById(R.id.signupButton);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String ID = IDInput.getText().toString();
                String password = passwordInput.getText().toString();
                String name = nameInput.getText().toString();

                if (!ID.isEmpty() && !password.isEmpty() && !name.isEmpty()) {

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    String profileString ="";
                    if(profileImage.getDrawable() != null) {
                        Bitmap profile = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                        profile.compress(Bitmap.CompressFormat.PNG, 100, out);
                        byte[] profileByteArray = out.toByteArray();
                        profileString = Base64.getEncoder().encodeToString(profileByteArray);
                    }


                    ArrayList<String> IDList = helper.getIDList();
                    for (int i = 0; i < IDList.size(); i++) {
                        if (IDList.get(i).equals(ID)) {
                            isSame = true;
                            break;
                        }
                    }
                    if (isSame) {
                        Toast.makeText(SignupActivity.this, "이미 있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        isSame = false;
                    } else {
                        //새로운 회원등록 후 복귀
                        helper.CreateUser(ID, password, name, profileString);
                        Toast.makeText(SignupActivity.this, "등록성공", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("id", ID);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "빈 칸을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인텐트에 액션을 지정해준다.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //intent에 타입을 지정해줘서 갤러리로 갈 수 있게.
                intent.setType("image/*");
                startActivityResult.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        Log.d("TAG", "데이터는 " + result.getData().getData());
                        try {
                            Bitmap bitmap = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), result.getData().getData()));
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getData().getData());
                            }
                            profileImage.setImageBitmap(bitmap);
//                            Glide.with(profileImage).load(bitmap).circleCrop().into(profileImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        profileImage.setBackground(null);
                    }
                }
            }
    );
}