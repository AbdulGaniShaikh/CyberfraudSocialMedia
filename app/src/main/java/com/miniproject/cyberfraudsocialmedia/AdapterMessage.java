package com.miniproject.cyberfraudsocialmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder> {

    List<ModelMessage> list;
    Context context;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onJoinClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public AdapterMessage(Context context, List<ModelMessage> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_chat,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelMessage m =list.get(position);

        holder.mName.setText(m.getName());
        holder.mText.setText(m.getMessage());
        holder.mDate.setText(m.getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mText,mName,mDate;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            mName = itemView.findViewById(R.id.chat_name);
            mText = itemView.findViewById(R.id.chat_message);
            mDate = itemView.findViewById(R.id.chat_time);

        }
    }
}

