package com.michaelmuenzer.android.scrollablenumberpicker.sample;

import android.os.Bundle;
import android.widget.Toast;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ScrollableNumberPicker verticalNumberPicker = findViewById(R.id.number_picker_vertical);
        verticalNumberPicker.setListener(new ScrollableNumberPickerListener() {
            @Override
            public void onNumberPicked(int value) {
                if(value == verticalNumberPicker.getMaxValue()) {
                    Toast.makeText(MainActivity.this, getString(R.string.msg_toast_max_value), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
