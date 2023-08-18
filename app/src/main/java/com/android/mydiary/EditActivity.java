package com.android.mydiary;

import static android.content.ContentValues.TAG;
import static com.android.mydiary.MainActivity.helper;
import static com.android.mydiary.MenuActivity.ID;
import static com.android.mydiary.MenuActivity.list;

import static java.sql.DriverManager.println;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.mydiary.data.*;

public class EditActivity extends AppCompatActivity implements MyApplication.OnResponseListener {
    TextView locationView;
    Button pictureButton;
    Button postButton;
    Button take_pictureButton;

    ImageView weatherView;
    EditText editText;
    EditText youtubeText;



    String weather;
    Boolean EditMode = false;

    EditAdapter adapter;

    Uri uri;

    Preferencemanager pref;

    public ArrayList<Bitmap> editPictures = new ArrayList<>();
    ArrayList<Uri> uriArrayList = new ArrayList<>();


    Location currentLocation;
    GPSListener gpsListener;

    int locationCount = 0;
    String currentWeather;
    String currentAddress;
    String currentDateString;
    Date currentDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        checkSelfPermission();





        pref = new Preferencemanager();

        //뷰 컴포넌트들 선언.
        locationView = findViewById(R.id.locationView);
        weatherView = findViewById(R.id.weatherView);
        pictureButton = findViewById(R.id.pictureButton);
        postButton = findViewById(R.id.postButton);
        editText = findViewById(R.id.editText);
        youtubeText = findViewById(R.id.youtubeText);
        take_pictureButton = findViewById(R.id.take_pictureButton);

        locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
//        Toast.makeText(this, ""+currentLocation, Toast.LENGTH_SHORT).show();
            }
        });


        //리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.recycler2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        //리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new EditAdapter(editPictures);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new EditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                editPictures.remove(position);
                adapter.notifyDataSetChanged();
            }
        });


        //날씨를 선택하는 버튼.
        weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, WeatherActivity.class);
                startActivityResult3.launch(intent);
            }
        });


        //카메라 촬영 후 사진을 불러오는 버튼.
        take_pictureButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // 카메라촬영 클릭 이벤트
                    case R.id.take_pictureButton:
                        // 카메라 기능을 Intent
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // 사진파일 변수 선언 및 경로세팅
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }

                        // 방금 생성한 파일에 사진을 저장.
                        if (photoFile != null) {
                            uri = FileProvider.getUriForFile(EditActivity.this, getPackageName() + ".fileprovider", photoFile);
                            //name을 output으로 하고 uri값을 value로 주면 해당 uri 경로에 사진을 저장.
                            intent.putExtra("output", uri);
                            startActivityResult2.launch(intent);
                        }
                        break;
                }
            }
        });

        //MenuActivity로 부터 intent를 받아서 왔는데 포지션이 있으면 수정, 없으면 추가.
        //수정인 경우 자료 불러오기.
        if (getIntent().getIntExtra("position", -1) != -1) {
            weather = list.get(getIntent().getIntExtra("position", -1)).getWeather();

            if (weather != null) {
                switch (weather) {
                    case "Clear":
                        weatherView.setImageResource(R.drawable.clear_icon);
                        break;
                    case "Partly Cloudy":
                        weatherView.setImageResource(R.drawable.partly_cloudy_icon);
                        break;
                    case "Mostly Cloudy":
                        weatherView.setImageResource(R.drawable.mostly_cloudy_icon);
                        break;
                    case "Cloudy":
                        weatherView.setImageResource(R.drawable.cloudy_icon);
                        break;
                    case "Rain":
                        weatherView.setImageResource(R.drawable.rain_icon);
                        break;
                    case "Snow/Rain":
                        weatherView.setImageResource(R.drawable.snow_rain_icon);
                        break;
                    case "Snow":
                        weatherView.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }

            ArrayList<String> pictures = list.get(getIntent().getIntExtra("position", -1)).getPictures();
            if(pictures!=null) {
                for (int i = 0; i < pictures.size(); i++) {
                    Bitmap bitmap = stringToBitmap(pictures.get(i));
                    editPictures.add(bitmap);
                }
//                    adapter.notifyDataSetChanged();
            }
            locationView.setText(list.get(getIntent().getIntExtra("position", -1)).getAddress());
            youtubeText.setText(list.get(getIntent().getIntExtra("position", -1)).getYoutube());


            editText.setText(list.get(getIntent().getIntExtra("position", -1)).getText());
            EditMode = true;
        }
        //추가인 경우 이전 작성하다 만 게 있으면 가져오기(사진 제외)
        else {
            String textData = pref.getString(getApplicationContext(), "textData");
            editText.setText(textData);
            weather = pref.getString(getApplicationContext(), "weather");

            if (weather != null) {
                switch (weather) {
                    case "Clear":
                        weatherView.setImageResource(R.drawable.clear_icon);
                        break;
                    case "Partly Cloudy":
                        weatherView.setImageResource(R.drawable.partly_cloudy_icon);
                        break;
                    case "Mostly Cloudy":
                        weatherView.setImageResource(R.drawable.mostly_cloudy_icon);
                        break;
                    case "Cloudy":
                        weatherView.setImageResource(R.drawable.cloudy_icon);
                        break;
                    case "Rain":
                        weatherView.setImageResource(R.drawable.rain_icon);
                        break;
                    case "Snow/Rain":
                        weatherView.setImageResource(R.drawable.snow_rain_icon);
                        break;
                    case "Snow":
                        weatherView.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }
            EditMode = false;
        }


        //포스트 버튼.
        postButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String text = editText.getText().toString();
                //빈mData선언.
                mData mData = new mData(null, null, null, 0);
                //id는 1번부터 시작이므로 넣기 전에는 0으로.

                //mData에 text 넣음.
                mData.setText(text);

                if (weather != null) {
                    mData.setWeather(weather);
                }

                if (editPictures != null) {
                    ArrayList<String> pictures = new ArrayList<>();
                    for(int i = 0; i < editPictures.size(); i++){
                        Bitmap bitmap = editPictures.get(i);
                        String picture = bitmapToString(bitmap);
                        pictures.add(picture);
                    }
                    mData.setPictures(pictures);
                }

                String youtube = youtubeText.getText().toString();
                if(youtube.startsWith("https://youtu.be/")) {
                    youtube = youtube.substring(17);
                    Log.d(TAG, "유튜브 링크: "+youtube);
                } else{
                    Log.d(TAG, "유튜브 링크: "+youtube);
                }
                mData.setYoutube(youtube);
                if(!locationView.getText().equals("날씨\n불러오기")) {
                    mData.setAddress(locationView.getText().toString());
                }



                //포지션을 받았으면 수정, 받지 못했으면 추가.
                if (getIntent().getIntExtra("position", -1) == -1) {
                    //인서트하면 id번호가 나오고 그것을 mdata의 id값에 저장.
                    mData.setDate(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date()));
                    long _id = helper.InsertDiary(mData, ID);
                    mData.set_id(_id);
                    list.add(0, mData);
                } else {
                    long _id = list.get(getIntent().getIntExtra("position", 0)).get_id();
                    list.remove(getIntent().getIntExtra("position", 0));
                    helper.UpdateDiary(_id, mData);
                    mData.set_id(_id);
                    list.add(getIntent().getIntExtra("position", 0), mData);
                }

                //포스팅을 완료하면 자동저장된 것들 삭제.
                editText.setText("");
                weather = "";

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인텐트에 액션을 지정해준다.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent에 타입을 지정해줘서 갤러리로 갈 수 있게.
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityResult.launch(intent);
            }
        });

    }


    //intent에 putExtra, getStringExtra 로 날씨이름을 주고 받을 수 있도록.
    ActivityResultLauncher<Intent> startActivityResult3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        weather = result.getData().getStringExtra("weather");
                        if (weather != null) {
                            switch (weather) {
                                case "Clear":
                                    weatherView.setImageResource(R.drawable.clear_icon);
                                    break;
                                case "Partly Cloudy":
                                    weatherView.setImageResource(R.drawable.partly_cloudy_icon);
                                    break;
                                case "Mostly Cloudy":
                                    weatherView.setImageResource(R.drawable.mostly_cloudy_icon);
                                    break;
                                case "Cloudy":
                                    weatherView.setImageResource(R.drawable.cloudy_icon);
                                    break;
                                case "Rain":
                                    weatherView.setImageResource(R.drawable.rain_icon);
                                    break;
                                case "Snow/Rain":
                                    weatherView.setImageResource(R.drawable.snow_rain_icon);
                                    break;
                                case "Snow":
                                    weatherView.setImageResource(R.drawable.snow_icon);
                                    break;
                            }
                        }
                    }
                }
            }
    );


    //생성한 파일경로를 넣은 uri에 카메라로 찍은 사진을 넣었으므로 이미지뷰에 uri 적용.
    ActivityResultLauncher<Intent> startActivityResult2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        try {
                            Bitmap bitmap = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            }
                            editPictures.add(bitmap);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    //갤러리로부터 받은 데이터 result.getData()로부터 이미지와 텍스트를 적용.
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if(result.getData().getClipData()==null){
                            Uri uri = result.getData().getData();
                            try {
                                Bitmap bitmap = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                                } else {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                }
                                editPictures.add(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            ClipData clipData = result.getData().getClipData();
                            Log.d(TAG, "클립데이터: "+clipData);
                            if(clipData.getItemCount() > 10){
                                Toast.makeText(EditActivity.this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                for (int i = 0; i < clipData.getItemCount(); i++){
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    try {
                                        Bitmap bitmap = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                                        } else {
                                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                        }
                                        editPictures.add(bitmap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                            adapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );



    //파일생성 메서드.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // 파일이름을 세팅 및 저장경로 세팅
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (weather != null) {
            switch (weather) {
                case "sunny":
                    Toast.makeText(this, "해가 쨍쩅한 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "rainy":
                    Toast.makeText(this, "비가 오는 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "windy":
                    Toast.makeText(this, "바람이 많이 부는 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "snow":
                    Toast.makeText(this, "눈이 오는 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "lightening":
                    Toast.makeText(this, "뇌우가 몰아치는 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "tornado":
                    Toast.makeText(this, "태풍이 몰려오는 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "cloudy":
                    Toast.makeText(this, "구름이 많은 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                case "moon":
                    Toast.makeText(this, "달이 밝은 날이야.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
        Log.d("에딧생명주기", "onResume 됨.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!EditMode) {
            String textData = editText.getText().toString();
            if (weather != null) {
                pref.setString(getApplicationContext(), "weather", weather);
            }
            pref.setString(getApplicationContext(), "textData", textData);
        }
        Log.d("에딧생명주기", "onPause 됨.");
    }



    //권한에 대한 응답이 있을때 작동하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //권한을 허용 했을 경우
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("MainActivity", "권한 허용 : " + permissions[i]);
                }
            }
        }

    }

    public void checkSelfPermission() {

        String temp = "";

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        //위치 권한
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.ACCESS_FINE_LOCATION + " ";
        }


        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }else {
            // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }



    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onRequest(String command) {
        if (command != null) {
            if (command.equals("getCurrentLocation")) {
                getCurrentLocation();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getCurrentLocation() {
        // set current time
        currentDateString = AppConstants.dateFormat3.format(new Date());
//        if (fragment2 != null) {
//            fragment2.setDateString(currentDateString);
//        }


        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "Last Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
                Toast.makeText(EditActivity.this, "겟 커런트"+message, Toast.LENGTH_SHORT).show();

                getCurrentWeather();
                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            println("Current location requested.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            manager.removeUpdates(gpsListener);

            println("Current location requested.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            currentLocation = location;

            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "Current Location -> Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Toast.makeText(EditActivity.this, "체인지 "+message, Toast.LENGTH_SHORT).show();

            getCurrentWeather();
            getCurrentAddress();
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    public void getCurrentAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            currentAddress = null;

            Address address = addresses.get(0);
            if (address.getLocality() != null) {
                currentAddress = address.getLocality();
            }

            if (address.getSubLocality() != null) {
                if (currentAddress != null) {
                    currentAddress +=  " " + address.getSubLocality();
                } else {
                    currentAddress = address.getSubLocality();
                }
            }

            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address : " + country + " " + adminArea + " " + currentAddress);
            locationView.setText(currentAddress);

//            if (fragment2 != null) {
//                fragment2.setAddress(currentAddress);
//            }
        }
    }

    public void getCurrentWeather() {

        Map<String, Double> gridMap = GridUtil.getGrid(currentLocation.getLatitude(), currentLocation.getLongitude());
        double gridX = gridMap.get("x");
        double gridY = gridMap.get("y");
        println("x -> " + gridX + ", y -> " + gridY);

        sendLocalWeatherReq(gridX, gridY);

    }

    public void sendLocalWeatherReq(double gridX, double gridY) {
        String url = "http://www.kma.go.kr/wid/queryDFS.jsp";
        url += "?gridx=" + Math.round(gridX);
        url += "&gridy=" + Math.round(gridY);

        Map<String,String> params = new HashMap<String,String>();

        MyApplication.send(AppConstants.REQ_WEATHER_BY_GRID, Request.Method.GET, url, params,  this);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void processResponse(int requestCode, int responseCode, String response) {
        if (responseCode == 200) {
            if (requestCode == AppConstants.REQ_WEATHER_BY_GRID) {
                // Grid 좌표를 이용한 날씨 정보 처리 응답
                //println("response -> " + response);

                XmlParserCreator parserCreator = new XmlParserCreator() {
                    @Override
                    public XmlPullParser createParser() {
                        try {
                            return XmlPullParserFactory.newInstance().newPullParser();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                GsonXml gsonXml = new GsonXmlBuilder()
                        .setXmlParserCreator(parserCreator)
                        .setSameNameLists(true)
                        .create();

                WeatherResult weathers = gsonXml.fromXml(response, WeatherResult.class);

                // 현재 기준 시간
                try {
                    Date tmDate = AppConstants.dateFormat.parse(weathers.header.tm);
                    String tmDateText = AppConstants.dateFormat2.format(tmDate);
                    println("기준 시간 : " + tmDateText);

                    for (int i = 0; i < weathers.body.datas.size(); i++) {
                        WeatherItem item = weathers.body.datas.get(i);
                        println("#" + i + " 시간 : " + item.hour + "시, " + item.day + "일째");
                        println("  날씨 : " + item.wfKor);
                        println("  기온 : " + item.temp + " C");
                        println("  강수확률 : " + item.pop + "%");

                        println("debug 1 : " + (int)Math.round(item.ws * 10));
                        float ws = Float.valueOf(String.valueOf((int)Math.round(item.ws * 10))) / 10.0f;
                        println("  풍속 : " + ws + " m/s");
                    }
                    weather = weathers.body.datas.get(0).wfEn;
                    if (weather != null) {
                        switch (weather) {
                            case "Clear":
                                weatherView.setImageResource(R.drawable.clear_icon);
                                break;
                            case "Partly Cloudy":
                                weatherView.setImageResource(R.drawable.partly_cloudy_icon);
                                break;
                            case "Mostly Cloudy":
                                weatherView.setImageResource(R.drawable.mostly_cloudy_icon);
                                break;
                            case "Cloudy":
                                weatherView.setImageResource(R.drawable.cloudy_icon);
                                break;
                            case "Rain":
                                weatherView.setImageResource(R.drawable.rain_icon);
                                break;
                            case "Snow/Rain":
                                weatherView.setImageResource(R.drawable.snow_rain_icon);
                                break;
                            case "Snow":
                                weatherView.setImageResource(R.drawable.snow_icon);
                                break;
                        }
                    }
                    stopLocationService();

                    // set current weather
                    WeatherItem item = weathers.body.datas.get(0);
                    currentWeather = item.wfKor;
//                    if (fragment2 != null) {
//                        fragment2.setWeather(item.wfKor);
//                    }

                    // stop request location service after 2 times
                    if (locationCount > 1) {
                        stopLocationService();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }


            } else {
                // Unknown request code
                println("Unknown request code : " + requestCode);

            }

        } else {
            println("Failure response code : " + responseCode);

        }

    }

    private void println(String data) {
        Log.d(TAG, data);
    }






    //bitmap 을  string 형태로 변환하는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String bitmapToString(Bitmap bitmap) {
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        image = Base64.getEncoder().encodeToString(byteArray);
        return image;
    }

    //string 을  bitmap 형태로 변환하는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap stringToBitmap(String data) {
        Bitmap bitmap = null;
        byte[] byteArray = Base64.getDecoder().decode(data);
        ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
        bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }


}

