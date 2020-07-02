package com.example.groupstudyingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReportDialogFragment extends DialogFragment {
    int pos = 0; // default position

    public interface SingleChoiceListener{
        void onPositiveButtonClicked(String [] list, int position);
        void onNegativeButtonClicked();
    }

    SingleChoiceListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (SingleChoiceListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] choiceList = getActivity().getResources().getStringArray(R.array.report_choice_items);
        builder.setTitle("What is the reason for reporting?").setSingleChoiceItems(choiceList, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pos = which;
            }
        }).setPositiveButton("report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositiveButtonClicked(choiceList, pos);

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegativeButtonClicked();
            }
        });

        return builder.create();
    }
}
