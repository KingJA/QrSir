/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kingja.qrsir.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.kingja.qrsir.R;
import com.kingja.qrsir.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ScanView extends View {
    private static final String TAG = "ScanView";
    private static final int DEFAULT_MASK_COLOR = 0x60000000;
    private static final int DEFAULT_LASER_COLOR = 0xffcc0000;
    private static final int DEFAULT_SCAN_RECT = -1;
    private static final int DEFAULT_SCAN_DURATION = 2000;
    private static final int LASER_HEIGHT = 1;
    private int laserColor;
    private int maskColor;
    private int scanLineRes;
    private int scanRectRes;
    private int scanDuration;
    private CameraManager cameraManager;
    private Paint paint;
    private Bitmap scanRectBitmap;
    private Bitmap scanLineBitmap;
    private boolean hasDraw;
    private int scanLineY;

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScanView(context, attrs);
    }

    private void initScanView(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initBitmaps();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScanView);
        laserColor = a.getColor(R.styleable.ScanView_laserColor, DEFAULT_LASER_COLOR);
        maskColor = a.getColor(R.styleable.ScanView_maskColor, DEFAULT_MASK_COLOR);
        scanRectRes = a.getResourceId(R.styleable.ScanView_scanRectRes, DEFAULT_SCAN_RECT);
        scanLineRes = a.getResourceId(R.styleable.ScanView_scanLineRes, DEFAULT_SCAN_RECT);
        scanDuration = a.getInt(R.styleable.ScanView_scanDuration, DEFAULT_SCAN_DURATION);
        a.recycle();
    }

    private void initBitmaps() {
        if (scanLineRes != -1) {
            scanLineBitmap = BitmapFactory.decodeResource(getResources(), scanLineRes);
        }
        if (scanRectRes != -1) {
            scanRectBitmap = BitmapFactory.decodeResource(getResources(), scanRectRes);
        }
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        drawMask(canvas, frame, width, height);
        drawScanRect(canvas, frame);
        drawScanLine(canvas, frame);
        if (!hasDraw) {
            startAnimation(frame);
            hasDraw = true;
        }
    }

    private void startAnimation(final Rect frame) {
        ValueAnimator scanLineAnimator = ValueAnimator.ofInt(frame.top, frame.bottom);
        scanLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scanLineY = (int) animation.getAnimatedValue();
                invalidate(frame.left, frame.top, frame.right, frame.bottom);
            }
        });
        scanLineAnimator.setDuration(scanDuration);
        scanLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanLineAnimator.start();
    }

    private void drawScanLine(Canvas canvas, Rect frame) {
        if (scanLineBitmap != null) {
            canvas.drawBitmap(scanLineBitmap, new Rect(0, 0, scanLineBitmap.getWidth(), scanLineBitmap.getHeight()), new
                    Rect(frame.left, scanLineY - scanLineBitmap.getHeight(), frame.right, scanLineY + scanLineBitmap
                    .getHeight()), paint);
        } else {
            paint.setColor(laserColor);
            canvas.drawRect(frame.left, scanLineY, frame.right, scanLineY + dp2px(LASER_HEIGHT), paint);
        }
    }

    private void drawScanRect(Canvas canvas, Rect frame) {
        if (scanRectBitmap != null) {
            canvas.drawBitmap(scanRectBitmap, new Rect(0, 0, scanRectBitmap.getWidth(), scanRectBitmap.getHeight()), new
                    Rect(frame.left, frame.top, frame.right, frame.bottom), paint);
        }
    }

    private void drawMask(Canvas canvas, Rect frame, int width, int height) {
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
        canvas.drawRect(0, frame.bottom, width, height, paint);
    }

    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getContext().getResources()
                .getDisplayMetrics());
    }
}
