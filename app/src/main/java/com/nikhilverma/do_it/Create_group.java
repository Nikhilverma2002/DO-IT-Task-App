package com.nikhilverma.do_it;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.nikhilverma.do_it.adapters.AdapterTask;
import com.nikhilverma.do_it.Models.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Create_group extends Fragment {

    View view;
    Context contextNullSafe;
    EditText editText_create;
    ConstraintLayout layout;
    TextView newTaskSaveButton;
    private String selectedNoteColor;
    DatabaseReference reference_ret,user_ref;
    FirebaseAuth auth;
    FirebaseUser user;
    List<Model> list;
    TextView heading,cancel;
    RecyclerView recyclerView;
    ImageView back, add;
    LottieAnimationView plus;
    private AdapterTask taskAdapter;
    ImageView add_mem;
    static String pushkey, edt;
    //Simple date format
    Date date = Calendar.getInstance().getTime();
    @SuppressLint("SimpleDateFormat")
    DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy , hh:mm aa");


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_group, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        list = new ArrayList<>();
        heading = view.findViewById(R.id.group_heading);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        layout = view.findViewById(R.id.constraint_create);
        selectedNoteColor = "#c9faf5"; //default note color.
        layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
        back = view.findViewById(R.id.back);
        plus = view.findViewById(R.id.frag_plus_task);
        recyclerView = view.findViewById(R.id.recyclerView2);

        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        add_mem = view.findViewById(R.id.add_member);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(linearLayoutManager);
        taskAdapter = new AdapterTask(getContextNullSafety(), list);
        //get_reference_ret();


        back.setOnClickListener(v->{
            assert getFragmentManager() != null;
            getFragmentManager ().beginTransaction ().remove (Create_group.this).commit ();
        });
        open_dialog_box();

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(),	R.color.glass));

        /*add_mem.setOnClickListener(v-> {
            String[] parts = txt_name.split("\\s+");
            String firstname = "" + parts[0];
            String profile_deep_link = "https://doit.android/nikhil/" + user_uid  + "/list" + "/" + firstname;

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, profile_deep_link);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });*/


        //new AsyncCaller().execute();

        get_Data();
        return view;
    }

   /* @SuppressLint("StaticFieldLeak")
    private class AsyncCaller extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            get_Data();
            //this method will be running on UI thread
        }

        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            get_Data();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            get_Data();
            //this method will be running on UI thread
        }

    }*/

    /*private void get_reference_ret() {
        reference_ret.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot keys : snapshot.getChildren()) {
                        pushkey = keys.child("pushkey").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

 private void get_users_data() {
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<list.size();i++){
                    String name = snapshot.child(list.get(i)).child("name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    @SuppressLint("SetTextI18n")
    public void open_dialog_box() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_new_group);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialog.setCancelable(false);
        newTaskSaveButton = dialog.findViewById(R.id.create_list);
        editText_create = dialog.findViewById(R.id.create_group);
        cancel = dialog.findViewById(R.id.cancel_list);
        final ImageView imageColor1 = dialog.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = dialog.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = dialog.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = dialog.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = dialog.findViewById(R.id.imageColor5);

        cancel.setOnClickListener(v->{
            dialog.dismiss();
            assert getFragmentManager() != null;
            getFragmentManager ().beginTransaction ().remove (Create_group.this).commit ();

        });

        editText_create.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText_create.getText().toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setSaveEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                    newTaskSaveButton.setOnClickListener(v -> {
                        heading.setText(editText_create.getText().toString().trim().substring(0,1).toUpperCase() + editText_create.getText().toString().trim().substring(1).toLowerCase());
                        dialog.dismiss();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#c9faf5";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            heading.setTextColor(Color.parseColor("#0D8C80"));
            layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
            //setSubtitleIndicatorColor();
        });

        dialog.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#D3EDDB";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            heading.setTextColor(Color.parseColor("#398B53"));
            layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
            //setSubtitleIndicatorColor();
        });

        dialog.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#79fad8";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            heading.setTextColor(Color.parseColor("#089F7C"));
            layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
           // setSubtitleIndicatorColor();
        });

        dialog.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#FCFADE";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            heading.setTextColor(Color.parseColor("#CDBD11"));
            layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
           // setSubtitleIndicatorColor();
        });

        dialog.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#FCDFD7";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            heading.setTextColor(Color.parseColor("#D23A10"));
            layout.setBackgroundColor(Color.parseColor(selectedNoteColor));
            //setSubtitleIndicatorColor();
        });



        plus.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            NewTask.newInstance(editText_create.getText().toString().trim().substring(0,1).toUpperCase() +
                    editText_create.getText().toString().trim().substring(1).toLowerCase(), selectedNoteColor).show(getFragmentManager(), NewTask.TAG);
        });


    }

    public void exit(){
        getActivity().getSupportFragmentManager().popBackStack();
    }
   /* private void gr_push_data() {

        reference.child(push).child("uid").setValue(user.getUid());
        reference.child(push).child("title").setValue(editText_create.getText().toString());
        reference.child(push).child("color").setValue(selectedNoteColor);
        reference.child(push).child("pending").setValue("0/0 Pending Tasks");
        reference.child(push).child("members").setValue("5 Members");
        reference.child(push).child("pushkey").setValue(push.toString());
        reference.child(push).child("member").child(user.getUid()).setValue(user.getUid());
        today = formatter.format(date);
        reference.child(push).child("date").setValue(today.toString());
        heading.setText(editText_create.getText().toString());
    }*/

    public void get_Data() {

        reference_ret = FirebaseDatabase.getInstance().getReference().child("Groups").child(editText_create.getText().toString().trim()).child("tasks");
        reference_ret.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        list.add(dataSnapshot.child(Objects.requireNonNull(keys.getKey())).getValue(Model.class));
                           /* lottieAnimationView_empty.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);*/
                    }/* else {
                            lottieAnimationView_empty.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                        }*/
                    Collections.reverse(list);
                    AdapterTask adapter = new AdapterTask(getContextNullSafety(), list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    /*public void fetch_data(View view, Context contextNullSafety, String value_task) {
        List<Model> list=new ArrayList<>();

        reference_ret = FirebaseDatabase.getInstance().getReference().child("Groups").child(value_task).child("tasks");
        reference_ret.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot keys : dataSnapshot.getChildren()) {
                    list.add(dataSnapshot.child(Objects.requireNonNull(keys.getKey())).getValue(Model.class));
                }
                Collections.reverse(list);
                RecyclerView recyclerView= view.findViewById(R.id.recyclerView2);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContextNullSafety());
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setLayoutManager(linearLayoutManager);

                AdapterTask adapter = new AdapterTask(contextNullSafety, list);
                adapter.notifyDataSetChanged();

                recyclerView.setAdapter(adapter);
                Log.e("entryrr","ooo");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }*/

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        //if (requireContext() != null) return requireContext();
//        if (requireActivity() != null) return requireActivity();
        //if (requireView() != null && requireView().getContext() != null)
           // return requireView().getContext();

        return null;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
}
