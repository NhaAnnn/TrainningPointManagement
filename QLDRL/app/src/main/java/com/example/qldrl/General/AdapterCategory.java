package com.example.qldrl.General;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.qldrl.R;


import java.util.List;

public class AdapterCategory extends ArrayAdapter<Category> {


    public AdapterCategory(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_selected, parent, false);
        }

        TextView txtSelected = convertView.findViewById(R.id.txtVMistake);
        if (txtSelected != null) {
            Category category = this.getItem(position);
            if (category != null) {
                txtSelected.setText(category.getNameCategory());
            }
        }

        return convertView;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_category, parent, false);
        }

        TextView txtCategory = convertView.findViewById(R.id.txtCategory);
        if (txtCategory != null) {
            Category category = this.getItem(position);
            if (category != null) {
                txtCategory.setText(category.getNameCategory());
            }
        }

        return convertView;
    }


}
