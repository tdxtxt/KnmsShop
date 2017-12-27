package com.knms.shop.android.ui;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 追加文字
 * Created by 654654 on 2017/5/10.
 */

public class AppendTextAll implements Runnable{
    private TextView mTarget;
    private int maxLine;
    public AppendTextAll(TextView target){
        this(target,3);
    }

    private AppendTextAll(TextView target, int maxLine){
        mTarget = target;
        this.maxLine = maxLine;
        if (this.maxLine < 1) this.maxLine = 1;
    }

    public void run(){
        appendAll();
    }

    private void appendAll(){
        if (null == mTarget || TextUtils.isEmpty(mTarget.getText()) || mTarget.getText().length() < 10) return;
        //得到TextView的布局
        Layout layout = mTarget.getLayout();
        //得到TextView显示有多少行
        int lines = mTarget.getLineCount();
        if (maxLine >= lines) return;
        //为了转换String 到 StringBuilder
        StringBuilder SrcStr = new StringBuilder(mTarget.getText().toString());
        //使用getLineStart 和 getLineEnd 得到指定行的开始和结束的坐标，坐标范围是SrcStr整个字符串范围内。
        String lineStr = SrcStr.subSequence(layout.getLineStart(0),layout.getLineEnd(maxLine - 1)).toString();
        String all = "全文";
        lineStr = lineStr.substring(0,lineStr.length() - 4)+"...";
        SpannableString spannableString = new SpannableString(all);
//                spannableString.setSpan(new TextExpandClick(content, SrcStr, null), 0, all.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(0xFFffb000),  0, all.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan((int) (mTarget.getTextSize()+5)), 0, all.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTarget.setText(lineStr);
        mTarget.append(spannableString);
//                content.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
