package online.himanshudama.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    public List<Messages> messagesList;
    public Context context;
    private FirebaseAuth  mAuth;

    public ChatRecyclerAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list, viewGroup, false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        return new ChatRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        final String receivedMessage = messagesList.get(i).getMessageText();
        final String receivedImage = messagesList.get(i).getMessageImg();
        final Date receivedMessageTime = messagesList.get(i).getCreatedAt();
        final String senderUserName = messagesList.get(i).getSenderUserName();

        if(!TextUtils.isEmpty(receivedImage)) {
            if (senderUserName.equals(mAuth.getCurrentUser().getEmail())) {

                Glide.with(context).load(receivedImage).into(viewHolder.senderImageView);
                viewHolder.senderCardView.setVisibility(View.VISIBLE);

            } else {

                Glide.with(context).load(receivedImage).into(viewHolder.receiverImageView);
                viewHolder.receiverCardView.setVisibility(View.VISIBLE);

            }
        } else{

            if (senderUserName.equals(mAuth.getCurrentUser().getEmail())) {

                viewHolder.senderTextView.setText(receivedMessage);
                viewHolder.senderTextView.setVisibility(View.VISIBLE);

            } else {

                viewHolder.receiverTextView.setText(receivedMessage);
                viewHolder.receiverTextView.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView receiverTextView;
        public TextView senderTextView;
        public ImageView receiverImageView;
        public ImageView senderImageView;
        public CardView receiverCardView;
        public CardView senderCardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverTextView = itemView.findViewById(R.id.receiverTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            receiverImageView = itemView.findViewById(R.id.receiverImageView);
            senderImageView = itemView.findViewById(R.id.senderImageView);
            receiverCardView = itemView.findViewById(R.id.receiverCardView);
            senderCardView = itemView.findViewById(R.id.senderCardView);

        }

    }

}
