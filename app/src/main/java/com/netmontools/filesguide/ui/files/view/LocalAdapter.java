package com.netmontools.filesguide.ui.files.view;

import static com.netmontools.filesguide.MainActivity.localViewModel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netmontools.filesguide.R;
import com.netmontools.filesguide.ui.files.model.Folder;
import com.netmontools.filesguide.utils.SimpleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalHolder> {

    private static final int TYPE_FILE = 0;
    private static final int TYPE_FILE_CHECKED = 1;
    private static final int TYPE_BIG_IMAGE = 2;
    private List<Folder> points = new ArrayList<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    @NonNull
    @Override
    public LocalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = null;

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

       if (viewType == TYPE_FILE){
            itemView = layoutInflater.inflate(R.layout.local_item, parent, false);
        } else if (viewType == TYPE_FILE_CHECKED) {
            itemView = layoutInflater.inflate(R.layout.local_checked_item, parent, false);
        } else if (viewType == TYPE_BIG_IMAGE) {
           itemView = layoutInflater.inflate(R.layout.local_big_image_item, parent, false);
       }

        final LocalHolder holder = new LocalHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(points.get(position));
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(parent.getContext(),v);
                popupMenu.getMenu().add("OPEN");
                popupMenu.getMenu().add("SCAN");
//                popupMenu.getMenu().add("DELETE");
//                popupMenu.getMenu().add("MOVE");
//                popupMenu.getMenu().add("RENAME");

                int position = holder.getLayoutPosition();
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(points.get(position));
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("OPEN")) {
                            localViewModel.open(getPointAt(position));
                        }

                        if(item.getTitle().equals("SCAN")) {
                            localViewModel.scan(getPointAt(position));
                        }

//                        if(item.getTitle().equals("DELETE")){
//                            boolean deleted = true;//selectedFile.delete();
//                            if(deleted){
//                                Toast.makeText(App.instance.getApplicationContext(),"DELETED ",Toast.LENGTH_SHORT).show();
//                                v.setVisibility(View.GONE);
//                            }
//                        }
//
//                        if(item.getTitle().equals("MOVE")){
//                            Toast.makeText(App.instance.getApplicationContext(),"MOVED ",Toast.LENGTH_SHORT).show();
//
//                        }
//
//                        if(item.getTitle().equals("RENAME")){
//                            Toast.makeText(App.instance.getApplicationContext(),"RENAME ",Toast.LENGTH_SHORT).show();
//
//                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });

        //return new LocalHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalHolder holder, int position) {
        Folder currentPoint = points.get(position);
        ImageView imageView = holder.photoImageView;
        File file = new File(currentPoint.getPath());
        //Picasso.get().load(file).fit().into(imageView);
        if(file.exists() && file.isFile()) {
            if (currentPoint.isVideo() || currentPoint.isImage()) {
                Glide
                        .with(holder.photoImageView.getContext())
                        .load(file)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            } else {
                holder.photoImageView.setImageDrawable(currentPoint.getImage());
            }
        } else if(file.isDirectory()) {
            holder.photoImageView.setImageDrawable(currentPoint.getImage());
        }

        holder.textViewTitle.setText(currentPoint.getName());
        holder.textViewSize.setText(SimpleUtils.formatCalculatedSize(currentPoint.getSize()));
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (points.get(position).isChecked()) {
            return TYPE_FILE_CHECKED;
        } else if(points.get(position).isImage()) {
            return TYPE_BIG_IMAGE;
        } else
            return TYPE_FILE;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPoints(List<Folder> points) {
        this.points = points;
        notifyDataSetChanged();
    }

    public Folder getPointAt(int position) {
        return points.get(position);
    }

    class LocalHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewSize;
        private final ImageView photoImageView;

        public LocalHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewSize = itemView.findViewById(R.id.text_view_size);
            photoImageView = itemView.findViewById(R.id.local_image_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Folder point);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Folder point);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}


