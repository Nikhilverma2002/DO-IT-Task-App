package com.nikhilverma.do_it.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nikhilverma.do_it.Models.Model;
import com.nikhilverma.do_it.R;
import com.nikhilverma.do_it.ViewTask;

import java.util.List;



public class AdapterTask extends RecyclerView.Adapter<AdapterTask.ViewHolder> {

    DatabaseReference reference,referencex,reference_grp;
    List<Model> list;
    Context context;
    ViewTask viewTask;

    int x = 0;
    int y = 0;

    public AdapterTask(Context context, List<Model> list){
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public AdapterTask.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gr_task_layout,parent,false);
        return new AdapterTask.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterTask.ViewHolder holder, int position) {

        holder.textView.setText(list.get(position).getTask());

        if (list.get(position).getCheckBox() != null) {
            if (list.get(position).getCheckBox().equals("checkbox")) {
                holder.checkBox.setChecked(true);
                holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.checkBox.setChecked(false);
            }
        }

        if(list.get(position).getTitle() != null) {
            reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(list.get(position).getTitle());

            holder.checkBox.setOnClickListener(v -> {

                if (list.get(position).getCheckBox() == null) {
                    reference.child("tasks").child(list.get(position).getPushkey()).child("CheckBox").setValue("checkbox");
                    holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // reference.child("complete").setValue(x);
                    reference.child("tasks").child(list.get(position).getPushkey()).child("CheckBox").removeValue();
                }
            });

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("color").exists()) {
                        if (snapshot.child("color").getValue().equals("#FCFADE")) {
                            holder.textView.setTextColor(Color.parseColor("#B5A70F"));
                            holder.checkBox.setTextColor(Color.parseColor("#B5A70F"));
                           // holder.checkBox.setBackgroundColor(Color.parseColor("#B5A70F"));
                        }
                        else if (snapshot.child("color").getValue().equals("#D3EDDB")) {
                            holder.textView.setTextColor(Color.parseColor("#2F7244"));
                            holder.checkBox.setTextColor(Color.parseColor("#2F7244"));
                        }
                        else if (snapshot.child("color").getValue().equals("#79fad8")) {
                            holder.textView.setTextColor(Color.parseColor("#06785D"));
                            holder.checkBox.setTextColor(Color.parseColor("#06785D"));
                        }
                        else if (snapshot.child("color").getValue().equals("#c9faf5")) {
                            holder.textView.setTextColor(Color.parseColor("#0A6C62"));
                            holder.checkBox.setTextColor(Color.parseColor("#0A6C62"));
                        }
                        else if (snapshot.child("color").getValue().equals("#FCDFD7")) {
                            holder.textView.setTextColor(Color.parseColor("#91280B"));
                            holder.checkBox.setTextColor(Color.parseColor("#91280B"));
                        }

                        //#FCDFD7
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            reference.child("tasks").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int z = (int) snapshot.getChildrenCount();
                    for (DataSnapshot keys : snapshot.getChildren()) {
                        if (keys.child("CheckBox").exists()) {
                            x++;
                            y = x / z;
                        }
                    }
                    reference.child("complete").setValue(String.valueOf(y));
                    reference.child("total").setValue(String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        if(list.get(position).getStar()!=null) {
            if (!list.get(position).getStar().equals("")) {
                holder.star.setVisibility(View.VISIBLE);
            }
        }
    }

    public Context getContext() {
        return context;
    }


    public void deleteItem(int position) {

        //if (!list.get(position).getTotal().equals("1")) {
        referencex = FirebaseDatabase.getInstance().getReference().child("Groups").child(list.get(position).getTitle());
        referencex.child("tasks").child(list.get(position).getPushkey()).removeValue();
        //referencex.child(list.get(position).getPushkey()).removeValue();
        //Log.e("dsfsf", String.valueOf((list.get(position).getPushkey())));
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView textView;
        ImageView star;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.gr_checkBox);
            textView = itemView.findViewById(R.id.gr_textView);
            star = itemView.findViewById(R.id.star);
        }
    }
}
