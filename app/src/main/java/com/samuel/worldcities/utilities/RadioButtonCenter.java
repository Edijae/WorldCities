package com.samuel.worldcities.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;

import com.samuel.worldcities.R;

public class RadioButtonCenter extends androidx.appcompat.widget.AppCompatRadioButton {

    Drawable buttonDrawable;

    public RadioButtonCenter(Context context) {
        super(context);
    }

    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadioButtonCenter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonCenter, 0, 0);
        buttonDrawable = a.getDrawable(R.styleable.RadioButtonCenter_android_button);
        setButtonDrawable(android.R.color.transparent);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        buttonDrawable.setState(getDrawableState());
        final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        final int height = buttonDrawable.getIntrinsicHeight();

        int y = 0;

        switch (verticalGravity) {
            case Gravity.BOTTOM:
                y = getHeight() - height;
                break;
            case Gravity.CENTER_VERTICAL:
                y = (getHeight() - height) / 2;
                break;
        }

        int buttonWidth = buttonDrawable.getIntrinsicWidth();
        int buttonLeft = (getWidth() - buttonWidth) / 2;
        buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
        buttonDrawable.draw(canvas);
    }

}
