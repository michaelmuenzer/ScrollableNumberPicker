package com.michaelmuenzer.android.scrollablennumberpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollableNumberPicker extends LinearLayout {
    private final static int MIN_UPDATE_INTERVAL_MS = 50;

    @DrawableRes
    private int downIcon = R.drawable.ic_arrow_down;

    @DrawableRes
    private int upIcon = R.drawable.ic_arrow_up;

    @DrawableRes
    private int leftIcon = R.drawable.ic_arrow_left;

    @DrawableRes
    private int rightIcon = R.drawable.ic_arrow_right;

    private int mValue;
    private int mMaxValue;
    private int mMinValue;
    private int mStepSize;
    private int mUpdateIntervalMillis;
    private int mOrientation;
    private ColorStateList mButtonColorStateList;

    private AppCompatButton mMinusButton;
    private AppCompatButton mPlusButton;
    private TextView mValueTextView;

    private boolean mAutoIncrement;
    private boolean mAutoDecrement;

    private Handler mUpdateIntervalHandler;

    private ScrollablelNumberPickerListener mListener;

    public ScrollableNumberPicker(Context context) {
        super(context);
        init(context, null);
    }

    public ScrollableNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollableNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        LayoutInflater layoutInflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.number_picker, this);

        TypedArray typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ScrollableNumberPicker);
        Resources res = getResources();

        downIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_downIcon, downIcon);
        upIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_upIcon, upIcon);
        leftIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_leftIcon, leftIcon);
        rightIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_rightIcon, rightIcon);

        mMinValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_minNumber,
            res.getInteger(R.integer.default_minValue));
        mMaxValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_maxNumber,
            res.getInteger(R.integer.default_maxValue));

        mStepSize = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_stepSize,
            res.getInteger(R.integer.default_stepSize));

        mUpdateIntervalMillis = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_repeatDelay,
            res.getInteger(R.integer.default_updateInterval));

        mOrientation = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_orientation,
            LinearLayout.HORIZONTAL);

        mValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_value,
            res.getInteger(R.integer.default_value));

        mButtonColorStateList = ContextCompat.getColorStateList(context, typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonBackgroundTintSelector, R.color.btn_tint_selector));

        typedArray.recycle();

        initViews();
        setValue();

        mAutoIncrement = false;
        mAutoDecrement = false;

        mUpdateIntervalHandler = new Handler();
    }

    private void initViews() {
        mValueTextView = (TextView) findViewById(R.id.text_value);
        setOrientation(mOrientation);

        initButtonPlus();
        initButtonMinus();
    }

    private void initButtonPlus() {
        if (mOrientation == LinearLayout.VERTICAL) {
            mPlusButton = (AppCompatButton) findViewById(R.id.button_first);
            mPlusButton.setBackground(ContextCompat.getDrawable(getContext(), upIcon));
        } else if (mOrientation == LinearLayout.HORIZONTAL) {
            mPlusButton = (AppCompatButton) findViewById(R.id.button_last);
            mPlusButton.setBackground(ContextCompat.getDrawable(getContext(), rightIcon));
        }

        mPlusButton.setSupportBackgroundTintList(mButtonColorStateList);

        mPlusButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                increment();
            }
        });

        mPlusButton.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                mUpdateIntervalHandler.post(new RepeatRunnable());
                return false;
            }
        });

        mPlusButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mAutoIncrement) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });
    }

    private void initButtonMinus() {
        if (mOrientation == LinearLayout.VERTICAL) {
            mMinusButton = (AppCompatButton) findViewById(R.id.button_last);
            mMinusButton.setBackground(ContextCompat.getDrawable(getContext(), downIcon));
        } else if (mOrientation == LinearLayout.HORIZONTAL) {
            mMinusButton = (AppCompatButton) findViewById(R.id.button_first);
            mMinusButton.setBackground(ContextCompat.getDrawable(getContext(), leftIcon));
        }

        mMinusButton.setSupportBackgroundTintList(mButtonColorStateList);

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decrement();
            }
        });

        mMinusButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                mAutoDecrement = true;
                mUpdateIntervalHandler.post(new RepeatRunnable());
                return false;
            }
        });

        mMinusButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mAutoDecrement) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });
    }

    public AppCompatButton getButtonMinusView() {
        return mMinusButton;
    }

    public AppCompatButton getButtonPlusView() {
        return mPlusButton;
    }

    public TextView getTextValueView() {
        return mValueTextView;
    }

    public void increment() {
        if (mValue < mMaxValue) {
            setValue(mValue + mStepSize);
        }
    }

    public void decrement() {
        if (mValue > mMinValue) {
            setValue(mValue - mStepSize);
        }
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        if (value < mMinValue) {
            value = mMinValue;
        }

        mValue = value;
        setValue();
    }

    private void setValue() {
        mValueTextView.setText(String.valueOf(mValue));

        if (mListener != null) {
            mListener.onNumberPicked(mValue);
        }
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        if (maxValue < mValue) {
            mValue = maxValue;
            setValue();
        }
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        if (minValue > mValue) {
            mValue = minValue;
            setValue();
        }
    }

    public int getStepSize() {
        return mStepSize;
    }

    public void setStepSize(int stepSize) {
        mStepSize = stepSize;
    }

    public long getOnLongPressUpdateInterval() {
        return mUpdateIntervalMillis;
    }

    public void setOnLongPressUpdateInterval(int intervalMillis) {
        if (intervalMillis < MIN_UPDATE_INTERVAL_MS) {
            intervalMillis = MIN_UPDATE_INTERVAL_MS;
        }

        mUpdateIntervalMillis = intervalMillis;
    }

    public void setListener(ScrollablelNumberPickerListener listener) {
        mListener = listener;
    }

    private class RepeatRunnable implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                increment();
                mUpdateIntervalHandler.postDelayed(new RepeatRunnable(), mUpdateIntervalMillis);
            } else if (mAutoDecrement) {
                decrement();
                mUpdateIntervalHandler.postDelayed(new RepeatRunnable(), mUpdateIntervalMillis);
            }
        }
    }
}
