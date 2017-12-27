package com.knms.shop.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.knms.shop.android.R;

/**
 * 吸顶效果<br/>最好是LinearLayoutManager 其它模式没兼容
 */
public class StickyDecoration extends RecyclerView.ItemDecoration {

    private DecorationCallback callback;
    private TextPaint textPaint;
    private Paint paint,linePaint;
    private int topGap;
    private int alignBottom;
    private int lineHeight;
    private int txtPadingLeft;

    public StickyDecoration(Context context, DecorationCallback decorationCallback) {
        Resources res = context.getResources();
        this.callback = decorationCallback;
        if (null == callback) return;

        int backColor = 0xFFf5f5f5;//悬浮和分割线颜色
        //设置悬浮栏的画笔---paint
        paint = new Paint();
        paint.setColor(backColor);
        //分割线
        linePaint = new Paint();
        linePaint.setColor(backColor);

        //设置悬浮栏中文本的画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(res.getDimensionPixelSize(R.dimen.sticky_txt_size));
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);
        //决定悬浮栏的高度等
        topGap = res.getDimensionPixelSize(R.dimen.sticky_height);
        alignBottom = (int) (topGap / 2 - Math.abs(textPaint.ascent() + textPaint.descent()) / 2);
        lineHeight = res.getDimensionPixelSize(R.dimen.sticky_line_height);
        txtPadingLeft = res.getDimensionPixelSize(R.dimen.sticky_txt_left);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (null == callback || !callback.isSticky()) return;

        int pos = parent.getChildAdapterPosition(view);
        if (pos == RecyclerView.NO_POSITION) return;
        if (isFirstInGroup(pos)) {
            outRect.top = topGap;
        } else {
            outRect.top = lineHeight;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (null == callback || !callback.isSticky()) return;

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) continue;
            String textLine = callback.getTagName(position).toUpperCase();
            float top = view.getTop() - topGap;
            float bottom = view.getTop();

            if (isFirstInGroup(position)) {
                c.drawRect(left, top, right, bottom, paint);//绘制悬浮栏背景
                c.drawText(textLine, txtPadingLeft, bottom - alignBottom, textPaint); //绘制文本
            }else {
                c.drawRect(left, bottom - lineHeight, right, bottom, linePaint);//绘制分割线
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (null == callback || !callback.isSticky()) return;
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        String tagName = "";
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            if (position == RecyclerView.NO_POSITION) continue;
            String textLine = callback.getTagName(position).toUpperCase();
            if (TextUtils.isEmpty(textLine) || textLine.equals(tagName)) continue;
            tagName = textLine;
            int viewBottom = view.getBottom();
            float textY = Math.max(topGap, view.getTop());
            //下一个和当前不一样移动当前
            if (position + 1 < itemCount) {
                String nextTagName = callback.getTagName(position + 1);
                //组内最后一个view进入了header
                if (!nextTagName.equals(tagName) && viewBottom < textY) {
                    textY = viewBottom;
                }
            }
            c.drawRect(left, textY - topGap, right, textY, paint);
            c.drawText(textLine, txtPadingLeft, textY - alignBottom, textPaint);
        }
        /*int childCount = parent.getChildCount();
        View topView = parent.getChildAt(0);
        if (null == topView) return;

        int topViewBottom = topView.getBottom();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int position = parent.getChildAdapterPosition(topView);
        int next = position +1;

        String sTagName = callback.getTagName(position);
        //只有在两个标签接触到的时候才开始偏移，否则还是不偏移
        if (next <= childCount && isFirstInGroup(next)*//** 下一个是悬浮标签 *//* && topViewBottom <= topGap*//** 2个标签接触 *//*){
            c.drawRect(left, 0, right, topViewBottom, paint);
            c.drawText(sTagName, txtPadingLeft,topViewBottom - alignBottom, textPaint);
            return;
        }
        c.drawRect(left, 0, right, topGap, paint);
        c.drawText(sTagName, txtPadingLeft, topGap - alignBottom, textPaint);*/
    }


    /**
     * 判断是不是组中的第一个位置
     *
     * @param pos
     * @return
     */
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            // 因为是根据 字符串内容的相同与否 来判断是不是一个组
            String prevName = callback.getTagName(pos - 1);
            String name = callback.getTagName(pos);

            if (prevName.equals(name)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public interface DecorationCallback {
        /** 获取悬浮栏名字 */
       String getTagName(int position);
        boolean isSticky();
    }
}

