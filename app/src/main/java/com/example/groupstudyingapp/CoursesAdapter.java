package com.example.groupstudyingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> implements Filterable {

    private ItemClickListener mClickListener;
    private ArrayList<String> coursesIds;
    private ArrayList<Course> coursesList;
    private ArrayList<Course> coursesListFull;
    private FireStoreHandler fireStoreHandler;
    private Context context;

    @Override
    public Filter getFilter() {
        return courseFilter;
    }

    private Filter courseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Course> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(coursesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Course course : coursesListFull) {
                    if (course.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(course);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            coursesList.clear();
            coursesList.addAll((ArrayList) results.values);
            MainActivity context = (MainActivity) CoursesAdapter.this.context;
            context.toggleEmptySearchResults();
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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


    public CoursesAdapter(FireStoreHandler fsh, ArrayList<Course> coursesList, Context context) {
        fireStoreHandler = fsh;
        coursesIds = fsh.getCoursesIds();
        this.coursesList = coursesList;
        updatedCoursesList();
        coursesListFull = new ArrayList<>(coursesList);
        this.context = context;
    }

    private void updatedCoursesList() {
        for (String id : coursesIds) {
            Course course = fireStoreHandler.getCourseById(id);
            coursesList.add(course);
        }
        sortCoursesAlphabetically(coursesList);
    }

    private void sortCoursesAlphabetically(ArrayList<Course> coursesArrayList) {
        Collections.sort(coursesArrayList, new Comparator<Course>() {
            public int compare(Course c1, Course c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
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
        final Course course = coursesList.get(position);

        TextView textView = viewHolder.nameTextView;
        textView.setText(course.getName());
    }


    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


