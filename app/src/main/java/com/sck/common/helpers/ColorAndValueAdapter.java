package com.sck.common.helpers;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.RadiationEmulator.R;

import java.util.List;

public class ColorAndValueAdapter extends ArrayAdapter<ColorAndValue> {
    private Context mContext;
    private List<ColorAndValue> dataSet;
    private ColorAndValueCallback callback;

    public ColorAndValueAdapter(List<ColorAndValue> data, Context context) {
        super(context, R.layout.color_list_item, data);
        dataSet = data;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            LayoutInflater vi = LayoutInflater.from(mContext);
            result = vi.inflate(R.layout.color_list_item, null);
        }
        ColorAndValue em = getItem(position);
        if (em != null) {
            Button max = result.findViewById(R.id.maxValue);
            max.setText(String.valueOf(em.getValue()));
            max.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Value");


                // Set up the input
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setHint("Old value = " + em.getValue());

                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String newVal = input.getText().toString();
                    if (newVal.equals("")) dialog.cancel();
                    else {
                        callback.changeValue(em, Integer.parseInt(newVal));
                        max.setText(newVal);
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            });

            Button color = result.findViewById(R.id.btnColor);
            color.setBackgroundColor(em.getColor());
            color.setOnClickListener(view -> {
                if (callback != null) {
                    ColorPickerDialogBuilder
                            .with(getContext())
                            .setTitle("Choose color")
                            .initialColor(em.getColor())
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> callback.changeColor(em, selectedColor))
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();
                }

            });

            FloatingActionButton delete = result.findViewById(R.id.delete);
            delete.setOnClickListener(view -> {
                if (callback != null) callback.deletePressed(em);
            });
        }
        return result;
    }

    public void setCallback(ColorAndValueCallback callback) {

        this.callback = callback;
    }

    public interface ColorAndValueCallback {
        void deletePressed(ColorAndValue colorAndValue);

        void changeColor(ColorAndValue colorAndValue, int color);

        void changeValue(ColorAndValue colorAndValue, int value);
    }

}
