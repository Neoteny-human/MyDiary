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

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {

    private ArrayList<Bitmap> pictures = null;
    int count = 0;

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    //아이템 뷰를 저장하는 뷰홀터 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView editImageView;
        TextView imageNumber;


        ViewHolder(View itemView){
            super(itemView);

            //뷰 객체에 대한 참조. (hold strong reference)
            editImageView = itemView.findViewById(R.id.editImageView);
            imageNumber = itemView.findViewById(R.id.imageNumber);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    //-1 => 포지션이 없음
                    if(pos!=-1){
                        if(mListener!=null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });


        }

    }

    //생성자에서 데이터 리스트 객체를 전달받음.
    EditAdapter(ArrayList<Bitmap> pictures){
        this.pictures = pictures;
    }



    //onCreateViewHolder()  - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public EditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_edit_item, parent, false);
        ViewHolder viewHolder = new EditAdapter.ViewHolder(view);



        return viewHolder;
    }

    //onBindingViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(EditAdapter.ViewHolder vh, int position) {
        if(pictures.get(position)!= null) {
            vh.editImageView.setImageBitmap(pictures.get(position));
        }
        else{vh.editImageView.setImageBitmap(null);}

        String p = String.valueOf(position+1);
        vh.imageNumber.setText(p);

    }

    //getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return pictures.size();
    }



}
