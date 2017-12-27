package com.knms.shop.android.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 显示Edit内容字数
 * Created by 654654 on 2017/4/25.
 */

public class UiNumberEdit extends FrameLayout {
    private DisplayMetrics displayMetrics;
    private EditText message;
    private TextView number;
    private TextChanged onTextChanged;
    private int maxLen = 200;
    public UiNumberEdit(@NonNull Context context) {
        super(context);
        displayMetrics = context.getResources().getDisplayMetrics();
        initChild(context);
    }
    public UiNumberEdit(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public UiNumberEdit(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        displayMetrics = context.getResources().getDisplayMetrics();
        initChild(context);
    }

    public void setText(CharSequence text){
        if (null == text)
            message.setText("");
        else
            message.setText(text);
        int len = message.getText().length();
        message.setSelection(len);
    }
    public void setHint(CharSequence hint){
        if (null == hint)
            message.setHint("");
        else
            message.setHint(hint);
    }
    public String getText(){
        return message.getText().toString();
    }
    public void setContentTextSize(float size){
        message.setTextSize(size);
    }
    public void setNumberTextSize(float size){
        number.setTextSize(size);
    }
    public void setContentTextColor(@ColorInt int color){
        message.setTextColor(color);
    }
    public void setContentHintTextColor(@ColorInt int color){
        message.setHintTextColor(color);
    }
    public void setNumberTextColor(@ColorInt int color){
        number.setTextColor(color);
    }
    public String getHint(){
        return message.getHint().toString();
    }
    public void setCursorVisible(boolean cursorVisible){
        message.setCursorVisible(cursorVisible);
    }
    public void setFocusable(boolean focusable){
        message.setFocusable(focusable);
    }
    public void setFocusableInTouchMode(boolean focusableInTouchMode){
        message.setFocusableInTouchMode(focusableInTouchMode);
    }
    public void setMaxLength(int len){
        if (len < 1) len = 1;
        maxLen = len;
        String str = message.getText().toString();
        message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLen)});
        message.setText(str);
    }
    public void setOnTextChanged(TextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    private void initChild(Context context) {
        removeAllViews();
        message = new EditText(context);
        number = new TextView(context);
        int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics);
        message.setBackgroundColor(0xFFFFFFFF);
        message.setHintTextColor(0xFF999999);
        message.setTextSize(14);
        number.setTextSize(14);
        message.setPadding(margins,margins,margins,margins);
        number.setTextColor(0xFF999999);
        message.setGravity(Gravity.TOP | Gravity.LEFT);
        FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        message.setLayoutParams(lp);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM |Gravity.RIGHT;
        lp.setMargins(0,0,margins,margins);
        number.setLayoutParams(lp);
        message.addTextChangedListener(textWatcher);
        message.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_MULTI_LINE| InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLen)});
        message.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    message.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        int len = message.getText().length();
        number.setText(len +"/"+maxLen);
        message.setSelection(len);
        addView(message);
        addView(number);
        requestLayout();
        invalidate();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int len = message.getText().length();
            if (len <= maxLen){
                number.setText(len +"/"+maxLen);
            }
            if (null != onTextChanged)
                onTextChanged.onTextChanged(s,start,before,count);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    /** 监听输入内容变化 */
    public interface TextChanged{
        void onTextChanged(CharSequence s, int start, int before, int count);
    }
}
