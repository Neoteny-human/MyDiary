package com.android.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

//        //리사이클러뷰에 표시할 데이터 리스트.
//        ArrayList<mData> list = new ArrayList<>();
//        list.add(new mData(null, null, "안녕"));
//        BitmapDrawable dr = (BitmapDrawable) getResources().getDrawable(R.drawable.abc);
//        list.add(new mData(dr, dr, "안뇽"));
//
//        //리사이클러뷰에 LinearLayoutManager 객체 지정.
//        RecyclerView recyclerView = findViewById(R.id.recycler1);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        //리사이클러뷰에 SimpleTextAdapter 객체 지정.
//        SimpleTextAdapter adapter = new SimpleTextAdapter(list);
//        recyclerView.setAdapter(adapter);

    }
}