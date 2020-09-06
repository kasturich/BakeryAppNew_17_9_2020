package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.CategoryDatabaseDetails;
import com.mi5.bakeryappnew.model.CategoryDetails;

import java.util.List;

/**
 * Created by User on 16-01-2018.
 */

public class SimpleSpinnerArrayAdapter extends BaseAdapter {

    Context context;
    CategoryDetails categoryDatabaseDetails;
    List<CategoryDetails> CategoryDatabaseDetailsList;
    LayoutInflater inflter;

    public SimpleSpinnerArrayAdapter(Context applicationContext,
                                     List<CategoryDetails> CategoryDatabaseDetailsList) {
        this.context = applicationContext;
        this.CategoryDatabaseDetailsList = CategoryDatabaseDetailsList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {

        System.out.println("in adapter of simple spinner = " + CategoryDatabaseDetailsList.size());
        return CategoryDatabaseDetailsList.size();
    }

    @Override
    public Object getItem(int i) {
        return CategoryDatabaseDetailsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = inflter.inflate(R.layout.custom_simple_spinner, null);

        TextView names = (TextView) view.findViewById(R.id.simple_spinner_text);

        categoryDatabaseDetails = (CategoryDetails) getItem(i);

        if(categoryDatabaseDetails.getMsg().equals("0"))
        {
            names.setText("No category is available");
        }
        else {
            names.setText(categoryDatabaseDetails.getCategory());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    static class ViewHolder {

        TextView txtSpinner ;

    }
}
