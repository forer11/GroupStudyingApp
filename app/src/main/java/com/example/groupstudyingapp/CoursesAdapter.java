package com.example.groupstudyingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    private ItemClickListener mClickListener;
    private ArrayList<String> coursesIds;
    private FireStoreHandler fireStoreHandler;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            nameTextView = itemView.findViewById(R.id.course_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }


    public CoursesAdapter(FireStoreHandler fsh) {
        fireStoreHandler = fsh;
        coursesIds = fsh.getCoursesIds();
    }

    @Override
    public CoursesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.course_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CoursesAdapter.ViewHolder viewHolder, int position) {
        String courseId = coursesIds.get(position);
        final Course course = fireStoreHandler.getCourseById(courseId);

        TextView textView = viewHolder.nameTextView;
        textView.setText(course.getName());
    }


    @Override
    public int getItemCount() {
        return coursesIds.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


