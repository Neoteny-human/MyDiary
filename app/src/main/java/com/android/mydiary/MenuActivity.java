package com.android.mydiary;

import static com.android.mydiary.MainActivity.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;


public class MenuActivity extends AppCompatActivity {
    ImageView weatherImage;
    ImageView pictureImage;
    TextView text;

    Uri uri;
    SimpleTextAdapter adapter;
    public static String ID;

    //리사이클러뷰에 표시할 데이터 리스트.
    public static ArrayList<mData> list = new ArrayList<>();

    Preferencemanager pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        String id = getIntent().getStringExtra("id");
        if(id!=null && !id.isEmpty()) {
            ID = id;
            list = helper.getDiary(ID);
        }


        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, profileActivity.class);
                startActivity(i);
            }
        });



//        //맨 마지막에 포스팅된 다이어리의 _id값 확인.(diary table의 PK의 값과 동일).
//        if(list.size()>0) {
//            Log.d("id값", "확인:" + list.get(0).get_id());
//        }


        //리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.recycler1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        //리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new SimpleTextAdapter(list);
        recyclerView.setAdapter(adapter);






        //만든 커스텀 리스너 인터페이스에서 2개의 추상메서드를 정의하면서 새로운 리스너 인스턴스 선언.
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position!=(-1)) {
                    Intent intent = new Intent(MenuActivity.this, EditActivity.class);
                    intent.putExtra("position", position);
                    startActivityResult.launch(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
//                list.remove(position);
//                adapter.notifyDataSetChanged();
            }
        }));

        Log.d("이거저거", "1. "+getLayoutInflater()+"2. "+getLayoutInflater().getContext()+"3. "
        +getLayoutInflater().getContext().getTheme());




        // process received intent
        Intent receivedIntent = getIntent();
        String username = receivedIntent.getStringExtra("username");
        String password = receivedIntent.getStringExtra("password");

//        if (password.isEmpty() || username.isEmpty()) {
//            Toast.makeText(this, "아이디, 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            Toast.makeText(this, "아이디: " + username + ", 비번: " + password, Toast.LENGTH_SHORT).show();
//        }

        weatherImage = findViewById(R.id.weatherImage);
        pictureImage = findViewById(R.id.pictureImage);


        text = findViewById(R.id.text);






        Button writeButton = findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, EditActivity.class);
                startActivityResult.launch(intent);
            }
        });
    }



    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    adapter.notifyDataSetChanged();
//                    mData mData = null;
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        if (result.getData()!=null){
////                            weatherImage.setImageBitmap(result.getData().getParcelableExtra("weather"));
//                            mData.setWeatherBitmap(result.getData().getParcelableExtra("weather"));
//                            if(result.getData().getParcelableExtra("picture")!=null) {
//                                try {
//                                    uri = result.getData().getParcelableExtra("picture");
//                                    Log.d("TAG", "uri값 확인"+uri);
//                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getData().getParcelableExtra("picture"));
//                                    BitmapDrawable drawable = new BitmapDrawable(getResources(),bitmap);
////                                    pictureImage.setImageBitmap(bitmap);
//                                    mData.setPictureBitmap(drawable);
//                                    } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            text.setText(result.getData().getStringExtra("text"));
//                            list.add(mData);
//                            Log.d("list추적", "menu result"+list);
//
//                        }
//                    }
                }
            }
    );

//    @RequiresApi(api = Build.VERSION_CODES.P)
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        text.setText(savedInstanceState.getString("text"));
//        Bitmap bitmap = savedInstanceState.getParcelable("weather");
//        weatherImage.setImageBitmap(bitmap);
////        Bitmap bitmap2 = savedInstanceState.getParcelable("picture");
////        pictureImage.setImageBitmap(bitmap2);
////        uri = savedInstanceState.getParcelable("picture");
////        try {
////            InputStream in = getContentResolver().openInputStream(uri);
////            if (in != null) {
////                Bitmap bitmap2 = BitmapFactory.decodeStream(in);
////                pictureImage.setImageBitmap(bitmap2);
////            }
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
//
//
////        if(uri!=null) {
////            try {
////                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
////                pictureImage.setImageBitmap(bitmap2);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//
//
//
//
//
//        try {
//            byte[] decodedString = Base64.decode(savedInstanceState.getString("picture"), Base64.DEFAULT);
//            InputStream in = new ByteArrayInputStream(decodedString);
//            Bitmap bitmap2 = BitmapFactory.decodeStream(in);
//            pictureImage.setImageBitmap(bitmap2);
//        }catch (Exception e){e.printStackTrace();}
//
//    }


