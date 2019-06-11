package com.technion.android.joblin;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aminography.redirectglide.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.CHATS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class MessageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentUserMail;
    Intent intent;
    ImageView profileImage;
    TextView profileName;
    String otherEmail;
    ImageView backButton;
    EditText messageTxt;
    ImageButton sendButton;
    ChatAdapter adapter;
    RecyclerView recyclerViewMessages;
    List<Message> chat;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference chatsCollection = db.collection(CHATS_COLLECTION_NAME);

    private ImageView toProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        currentUserMail = mAuth.getCurrentUser().getEmail();
        intent = getIntent();
        profileName = findViewById(R.id.profileMessageName);
        profileImage = findViewById(R.id.profileMessageImage);
        backButton = findViewById(R.id.profile_back_button);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        otherEmail = intent.getStringExtra("email");
        if(intent.getStringExtra("type").equals("rec"))
            getRecruiter(otherEmail);
        else
            getCandidate(otherEmail);

        messageTxt = findViewById(R.id.messageTxt);
        sendButton = findViewById(R.id.sendMessageBtn);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(currentUserMail,otherEmail,messageTxt.getText().toString(), Timestamp.now());
                sendMessage(message);
            }
        });
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);

        readMessages(currentUserMail,otherEmail);
    }
    void getCandidate(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Candidate mProfile = document.toObject(Candidate.class);
                        profileName.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
                        GlideApp.with(profileImage.getContext()).load(mProfile.getImageUrl()).into(profileImage);
                    }
                }
            }
        });
    }
    void getRecruiter(final String email) {
        DocumentReference docRef = recruitersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recruiter mProfile = document.toObject(Recruiter.class);
                        profileName.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
                        GlideApp.with(profileImage.getContext()).load(mProfile.getImageUrl()).into(profileImage);
                    }
                }
            }
        });
    }

    void sendMessage(Message message) {

        WriteBatch batch = db.batch();

        DocumentReference chatDocumentReference = chatsCollection.document();
        batch.set(chatDocumentReference, message);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                messageTxt.setText("");
            }
        });
    }

    void readMessages(String currentUser, String otherUser) {
        chat = new ArrayList<>();
        chatsCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        chat.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Message message = document.toObject(Message.class);
                            if(((message.getReceiver().equals(currentUser))
                                    && (message.getSender().equals(otherUser)))
                                ||
                              ((message.getReceiver().equals(otherUser))
                                      &&(message.getSender().equals(currentUser))))
                                chat.add(message);
                        }
                        Collections.sort(chat,new Comparator<Message>() {
                            @Override
                            public int compare(Message o1, Message o2) {
                                return o1.getTimestamp().compareTo(o2.getTimestamp());
                            }
                        });
                        adapter = new ChatAdapter(MessageActivity.this,chat);
                        recyclerViewMessages.setAdapter(adapter);
                    }
                });
    }

    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        currentActivityReceiver = new CurrentActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(currentActivityReceiver, CurrentActivityReceiver.CURRENT_ACTIVITY_RECEIVER_FILTER);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(currentActivityReceiver);
        currentActivityReceiver = null;
        super.onPause();
    }
}
