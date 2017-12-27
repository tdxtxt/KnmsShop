package net.lucode.hackware.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.R;
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import static android.R.attr.visibility;

/**
 * Created by Administrator on 2017/3/29.
 */

public class CustomTitleView extends LinearLayout implements IPagerTitleView,IMeasurablePagerTitleView {
    protected int mSelectedColor;
    protected int mNormalColor;
    TextView tv_title;
    ImageView iv_red;
    public CustomTitleView(Context context) {
        super(context);
        init(context);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomTitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        setGravity(Gravity.CENTER);
        int padding = UIUtil.dip2px(context, 10);
        setPadding(padding, 0, padding, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_title_view, this, true);
        iv_red = (ImageView) view.findViewById(R.id.iv_red);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
    }
    @Override
    public void onSelected(int index, int totalCount) {
        Log.i("selectNimx","index:"+index + ";totalCount:" + totalCount);
        tv_title.setTextColor(mSelectedColor);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        tv_title.setTextColor(mNormalColor);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        tv_title.setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        tv_title.setTextColor(color);
    }

    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        tv_title.getPaint().getTextBounds(tv_title.getText().toString(), 0, tv_title.getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = tv_title.getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        tv_title.getPaint().getTextBounds(tv_title.getText().toString(), 0, tv_title.getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = tv_title.getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    public int getSelectedColor() {
        return mSelectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    public int getNormalColor() {
        return mNormalColor;
    }

    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public void setText(String text){
        tv_title.setText(text);
    }

    public void setRedVisibility(int count){
        iv_red.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }
}
