package com.nikhilverma.do_it;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhilverma.do_it.Models.Model;
import com.nikhilverma.do_it.adapters.AdapterTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NewTask extends BottomSheetDialogFragment {

    View view;
    EditText editText;
    Button save;
    ImageView unstar;
    ImageView star;
    Dialog dialog;
    DatabaseReference reference ;
    LottieAnimationView lottieAnimationView;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference_ret;
    boolean x;
    Context contextNullSafe;
    LinearLayout layout_time,layout_date;
    static String grup_name;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView txt_date,txt_time;
    LinearLayout layoutTime,layoutDate;
    String push;
    List<Model> list=new ArrayList<>();
    static String selectedNoteColor;


    public static final String TAG = "ActionBottomDialog1";

    public static NewTask newInstance(String edt, String color) {
        grup_name=edt;
        selectedNoteColor = color;
       // Log.e("deep_push",grup_name);
        return new NewTask();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (contextNullSafe == null) getContextNullSafety();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        String user_uid = user.getUid();
        String txt_name = user.getDisplayName();
        editText = view.findViewById(R.id.edit_text_task);
        save = view.findViewById(R.id.save);
        star = view.findViewById(R.id.new_star);
        unstar = view.findViewById(R.id.new_unstar);
        txt_time = view.findViewById(R.id.text_time);
        layout_time = view.findViewById(R.id.linearLayout5);
        layout_date = view.findViewById(R.id.linearLayout4);
        layoutTime = view.findViewById(R.id.layout_time);
        txt_date = view.findViewById(R.id.text_date);
        layoutDate = view.findViewById(R.id.layout_date);
        reference = FirebaseDatabase.getInstance().getReference().child("Groups");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    save.setEnabled(false);
                    save.setTextColor(Color.GRAY);
                } else {
                    save.setEnabled(true);
                    save.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        layout_date.setOnClickListener(v -> {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getContextNullSafety(),
                    new DatePickerDialog.OnDateSetListener() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txt_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            if (!txt_date.getText().toString().equals("")){
                                layout_date.setVisibility(View.GONE);
                                layoutDate.setVisibility(View.VISIBLE);
                            }

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });


        layout_time.setOnClickListener(v->{

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContextNullSafety(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txt_time.setText(hourOfDay + ":" + minute);
                            if (!txt_time.getText().toString().equals("")){
                                layout_time.setVisibility(View.GONE);
                                layoutTime.setVisibility(View.VISIBLE);
                            }
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        });

        unstar.setOnClickListener(v->{
            star.setVisibility(View.VISIBLE);
            unstar.setVisibility(View.GONE);
            x=true;

        });
        star.setOnClickListener(v->{
            unstar.setVisibility(View.VISIBLE);
            star.setVisibility(View.GONE);
            x=false;
        });


        save.setOnClickListener(v->{
            reference = FirebaseDatabase.getInstance().getReference().child("Groups");
            push = reference.push().getKey();
            if (!editText.getText().toString().trim().equals("")){
                gr_push_data();
            }
            reference.child(grup_name).child("tasks").child(push).child("task").setValue(editText.getText().toString().trim());
            reference.child(grup_name).child("tasks").child(push).child("date").setValue(txt_date.getText().toString().trim());
            reference.child(grup_name).child("tasks").child(push).child("time").setValue(txt_time.getText().toString().trim());
            reference.child(grup_name).child("tasks").child(push).child("pushkey").setValue(push);
            reference.child(grup_name).child("tasks").child(push).child("title").setValue(grup_name);
           // reference.child(grup_name).child("tasks").child(push).child("CheckBox").setValue("");
            if (x){
                reference.child(grup_name).child("tasks").child(push).child("star").setValue("star");
            }else {
                reference.child(grup_name).child("tasks").child(push).child("star").setValue("");
            }
           /* View view = inflater.inflate(R.layout.fragment_create_group, container, false);
            Create_group create_group=new Create_group();
            create_group.fetch_data(view,getContextNullSafety(),grup_name.trim());*/
           /* Intent intent = new Intent(contextNullSafe,GroupTask.class);
            startActivity(intent);*/
            //TODO: YHA PAAS KO DEKHNA HAI THODA
           /* Create_group create_group = new Create_group();
            create_group.exit();*/
            dismiss();
        });
        return view;
    }

    private void gr_push_data() {

        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mem = snapshot.child("members").getChildrenCount();
                Log.e("member", String.valueOf(mem));
                //reference.child(push).child("members").setValue(String.valueOf(mem));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        reference.child(grup_name).child("uid").setValue(user.getUid());
        reference.child(grup_name).child("title").setValue(grup_name);
        reference.child(grup_name).child("color").setValue(selectedNoteColor);
        reference.child(grup_name).child("pending").setValue("0/0 Pending Tasks");
        reference.child(grup_name).child("complete").setValue("0");
        reference.child(grup_name).child("total").setValue("1");
        reference.child(grup_name).child("member").child(user.getUid()).setValue(user.getUid());
        reference.child(grup_name).child("tasks").child(push).child("task").setValue(editText.getText().toString().trim());
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String today = formatter.format(date);
        reference.child(grup_name).child("date").setValue(today);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /*boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            editText.setText(task);
            assert task != null;
            if(task.length()>0)
                save.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }*/
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }

   /* public void open_dialog_box() {
        dialog = new Dialog(getContextNullSafety());
        dialog.setContentView(R.layout.dialog_date_time);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setCancelable(true);
        layout_time = dialog.findViewById(R.id.layout_time);
        layout_date = dialog.findViewById(R.id.layout_date);
    }*/

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

        // It is used to set the algorithm names to our array list.
    }
}