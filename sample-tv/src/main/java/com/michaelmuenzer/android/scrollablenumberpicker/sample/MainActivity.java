package com.michaelmuenzer.android.scrollablenumberpicker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablenumberpicker.R;

public class MainActivity extends Activity {
    private ScrollableNumberPicker horizontalNumberPicker;

    private ScrollableNumberPicker verticalNumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalNumberPicker = (ScrollableNumberPicker) findViewById(R.id.number_picker_horizontal);
        verticalNumberPicker = (ScrollableNumberPicker) findViewById(R.id.number_picker_vertical);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return onKey(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return onKey(keyCode, event);
    }

    private boolean onKey(int keyCode, KeyEvent event) {
        if (horizontalNumberPicker.isFocused()) {
            return horizontalNumberPicker.handleKeyEvent(keyCode, event);
        } else if (verticalNumberPicker.isFocused()) {
            return verticalNumberPicker.handleKeyEvent(keyCode, event);
        }

        return false;
    }
}
