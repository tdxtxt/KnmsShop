package com.knms.shop.android.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 分割线
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    private Paint linePaint;
    private int lineHeight;
    public ItemDecoration(){
        this(2);
    }
    public ItemDecoration(int lineHeight){
        if (lineHeight < 1)
            lineHeight = 1;
        this.lineHeight = lineHeight;
        int backColor = 0xFFf5f5f5;//悬浮和分割线颜色
        linePaint = new Paint();
        linePaint.setColor(backColor);
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = lineHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) continue;
            c.drawRect(left, view.getTop() - lineHeight, right, view.getTop(), linePaint);//绘制分割线
        }
    }
}
