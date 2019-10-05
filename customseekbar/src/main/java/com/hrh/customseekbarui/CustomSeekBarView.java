package com.hrh.customseekbarui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;

import java.util.ArrayList;

public class CustomSeekBarView extends View implements View.OnTouchListener {
    private static final String TAG = CustomSeekBarView.class.getSimpleName();

    private int mProgress = 0;
    private int mPreviousAction = -99;
    private int MAX_COUNT = 0;
    private final int MIN_COUNT = 0;
    private float valX = 0;
    private final Context mContext;
    private int mScaledTouchSlop;
    private int mThumbOffset;

    private Drawable mThumbMark;
    private boolean mIsDragging;
    private float mTouchDownX;

    private ArrayList<com.hrh.customseekbarui.TickMark> switchTicksList;

    private OnCustomSeekBarChangeListener mOnCustomSeekBarChangeListener;

    public CustomSeekBarView(Context context) {
        super(context);
        setOnTouchListener(this);
        mContext = context;
    }

    public CustomSeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOnTouchListener(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBarView);
        try {
            //get the thumbDrawable & tickMark Drawable  specified using the names in attrs.xml
            mThumbMark = a.getDrawable(R.styleable.CustomSeekBarView_customTumb);
            setThumbOffSet();
            setInitiallAxisToDraw();
        } finally {
            a.recycle();
        }

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawThumb(canvas);
        drawTickMarks(canvas);
    }

    public void setSwitchTicksList(ArrayList<TickMark> switchTicksList) {
        this.switchTicksList = switchTicksList;
        MAX_COUNT = switchTicksList.size() - 1;
    }

    private void drawTickMarks(Canvas canvas) {
        if (switchTicksList != null && switchTicksList.size() > 0) {
            TickMark tickMarkObj = switchTicksList.get(0);
            Drawable tickEnableDraw = tickMarkObj.getmTickMarkEnabled();
            if (tickEnableDraw != null) {
                final int count = MAX_COUNT - MIN_COUNT;
                if (count > 0) {
                    final int w = tickEnableDraw.getIntrinsicWidth();
                    final int h = tickEnableDraw.getIntrinsicHeight();
                    final int halfW = w >= 0 ? w / 2 : 1;
                    final int halfH = h >= 0 ? h / 2 : 1;

                    final float spacing = (getWidth() - (getPaddingLeft() + mThumbOffset) - (getPaddingRight() + mThumbOffset)) / (float) count;
                    final int saveCount = canvas.save();
                    canvas.translate(getPaddingLeft() + mThumbOffset, getHeight() / 2);
                    for (int i = 0; i <= count; i++) {
                        TickMark tickMark = switchTicksList.get(i);
                        Drawable tickEnable = tickMark.getmTickMarkEnabled();
                        Drawable tickDisabled = tickMark.getTickMarkDisabled();
                        tickEnable.setBounds(-halfW, -halfH, halfW, halfH);
                        tickDisabled.setBounds(-halfW, -halfH, halfW, halfH);
                        if (mProgress == i) {
                            tickEnable.draw(canvas);
                            setProgress(mProgress);
                        } else {
                            tickDisabled.draw(canvas);
                        }
                        canvas.translate(spacing, 0);
                    }
                    canvas.restoreToCount(saveCount);
                }
            }
        }
    }

    /*    private void drawThumb(Canvas canvas) {
        if (mThumbMark != null) {
            final int w = mThumbMark.getIntrinsicWidth();
            final int h = mThumbMark.getIntrinsicHeight();
            final int halfW = w >= 0 ? w / 2 : 1;
            final int halfH = h >= 0 ? h / 2 : 1;
            mThumbMark.setBounds(-halfW, -halfH, halfW, halfH);
            final int saveCount = canvas.save();
            // Translate the padding. For the x, we need to allow the thumb to
            canvas.translate(valX, getHeight() / 2);
            mThumbMark.draw(canvas);
            canvas.restoreToCount(saveCount);
            invalidate();
        }

    }*/


    /**
     * Draw the thumb using canvas drawcircle
     */
    private void drawThumb(Canvas canvas) {
        Paint paint = new Paint();
        if (mProgress == 0) {
            paint.setColor(mContext.getResources().getColor(R.color.snack_bar_background));
        } else {
            paint.setColor(mContext.getResources().getColor(R.color.red));
        }
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        int radius = (int) mContext.getResources().getDimension(R.dimen.seek_bar_height);
        //mThumbOffset = mTickMark.getIntrinsicWidth() / 2;
        canvas.drawCircle(valX, getHeight() / 2, radius / 2, paint);
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchDownX = event.getX();
                    Log.d(TAG, "ACTION_DOWN");
                    startDrag(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ACTION_MOVE");
                    if (mIsDragging) {
                        Log.d(TAG, "mIsDragging");
                        trackTouchEvent(event);
                    } else {
                        Log.d(TAG, "mIsDragging not");
                        final float x = event.getX();
                        if (Math.abs(x - mTouchDownX) > mScaledTouchSlop) {
                            startDrag(event);
                            Log.d(TAG, "startDrag");
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ACTION_UP");
                    if (mIsDragging) {
                        Log.d(TAG, "mIsDragging in ACTION_UP");
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                        setPressed(false);
                    } else {
                        Log.d(TAG, "mIsDragging not in ACTION_UP");
                        // Touch up when we never crossed the touch slop threshold should
                        // be interpreted as a tap-seek to that location.
                        onStartTrackingTouch();
                        trackTouchEvent(event);
                        onStopTrackingTouch();

                    }
                    // ProgressBar doesn't know to repaint the thumb drawable
                    // in its inactive state when the touch stops (because the
                    // value has not apparently changed)
                    setThumbPos(getWidth(), mThumbMark, getScale());
                    Log.d(TAG, "mPreviousAction:" + mPreviousAction + " & mProgress:" + getProgress());
                    if (mPreviousAction != switchTicksList.get(getProgress()).mAction) {
                        mPreviousAction = switchTicksList.get(getProgress()).mAction;
                        if (mOnCustomSeekBarChangeListener != null) {
                            mOnCustomSeekBarChangeListener.onSwitchProgressChanged(this, getProgress(), true, switchTicksList.get(getProgress()));
                        }
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mIsDragging) {
                        onStopTrackingTouch();
                        setPressed(false);
                    }
                    invalidate(); // see above explanation
                    break;
            }
        }
        return true;
    }

    public void setPriousAction(int prevAction) {
        mPreviousAction = prevAction;
    }

    private void trackTouchEvent(MotionEvent event) {
        final int x = Math.round(event.getX());
        final int width = getWidth();
        final int availableWidth = width - getPaddingLeft() - getPaddingRight();
        //int availableWidth = width - (getPaddingLeft()+mThumbOffset) - (getPaddingRight()+mThumbOffset);
        int mTouchProgressOffset = 0;
        final float scale;

        //If x value is lesser the getPaddingLeft() + mThumbOffset, it means at starting or of mProgress 0
        if (x < (getPaddingLeft() + mThumbOffset * 2)) {
            Log.d(TAG, "set mProgress to 0 as x < (getPaddingLeft()");
            scale = 0.0f;
            valX = scale + mThumbOffset;
            mProgress = 0;
        } else if (x > availableWidth - mThumbOffset * 2) {
            Log.d(TAG, "set mProgress to max as x > availableWidth");
            Log.d(TAG, "x > width");
            valX = availableWidth - mThumbOffset;
            mProgress = MAX_COUNT;
        } else {
            Log.d(TAG, "set mProgress based on scale");
            valX = x;
            final int thumbWidth = mThumbMark.getIntrinsicWidth();
            // availableWidth -= thumbWidth;
            scale = (x + (thumbWidth)) / (float) (availableWidth);
            mProgress = mTouchProgressOffset;

            final int range = MAX_COUNT - MIN_COUNT;
            Log.d(TAG, "scale * range:" + scale * range);
            mProgress += scale * range;
            mProgress = Math.round(mProgress);
        }

        Log.d(TAG, "trackTouchEvent mProgress" + Math.round(mProgress));

        mProgress = MathUtils.clamp(mProgress, MIN_COUNT, MAX_COUNT);
    }

    /*
   This methos will return the scale based on the mProgress
   */
    private float getScale() {
        int min = 0;
        int max = MAX_COUNT;
        int range = max - min;
        float scale = range > 0 ? (mProgress - min) / (float) range : 0;
        Log.d(TAG, "valX in scale" + scale);
        return scale;
    }

    /*
      This method will set the thumb to exact position of tickmark
    */
    private void setThumbPos(int w, Drawable thumb, float scale) {
        int available = w - (getPaddingLeft() + mThumbOffset) - (getPaddingRight() + mThumbOffset);
        final int thumbWidth = thumb.getIntrinsicWidth();
        available -= thumbWidth;

        // The extra space for the thumb to move on the track
        available += mThumbOffset * 2;

        final int thumbPos = (int) (scale * available + 0.5f);

        //Canvas will be translated to  valX
        valX = thumbPos + mThumbOffset;

    }


    private void startDrag(MotionEvent event) {
        setPressed(true);

        if (mThumbMark != null) {
            // This may be within the padding region.
            invalidate(mThumbMark.getBounds());
        }

        onStartTrackingTouch();
        trackTouchEvent(event);
    }

    /**
     * This is called when the user has started touching this widget.
     */
    private void onStartTrackingTouch() {
        mIsDragging = true;
        if (mOnCustomSeekBarChangeListener != null) {
            mOnCustomSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }


    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    private void onStopTrackingTouch() {
        mIsDragging = false;
        if (mOnCustomSeekBarChangeListener != null) {
            mOnCustomSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    /**
     * A callback that notifies clients when the mProgress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnCustomSeekBarChangeListener {
        /**
         * Notification that the mProgress level has changed. User can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         * <p>
         * //@param customSeekBar The customSeekBar whose mProgress has changed
         * //@param mProgress The current mProgress level. This will be in the range min..max
         * //@param fromUser True if the mProgress change was initiated by the user.
         */
        void onSwitchProgressChanged(CustomSeekBarView customSeekBar, int progress, boolean fromUser, TickMark tickMark);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         * <p>
         * //@param customSeekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(CustomSeekBarView customSeekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         * <p>
         * //@param customSeekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(CustomSeekBarView customSeekBar);

    }

    /**
     * Sets a listener to receive notifications of changes to the CustomSeekBar's mProgress level. Also
     * provides notifications of when the user starts and stops a touch gesture within the SeekBar.
     *
     * @param l The seek bar notification listener
     * @see OnCustomSeekBarChangeListener
     */
    public void setOnCustomSeekBarChangeListener(OnCustomSeekBarChangeListener l) {
        mOnCustomSeekBarChangeListener = l;
    }

    private int getProgress() {
        return mProgress;
    }

    //Will set the progrss & move the thumb to that position
    public void setProgress(int progress) {
        Log.d(TAG, "setProgress:" + progress);
        if (isEnabled()) {
            mProgress = progress;
            setThumbPos(getWidth(), mThumbMark, getScale());
        }
    }

    //Starting X_axis & Y_axis initialization for drawing to start
    //draw a thumb at xaxis where valx = 0 & mThumbOffset = radius of the thumb
    private void setInitiallAxisToDraw() {
        valX = valX + getThumbOffset();
    }

    //ThumbOffset : get the width of the thumb / 2,so you get the center/radius of it.
    private void setThumbOffSet() {
        mThumbOffset = mThumbMark.getIntrinsicWidth() / 2;
    }

    //returns the X_axis of the thumb
    public float getThumbXaxis() {
        return valX;
    }

    //returns the thumb offset of the thumb
    private float getThumbOffset() {
        return mThumbOffset;
    }

    //returns the thumb offset of the thumb
    public void setMax(int max) {
        MAX_COUNT = max;
    }

}
