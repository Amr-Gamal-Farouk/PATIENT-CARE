package com.example.amrgamal.testwear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.amrgamal.testwear.Fragment.ChatFragment;
import com.example.amrgamal.testwear.OnListFragmentInteractionListener;
import com.example.amrgamal.testwear.R;
import com.example.amrgamal.testwear.model.Image;

import java.util.List;

/**
 * Created by tito on 2/8/2018.
 */

public class Image_Adapter extends RecyclerView.Adapter<Image_Adapter.ImageViewHolder>{

    private List<Image> mImageList;
    Context context;
    OnListFragmentInteractionListener mListener;

    public Image_Adapter(List<Image> mImageList, Context context, OnListFragmentInteractionListener mListener) {

        this.context = context;
        this.mImageList = mImageList;
        this.mListener=mListener;


        Log.v("hhhhhhhhhhhh",mImageList.size()+"");
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item ,parent, false);

        Log.v("hhhhhhhhhhhh","test1");
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        Image c = mImageList.get(position);

//        Log.v("hhhhhhhhhhhh","test2");
//        Log.v("hhhhhhhhhhhh",c.getDirect()+"");
//        Log.v("hhhhhhhhhhhh",c.getLink()+"");
        holder.view.setBackgroundResource(c.getDirect());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Log.v("hhhhhhhhhhhh",position+"");

                mListener.onListClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView view;
        public ImageViewHolder(View itemView) {
            super(itemView);

            view = (ImageView) itemView.findViewById(R.id.imageItem);
        }
    }

}
