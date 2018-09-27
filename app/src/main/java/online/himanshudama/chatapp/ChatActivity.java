package online.himanshudama.chatapp;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class ChatActivity extends AppCompatActivity {

    private Button attachImageBtn;
    private EditText messageEditText;
    private Button sendMessageBtn;
    private CircleImageView chatUserProfileImageView;
    private TextView chatUserNameTextView;
    private RecyclerView chatRecyclerView;
    private List<Messages> messagesList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private String chatUserName;
    private String chatUserEmail;
    private String chatUserImage;
    private int unreadCount = 1;
    String conversationID;
    String conversationIDReverse;
    private Bitmap compressedImageFile;
    private Uri mainImageURI = null;
    private ProgressBar imageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        attachImageBtn = findViewById(R.id.sendImageButton);
        sendMessageBtn = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
        chatUserProfileImageView = findViewById(R.id.chatUserProfileImageView);
        chatUserNameTextView = findViewById(R.id.chatUserName);
        messagesList = new ArrayList<>();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        imageProgressBar = findViewById(R.id.imageProgress);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        chatRecyclerAdapter = new ChatRecyclerAdapter(messagesList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerView.setHasFixedSize(true);

        Intent myIntent = getIntent();
        chatUserName = myIntent.getStringExtra("chatUserName");
        chatUserEmail = myIntent.getStringExtra("chatUserEmail");
        chatUserImage = myIntent.getStringExtra("chatUserImage");
        setChatUserInfo(chatUserName, chatUserImage);

        conversationID = mAuth.getCurrentUser().getEmail() + chatUserEmail;
        conversationIDReverse = chatUserEmail + mAuth.getCurrentUser().getEmail();


        attachImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        ImagePicker();

                    }

                } else {

                    ImagePicker();

                }
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser() != null) {
                    sendMessage(messageEditText.getText().toString(), "text");
                    messageEditText.setText("");
                }
            }
        });

        if (mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            Query firstQuery = firebaseFirestore.collection("Conversations/" + conversationID + "/Messages").orderBy("createdAt", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Messages messages = doc.getDocument().toObject(Messages.class);
                                messagesList.add(messages);
                                chatRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                    }

                }

            });

        }


    }

    public void setChatUserInfo(String chatUserName, String chatUserImage) {
        chatUserNameTextView.setText(chatUserName);
        Glide.with(this).load(chatUserImage).into(chatUserProfileImageView);
    }

    private void ImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ChatActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageProgressBar.setVisibility(View.VISIBLE);

                mainImageURI = result.getUri();
                File newImageFile = new File(mainImageURI.getPath());
                try {

                    compressedImageFile = new Compressor(ChatActivity.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray();
                String imageID = UUID.randomUUID().toString();
                UploadTask uploadImage = storageReference.child("img_message").child(imageID + ".jpg").putBytes(thumbData);

                uploadImage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            Uri imageUri = task.getResult().getDownloadUrl();
                            sendMessage(imageUri.toString(), "image");
                            imageProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


    public void sendMessage(final String myMessage, final String type) {

        if (!TextUtils.isEmpty(myMessage)) {

            Map<String, Object> userConversationMap = new HashMap<>();
            userConversationMap.put("conversationId", conversationID);
            userConversationMap.put("unreadCount", unreadCount);



            firebaseFirestore.collection("Users/" + mAuth.getCurrentUser().getUid() + "/Conversations/").document(chatUserEmail).set(userConversationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull final Task<Void> task) {

                    if (task.isSuccessful()) {


                        Map<String, Object> conversationMap = new HashMap<>();
                        conversationMap.put("lastMessage", myMessage);
                        conversationMap.put("unreadCount", unreadCount);
                        conversationMap.put("lastMessageTime", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Conversations").document(conversationID).set(conversationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Map<String, Object> messageMap = new HashMap<>();

                                    messageMap.put("createdAt", FieldValue.serverTimestamp());

                                    if (type.equals("image")) {
                                        messageMap.put("type", "image");
                                        messageMap.put("messageImg", myMessage);
                                    }
                                    if (type.equals("text")) {
                                        messageMap.put("type", "text");
                                        messageMap.put("messageText", myMessage);
                                    }
                                    messageMap.put("senderUserName", mAuth.getCurrentUser().getEmail());

                                    firebaseFirestore.collection("Conversations/" + conversationID + "/Messages").document().set(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                unreadCount += 1;
                                            }
                                        }
                                    });

                                    firebaseFirestore.collection("Conversations/" + conversationIDReverse + "/Messages").document().set(messageMap);


                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(ChatActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                }
                            }
                        });


                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                    }

                }
            });


        }
    }
}
