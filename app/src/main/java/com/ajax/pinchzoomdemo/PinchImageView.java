package com.ajax.pinchzoomdemo;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/10/21.
 */
public class PinchImageView extends ImageView {
    private int mode = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();
    private Matrix startMatrix = new Matrix();

    private GestureDetector mGestureDetector;

    private float startHeight;
    private float startWidth;
    private float startDis;
    private PointF midPoint;

    public PinchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MatrixTouchListener mListener = new MatrixTouchListener();
        setOnTouchListener(mListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
    }

    public class MatrixTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    startMatrix = getMatrix();
                    currentMatrix.set(getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    startHeight = getMeasuredHeight();
                    startWidth = getMeasuredWidth();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x;
                        float dy = event.getY() - startPoint.y;
                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                    } else if (mode == MODE_ZOOM) {
                        float endDis = distance(event);
                        if (endDis > 10f) {
                            float scale = endDis / startDis;
                            matrix.set(currentMatrix);
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    startDis = distance(event);
                    if (startDis > 10f) {
                        midPoint = mid(event);
                        currentMatrix.set(getImageMatrix());
                    }
                    break;
            }
            setImageMatrix(matrix);
            return mGestureDetector.onTouchEvent(event);
        }

        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        private PointF mid(MotionEvent event) {
            float midX = (event.getX(1) + event.getX(0)) / 2;
            float midY = (event.getY(1) + event.getY(0)) / 2;
            return new PointF(midX, midY);
        }

        //FIXME 判定缩放扩大和移动后进行还原
        public void onDoubleClick() {
            float scale = 1;
            if (startMatrix != null) {
                currentMatrix.set(startMatrix);
                currentMatrix.postScale(scale, scale, startWidth, startHeight);
                setImageMatrix(currentMatrix);
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final MatrixTouchListener listener;

        public GestureListener(MatrixTouchListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onDown(MotionEvent e) { //捕获Down事件
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) { //触发双击事件
            listener.onDoubleClick();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX,
                    distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }
}
