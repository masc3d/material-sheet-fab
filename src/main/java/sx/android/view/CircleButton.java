package sx.android.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import sx.android.R;

/**
 * Circle button
 */
public class CircleButton extends ImageView {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int DEFAULT_OUTER_RING_WIDTH_DIP = 2;
    private static final int DEFAULT_OUTER_RING_MARGIN_DIP = 2;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int mCenterY;
    private int mCenterX;
    private int mOuterRadius;
    private int mPressedRingRadius;
    private int mOuterRingWidth;
    private int mOuterRingMargin;

    private Paint mCirclePaint;
    private Paint mOuterCirclePaint;
    private Paint mFocusPaint;

    private float mAnimationProgress;

    private int mPressedRingWidth;
    private int mDefaultColor = Color.BLACK;
    private int mInnerColor = Color.BLACK;
    private int mOuterColor = Color.BLACK;
    private int mPressedColor;
    private ObjectAnimator mPressedAnimator;

    public CircleButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (mCirclePaint != null) {
            mCirclePaint.setColor(pressed ? mPressedColor : mInnerColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mPressedRingRadius + mAnimationProgress, mFocusPaint);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius - mPressedRingWidth - mOuterRingWidth - mOuterRingMargin, mCirclePaint);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius - mPressedRingWidth, mOuterCirclePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mOuterRadius = Math.min(w, h) / 2;
        mPressedRingRadius = mOuterRadius - mPressedRingWidth - mPressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return mAnimationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.mAnimationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
        this.mDefaultColor = color;
        this.mInnerColor = color;
        this.mOuterColor = color;
        this.mPressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        mCirclePaint.setColor(mInnerColor);
        mOuterCirclePaint.setColor(mOuterColor);
        mFocusPaint.setColor(mDefaultColor);
        mFocusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    public void setInnerColor(int color) {
        mInnerColor = color;
        mCirclePaint.setColor(mInnerColor);
        this.invalidate();
    }

    public void setOuterColor(int color) {
        mOuterColor = color;
        mOuterCirclePaint.setColor(mOuterColor);
        this.invalidate();
    }

    private void hidePressedRing() {
        mPressedAnimator.setFloatValues(mPressedRingWidth, 0f);
        mPressedAnimator.start();
    }

    private void showPressedRing() {
        mPressedAnimator.setFloatValues(mAnimationProgress, mPressedRingWidth);
        mPressedAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);

        // Convert DIP of default values
        mPressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());
        mOuterRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_OUTER_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());
        mOuterRingMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_OUTER_RING_MARGIN_DIP, getResources()
                .getDisplayMetrics());

        // Read attributes, override defaults
        int color = Color.BLACK;
        int outerColor = color;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
            color = a.getColor(R.styleable.CircleButton_cb_color, color);
            outerColor = a.getColor(R.styleable.CircleButton_cb_outerColor, outerColor);
            mPressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, mPressedRingWidth);
            mOuterRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_outerRingWidth, mOuterRingWidth);
            mOuterRingMargin = (int) a.getDimension(R.styleable.CircleButton_cb_outerRingMargin, mOuterRingMargin);
            a.recycle();
        }

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFocusPaint.setStyle(Paint.Style.STROKE);
        mFocusPaint.setStrokeWidth(mPressedRingWidth);

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(mOuterRingWidth);

        setColor(color);
        setOuterColor(outerColor);

        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        mPressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        mPressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }
}