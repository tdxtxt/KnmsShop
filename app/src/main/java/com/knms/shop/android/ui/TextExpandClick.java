package com.knms.shop.android.ui;

import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * 展开全文或者收起
 * Created by 654654 on 2017/4/28.
 */

public class TextExpandClick extends ClickableSpan {
    private TextView tv;
    private String txt;
    private SpannableString end;
    private int color = 0xFFffb000;//默认黄色

    /**
     *
     * @param tv    变化的TextView
     * @param txt   点击后显示的文字
     * @param end   尾部追加span
     */
    public TextExpandClick(TextView tv,String txt,SpannableString end) {
        this.tv = tv;
        this.txt = txt;
        if (null == this.txt)
            this.txt = "";
        this.end = end;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setTextSize(ds.getTextSize() + 4.0f);//字体大小比文本内容大
        ds.setColor(color);
    }

    @Override
    public void onClick(View widget) {
        if (null == tv) return;
        tv.setText(txt);
        if (null != end)
            tv.append(end);
    }
}
