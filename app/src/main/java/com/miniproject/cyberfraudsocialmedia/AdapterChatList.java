package com.miniproject.cyberfraudsocialmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.fahmisdk6.avatarview.AvatarView;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyViewHolder> {

    List<ModelChatList> list;
    Context context;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onClick(int position);
        void onChatDeleteClick(int i);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public AdapterChatList(Context context, List<ModelChatList> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_chatlist,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelChatList m =list.get(position);

        holder.mName.setText(m.getName());
        holder.mLM.setText(m.getLm());
        holder.mDP.bind(m.getName(),null);

        holder.mLL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu menu = new PopupMenu(context, holder.mName);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mListener.onChatDeleteClick(holder.getAdapterPosition());
                        return true;
                    }
                });
                menu.inflate(R.menu.post_menu);
                menu.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mName,mLM;
        LinearLayout mLL;
        AvatarView mDP;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            mLL = itemView.findViewById(R.id.chatlist_container);

            mName = itemView.findViewById(R.id.chatlist_topicname);
            mLM = itemView.findViewById(R.id.chatlist_lm);
            mDP = itemView.findViewById(R.id.chatlist_dp);

            mLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(getAdapterPosition());
                }
            });

        }
    }
}
