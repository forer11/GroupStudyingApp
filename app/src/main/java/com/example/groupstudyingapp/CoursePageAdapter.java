package com.example.groupstudyingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CoursePageAdapter extends RecyclerView.Adapter<CoursePageAdapter.ViewHolder>
        implements Filterable {

    private List<Question> questions;
    private List<Question> allQuestions;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    CoursePageAdapter(Context context, List<Question> questions) {
        this.mInflater = LayoutInflater.from(context);
        this.questions = questions;
        this.context = context;
        this.allQuestions = new ArrayList<>(this.questions);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.question_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Question question = questions.get(position);
        holder.questionName.setText(question.getTitle());
        holder.rateText.setText(String.format("%.1f", question.getRating()));
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDeleteDialog(position);
//                questions.remove(position);
//                notifyDataSetChanged();
            }
        });
    }

    private void callDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("Are you sure you want to delete?")
                .setNegativeButton("cancel",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing and return to activity
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        questions.remove(position);
                        notifyDataSetChanged();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // total number of questions
    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public Filter getFilter() {
        return questionsFilter;
    }

    private Filter questionsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Question> filteredQuestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredQuestions.addAll(allQuestions);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Question question : allQuestions) {
                    if (question.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredQuestions.add(question);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredQuestions;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            questions.clear();
            questions.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView questionName;
        TextView rateText;
        ImageView removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            questionName = itemView.findViewById(R.id.questionName);
            rateText = itemView.findViewById(R.id.rateText);
            removeButton = itemView.findViewById(R.id.removeQuestionButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Question getItem(int id) {
        return questions.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}