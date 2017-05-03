ScrollableNumberPicker
============
This view provides an user-friendly numerical input interface. It can easily be customized and is built to be used on Android-TV as well.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ScrollableNumberPicker-orange.svg?style=flat)](https://android-arsenal.com/details/1/5676)
[![Download](https://api.bintray.com/packages/michaelmuenzer/ScrollableNumberPicker/ScrollableNumberPicker/images/download.svg) ](https://bintray.com/michaelmuenzer/ScrollableNumberPicker/ScrollableNumberPicker/_latestVersion)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)

How does it look like?
--------
![alt tag](https://raw.github.com/michaelmuenzer/ScrollableNumberPicker/master/media/sample.gif)

Getting started
--------
The library is available on `jcenter()`. Just add these lines in your `build.gradle`:

```groovy
dependencies {
    compile 'com.michaelmuenzer.android:ScrollableNumberPicker:0.2.2'
}
```

Alternatively you can use [jitpack.io](https://jitpack.io/#michaelmuenzer/ScrollableNumberPicker)

Usage
--------
Just include `ScrollableNumberPicker` inside our xml-layout. There are samples available for mobile and tv applications inside this repository.

```xml
<com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
    android:id="@+id/number_picker_horizontal"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

If you want to change the value by scrolling on the view, you can enable and control speed like this:
```xml
<com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
    [...]
    app:snp_scrollEnabled="true"
    app:snp_updateInterval="25"
    />
```

You can make use of various other custom attributes to define how the increment and decrement interactions should behave:
```xml
<LinearLayout
    [...]
    xmlns:app="http://schemas.android.com/apk/res-auto"
    >
    
    <com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
        [...]
        app:snp_maxValue="1000"
        app:snp_minValue="10"
        app:snp_stepSize="5"
        app:snp_updateInterval="100"
        app:snp_value="10"
        />
</Linearlayout>
```

There exist further attributes which let you customize the general appearance of the view:
```xml
<com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
        [...]
        app:snp_buttonIconLeft="@drawable/btn_left_selector_main"
        app:snp_buttonIconRight="@drawable/btn_right_selector_main"            
        app:snp_buttonBackgroundTintSelector="@color/white"
        app:snp_buttonTouchScaleFactor="0.8"
        app:snp_orientation="horizontal"
        app:snp_valueMarginEnd="5dp"
        app:snp_valueMarginStart="5dp"
        app:snp_value_text_color="@color/colorPrimary"
        app:snp_value_text_size="16sp"
        app:snp_value_text_appearance="?android:attr/textAppearanceMedium"
        app:snp_buttonPaddingBottom="8dp"
        app:snp_buttonPaddingLeft="8dp"
        app:snp_buttonPaddingRight="8dp"
        app:snp_buttonPaddingTop="8dp"
        />
```

You can essentially make the element look exactly like you want by using the `android:background` attribute:
```xml
<com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
        [...]
        android:background="@drawable/number_picker_bg_color"
        />
```

number_picker_bg_color.xml:
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
       android:shape="rectangle">

    <solid android:color="@color/green"/>

    <corners android:radius="24dp"/>

    <size
        android:width="24dp"
        android:height="24dp"/>
</shape>
```

You can use `ScrollableNumberPickerListener` to build further processing logic around the selected number: 

```Java
numberPicker.setListener(new ScrollableNumberPickerListener() {
    @Override
    public void onNumberPicked(int value) {
        // Do some magic
    }
});
```
        
If you use it on Android TV, please include the following to allow D-pad support.

```xml
<com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
    android:id="@+id/number_picker_horizontal"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:focusable="true"
    android:nextFocusUp="@+id/number_picker_vertical"/>
```

```Java
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
    }

    return false;
}
```

Questions?
--------
If you have any questions feel free to open a github issue with a 'question' label

License
--------
Licensed under the MIT license. See [LICENSE](LICENSE.md).
