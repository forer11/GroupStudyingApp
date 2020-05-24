package com.example.groupstudyingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;

public class CoursePageActivity extends AppCompatActivity {
    private ExpandingList expandingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);
        expandingList = findViewById(R.id.expanding_list_main);
        createItems();

    }

    private void createItems() {
        addItem("2019 MOED A", new String[]{"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7"}, R.color.pink, R.drawable.exam);
        addItem("2019 MOED B", new String[]{"Q1", "Q2", "Q3"}, R.color.blue, R.drawable.exam);
        addItem("2018 MOED A", new String[]{"Q1"}, R.color.purple, R.drawable.exam);
        addItem("2018 MOED B", new String[]{"Q1", "Q2", "Q3"}, R.color.yellow, R.drawable.exam);
        addItem("2017 MOED A", new String[]{}, R.color.orange, R.drawable.exam);
        addItem("2017 MOED B", new String[]{"Q1", "Q2"}, R.color.green, R.drawable.exam);
        addItem("2016 MOED A", new String[]{"Q1", "Q2", "Q3", "Q4", "Q5"}, R.color.blue, R.drawable.exam);
        addItem("2016 MOED B", new String[]{"Q1", "Q2", "Q3"}, R.color.yellow, R.drawable.exam);
    }

    private void showInsertDialog(final MainActivity.OnItemCreated positive) {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(CoursePageActivity.this);
        builder.setView(text);
        builder.setTitle("Add another question");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positive.itemCreated(text.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private void addItem(String title, String[] subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);
        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);

            //We can create items in batch.
            item.createSubItems(subItems.length);
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems[i]);
            }
            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInsertDialog(new MainActivity.OnItemCreated() {
                        @Override
                        public void itemCreated(String title) {
                            View newSubItem = item.createSubItem();
                            configureSubItem(item, newSubItem, title);
                        }
                    });
                }
            });

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandingList.removeItem(item);
                }
            });
        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, String subTitle) {
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.removeSubItem(view);
            }
        });
    }

}
