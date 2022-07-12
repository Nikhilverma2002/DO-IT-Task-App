package com.nikhilverma.do_it.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhilverma.do_it.BuildConfig;
import com.nikhilverma.do_it.GroupTask;
import com.nikhilverma.do_it.Models.Model;
import com.nikhilverma.do_it.R;
import com.nikhilverma.do_it.ViewTask;
import java.util.List;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ViewHolder>  {

    List<Model> list;
    Context context;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference_ret,reference;
    String key;

    public AdapterGroup(GroupTask groupTask, List<Model> list) {
        this.context = groupTask;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_task,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterGroup.ViewHolder holder, int position) {


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference_ret = FirebaseDatabase.getInstance().getReference().child("Groups");

        holder.title.setText(list.get(position).getTitle());
        holder.date.setText(list.get(position).getDate());

        if (list.get(position).getTotal() != null) {
            if (list.get(position).getTotal().equals("1") || list.get(position).getTotal().equals("0")) {
                holder.task.setText(list.get(position).getTotal() + " Task");
            } else {
                holder.task.setText(list.get(position).getTotal() + " Tasks");
            }
        }

        holder.view.setOnClickListener(v -> {
            // Log.e("check1",list.get(position).getTitle());
            ViewTask viewTask = new ViewTask();
            Bundle bundle = new Bundle();
            bundle.putString("groupName", list.get(position).getTitle());
            bundle.putString("colorName", list.get(position).getColor());
            bundle.putString("dateName", list.get(position).getDate());
            //bundle.putString("groupName", list.get(position).getTitle());
            viewTask.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.group_task_layout, viewTask)
                    .commit();
            //Toast.makeText(context,list.get(position).getTitle() , Toast.LENGTH_SHORT).show();
        });

        if (list.get(position).getTitle() != null) {
            reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(list.get(position).getTitle());
            reference.child("member").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() != 1) {
                        holder.members.setText(snapshot.getChildrenCount() + " members");
                    } else {
                        holder.members.setText(snapshot.getChildrenCount() + " member");
                    }
                    // key = snapshot.child("tasks").child(list.get(position).getPushkey()).child("pushkey").getValue(String.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        holder.share.setOnClickListener(v -> {
            /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message );
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));*/

            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Download App");
                char ch = '_';
                String message = "https://doit.android/" + list.get(position).getTitle().replace(' ', ch) + "/" + user.getUid() + "/" +
                        "\n\n\n https://play.google.com/store/apps/details?=" + BuildConfig.APPLICATION_ID + "\n\n";
                intent.putExtra(Intent.EXTRA_TEXT, message);
                context.startActivity(Intent.createChooser(intent, "Share using"));
            } catch (Exception e) {
                Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

        });


        if (list.get(position).getTotal() != null && list.get(position).getComplete() != null) {
            int total_childs = Integer.parseInt(list.get(position).getTotal());
            int count = Integer.parseInt(list.get(position).getComplete());

            if (total_childs != 0) {
                float percentage = (float) ((count * 100) / total_childs);
                int percent = (int) percentage;
                String str_per = percent + "%";
                Log.e("testing_x",str_per);
                holder.text_view_progress.setText(str_per);
                holder.progress_bar.setProgress(percent);
            }
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date,view,text_view_progress,task;
        TextView members;
        ConstraintLayout layout;
        ImageView share;
        ProgressBar progress_bar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_txt);
            date = itemView.findViewById(R.id.date_txt);
            members = itemView.findViewById(R.id.member_txt);
            layout = itemView.findViewById(R.id.card_grp);
            view = itemView.findViewById(R.id.view);
            progress_bar = itemView.findViewById(R.id.progress_bar);
            text_view_progress = itemView.findViewById(R.id.text_view_progress);
            task = itemView.findViewById(R.id.task_txt);
            share = itemView.findViewById(R.id.imageView_share);
        }
    }
}
