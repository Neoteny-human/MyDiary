package com.android.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;

public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

    private ArrayList<mData> mDataArrayList = null;
    int count = 0;

    //아이템 뷰를 저장하는 뷰홀터 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView1;
        ImageView pictureImage;
        ImageView weatherImage;
        TextView addressView;
        TextView dateView;


        ViewHolder(View itemView){
            super(itemView);

            //뷰 객체에 대한 참조. (hold strong reference)
            textView1=(TextView) itemView.findViewById(R.id.text);
            pictureImage=(ImageView) itemView.findViewById(R.id.pictureImage);
            weatherImage=(ImageView) itemView.findViewById(R.id.weatherImage);
            addressView = itemView.findViewById(R.id.addressView);
            dateView = itemView.findViewById(R.id.dateView);


            //아이템뷰 클릭 시 보여주는 화면으로 이동.
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if(pos != -1){
//                        Intent i = new Intent(itemView.getRootView().getContext(), ShowActivity.class);
//                        i.putExtra("position", pos);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        itemView.getRootView().getContext().startActivity(i);
//
//                    }
//                }
//            });
//            itemView.setOnLongClickListener(new View.OnLongClickListener(){
//                @Override
//                public boolean onLongClick(View v) {
//                    return false;
//                }
//            });


        }
        public void setTextView (String text){
            textView1.setText(text);
        }




    }

    //생성자에서 데이터 리스트 객체를 전달받음.
    SimpleTextAdapter(ArrayList<mData> list){
        mDataArrayList = list;
    }



    //onCreateViewHolder()  - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public SimpleTextAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new SimpleTextAdapter.ViewHolder(view);




//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), ""+viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
//
//            }
//        });

        return viewHolder;
    }

    //onBindingViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(SimpleTextAdapter.ViewHolder vh, int position) {
        vh.setTextView(mDataArrayList.get(position).getText());
        

        if(mDataArrayList.get(position).getPictures().size()>0) {

            ArrayList<String>pictures = mDataArrayList.get(position).getPictures();
            String picture = pictures.get(0);
            Bitmap bitmap = stringToBitmap(picture);
            vh.pictureImage.setImageBitmap(bitmap);
        }
        else{vh.pictureImage.setImageBitmap(null);}

        if(mDataArrayList.get(position).getWeather()!=null) {
            String weather = mDataArrayList.get(position).getWeather();
            switch(weather){
                case "Clear":
                    vh.weatherImage.setImageResource(R.drawable.clear_icon);
                    break;
                case "Partly Cloudy":
                    vh.weatherImage.setImageResource(R.drawable.partly_cloudy_icon);
                    break;
                case "Mostly Cloudy":
                    vh.weatherImage.setImageResource(R.drawable.mostly_cloudy_icon);
                    break;
                case "Cloudy":
                    vh.weatherImage.setImageResource(R.drawable.cloudy_icon);
                    break;
                case "Rain":
                    vh.weatherImage.setImageResource(R.drawable.rain_icon);
                    break;
                case "Snow/Rain":
                    vh.weatherImage.setImageResource(R.drawable.snow_rain_icon);
                    break;
                case "Snow":
                    vh.weatherImage.setImageResource(R.drawable.snow_icon);
                    break;
            }
        }
        else{vh.weatherImage.setImageBitmap(null);}

        vh.addressView.setText(mDataArrayList.get(position).getAddress());
        vh.dateView.setText(mDataArrayList.get(position).getDate());


        count+=1;
        Log.d("tag1", count+"번째 호출된 onBindViewHolder");



        //클릭반응
//        vh.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), ""+ vh.getAdapterPosition(), Toast.LENGTH_SHORT).show();
//
//            }
//        });



    }

    //getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataArrayList.size();
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
