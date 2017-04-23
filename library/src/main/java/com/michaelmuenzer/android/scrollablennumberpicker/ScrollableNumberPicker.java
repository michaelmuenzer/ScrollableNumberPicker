package com.michaelmuenzer.android.scrollablennumberpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

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
    private float mButtonTouchScaleFactor;
    private int mOrientation;
    private ColorStateList mButtonColorStateList;

    private ImageView mMinusButton;
    private ImageView mPlusButton;
    private TextView mValueTextView;

    private boolean mAutoIncrement;
    private boolean mAutoDecrement;

    private Handler mUpdateIntervalHandler;

    private ScrollableNumberPickerListener mListener;

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

        downIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconDown, downIcon);
        upIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconUp, upIcon);
        leftIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconLeft, leftIcon);
        rightIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconRight, rightIcon);

        mMinValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_minNumber,
            res.getInteger(R.integer.default_minValue));
        mMaxValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_maxNumber,
            res.getInteger(R.integer.default_maxValue));

        mStepSize = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_stepSize,
            res.getInteger(R.integer.default_stepSize));

        mUpdateIntervalMillis = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_updateInterval,
            res.getInteger(R.integer.default_updateInterval));

        mOrientation = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_orientation,
            LinearLayout.HORIZONTAL);

        mValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_value,
            res.getInteger(R.integer.default_value));

        mButtonColorStateList = ContextCompat.getColorStateList(context, typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonBackgroundTintSelector, R.color.btn_tint_selector));

        TypedValue outValue = new TypedValue();
        res.getValue(R.dimen.default_button_scale_factor, outValue, true);
        float defaultValue = outValue.getFloat();
        mButtonTouchScaleFactor = typedArray.getFloat(R.styleable.ScrollableNumberPicker_snp_buttonTouchScaleFactor, defaultValue);

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
        if (mOrientation == HORIZONTAL) {
            setGravity(Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_HORIZONTAL);
        }

        initButtonPlus();
        initButtonMinus();
    }

    private void initButtonPlus() {
        setButtonPlusImage();

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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scaleImageViewDrawable(mPlusButton, mButtonTouchScaleFactor);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mAutoIncrement) {
                        mAutoIncrement = false;
                    }

                    setButtonPlusImage();
                }

                return false;
            }
        });
    }

    private void setButtonPlusImage() {
        if (mOrientation == LinearLayout.VERTICAL) {
            mPlusButton = (ImageView) findViewById(R.id.button_increase);
            mPlusButton.setImageResource(upIcon);
        } else if (mOrientation == LinearLayout.HORIZONTAL) {
            mPlusButton = (ImageView) findViewById(R.id.button_decrease);
            mPlusButton.setImageResource(rightIcon);
        }

        tintButton(mPlusButton, mButtonColorStateList);
    }

    private void scaleImageViewDrawable(ImageView view, float scaleFactor) {
        Drawable drawable = view.getDrawable();
        int currentWidth = drawable.getIntrinsicWidth();
        int currentHeight = drawable.getIntrinsicHeight();
        int newWidth = (int) (currentWidth * scaleFactor);
        int newHeight = (int) (currentHeight * scaleFactor);

        drawable.setBounds(0, 0, newWidth, newHeight);

        if (newWidth < currentWidth && newHeight < currentHeight) {
            int insetWidth = (currentWidth - newWidth) / 2;
            int insetHeight = (currentHeight - newHeight) / 2;
            InsetDrawable insetDrawable = new InsetDrawable(drawable, insetWidth, insetHeight, insetWidth, insetHeight);

            view.setImageDrawable(insetDrawable);
        }
    }

    private void tintButton(@NonNull ImageView button, ColorStateList colorStateList) {
        Drawable drawable = DrawableCompat.wrap(button.getDrawable());
        DrawableCompat.setTintList(drawable, colorStateList);
        button.setImageDrawable(drawable);
    }

    private void initButtonMinus() {
        setButtonMinusImage();

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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scaleImageViewDrawable(mMinusButton, mButtonTouchScaleFactor);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mAutoDecrement) {
                        mAutoDecrement = false;
                    }

                    setButtonMinusImage();
                }

                return false;
            }
        });
    }

    private void setButtonMinusImage() {
        if (mOrientation == LinearLayout.VERTICAL) {
            mMinusButton = (ImageView) findViewById(R.id.button_decrease);
            mMinusButton.setImageResource(downIcon);
        } else if (mOrientation == LinearLayout.HORIZONTAL) {
            mMinusButton = (ImageView) findViewById(R.id.button_increase);
            mMinusButton.setImageResource(leftIcon);
        }

        tintButton(mMinusButton, mButtonColorStateList);
    }

    @SuppressWarnings("unused")
    public ImageView getButtonMinusView() {
        return mMinusButton;
    }

    @SuppressWarnings("unused")
    public ImageView getButtonPlusView() {
        return mPlusButton;
    }

    @SuppressWarnings("unused")
    public TextView getTextValueView() {
        return mValueTextView;
    }

    private void increment() {
        if (mValue < mMaxValue) {
            setValue(mValue + mStepSize);
        }
    }

    private void decrement() {
        if (mValue > mMinValue) {
            setValue(mValue - mStepSize);
        }
    }

    @SuppressWarnings("unused")
    public int getValue() {
        return mValue;
    }

    @SuppressWarnings("WeakerAccess")
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

    @SuppressWarnings("unused")
    public int getMaxValue() {
        return mMaxValue;
    }

    @SuppressWarnings("unused")
    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        if (maxValue < mValue) {
            mValue = maxValue;
            setValue();
        }
    }

    @SuppressWarnings("unused")
    public int getMinValue() {
        return mMinValue;
    }

    @SuppressWarnings("unused")
    public void setMinValue(int minValue) {
        mMinValue = minValue;
        if (minValue > mValue) {
            mValue = minValue;
            setValue();
        }
    }

    @SuppressWarnings("unused")
    public int getStepSize() {
        return mStepSize;
    }

    @SuppressWarnings("unused")
    public void setStepSize(int stepSize) {
        mStepSize = stepSize;
    }

    @SuppressWarnings("unused")
    public long getOnLongPressUpdateInterval() {
        return mUpdateIntervalMillis;
    }

    @SuppressWarnings("unused")
    public void setOnLongPressUpdateInterval(int intervalMillis) {
        if (intervalMillis < MIN_UPDATE_INTERVAL_MS) {
            intervalMillis = MIN_UPDATE_INTERVAL_MS;
        }

        mUpdateIntervalMillis = intervalMillis;
    }

    @SuppressWarnings("unused")
    public void setListener(ScrollableNumberPickerListener listener) {
        mListener = listener;
    }

    //TODO: Refactor code-duplication
    public boolean handleKeyEvent(int keyCode, KeyEvent event) {
        int eventAction = event.getAction();
        if (eventAction == KeyEvent.ACTION_DOWN) {
            if (mOrientation == HORIZONTAL) {
                if (keyCode == KEYCODE_DPAD_LEFT) {
                    if (event.getRepeatCount() == 0) {
                        scaleImageViewDrawable(mMinusButton, mButtonTouchScaleFactor);
                    }
                    decrement();
                    return true;
                } else if (keyCode == KEYCODE_DPAD_RIGHT) {
                    if (event.getRepeatCount() == 0) {
                        scaleImageViewDrawable(mPlusButton, mButtonTouchScaleFactor);
                    }
                    increment();
                    return true;
                }
            } else {
                if (keyCode == KEYCODE_DPAD_UP) {
                    if (event.getRepeatCount() == 0) {
                        scaleImageViewDrawable(mPlusButton, mButtonTouchScaleFactor);
                    }
                    increment();
                    return true;
                } else if (keyCode == KEYCODE_DPAD_DOWN) {
                    if (event.getRepeatCount() == 0) {
                        scaleImageViewDrawable(mMinusButton, mButtonTouchScaleFactor);
                    }
                    decrement();
                    return true;
                }
            }
        } else if (eventAction == KeyEvent.ACTION_UP) {
            if (mOrientation == HORIZONTAL) {
                if (keyCode == KEYCODE_DPAD_LEFT) {
                    setButtonMinusImage();
                    return true;
                } else if (keyCode == KEYCODE_DPAD_RIGHT) {
                    setButtonPlusImage();
                    return true;
                }
            } else {
                if (keyCode == KEYCODE_DPAD_UP) {
                    setButtonPlusImage();
                    return true;
                } else if (keyCode == KEYCODE_DPAD_DOWN) {
                    setButtonMinusImage();
                    return true;
                }
            }
        }

        return false;
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
