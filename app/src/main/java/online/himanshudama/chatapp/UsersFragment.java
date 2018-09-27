package online.himanshudama.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {


    private RecyclerView usersRecyclerView;
    private List<Users> userList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private UsersRecyclerAdapter usersRecyclerAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        userList = new ArrayList<>();
        usersRecyclerView = view.findViewById(R.id.userRecyclerView);

        firebaseAuth = FirebaseAuth.getInstance();

        usersRecyclerAdapter = new UsersRecyclerAdapter(userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        usersRecyclerView.setAdapter(usersRecyclerAdapter);
        usersRecyclerView.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            Query firstQuery = firebaseFirestore.collection("Users").orderBy("firstName", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Users users = doc.getDocument().toObject(Users.class);


                                userList.add(users);
                                for (int i = 0; i < userList.size(); i++) {
                                    if (firebaseAuth.getCurrentUser().getEmail().equals(userList.get(i).getUserName())) {
                                        userList.remove(i);
                                    }
                                }

                                usersRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                    }

                }

            });

            int totalUsers =userList.size();

            /*Query secondQuery = firebaseFirestore.collection("Conversations/").orderBy("firstName", Query.Direction.ASCENDING);
            secondQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Users users = doc.getDocument().toObject(Users.class);


                                userList.add(users);
                                for (int i = 0; i < userList.size(); i++) {
                                    if (firebaseAuth.getCurrentUser().getEmail().equals(userList.get(i).getUserName())) {
                                        userList.remove(i);
                                    }
                                }

                                usersRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                    }

                }

            });*/



        }


        return view;
    }

}
