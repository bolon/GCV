package com.rere.fish.gcv.uicustoms;

/**
 * Thx to https://github.com/MostafaNasiri/CircularPulsingButton
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rere.fish.gcv.R;

/**
 * Created by Android dev on 5/14/17.
 */

public class CircularPulsingButton extends FrameLayout {
    private Circle backgroundCircle;
    private TextView tvButtonText;
    private OnClickListener listener;
    private float zoomOutScale;
    private float zoomInScale;
    private int animationDuration;

    public CircularPulsingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public CircularPulsingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircularPulsingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) listener.onClick(this);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (listener != null) listener.onClick(this);
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CircularPulsingButton);

        setZoomOutScale(arr.getFloat(R.styleable.CircularPulsingButton_cpb_zoomOutScale, 0.5f));
        setZoomInScale(arr.getFloat(R.styleable.CircularPulsingButton_cpb_zoomInScale, 1.1f));
        setAnimationDuration(arr.getInt(R.styleable.CircularPulsingButton_cpb_animationDuration, 100));

        addBackgroundCircle();
        setColor(arr.getColor(R.styleable.CircularPulsingButton_cpb_color, Color.GRAY));

        tvButtonText = new TextView(context);
        tvButtonText.setText(arr.getString(R.styleable.CircularPulsingButton_cpb_text));
        tvButtonText.setTextColor(arr.getColor(R.styleable.CircularPulsingButton_cpb_textColor, Color.BLACK));

        arr.recycle();
    }

    /**
     * Set the color of this button.
     *
     * @param color Color code
     */
    public void setColor(int color) {
        backgroundCircle.setColor(color);
    }

    public void setDrawable(Drawable d) {
        backgroundCircle.setDrawable(d);
    }

    public void setText(@StringRes int resId) {
        tvButtonText.setText(resId);
    }

    /**
     * Set the color of this button's text.
     *
     * @param color Color code
     */
    public void setTextColor(int color) {
        tvButtonText.setTextColor(color);
    }

    public String getText() {
        return tvButtonText.getText().toString();
    }

    /**
     * Set the string value of this button's TextView.
     */
    public void setText(String text) {
        tvButtonText.setText(text);
    }

    public float getZoomOutScale() {
        return zoomOutScale;
    }

    /**
     * @param scale Value must be between 0 and 1.
     */
    public void setZoomOutScale(float scale) {
        if (scale >= 1) {
            zoomOutScale = 0.9f;
        } else if (scale <= 0) {
            zoomOutScale = 0.1f;
        } else {
            zoomOutScale = scale;
        }
    }

    public float getZoomInScale() {
        return zoomInScale;
    }

    /**
     * @param scale Value must be between 1 and 2.
     */
    public void setZoomInScale(float scale) {
        if (scale <= 1) {
            zoomInScale = 1.1f;
        } else if (scale >= 2) {
            zoomInScale = 1.9f;
        } else {
            zoomInScale = scale;
        }
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    /**
     * @param duration Value must be greater than 0.
     */
    public void setAnimationDuration(int duration) {
        if (duration <= 0) {
            animationDuration = 100;
        } else {
            animationDuration = duration;
        }
    }

    private void addBackgroundCircle() {
        backgroundCircle = new Circle(getContext(), zoomInScale);
        backgroundCircle.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    scaleView(backgroundCircle, 1, zoomOutScale);
                    scaleView(tvButtonText, 1, zoomOutScale);
                    break;

                case MotionEvent.ACTION_UP:
                    scaleView(backgroundCircle, zoomOutScale, 1f);
                    scaleView(tvButtonText, zoomOutScale, 1f);
                    scaleView(backgroundCircle, zoomInScale, 1f);
                    scaleView(tvButtonText, zoomInScale, 1f);
                    break;
            }
            return true;
        });
        addView(backgroundCircle);
        backgroundCircle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addButtonTextView();
                backgroundCircle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void addButtonTextView() {
        int width = backgroundCircle.getWidth() - (backgroundCircle.getWidth() * 35 / 100);
        int height = backgroundCircle.getHeight() - (backgroundCircle.getHeight() * 50 / 100);
        LayoutParams params = new LayoutParams(width, height, Gravity.CENTER);
        addView(tvButtonText, params);
        tvButtonText.setGravity(Gravity.CENTER);
    }

    private void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(startScale, endScale, startScale, endScale, Animation.ABSOLUTE, v.getWidth() / 2, // Pivot point of X scaling
                Animation.ABSOLUTE, v.getHeight() / 2); // Pivot point of Y scaling
        anim.setFillAfter(true);
        anim.setDuration(animationDuration);
        v.startAnimation(anim);
    }

    private class Circle extends View {
        private Paint paint;
        private float zoomInScale;
        private Drawable d;

        private Circle(Context context, float zoomInScale) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);
            this.zoomInScale = zoomInScale;
        }

        private void setColor(int color) {
            paint.setColor(color);
            invalidate();
        }

        private void setDrawable(Drawable d) {
            this.d = d;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float cx = canvas.getWidth() / 2;
            float cy = canvas.getHeight() / 2;

            float radius = cx - (cx * ((zoomInScale - 1) * 100) / 100);

            canvas.drawCircle(cx, cy, radius, paint);

            if (d != null) {
                d.setBounds((int) (getLeft() + cx / 2), (int) (getTop() + cy / 2), (int) (getRight() - cx / 2), (int) (getBottom() - cy / 2));
                d.draw(canvas);
            }
        }
    }
}