package online.himanshudama.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>{

    public List<Users> usersList;
    public Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    Integer unreadCount;

    public UsersRecyclerAdapter(List<Users> userList) {
        this.usersList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final String imageURL = usersList.get(i).getImage();
        final String fullName = usersList.get(i).getFirstName() + " " + usersList.get(i).getLastName();
        viewHolder.setUserInfo(imageURL, fullName);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        final String selectedUserEmail = usersList.get(i).getUserName();
        final String selectedUserName = usersList.get(i).getFirstName() + " " +usersList.get(i).getLastName();
        final String selectedUserImage = usersList.get(i).getImage();
        final String conversationID = selectedUserEmail + mAuth.getCurrentUser().getEmail();



        viewHolder.unReadCount.setVisibility(View.VISIBLE);
        //viewHolder.unReadCount.setText(unreadCount.toString());


        viewHolder.userListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("chatUserName", selectedUserName);
                chatIntent.putExtra("chatUserEmail", selectedUserEmail);
                chatIntent.putExtra("chatUserImage", selectedUserImage);
                context.startActivity(chatIntent);

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("unreadCount", 0);

                firebaseFirestore.collection("Conversations").document(conversationID).update(userMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImage;
        private TextView userFullName;
        private ConstraintLayout userListLayout;
        private TextView unReadCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userListProfileImageView);
            userFullName = itemView.findViewById(R.id.userListNameTextView);
            userListLayout = itemView.findViewById(R.id.userListLayout);
            unReadCount = itemView.findViewById(R.id.unreadCount);

        }

        public void setUserInfo(String imageURL, String fullName){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_default_picture);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageURL).into(userImage);
            userFullName.setText(fullName);
        }
    }

}

