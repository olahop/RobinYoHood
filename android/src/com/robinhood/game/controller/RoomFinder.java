package com.robinhood.game.controller;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robinhood.game.model.Player;

import java.util.ArrayList;

public class RoomFinder {

    //vil være en liste over rooms hvor game ikke har startet
    private ArrayList<String> rooms = new ArrayList<>();

    private DatabaseReference mDatabase;

    private String roomId;



    public RoomFinder(final Player player) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");

        System.out.println(rooms);
        System.out.println(mDatabase);


        //sjekk om det finnes et rom med en spiller - hvis ikke, lag nytt rom


        //hente øverste room
        //hvis room.players < 2 legg til player her
        //ellers lag nytt rom


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    final String roomId = dataSnapshot.getKey();
                    mDatabase.child(roomId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            addPlayer(player);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e) {
                    Log.e("Error:", e.toString());
                }
                System.out.println(rooms);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addPlayer(Player player){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomId).child("players");
        mDatabase.setValue(player);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            int counter = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Player player = snapshot.getValue(Player.class);
                    System.out.println(player.getName());
                    counter += 1;
                    if(counter == 2){
                        //startgame
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public ArrayList<String> getRooms() {
        return rooms;
    }
}