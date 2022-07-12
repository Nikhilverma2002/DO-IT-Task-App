package com.nikhilverma.do_it;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ViewTask extends Fragment {

    View view;
    RecyclerView recyclerView;
    private AdapterTask taskAdapter;
    Context contextNullSafe;
    List<Model> list;
    AdapterTask grp;
    String title_args, color_args, date_args;
    DatabaseReference reference_ret, reference;
    ConstraintLayout constraintLayout;
    TextView date_text, title_text, textView;
    LottieAnimationView lottieAnimationView, lottieAnimationView_empty;
    ImageView back, add_mem;
    ImageView delete_img;
    Dialog dialog;
    TextView delete,cancel;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView exit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_task, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_view);
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(linearLayoutManager);
        taskAdapter = new AdapterTask(getContextNullSafety(), list);
        constraintLayout = view.findViewById(R.id.constraint_view);
        date_text = view.findViewById(R.id.group_date_view);
        title_text = view.findViewById(R.id.group_heading);
        lottieAnimationView = view.findViewById(R.id.frag_plus_view_task);
        back = view.findViewById(R.id.back);
        add_mem = view.findViewById(R.id.add_member);
        grp = new AdapterTask(contextNullSafe, list);
        textView = view.findViewById(R.id.text_lottie);
        lottieAnimationView_empty = view.findViewById(R.id.animation_empty);
        delete_img = view.findViewById(R.id.delete);
        exit = view.findViewById(R.id.exit_group);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //above colour


        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelperGroup(grp));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //setOnback
        back.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(ViewTask.this).commit();
        });


        exit.setOnClickListener(v->{

        });

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(ViewTask.this).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);



        try {
            assert getArguments() != null;
            title_args = getArguments().getString("groupName");
            color_args = getArguments().getString("colorName");
            date_args = getArguments().getString("dateName");

        } catch (Exception e) {
            e.printStackTrace();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(title_args);


        constraintLayout.setBackgroundColor(Color.parseColor(color_args));

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("color").exists()) {
                    if (snapshot.child("color").getValue().equals("#FCFADE")) {
                        title_text.setTextColor(Color.parseColor("#CDBD11"));
                        date_text.setTextColor(Color.parseColor("#CDBD11"));
                        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.first));
                    } else if (snapshot.child("color").getValue().equals("#D3EDDB")) {
                        title_text.setTextColor(Color.parseColor("#398B53"));
                        date_text.setTextColor(Color.parseColor("#398B53"));
                        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.second));
                    } else if (snapshot.child("color").getValue().equals("#79fad8")) {
                        title_text.setTextColor(Color.parseColor("#089F7C"));
                        date_text.setTextColor(Color.parseColor("#089F7C"));
                        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.third));
                    } else if (snapshot.child("color").getValue().equals("#c9faf5")) {
                        title_text.setTextColor(Color.parseColor("#0D8C80"));
                        date_text.setTextColor(Color.parseColor("#0D8C80"));
                        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.fourth));
                    } else if (snapshot.child("color").getValue().equals("#FCDFD7")) {
                        title_text.setTextColor(Color.parseColor("#D23A10"));
                        date_text.setTextColor(Color.parseColor("#D23A10"));
                        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.fifth));
                    }
                }
                if (Objects.equals(snapshot.child("uid").getValue(), user.getUid())) {
                    delete_img.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        exit.setOnClickListener(c->{
            exit_dialog();
        });
        delete_img.setOnClickListener(v->{
            delete_dialog();
        });
        title_text.setText(title_args);
        date_text.setText(date_args);
        get_Data();
        lottieAnimationView.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            NewViewTask.newInstance(title_args).show(getFragmentManager(), NewViewTask.TAG);
        });
        return view;
    }

    private void delete_dialog() {
        dialog = new Dialog(contextNullSafe);
        dialog.setContentView(R.layout.layout_delete_note);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        delete = dialog.findViewById(R.id.textExitNote);
        cancel = dialog.findViewById(R.id.textCancelExit);

        delete.setOnClickListener(v -> {
            //finish();
            reference.removeValue();
            getActivity();
            dialog.dismiss();

        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void exit_dialog() {
        dialog = new Dialog(contextNullSafe);
        dialog.setContentView(R.layout.dialog_exitgroup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        delete = dialog.findViewById(R.id.textExitNote);
        cancel = dialog.findViewById(R.id.textCancelExit);

        delete.setOnClickListener(v -> {
            //finish();
            //TODO: remove from group or exit
            reference.child("members").child(user.getUid()).child(user.getUid()).removeValue();
            getActivity();
            dialog.dismiss();

        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void get_Data() {

        reference_ret = FirebaseDatabase.getInstance().getReference().child("Groups").child(title_args).child("tasks");
        reference_ret.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        list.add(dataSnapshot.child(Objects.requireNonNull(keys.getKey())).getValue(Model.class));
                        lottieAnimationView_empty.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                    }
                }
                else {
                    lottieAnimationView_empty.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
                    Collections.reverse(list);
                    AdapterTask adapter = new AdapterTask(getContextNullSafety(), list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
}