package com.sck.common.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sck.RadiationEmulator.Model.EmulatedMeasurement;
import com.sck.RadiationEmulator.R;

import java.util.List;

/**
 * Custom ArrayAdapter to  display a list of EmulatedMeasurements in a listview
 *
 * @see EmulatedMeasurement
 * @see ArrayAdapter
 */
public class EmulatedMeasurementAdapter extends ArrayAdapter<EmulatedMeasurement> implements View.OnClickListener {
    private Context mContext;
    private List<EmulatedMeasurement> dataSet;
    private int lastPosition = -1;

    public EmulatedMeasurementAdapter(List<EmulatedMeasurement> data, Context context) {
        super(context, R.layout.measure_list_item, data);
        dataSet = data;
        mContext = context;
    }

    /**
     * When clicking on an element in the Listview, display the EmulatedMeasurement.toString
     * in a snackbar
     *
     * @see EmulatedMeasurement
     */
    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        EmulatedMeasurement em = getItem(position);


        Snackbar.make(view, "Clicked on " + em.toString(), Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            LayoutInflater vi = LayoutInflater.from(mContext);
            result = vi.inflate(R.layout.measure_list_item, null);
        }
        EmulatedMeasurement em = getItem(position);
        if (em != null) {
            TextView toSet = result.findViewById(R.id.toSet);
            toSet.setText(em.toString());
        }
        return result;
    }


}
