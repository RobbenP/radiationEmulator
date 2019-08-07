package com.sck.common.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sck.RadiationEmulator.Model.RadiationSource;

import java.util.List;

public class RadiationSourceAdapter extends ArrayAdapter<RadiationSource> {
    private Context mContext;
    private List<RadiationSource> values;

    public RadiationSourceAdapter(List<RadiationSource> values, int textViewResourceId, Context context) {
        super(context, textViewResourceId, values);
        mContext = context;
        this.values = values;

    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public RadiationSource getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setText(values.get(position).getName());
        tv.setTextColor(Color.BLACK);
        return v;
    }
}
