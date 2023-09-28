package com.nikhilverma.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhilverma.do_it.adapters.AdapterGroup;
import com.nikhilverma.do_it.Models.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class GroupTask extends AppCompatActivity {

    DatabaseReference reference,reference_name;
    RecyclerView rv;
    FirebaseAuth auth;
    FirebaseUser user;
    List val_list;
    String param ,uid;
    ConstraintLayout layout;
    TextView dialog_txt;
    private AdapterGroup taskAdapter;
    LottieAnimationView plus;
    List<Model> list;
    private String selectedNoteColor;
    LottieAnimationView lottieAnimationView_empty;
    TextView textView;
    Uri deep_link_uri;
    private Dialog dialog;
    int check_key_one=0,check_key_two=0,check_key_three=0;
    TextView textView_name;
    SmoothBottomBar bottomBar;
    String key_pin1,key_pin2,key_pin3;
    ArrayList<Model> pin_list_item;
    TextView deny, accept;

    ArrayList<String> list_ret = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_task);

        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        rv = findViewById(R.id.recyclerView);
        plus = findViewById(R.id.animation_plus_task_gr);
        layout = findViewById(R.id.layout_dialog);
        lottieAnimationView_empty = findViewById(R.id.animation_empty_gr);
        textView = findViewById(R.id.text_lottie_gr);
        textView_name = findViewById(R.id.textView_name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setItemViewCacheSize(20);
        rv.setLayoutManager(linearLayoutManager);
        taskAdapter = new AdapterGroup(GroupTask.this, list);
        selectedNoteColor = "#333333"; //default note color.
        val_list = new ArrayList();
        pin_list_item=new ArrayList<>();

        deep_link_uri = getIntent().getData();//deep link value
        // checking if the uri is null or not.
        open_frag();
        Toast.makeText(GroupTask.this, String.valueOf(deep_link_uri), Toast.LENGTH_SHORT).show();
        //goToFragment();
        shared_pref();
        //white upper
        Window window = GroupTask.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(GroupTask.this, R.color.bg_color_gr));
        reference_name = FirebaseDatabase.getInstance().getReference().child("users");


        goToFragment();
        plus.setOnClickListener(v -> {
            /*GroupTask.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.group_task_layout, new Create_group())
                    .commit();
*/
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.group_task_layout, new Create_group())
                    .addToBackStack(null)
                    .commit();
        });


        new Handler(Looper.myLooper()).postDelayed(this::freeMemory,2500);
        key_pin1=GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_1", Context.MODE_PRIVATE)
                .getString("pin_1_announcement","");
        key_pin2=GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE)
                .getString("pin_2_announcement","");
        key_pin3=GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE)
                .getString("pin_3_announcement","");

        reference_name.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    if(name!=null) {
                        String[] parts = name.split("\\s+");
                        String firstname = "Welcome " + parts[0];
                        textView_name.setText(firstname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        get_Data();

        bottomBar = findViewById(R.id.bottomBar);

        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(0);

        bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            if (i == 1) {
                bottomBar.setItemActiveIndex(0);
                Intent intent = new Intent(GroupTask.this,MainActivity.class);
                startActivity(intent);
            }
            return false;
        });
    }

    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void open_frag(){
        if(deep_link_uri!=null){
            List<String> parameters = deep_link_uri.getPathSegments();

        }
    }
    private void goToFragment() {
        if (deep_link_uri != null) {
            open_dialog_box();
            // if the uri is not null then we are getting the
            // path segments and storing it in list.
            List<String> parameters = deep_link_uri.getPathSegments();
            // after that we are extracting string from that parameters.
            if (parameters != null) {
                param = parameters.get(0);
                uid = parameters.get(1);
                // on below line we are setting
                // that string to our text view
                // which we got as params.
                        /*Bundle args = new Bundle();
                        args.putString("deep_link_value", param);
                        args.putString("deep_link_uid_value", uid);
                        fragment.setArguments(args);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, fragment, "mainFrag").commit();*/
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void open_dialog_box() {
        Dialog dialog = new Dialog(GroupTask.this);
        dialog.setContentView(R.layout.dialog_friend);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialog.setCancelable(false);
        deny = dialog.findViewById(R.id.deny);
        accept = dialog.findViewById(R.id.accept);
        dialog_txt = dialog.findViewById(R.id.dialog_txt);

        List<String> parameters = deep_link_uri.getPathSegments();
        // after that we are extracting string from that parameters.
        if (parameters != null) {
            param = parameters.get(0);
            uid = parameters.get(1);

            dialog_txt.setText("Are you sure you want to join " + param + " group?");

            deny.setOnClickListener(v -> {
                dialog.dismiss();
            });

            accept.setOnClickListener(v -> {
                Toast.makeText(GroupTask.this, "You are added to the " + param + " group", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(param);
                reference.child("member").child(user.getUid()).setValue(user.getUid());
                dialog.dismiss();
            /*Bundle args = new Bundle();
            args.putString("deep_link_value", param);
            args.putString("deep_link_uid_value", uid);*/
            /*ViewTask fragment = new ViewTask();
           // fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.group_task_layout, fragment, "GroupTask").commit();*/

            });
        }
    }


        private void get_Data() {
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        if (dataSnapshot.child(Objects.requireNonNull(keys.getKey())).child("member").child(user.getUid()).exists()) {
                            list.add(dataSnapshot.child(Objects.requireNonNull(keys.getKey())).getValue(Model.class));
                            lottieAnimationView_empty.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            //pin_list_item.clear();

                            /*if(!key_pin1.equals("")){
                                for(Model object1:list) {
                                    if(object1.getTitle().equals(key_pin1)){
                                        pin_list_item.add(0,object1);
                                        check_key_one=1;
                                        break;
                                    }
                                }
                                if(check_key_one==0){
                                    GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE).edit()
                                            .putString("pin_1_announcement","").apply();
                                }
                            }
                            if(!key_pin2.equals("")){
                                for(Model object1:list) {
                                    if(object1.getTitle().equals(key_pin2)){
                                        pin_list_item.add(0,object1);
                                        check_key_two=1;
                                        break;
                                    }
                                }
                                if(check_key_two==0){
                                    GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE).edit()
                                            .putString("pin_2_announcement","").apply();
                                }
                            }
                            if(!key_pin3.equals("")){
                                for(Model object1:list) {
                                    if(object1.getTitle().equals(key_pin3)){
                                        pin_list_item.add(0,object1);
                                        check_key_three=1;
                                        break;
                                    }
                                }
                                if(check_key_three==0){
                                    GroupTask.this.getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE).edit()
                                            .putString("pin_3_announcement","").apply();
                                }
                            }*/

                        } /*else {
                            lottieAnimationView_empty.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                        }*/

                        //Collections.reverse(list);
                        AdapterGroup adapter = new AdapterGroup(GroupTask.this, list);
                        adapter.notifyDataSetChanged();
                        rv.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void shared_pref() {
        //shared preference
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getSharedPreferences("testapp", MODE_PRIVATE);
        editor = pref.edit();
        if (pref.contains("register")) {
            String getStatus = pref.getString("register", "nil");
            if (getStatus.equals("true")) {

            } else {
                //first time
                editor.putString("register", "true");
                editor.apply();
                MotionToast.Companion.darkColorToast(GroupTask.this,
                        "Hey! Welcome To DO IT",
                        "Thank You For Downloading" + "\uD83D\uDE4F",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(GroupTask.this, R.font.quicksand_medium));
                ///  show registration page again
            }
        } else { //first time
            editor = pref.edit();
            editor.putString("register", "true");
            editor.apply();
            MotionToast.Companion.darkColorToast(GroupTask.this,
                    "Hey! Welcome To DO IT",
                    "Thank You For Downloading" + "\uD83D\uDE4F",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(GroupTask.this, R.font.quicksand_medium));
            ///  show registration page again
        }
    }
}