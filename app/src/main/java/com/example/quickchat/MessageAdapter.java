package com.example.quickchat;

import static com.example.quickchat.ChatActivity.rImage;
import static com.example.quickchat.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout_item,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.txtMessageSender.setText(messages.getMessage());
            Picasso.get().load(sImage).into(viewHolder.circleImageViewSender);
        } else {
            ReceiverViewHolder viewHolder=(ReceiverViewHolder) holder;
            viewHolder.txtMessageReceiver.setText(messages.getMessage());
            Picasso.get().load(rImage).into(viewHolder.circleImageViewReceiver);
        }
    }

    @Override
    public int getItemCount() {
        //messagesArrayList=new ArrayList<>();
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageViewSender;
        TextView txtMessageSender;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageViewSender=itemView.findViewById(R.id.profile_image_sender);
            txtMessageSender=itemView.findViewById(R.id.txtMessages_sender);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageViewReceiver;
        TextView txtMessageReceiver;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageViewReceiver=itemView.findViewById(R.id.profile_image_receiver);
            txtMessageReceiver=itemView.findViewById(R.id.txtMessages_receiver);
        }
    }
}
