package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Note extends AppCompatActivity {

    FloatingActionButton editbtn;
    Button logoutbtn;

    RecyclerView rcv;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> noteAdapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        editbtn = findViewById(R.id.editbtn);
        logoutbtn = findViewById(R.id.logoutbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Createnote.class));
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); // Finish the current activity
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allnotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allnotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position, @NonNull firebasemodel model) {
                ImageView popbtn = noteViewHolder.itemView.findViewById(R.id.menupop);

                int colorcode = getRandomColor();
                noteViewHolder.notecont.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorcode, null));

                noteViewHolder.notetitle.setText(model.getTitle()); // Use model parameter instead of firebasemodel
                noteViewHolder.notecont.setText(model.getContent()); // Use model parameter instead of firebasemodel

                // Get the document ID of the current item
                String docId = getSnapshots().getSnapshot(position).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // open note details
                        Intent i = new Intent(getApplicationContext(), Notedetails.class);
                        i.putExtra("title", model.getTitle());
                        i.putExtra("content", model.getContent());
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });

                popbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu p1 = new PopupMenu(v.getContext(), v);
                        p1.setGravity(Gravity.END);
                        p1.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                // edit code btn from
                                Intent i = new Intent(getApplicationContext(), Editnote.class);
                                i.putExtra("title", model.getTitle());
                                i.putExtra("content", model.getContent());
                                i.putExtra("noteId", docId);
                                v.getContext().startActivity(i);
                                return false;
                            }
                        });
                        p1.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        p1.getMenu().add("Pin").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {

                                // logic for pinning the note
                                Toast.makeText(getApplicationContext(),"Pinned",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                        p1.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        rcv = findViewById(R.id.recyclev);
        rcv.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcv.setLayoutManager(staggeredGridLayoutManager);
        rcv.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.startListening(); // Start listening for Firestore updates
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView notetitle;
        private TextView notecont;
        LinearLayout mnotee;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecont = itemView.findViewById(R.id.notecont);
            mnotee = itemView.findViewById(R.id.catdlinear);
        }
    }
    public int getRandomColor() {
        List<Integer> colorCodes = new ArrayList<>();
//        colorCodes.add(R.color.light_blue);
        colorCodes.add(R.color.dark_blue);
//        colorCodes.add(R.color.light_green);
        colorCodes.add(R.color.dark_green);
//        colorCodes.add(R.color.light_purple);
        colorCodes.add(R.color.dark_purple);
//        colorCodes.add(R.color.light_salmon);
        colorCodes.add(R.color.dark_red);
//        colorCodes.add(R.color.light_coral);
        colorCodes.add(R.color.dark_orange);
//        colorCodes.add(R.color.light_yellow);
        colorCodes.add(R.color.dark_brown);
//        colorCodes.add(R.color.light_gray);
        colorCodes.add(R.color.dark_gray);
//        colorCodes.add(R.color.light_cyan);
        colorCodes.add(R.color.dark_navy);
//        colorCodes.add(R.color.light_pink);
        colorCodes.add(R.color.dark_maroon);
//        colorCodes.add(R.color.light_orchid);
        colorCodes.add(R.color.dark_slate_blue);

        Random random = new Random();
        int index = random.nextInt(colorCodes.size());

        return colorCodes.get(index);
    }


}
