package com.example.rentzy.customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEdittextSemiBold extends EditText {


    public CustomEdittextSemiBold(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomEdittextSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomEdittextSemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomEdittextSemiBold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = com.example.rentzy.customfont.FontCache.getTypeface("BaiJamjuree-SemiBold.ttf", context);
        setTypeface(customFont);
    }
}
