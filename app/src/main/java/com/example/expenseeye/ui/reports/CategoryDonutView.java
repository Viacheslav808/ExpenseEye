package com.example.expenseeye.ui.reports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.expenseeye.model.reports.CategoryTotal;

import java.util.ArrayList;
import java.util.List;

public class CategoryDonutView extends View {

    private static final int[] SEGMENT_COLORS = {
            0xFF1F6FEB,
            0xFF0F9D58,
            0xFF9C27B0,
            0xFFF2994A,
            0xFF00ACC1
    };

    private final Paint segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF chartBounds = new RectF();

    private final List<CategoryTotal> data = new ArrayList<>();

    public CategoryDonutView(Context context) {
        super(context);
        init();
    }

    public CategoryDonutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CategoryDonutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(List<CategoryTotal> categories) {
        data.clear();
        if (categories != null) {
            data.addAll(categories);
        }
        invalidate();
    }

    private void init() {
        segmentPaint.setStyle(Paint.Style.STROKE);
        segmentPaint.setStrokeCap(Paint.Cap.BUTT);

        centerPaint.setColor(0xFFF8FAFF);
        centerPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(0xFF43536E);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(sp(12));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height) - dp(12);
        float stroke = size * 0.24f;
        float radius = size / 2f;

        float cx = width / 2f;
        float cy = height / 2f;

        chartBounds.set(cx - radius, cy - radius, cx + radius, cy + radius);
        segmentPaint.setStrokeWidth(stroke);

        double total = 0.0;
        for (CategoryTotal category : data) {
            total += category.getTotal();
        }

        if (total <= 0.0 || data.isEmpty()) {
            labelPaint.setTextSize(sp(13));
            canvas.drawText("No category data", cx, cy + dp(4), labelPaint);
            return;
        }

        float start = -90f;
        int maxSegments = Math.min(data.size(), SEGMENT_COLORS.length);
        for (int i = 0; i < maxSegments; i++) {
            CategoryTotal category = data.get(i);
            float sweep = (float) ((category.getTotal() / total) * 360f);
            segmentPaint.setColor(SEGMENT_COLORS[i % SEGMENT_COLORS.length]);
            canvas.drawArc(chartBounds, start, sweep, false, segmentPaint);
            start += sweep;
        }

        canvas.drawCircle(cx, cy, (radius - (stroke / 2f)) * 0.9f, centerPaint);

        CategoryTotal top = data.get(0);
        String firstLine = top.getCategoryName();
        if (firstLine.length() > 12) {
            firstLine = firstLine.substring(0, 12) + "…";
        }
        String secondLine = Math.round((top.getTotal() / total) * 100) + "% top";

        labelPaint.setColor(0xFF202C3D);
        labelPaint.setTextSize(sp(13));
        canvas.drawText(firstLine, cx, cy - dp(4), labelPaint);
        labelPaint.setColor(0xFF62748B);
        labelPaint.setTextSize(sp(11));
        canvas.drawText(secondLine, cx, cy + dp(14), labelPaint);
    }

    private float dp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private float sp(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
}
