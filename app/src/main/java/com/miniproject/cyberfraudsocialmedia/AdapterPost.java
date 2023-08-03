package com.miniproject.cyberfraudsocialmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyViewHolder> implements Filterable {

    List<ModelPost> list;
    List<ModelPost> listFiltered;
    Context context;
    String userId;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(ModelPost m);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public AdapterPost(Context context, List<ModelPost> list) {
        this.list = list;
        this.listFiltered = list;
        this.context = context;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_post,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelPost m =listFiltered.get(position);

        if (m.getImage()==null){
            holder.mImageView.setVisibility(View.GONE);
        }else {
            holder.mImageView.setImageBitmap(m.getImage());
            holder.mImageView.setVisibility(View.VISIBLE);
        }
        if (m.getId().equals(userId)){
            holder.mOption.setVisibility(View.VISIBLE);
        }else{
            holder.mOption.setVisibility(View.GONE);
        }

        holder.mName.setText(m.getName());
        //getHighlightedText() is used to highlight hastag words with blue color
        holder.mText.setText(Keys.getHighlightedText(m.getText()));
        holder.mDate.setText(m.getTime());

        holder.mOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mListener.onDeleteClick(listFiltered.get(holder.getAdapterPosition()));
                        return true;
                    }
                });
                menu.inflate(R.menu.post_menu);
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        ImageButton mOption;
        ShapeableImageView mDP;
        TextView mText,mName,mDate;
        FirebaseAuth fAuth;
        String userId;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.post_img);

            mName = itemView.findViewById(R.id.post_username);
            mText = itemView.findViewById(R.id.post_text);
            mDate = itemView.findViewById(R.id.post_time);
            mOption = itemView.findViewById(R.id.post_option);
            mDP = itemView.findViewById(R.id.post_dp);

            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = list;
                } else {
                    List<ModelPost> filteredList = new ArrayList<>();
                    for (ModelPost row : list) {
                        if (row.getText().toLowerCase().contains("#"+charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<ModelPost>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
