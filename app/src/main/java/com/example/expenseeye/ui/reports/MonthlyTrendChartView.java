package com.example.expenseeye.ui.reports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.expenseeye.model.reports.MonthlySpending;

import java.util.ArrayList;
import java.util.List;

public class MonthlyTrendChartView extends View {

    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<MonthlySpending> data = new ArrayList<>();

    public MonthlyTrendChartView(Context context) {
        super(context);
        init();
    }

    public MonthlyTrendChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthlyTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(List<MonthlySpending> points) {
        data.clear();
        if (points != null) {
            data.addAll(points);
        }
        invalidate();
    }

    private void init() {
        axisPaint.setColor(0x33566A8B);
        axisPaint.setStrokeWidth(dp(1));

        linePaint.setColor(0xFF1F6FEB);
        linePaint.setStrokeWidth(dp(3));
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint.setColor(0xFF1F6FEB);

        labelPaint.setColor(0xFF63758F);
        labelPaint.setTextSize(sp(11));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = dp(10);
        float right = getWidth() - dp(10);
        float top = dp(16);
        float bottom = getHeight() - dp(26);

        canvas.drawLine(left, bottom, right, bottom, axisPaint);

        if (data.isEmpty()) {
            canvas.drawText("No monthly data yet", left, top + dp(14), labelPaint);
            return;
        }

        int count = data.size();
        double max = 0.0;
        for (MonthlySpending item : data) {
            max = Math.max(max, item.getTotal());
        }
        if (max <= 0.0) {
            max = 1.0;
        }

        float spacing = count == 1 ? 0 : (right - left) / (count - 1);
        Path path = new Path();

        for (int i = 0; i < count; i++) {
            MonthlySpending item = data.get(i);
            float x = left + (spacing * i);
            float normalized = (float) (item.getTotal() / max);
            float y = bottom - ((bottom - top) * normalized);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            canvas.drawCircle(x, y, dp(3), pointPaint);

            if (i == 0 || i == count - 1 || i % 2 == 0) {
                String label = trimLabel(item.getMonthLabel());
                float labelWidth = labelPaint.measureText(label);
                canvas.drawText(label, x - (labelWidth / 2f), getHeight() - dp(8), labelPaint);
            }
        }

        canvas.drawPath(path, linePaint);
    }

    private String trimLabel(String monthLabel) {
        if (monthLabel == null) {
            return "";
        }
        return monthLabel.length() <= 3 ? monthLabel : monthLabel.substring(0, 3);
    }

    private float dp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private float sp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
}
