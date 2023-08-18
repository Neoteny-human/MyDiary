package com.android.mydiary;

import static android.content.ContentValues.TAG;
import static com.android.mydiary.MainActivity.helper;
import static com.android.mydiary.MenuActivity.ID;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class profileActivity extends AppCompatActivity {
    ImageView profileImage;
    TextView IDText;
    TextView usernameText;
    Button changeUsernameButton;
    Button changePasswordButton;
    Button logOutButton;
    Button signOutButton;
    Preferencemanager pref;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pref = new Preferencemanager();

        profileImage = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        IDText = findViewById(R.id.IDText);
        changeUsernameButton = findViewById(R.id.changeUsernameButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logOutButton = findViewById(R.id.logOutButton);
        signOutButton = findViewById(R.id.signOutButton);

        user user = helper.getUser(ID);
        if (user.getProfile() != null) {
            Toast.makeText(this, "1." + user.getPassword(), Toast.LENGTH_SHORT).show();
            if (user.getPassword().equals("null")) {
                Toast.makeText(this, "2." + user.getPassword(), Toast.LENGTH_SHORT).show();
                Glide.with(profileImage).load(user.getProfile()).circleCrop().into(profileImage);
            } else {
                Bitmap bitmap = null;
                byte[] byteArray = Base64.getDecoder().decode(user.getProfile());
                ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
                bitmap = BitmapFactory.decodeStream(in);
                Log.d(TAG, "프로필 비었을 때 비트맵:" + bitmap);
                if (bitmap != null) {
                    Glide.with(profileImage).load(bitmap).circleCrop().into(profileImage);
                }
            }

        }
        Log.d("데이터베이스 ID", "" + user.getName());
        usernameText.setText(user.getName());
        IDText.setText(user.get_id());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getPassword().equals("null")) {
                    //인텐트에 액션을 지정해준다.
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    //intent에 타입을 지정해줘서 갤러리로 갈 수 있게.
                    intent.setType("image/*");
                    startActivityResult.launch(intent);
                }
            }
        });


        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getPassword().equals("null")) {
                    Intent i = new Intent(profileActivity.this, ChangeName.class);
                    startActivityResult2.launch(i);
                }
            }
        });


        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getPassword().equals("null")) {
                    Intent i = new Intent(profileActivity.this, ChangeInformation.class);
                    startActivityResult3.launch(i);
                }
            }
        });


        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getPassword().equals("null")) {
                    Intent i = new Intent(profileActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pref.setBoolean(getApplicationContext(), "auto", false);
                    startActivity(i);
                }
                else{
                    UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                        @Override
                        public Unit invoke(Throwable throwable) {
                            Intent i = new Intent(profileActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pref.setBoolean(getApplicationContext(), "auto", false);
                            startActivity(i);
                            return null;
                        }
                    });
                }
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);
                builder.setTitle("회원 탈퇴");
                builder.setMessage("탈퇴하시면 모든 데이터가 사라집니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        helper.DeleteUserDiary(ID);
                        helper.DeleteUser(ID);

                        if(user.getPassword().equals("null")){
                            UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
                                @Override
                                public Unit invoke(Throwable throwable) {
                                    return null;
                                }
                            });
                        }

                        Intent i = new Intent(profileActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pref.setBoolean(getApplicationContext(), "auto", false);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });


    }


    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            byte[] byteArray = out.toByteArray();
                            String profile = Base64.getEncoder().encodeToString(byteArray);
                            helper.UpdateUserProfile(profile, ID);
                            Glide.with(profileImage).load(bitmap).circleCrop().into(profileImage);
//                            profileImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> startActivityResult2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        String name = result.getData().getStringExtra("name");
                        usernameText.setText(name);

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> startActivityResult3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(), "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


}