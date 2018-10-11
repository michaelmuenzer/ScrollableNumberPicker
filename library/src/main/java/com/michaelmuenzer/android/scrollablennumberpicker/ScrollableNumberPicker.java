package com.michaelmuenzer.android.scrollablennumberpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

public class ScrollableNumberPicker extends LinearLayout {
    private final static int INVALID_RES = -1;
    private final static float SLOWING_FACTOR = 1.25f;
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

    private float mValueTextSize;
    private int mValueTextColor;
    private int mValueTextAppearanceResId;

    private boolean mScrollEnabled;
    private int mUpdateIntervalMillis;
    private float mButtonTouchScaleFactor;
    private int mOrientation;
    private ColorStateList mButtonColorStateList;

    private int mValueMarginStart;
    private int mValueMarginEnd;

    private ImageView mMinusButton;
    private ImageView mPlusButton;
    private TextView mValueTextView;

    private int mButtonPaddingLeft;
    private int mButtonPaddingRight;
    private int mButtonPaddingTop;
    private int mButtonPaddingBottom;

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

    @SuppressWarnings("unused")
    public ImageView getButtonMinusView() {
        return mMinusButton;
    }

    @SuppressWarnings("unused")
    public ImageView getButtonPlusView() {
        return mPlusButton;
    }

    @SuppressWarnings("unused")
    public boolean isScrollEnabled() {
        return mScrollEnabled;
    }

    @SuppressWarnings("unused")
    public int getUpdateIntervalMillis() {
        return mUpdateIntervalMillis;
    }

    @SuppressWarnings("unused")
    public float getButtonTouchScaleFactor() {
        return mButtonTouchScaleFactor;
    }

    @SuppressWarnings("unused")
    public int getOrientation() {
        return mOrientation;
    }

    @SuppressWarnings("unused")
    public ColorStateList getButtonColorStateList() {
        return mButtonColorStateList;
    }

    @SuppressWarnings("unused")
    public int getValueMarginStart() {
        return mValueMarginStart;
    }

    @SuppressWarnings("unused")
    public int getValueMarginEnd() {
        return mValueMarginEnd;
    }

    @SuppressWarnings("unused")
    public TextView getValueView() {
        return mValueTextView;
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
    public float getValueTextSize() {
        return mValueTextSize;
    }

    @SuppressWarnings("unused")
    public void setValueTextSize(float valueTextSize) {
        mValueTextSize = valueTextSize;
        mValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mValueTextSize);
    }

    @SuppressWarnings("unused")
    public int getValueTextColor() {
        return mValueTextColor;
    }

    @SuppressWarnings("unused")
    public void setValueTextColor(int valueTextColor) {
        mValueTextColor = valueTextColor;
        mValueTextView.setTextColor(mValueTextColor);
    }

    @SuppressWarnings("unused")
    public void setScrollEnabled(boolean scrollEnabled) {
        mScrollEnabled = scrollEnabled;
    }

    @SuppressWarnings("unused")
    public int getButtonPaddingLeft() {
        return mButtonPaddingLeft;
    }

    @SuppressWarnings("unused")
    public void setButtonPaddingLeft(int buttonPaddingLeft) {
        mButtonPaddingLeft = buttonPaddingLeft;
        setPlusMinusButtonPaddings();
    }

    @SuppressWarnings("unused")
    public int getButtonPaddingRight() {
        return mButtonPaddingRight;
    }

    @SuppressWarnings("unused")
    public void setButtonPaddingRight(int buttonPaddingRight) {
        mButtonPaddingRight = buttonPaddingRight;
        setPlusMinusButtonPaddings();
    }

    @SuppressWarnings("unused")
    public int getButtonPaddingTop() {
        return mButtonPaddingTop;
    }

    @SuppressWarnings("unused")
    public void setButtonPaddingTop(int buttonPaddingTop) {
        mButtonPaddingTop = buttonPaddingTop;
        setPlusMinusButtonPaddings();
    }

    @SuppressWarnings("unused")
    public int getButtonPaddingBottom() {
        return mButtonPaddingBottom;
    }

    @SuppressWarnings("unused")
    public void setButtonPaddingBottom(int buttonPaddingBottom) {
        mButtonPaddingBottom = buttonPaddingBottom;
        setPlusMinusButtonPaddings();
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

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.number_picker, this);

