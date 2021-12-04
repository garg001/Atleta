package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class SwipeActivity extends AppCompatActivity {

    private static final String TAG ="SwipeActivity" ;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private Bundle extras;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,userRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean isLoading=false;
    private String key=null;
    private Query query;
    private ProgressBar simpleProgressBar;
    private String swipe,swipe2,swipe3;
    private ImageView profileButton,chatButton;
    private ItemModel user4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        profileButton=findViewById(R.id.profileButton);
        chatButton=findViewById(R.id.chatButton);

        profileButton.setOnClickListener(v->{
            startActivity(new Intent(SwipeActivity.this,UserProfileActivity.class));
        });
        chatButton.setOnClickListener(v->{
            startActivity(new Intent(SwipeActivity.this,ChatActivity.class));
        });

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        simpleProgressBar = findViewById(R.id.progressBar4);
        mDatabase=  database.getReference();
        userRef= mDatabase.child("users");

        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        manager=new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG,"onCardDragging: d="+ direction.name() + " ratio=" +ratio);
                if(direction.name()=="Bottom" && ratio>=0.6) {
                    Intent intent = new Intent(SwipeActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                }
                if(direction.name()=="Top" && ratio>=0.43) {
                    Intent intent1 = new Intent(SwipeActivity.this, BasicUserProfileActivity.class);
                     user4=adapter.getItems().get(manager.getTopPosition());
                    intent1.putExtra("basicUser",user4);
                    startActivity(intent1);
                }
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG,"onCardSwiped: p="+ manager.getTopPosition() + " d="+ direction );
                ItemModel user3=adapter.getItems().get(manager.getTopPosition()-1);
                swipe=user.getUid()+user3.getuID();
                swipe2=user3.getuID()+user.getUid();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.child("swipes").child(swipe2).exists()){
                            if(direction == Direction.Right){
                                mDatabase.child("swipes").child(swipe).setValue("Right");
                            }
                        }
                        else{
                            if(direction == Direction.Right){
                                if(snapshot.child("swipes").child(swipe2).getValue().toString().equals("Right")){
                                    Toast.makeText(SwipeActivity.this,"It's a Match!",Toast.LENGTH_SHORT).show();
                                    mDatabase.child("matches").child(swipe).setValue("Match");
                                    String chatKey="1";
                                    if (snapshot.hasChild("chat")) {
                                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                                    }
                                    mDatabase.child("chat").child(chatKey).child("user_1").setValue(user.getUid());
                                    mDatabase.child("chat").child(chatKey).child("user_2").setValue(user3.getuID());
                                }
                                    mDatabase.child("swipes").child(swipe).setValue("Right");
                            }
                        }
                        if(direction == Direction.Left){
                            mDatabase.child("swipes").child(swipe).setValue("Left");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SwipeActivity.this , "Error "+error, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"swipe error: "+error);
                    }
                });



                //paginating
                if(adapter.getItemCount() < manager.getTopPosition() + 1){
                    if(!isLoading){
                        isLoading=true;
                        addList();
                    }
                }


            }

            @Override
            public void onCardRewound() {
                Log.d(TAG,"onCardRewound: "+ manager.getTopPosition() );

            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG,"onCardCancelled: "+ manager.getTopPosition() );
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: " + position + ", name: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(this);
        addList();
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void addList() {
        simpleProgressBar.setVisibility(View.VISIBLE);
        if(key==null){
            query=userRef.orderByKey().limitToFirst(8);
        }
        else{
            query=userRef.orderByKey().startAfter(key).limitToFirst(8);
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ItemModel> items =new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()){
                    User user2=data.getValue(User.class);
                    swipe3=user.getUid()+user2.getuID();

                    if(!(user2.getuID().equals(user.getUid()))){
                        mDatabase.child("swipes").child(swipe3).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    ItemModel itemModel=new ItemModel(user2.getDpURL(),user2.getUserName(),user2.getAge(),user2.getLocation(),user2.getuID());
                                    items.add(itemModel);
                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SwipeActivity.this , "Error "+error, Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"addList error: "+error);
                            }
                        });
                    }
                    key=data.getKey();
                }
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
                isLoading=false;
                simpleProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SwipeActivity.this , "Error "+error, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"SwipeActivity error: "+error);
                simpleProgressBar.setVisibility(View.INVISIBLE);
            }

        });

    }
}