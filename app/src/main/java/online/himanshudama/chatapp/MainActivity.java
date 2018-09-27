package online.himanshudama.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar myToolBar;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserID;

    private UsersFragment usersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null) {

            myToolBar = findViewById(R.id.myToolBar);
            setSupportActionBar(myToolBar);
            // FRAGMENTS
            usersFragment = new UsersFragment();
            initializeFragment();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        }

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logOutButton:
                logOut();
                return true;

            case R.id.profileButton:

                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);

                return true;


            default:
                return false;


        }

    }

    private void logOut() {


        mAuth.signOut();
        sendToLogin();
    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.mainFrame, usersFragment);

        fragmentTransaction.commit();

    }

}
