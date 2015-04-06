package com.mycompany.listviewdemo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by VIanoshchuk on 10.03.2015.
 */
public class CustomCursorAdapter extends CursorAdapter {
    public CustomCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.rowlayout, parent, false);
        return retView; //LayoutInflater.from(context).inflate(R.layout.subj_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvSerNo = (TextView) view.findViewById(R.id.serNo);
        TextView tvName = (TextView) view.findViewById(R.id.name);
        ImageView ivImage = (ImageView) view.findViewById(R.id.icon);

        // Extract properties from cursor
        int SerNo = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        if (SerNo % 2 == 1)
            ivImage.setImageResource(R.mipmap.ic_d_m);
        else
            ivImage.setImageResource(R.mipmap.ic_d_w);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        // Populate fields with extracted properties
        tvSerNo.setText(String.valueOf(SerNo));
        tvName.setText(String.valueOf(name));
    }

}
