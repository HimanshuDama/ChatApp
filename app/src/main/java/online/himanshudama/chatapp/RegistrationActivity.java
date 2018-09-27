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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText userNameEditText;
    private Button regBtn;
    private ProgressBar regProgress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Uri mainImageURI = null;
    private String userID;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileImageView = findViewById(R.id.regProfileImageView);
        firstNameEditText = findViewById(R.id.regFirstNameEditText);
        lastNameEditText = findViewById(R.id.regLastNameEditText);
        userNameEditText = findViewById(R.id.regUserNameEditText);
        regBtn = findViewById(R.id.regButton);
        regProgress = findViewById(R.id.regProgress);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String firstName = firstNameEditText.getText().toString();
                final String lastName = lastNameEditText.getText().toString();
                final String userName = userNameEditText.getText().toString()+"@domain.com";

                if(mainImageURI==null &&!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) & !TextUtils.isEmpty(userName)){

                    regProgress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(userName, "myPassword").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Intent setupIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(setupIntent);
                                finish();
                                userID = mAuth.getCurrentUser().getUid();
                                try {
                                File newImageFile = new File(mainImageURI.getPath());


                                    compressedImageFile = new Compressor(RegistrationActivity.this)
                                            .setMaxHeight(125)
                                            .setMaxWidth(125)
                                            .setQuality(50)
                                            .compressToBitmap(newImageFile);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegistrationActivity.this, "Image is required", Toast.LENGTH_SHORT).show();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadImage = storageReference.child("profile_images").child(userID + ".jpg").putBytes(thumbData);

                                uploadImage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            Uri imageUri = task.getResult().getDownloadUrl();
                                            Map<String, String> userMap = new HashMap<>();
                                            userMap.put("firstName", firstName);
                                            userMap.put("lastName", lastName);
                                            userMap.put("userName", userName);
                                            userMap.put("image", imageUri.toString());

                                            firebaseFirestore.collection("Users").document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        Toast.makeText(RegistrationActivity.this, "The user info is updated.", Toast.LENGTH_LONG).show();
                                                        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                                        startActivity(mainIntent);
                                                        finish();

                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(RegistrationActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                                    }

                                                    regProgress.setVisibility(View.INVISIBLE);

                                                }
                                            });


                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(RegistrationActivity.this, "(Image upload Failed, try again!) : " + error, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });


                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegistrationActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            regProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(RegistrationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(RegistrationActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        ImagePicker();

                    }

                } else {

                    ImagePicker();

                }

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void ImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(RegistrationActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profileImageView.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
