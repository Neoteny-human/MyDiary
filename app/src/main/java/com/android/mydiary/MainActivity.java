package com.android.mydiary;

import static android.content.ContentValues.TAG;

import static com.android.mydiary.ThemeUtil.DARK_MODE;
import static com.android.mydiary.ThemeUtil.LIGHT_MODE;
import static com.android.mydiary.ThemeUtil.applyTheme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Predicate;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;


public class MainActivity extends AppCompatActivity {
    static SQLiteDatabase db;
    static DBHelper helper;

    Preferencemanager pref = new Preferencemanager();

    boolean isExists = false;

    //레이아웃 속성들 선언.
    EditText usernameInput;
    EditText passwordInput;
    CheckBox autoCheckBox;
    Switch darkModeSwitch;

    Boolean isSame = false;

    private void createDatabase(){
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();


        KakaoSdk.init(this, "fddb0f34d9ffdaf45e30b2f63541dda2");


        getAppKeyHash();
        Button kakaoLoginButton = findViewById(R.id.kakaoLoginButton);


        // 카카오가 설치되어 있는지 확인 하는 메서드또한 카카오에서 제공 콜백 객체를 이용함
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            //invoke라는 메서드가 콜백으로 호출됨.
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 null이면 로그인 실패
                if (oAuthToken != null) {
                    Log.d(TAG, "invoke: "+oAuthToken);
                }
                //만약 오류가 있다면 throwable이 null이 아닐 것이므로 오류값으로 적절히 처리해야 함.
                if (throwable != null) {

                }
                //로그인 정보를 받아서 db에 저장하고, 인텐트로 넘기는 작업.
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        // 로그인이 되어있으면
                        if (user != null) {
                            // 유저의 아이디
                            Log.d(TAG, "invoke: id" + user.getId());

                            String profile = user.getKakaoAccount().getProfile().getProfileImageUrl();
                            String id = user.getKakaoAccount().getEmail();
                            String name = user.getKakaoAccount().getProfile().getNickname();

                            ArrayList<String> IDList = helper.getIDList();
                            for (int i = 0; i < IDList.size(); i++) {
                                if (IDList.get(i).equals(id)) {
                                    isSame = true;
                                    break;
                                }
                            }
                            if(!isSame) {
                                helper.CreateUser(id, null, name, profile);
                            }
                            else{
                                isSame = false;
                            }

                            Intent i = new Intent(MainActivity.this, MenuActivity.class);
                            i.putExtra("id", id);
                            startActivity(i);

                        } else {
                            // 로그인이 되어 있지 않다면
                            Toast.makeText(MainActivity.this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                });

                return null;
            }
        };

        // 로그인 버튼
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
                }
            }
        });






        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Boolean auto = pref.getBoolean(getApplicationContext(), "auto");
                    applyTheme(DARK_MODE);
                    pref.modSave(getApplicationContext(), DARK_MODE);
                    pref.setBoolean(getApplicationContext(), "auto", auto);
                }
                else{
                    Boolean auto = pref.getBoolean(getApplicationContext(), "auto");
                    applyTheme(LIGHT_MODE);
                    pref.modSave(getApplicationContext(), LIGHT_MODE);
                    pref.setBoolean(getApplicationContext(), "auto", auto);
                }
            }
        });




        String savedID = "";
        String savedPassword = "";
        String saved = pref.getString(getApplicationContext(), "user");


       if(pref.getBoolean(getApplicationContext(), "auto")) {

            try {
                JSONObject json = new JSONObject(saved);
                savedID = (String) json.getString("ID");
                savedPassword = (String) json.getString("password");
                if(savedPassword.equals(helper.getPassword(savedID))&&!savedPassword.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    //인텐트에 유저의 고유 아이디를 넣음.
                    intent.putExtra("id", savedID);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //뷰 컴포넌트 지정.
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        autoCheckBox = findViewById(R.id.autoCheckBox);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);




        //다크모드는 화면을 다시 그려내므로 자동로그인 안되게.
//        pref.setBoolean(getApplicationContext(), "auto", false);
        //앱을 켜자마자 다크모드 여부 불러와서 적용.
        Boolean auto = pref.getBoolean(getApplicationContext(), "auto");
        String mod = pref.modLoad(getApplicationContext());
        applyTheme(mod);
        pref.setBoolean(getApplicationContext(), "auto", auto);

        //앱 시작 시 기본설정이 다크모드일 경우 토글이 오른쪽에서 시작하도록.
        if(mod.equals(DARK_MODE)){
            darkModeSwitch.toggle();
        }



//        //Drawable, BitmapDrawable, Bitmap 실험;
//        Drawable D = getDrawable(R.drawable.picture);
//        Log.d(TAG, "드로우블 주소: "+D);
//        BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.drawable.picture);
//        Log.d(TAG, "비트맵 드로우블: "+drawable);
//        Bitmap bm = drawable.getBitmap();
//        Log.d(TAG, "비트맵: "+bm);



        //로그인 클릭
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ArrayList<String> IDList = helper.getIDList();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                Boolean auto = autoCheckBox.isChecked();

                for(int i = 0; i < IDList.size(); i++){
                    if(username.equals(IDList.get(i))){
                        isExists = true;
                        break;
                    }
                }
                if(isExists){
                    if(!password.equals(helper.getPassword(username))){
                        Toast.makeText(MainActivity.this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        isExists = false;
                    }
                    else{
                        //자동로그인 체크여부 확인하고 값 설정.


                        //JSONObject에 넣어서 로그인 성공 시 sharedpreferences에 저장.
                        JSONObject json = new JSONObject();
                        try {
                            json.put("ID", username);
                            json.put("password",password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pref.setString(getApplicationContext(), "user", json.toString());
                        pref.setBoolean(getApplicationContext(), "auto", auto);

                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        String ID = username;
                        //인텐트에 유저의 고유 아이디를 넣음.
                        intent.putExtra("id", ID);
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "존재하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });





        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent에 context설정해서 작업요청할 수 있게.
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivityResult.launch(intent);
            }
        });


    }






    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Log.d("TAGusename:", "은: " + helper.getUser(result.getData().getStringExtra("id")).get_id());
                        Log.d("TAGpassword:", "는: " + helper.getUser(result.getData().getStringExtra("id")).getPassword());
                    }
                }
            }
    );




    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 시작.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "종료", Toast.LENGTH_LONG).show();
    }

    //키카오 로그인 시 필요한 해시키를 얻는 메소드.
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash Key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    //bitmap 을  string 형태로 변환하는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String bitmapToString(Bitmap bitmap) {
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        image = java.util.Base64.getEncoder().encodeToString(byteArray);
        return image;
    }

    //string 을  bitmap 형태로 변환하는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap stringToBitmap(String data) {
        Bitmap bitmap = null;
        byte[] byteArray = java.util.Base64.getDecoder().decode(data);
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
        bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }



}