package com.nikhilverma.do_it;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewViewTask extends BottomSheetDialogFragment {

    View view;
    EditText editText;
    Button save;
    ImageView unstar;
    ImageView star;
    Dialog dialog;
    ImageView calendar,clock;
    DatabaseReference reference;
    LottieAnimationView lottieAnimationView;
    FirebaseUser user;
    FirebaseAuth auth;
    Context contextNullSafe;
    LinearLayout layout_time, layout_date;
    static String grup_name;
    boolean x;
    String push;
    static String selectedNoteColor;
    Button btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView txt_date,txt_time;
    LinearLayout layoutTime,layoutDate;


    public static final String TAG = "ActionBottomDialog1";

    public static NewViewTask newInstance(String edt) {
        grup_name = edt;

        return new NewViewTask();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_view_task, container, false);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (contextNullSafe == null) getContextNullSafety();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        calendar = view.findViewById(R.id.view_calendar);
        clock = view.findViewById(R.id.view_clock);
        save = view.findViewById(R.id.view_save);
        txt_date = view.findViewById(R.id.text_date);
        star = view.findViewById(R.id.new_star);
        unstar = view.findViewById(R.id.new_unstar);
        txt_time = view.findViewById(R.id.text_time);
        layout_time = view.findViewById(R.id.linearLayout5);
        layout_date = view.findViewById(R.id.linearLayout4);
        layoutTime = view.findViewById(R.id.layout_time);
        layoutDate = view.findViewById(R.id.layout_date);

        boolean isUpdate = false;

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

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            //String date = bundle.getString("date");
            editText.setText(task);
            Toast.makeText(contextNullSafe, task, Toast.LENGTH_SHORT).show();
            // textView_new.setText(date);
            assert task != null;
            if(task.length()>0)
                save.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }

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
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            });

            editText = view.findViewById(R.id.edit_text_view_task);
            save = view.findViewById(R.id.view_save);
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

        reference = FirebaseDatabase.getInstance().getReference().child("Groups");
        push = reference.push().getKey();


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


        save.setOnClickListener(view -> {

            if (!editText.getText().toString().trim().equals("")) {
                reference.child(grup_name).child("tasks").child(push).child("task").setValue(editText.getText().toString().trim());
                reference.child(grup_name).child("tasks").child(push).child("date").setValue(txt_date.getText().toString().trim());
                reference.child(grup_name).child("tasks").child(push).child("time").setValue(txt_time.getText().toString().trim());
                reference.child(grup_name).child("tasks").child(push).child("pushkey").setValue(push);
                //reference.child(grup_name).child("tasks").child(push).child("CheckBox").setValue("");
                reference.child(grup_name).child("tasks").child(push).child("title").setValue(grup_name);

                if (x){
                    reference.child(grup_name).child("tasks").child(push).child("star").setValue("star");
                }else {
                    reference.child(grup_name).child("tasks").child(push).child("star").setValue("");
                }

            }
            dismiss();
        });

        layoutTime.setOnClickListener(v->{
            layoutTime.setVisibility(View.GONE);
            layout_time.setVisibility(View.VISIBLE);
        });

        layoutDate.setOnClickListener(v->{
            layoutDate.setVisibility(View.GONE);
            layout_date.setVisibility(View.VISIBLE);
        });
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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