//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if(text.getText() != null) {
//            outState.putString("text", String.valueOf(text.getText()));
//        }
//        BitmapDrawable drawable = (BitmapDrawable) weatherImage.getDrawable();
//        if(drawable!=null){
//        Bitmap bitmap = drawable.getBitmap();
//        outState.putParcelable("weather", bitmap);
//        }
//
////        outState.putParcelable("picture",uri);
//
//
//
////        BitmapDrawable drawable2 = (BitmapDrawable) pictureImage.getDrawable();
////        if(pictureImage.getDrawable() != null) {
////            Bitmap bitmap2 = drawable2.getBitmap();
////            outState.putParcelable("picture", bitmap2);
////        }
//
//
//
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        BitmapDrawable drawable2 = (BitmapDrawable) pictureImage.getDrawable();
//        try {
//            if (drawable2 != null) {
//                Bitmap bitmap2 = drawable2.getBitmap();
//                bitmap2.compress(Bitmap.CompressFormat.PNG, 1, out);
//                byte[] bytes = out.toByteArray();
//                String a = Base64.encodeToString(bytes, Base64.DEFAULT);
//                outState.putString("picture", a);
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("메뉴생명주기", "onRestart 됨.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("메뉴생명주기", "onStart 됨.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("메뉴생명주기", "onResume 됨.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("메뉴생명주기", "onPause 됨.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("메뉴생명주기", "onStop 됨.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("메뉴생명주기", "onDestroy 됨.");
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        pref = new Preferencemanager();
//        pref.setBoolean(getApplicationContext(), "auto", false);
//    }

    //커스텀 리스너 인터페이스 생성.
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    //아이템터치리스너를 구현한 리사이클러 터치리스너 클래스 생성.
    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {


        private int clickCase;
        private GestureDetector gestureDetector;
        private MenuActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MenuActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            //제스터디텍터에서 필요한 동작만 감지할 수 있도록 override
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }

//                @Override
//                public boolean onDown(MotionEvent e) {
//                    return true;
//                }


                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    int position = recyclerView.getChildAdapterPosition(recyclerView.findChildViewUnder(e.getX(), e.getY()));
                    if(position!=-1) {
                        Intent intent = new Intent(MenuActivity.this, ShowActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }

                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child != null && clickListener != null){
                        clickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                    return false;
                }




                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        delete(recyclerView.getChildAdapterPosition(child));
//                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        //차일드뷰까지 터치이벤트를 흘려보낼 수 있도록 false유지.
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View child = rv.findChildViewUnder(e.getX(), e.getY());
//                boolean event;
//                event = gestureDetector.onTouchEvent(e);
//                Log.d("클릭 시", "온터치이벤트: " + event);
//                Log.d("클릭 케이스", "" + clickCase);
//                if (child != null && clickListener != null && event) {
//                    clickListener.onClick(child, rv.getChildAdapterPosition(child));
//                }
            if(gestureDetector.onTouchEvent(e)) {
                return true;
            }
            else{return false;}
        }

        //onInterceptTouchEvent가 true일 경우 실행 (하지만 올 일이 없음)
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//            int action = e.getAction();
//            if(action == MotionEvent.ACTION_DOWN) {
//                View child = rv.findChildViewUnder(e.getX(), e.getY());
//                Log.d("횟수", "onTouchEvent: ");
//                if (child != null && clickListener != null) {
//                    clickListener.onClick(child, rv.getChildAdapterPosition(child));
//                }
//            }

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            Log.d("횟수", "onTouchEvent: ");
            if (child != null && clickListener != null) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
        }

        //onInterceptTouchEvent가 중간에 신호를 감시하고 가로채는 걸 막을지(true) 허용할지(false)
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    //onLongClick시 다이얼로그를 띄워서 정말 삭제할 것인지 확인 후 삭제.
    void delete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long _id = list.get(position).get_id();
                        helper.DeleteDiary(_id);
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }








    //bitmap 을  string 형태로 변환하는 메서드 (이렇게 string 으로 변환된 데이터를 mysql 에서 longblob 의 형태로 저장하는식으로 사용가능)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String bitmapToString(Bitmap bitmap){
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        image = Base64.getEncoder().encodeToString(byteArray);
        return image;
    }

    //string 을  bitmap 형태로 변환하는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap stringToBitmap(String data){
        Bitmap bitmap = null;
        byte[] byteArray = Base64.getDecoder().decode(data);
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
        bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }

}
