package online.himanshudama.chatapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextView myName;
    private CircleImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        myName = findViewById(R.id.myName);
        myImageView = findViewById(R.id.myImageView);
        if (mAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (task.getResult().exists()) {

                            String name = task.getResult().getString("firstName") + " " + task.getResult().getString("lastName");
                            String image = task.getResult().getString("image");

                            myName.setText(name);

                            Glide.with(ProfileActivity.this).load(image).into(myImageView);


                        }

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
