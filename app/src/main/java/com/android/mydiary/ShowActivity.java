package com.android.mydiary;

import static com.android.mydiary.MenuActivity.list;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;

public class ShowActivity extends YouTubeBaseActivity {

    ImageView weatherImage;
    ImageView pictureImage;
    TextView text;
    Button playButton;

    EditAdapter adapter;

    FrameLayout frameLayout;

    public ArrayList<Bitmap> showPictures = new ArrayList<>();

    //유튜브 설정.
    YouTubePlayerView youTubeplayerView;
    YouTubePlayer player;

    private static String API_KEY = "AIzaSyD_uLpKGrHT_zyUc1IEAgTu93B__FEeymY";
    private String videoId = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        frameLayout = findViewById(R.id.frameLayout);





        //리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.recycler3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        //리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new EditAdapter(showPictures);
        recyclerView.setAdapter(adapter);



        weatherImage = findViewById(R.id.weatherImage);
        pictureImage = findViewById(R.id.pictureImage);
        text = findViewById(R.id.text);
        int position = getIntent().getIntExtra("position",-1);
        String weather = list.get(position).getWeather();
        if (weather != null) {
            switch (weather) {
                case "Clear":
                    weatherImage.setImageResource(R.drawable.clear_icon);
                    break;
                case "Partly Cloudy":
                    weatherImage.setImageResource(R.drawable.partly_cloudy_icon);
                    break;
                case "Mostly Cloudy":
                    weatherImage.setImageResource(R.drawable.mostly_cloudy_icon);
                    break;
                case "Cloudy":
                    weatherImage.setImageResource(R.drawable.cloudy_icon);
                    break;
                case "Rain":
                    weatherImage.setImageResource(R.drawable.rain_icon);
                    break;
                case "Snow/Rain":
                    weatherImage.setImageResource(R.drawable.snow_rain_icon);
                    break;
                case "Snow":
                    weatherImage.setImageResource(R.drawable.snow_icon);
                    break;
            }
        }

        ArrayList<String> pictures = list.get(position).getPictures();
        if(pictures!=null) {
            for (int i = 0; i < pictures.size(); i++) {
                Bitmap bitmap = stringToBitmap(pictures.get(i));
                showPictures.add(bitmap);
            }
        }

        String text1 = list.get(getIntent().getIntExtra("position",-1)).getText();
        text.setText(text1);

        String youtube = list.get(getIntent().getIntExtra("position", -1)).getYoutube();
        Log.d("TAG", "onCreate: 비었다면"+youtube);
        videoId = youtube;

        if(videoId.equals("")){
            frameLayout.setVisibility(View.INVISIBLE);
        }
        else{
            initPlayer();
        }


        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
                playButton.setVisibility(View.INVISIBLE);
            }
        });

    }



    private void initPlayer() {
        youTubeplayerView = findViewById(R.id.youTubePlayerView);

        youTubeplayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;

                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String id) {
                        player.play();
                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    private void playVideo() {
        if(player != null) {
            if(player.isPlaying()) {
                player.pause();
            }
            player.cueVideo(videoId);
        }
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