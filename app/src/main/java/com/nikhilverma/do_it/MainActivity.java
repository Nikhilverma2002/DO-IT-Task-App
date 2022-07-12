package com.nikhilverma.do_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikhilverma.do_it.Models.ToDoModel;
import com.nikhilverma.do_it.Utils.DatabaseHandler;
import com.nikhilverma.do_it.adapters.ToDoAdapter;

import java.util.Collections;
import java.util.List;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    SmoothBottomBar bottomBar;

    private DatabaseHandler db;
    LottieAnimationView fab;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    EditText inputSearch;
    private List<ToDoModel> taskList;
    LottieAnimationView lottieAnimationView;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(1);
        bottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            if (i == 0) {
                finish();
            }
            return false;
        });

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);
        lottieAnimationView = findViewById(R.id.animation_empty);
        textView = findViewById(R.id.text_lottie);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.animation_plus_task);
        inputSearch = findViewById(R.id.inputSearch_my_task);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        if (taskList.size() != 0){
            lottieAnimationView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        //search
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tasksAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputSearch.getText().toString().equals("")) {
                    tasksAdapter.setTasks(taskList);
                }
                else if(taskList.size() != 0){
                    tasksAdapter.searchNotes(s.toString());
                }
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}