        TypedArray typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ScrollableNumberPicker);
        Resources res = getResources();

        downIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconDown, downIcon);
        upIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconUp, upIcon);
        leftIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconLeft, leftIcon);
        rightIcon = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonIconRight, rightIcon);

        mMinValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_minValue,
            res.getInteger(R.integer.default_minValue));
        mMaxValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_maxValue,
            res.getInteger(R.integer.default_maxValue));

        mStepSize = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_stepSize,
            res.getInteger(R.integer.default_stepSize));

        mUpdateIntervalMillis = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_updateInterval,
            res.getInteger(R.integer.default_updateInterval));

        mOrientation = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_orientation,
            LinearLayout.HORIZONTAL);

        mValue = typedArray.getInt(R.styleable.ScrollableNumberPicker_snp_value,
            res.getInteger(R.integer.default_value));

        mValueTextSize = typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_value_text_size,
            INVALID_RES);
        mValueTextColor = typedArray.getColor(R.styleable.ScrollableNumberPicker_snp_value_text_color,
            0);
        mValueTextAppearanceResId = typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_value_text_appearance, INVALID_RES);

        mScrollEnabled = typedArray.getBoolean(R.styleable.ScrollableNumberPicker_snp_scrollEnabled,
            res.getBoolean(R.bool.default_scrollEnabled));

        mButtonColorStateList = ContextCompat.getColorStateList(context, typedArray.getResourceId(R.styleable.ScrollableNumberPicker_snp_buttonBackgroundTintSelector, R.color.btn_tint_selector));

        mValueMarginStart = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_valueMarginStart, res.getDimension(R.dimen.default_value_margin_start));
        mValueMarginEnd = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_valueMarginStart, res.getDimension(R.dimen.default_value_margin_end));

        mButtonPaddingLeft = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_buttonPaddingLeft, res.getDimension(R.dimen.default_button_padding_left));
        mButtonPaddingRight = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_buttonPaddingRight, res.getDimension(R.dimen.default_button_padding_right));
        mButtonPaddingTop = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_buttonPaddingTop, res.getDimension(R.dimen.default_button_padding_top));
        mButtonPaddingBottom = (int) typedArray.getDimension(R.styleable.ScrollableNumberPicker_snp_buttonPaddingBottom, res.getDimension(R.dimen.default_button_padding_bottom));

        TypedValue outValue = new TypedValue();
        res.getValue(R.dimen.default_button_scale_factor, outValue, true);
        float defaultValue = outValue.getFloat();
        mButtonTouchScaleFactor = typedArray.getFloat(R.styleable.ScrollableNumberPicker_snp_buttonTouchScaleFactor, defaultValue);

        typedArray.recycle();

        initViews();

        mAutoIncrement = false;
        mAutoDecrement = false;

        mUpdateIntervalHandler = new Handler();
    }

    private void initViews() {
        setOrientation(mOrientation);
        setGravity(Gravity.CENTER);

        initValueView();
        initButtonPlusView();
        initButtonMinusView();

        if (mScrollEnabled) {
            setOnTouchListener(new OnTouchListener() {
                private float lastX = 0.0f;
                private float lastY = 0.0f;
                private final int scrollOffsetPx = getResources().getDimensionPixelSize(R.dimen.default_scroll_offset);

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float currentX = motionEvent.getX();
                    float currentY = motionEvent.getY();

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = currentX;
                            lastY = currentY;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mOrientation == HORIZONTAL) {
                                float moveDeltaX = currentX - lastX;
                                if (moveDeltaX > scrollOffsetPx) {
                                    increment();
                                    break;
                                } else if ((scrollOffsetPx * -1) > moveDeltaX) {
                                    decrement();
                                    break;
                                }
                            } else {
                                float moveDeltaY = currentY - lastY;
                                if (moveDeltaY > scrollOffsetPx) {
                                    decrement();
                                    break;
                                } else if ((scrollOffsetPx * -1) > moveDeltaY) {
                                    increment();
                                    break;
                                }
                            }

                            lastX = currentX;
                            lastY = currentY;
                            break;
                        case MotionEvent.ACTION_UP:
                            int numberOfRuns;
                            int singleRunLength = getResources().getDimensionPixelSize(R.dimen.default_scroll_repeat_length);
                            if (mOrientation == HORIZONTAL) {
                                float moveDeltaX = currentX - lastX;
                                if (moveDeltaX > 0) {
                                    mAutoIncrement = true;
                                } else {
                                    mAutoDecrement = true;
                                }

                                numberOfRuns = (int) (Math.abs(moveDeltaX) / singleRunLength);
                            } else {
                                float moveDeltaY = currentY - lastY;
                                if (moveDeltaY > 0) {
                                    mAutoDecrement = true;
                                } else {
                                    mAutoIncrement = true;
                                }

                                numberOfRuns = (int) (Math.abs(moveDeltaY) / singleRunLength);
                            }

                            mUpdateIntervalHandler.post(new RepeatSlowingRunnable(numberOfRuns, mUpdateIntervalMillis));
                            break;
                        default:
                            return false;
                    }

                    return true;
                }
            });
        }
    }

    private void initValueView() {
        mValueTextView = findViewById(R.id.text_value);

        if (mValueTextAppearanceResId != INVALID_RES) {
            TextViewCompat.setTextAppearance(mValueTextView, mValueTextAppearanceResId);
        }

        if (mValueTextColor != 0) {
            mValueTextView.setTextColor(mValueTextColor);
        }

        if (mValueTextSize != INVALID_RES) {
            mValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mValueTextSize);
        }

        LinearLayout.LayoutParams layoutParams = (LayoutParams) mValueTextView.getLayoutParams();
        if (mOrientation == HORIZONTAL) {
            layoutParams.setMargins(mValueMarginStart, 0, mValueMarginEnd, 0);
        } else {
            layoutParams.setMargins(0, mValueMarginStart, 0, mValueMarginEnd);
        }

        mValueTextView.setLayoutParams(layoutParams);

        setValue();
    }

    private void initButtonPlusView() {
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

    private void initButtonMinusView() {
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

    private void setButtonPlusImage() {
        if (mOrientation == LinearLayout.VERTICAL) {
            mPlusButton = (ImageView) findViewById(R.id.button_increase);
            mPlusButton.setImageResource(upIcon);
        } else if (mOrientation == LinearLayout.HORIZONTAL) {
            mPlusButton = (ImageView) findViewById(R.id.button_decrease);
            mPlusButton.setImageResource(rightIcon);
        }

        tintButton(mPlusButton, mButtonColorStateList);

        setButtonLayoutParams(mPlusButton);
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

        setButtonLayoutParams(mMinusButton);
    }

    private void setButtonLayoutParams(ImageView button) {
        LayoutParams params = (LayoutParams) button.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(0, 0, 0, 0);
        button.setLayoutParams(params);

        setButtonPaddings(button);
    }

    private void setPlusMinusButtonPaddings() {
        setButtonPaddings(mMinusButton);
        setButtonPaddings(mPlusButton);
    }

    private void setButtonPaddings(ImageView button) {
        button.setPadding(mButtonPaddingLeft, mButtonPaddingTop, mButtonPaddingRight, mButtonPaddingBottom);
    }

    private void tintButton(@NonNull ImageView button, ColorStateList colorStateList) {
        Drawable drawable = DrawableCompat.wrap(button.getDrawable());
        DrawableCompat.setTintList(drawable, colorStateList);
        button.setImageDrawable(drawable);
    }

    private void scaleImageViewDrawable(ImageView view, float scaleFactor) {
        Drawable drawable = view.getDrawable();
        int currentWidth = drawable.getIntrinsicWidth();
        int currentHeight = drawable.getIntrinsicHeight();
        int newWidth = (int) (currentWidth * scaleFactor);
        int newHeight = (int) (currentHeight * scaleFactor);

        if (newWidth < currentWidth && newHeight < currentHeight) {
            int marginWidth = (currentWidth - newWidth) / 2;
            int marginHeight = (currentHeight - newHeight) / 2;

            //setBounds is not working on FireTV, that's why we use a workaround with margins
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = newWidth;
            params.height = newHeight;
            params.setMargins(marginWidth, marginHeight, marginWidth, marginHeight);
            view.setLayoutParams(params);
        }
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

    private class RepeatSlowingRunnable implements Runnable {
        long mUpdateIntervalMillis = 0;
        int mNumberOfLeftRuns = 0;

        RepeatSlowingRunnable(int numberOfLeftRuns, long millis) {
            mUpdateIntervalMillis = millis;
            mNumberOfLeftRuns = numberOfLeftRuns;
        }

        public void run() {
            long millisNextRun = (long) (mUpdateIntervalMillis * SLOWING_FACTOR);

            if (mNumberOfLeftRuns > 0) {
                int leftRuns = mNumberOfLeftRuns - 1;

                if (mAutoIncrement) {
                    increment();
                    mUpdateIntervalHandler.postDelayed(new RepeatSlowingRunnable(leftRuns, millisNextRun), mUpdateIntervalMillis);
                } else if (mAutoDecrement) {
                    decrement();
                    mUpdateIntervalHandler.postDelayed(new RepeatSlowingRunnable(leftRuns, millisNextRun), mUpdateIntervalMillis);
                }
            } else {
                mAutoIncrement = false;
                mAutoDecrement = false;
            }
        }
    }
}
