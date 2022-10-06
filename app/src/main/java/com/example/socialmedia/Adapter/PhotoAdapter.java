package com.example.socialmedia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Fragments.PostDetailFragment;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_photo_item,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.icon_bg).into(holder.postPhotoImageView);
        holder.postPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postID",post.getPostId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });
    }

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postPhotoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postPhotoImageView = itemView.findViewById(R.id.post_image_imageview);
        }
    }

}
