package com.zbf.iceseal.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Gallery;

public class SpecialGallery extends Gallery implements AdapterView.OnItemSelectedListener{

	private int mCoveflowCenter;
	private OnGalleryChildMoveListener mOnGalleryChildMoveListener;
	private OnItemSelectedListener mOnItemSelectedListener;
	private int selectedPosition;
	
	public SpecialGallery(Context context) {
		super(context);
		setStaticTransformationsEnabled(true);
		super.setOnItemSelectedListener(this);
	}

	public SpecialGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStaticTransformationsEnabled(true);
		super.setOnItemSelectedListener(this);
	}

	public SpecialGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setStaticTransformationsEnabled(true);
		super.setOnItemSelectedListener(this);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
		super.setOnItemSelectedListener(this);
	}
	
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		if(mOnGalleryChildMoveListener == null) {
			return super.getChildStaticTransformation(child, t);
		}
		if(getPositionForView(child) == selectedPosition) {
			float offset = (getCenterOfView(child) - mCoveflowCenter) * 100.0f / child.getWidth() + 0.5f;
			if(offset < -100 || offset > 100) {
				return super.getChildStaticTransformation(child, t);
			}
			mOnGalleryChildMoveListener.onChildMoving((int) offset, child, t);
		}
		return true;
	}
	
	private int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void setOnGalleryChildMoveListener(OnGalleryChildMoveListener listener) {
		mOnGalleryChildMoveListener = listener;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
        if(isScrollingLeft(e1, e2)){
          kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
          kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;  
	}
	
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){
        return e2.getX() > e1.getX();
	}
	
	public void moveToNext() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		mTrackMotionScroll(-1);
		onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}
	
	public void moveToLast() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		mTrackMotionScroll(1);
		onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	}
	
	private void mTrackMotionScroll(int arg) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method1 = Gallery.class.getDeclaredMethod("trackMotionScroll", new Class[]{int.class});
		method1.setAccessible(true);
		method1.invoke(this, new Object[]{arg});
	}
	
	public static interface OnGalleryChildMoveListener {
		/**
		 * 
		 * @param offset -100 ~ 100 (负 左  正 右)
		 * @param child
		 * @param t
		 */
		public void onChildMoving(int offset, View child, Transformation t);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if(mOnItemSelectedListener != null) {
			mOnItemSelectedListener.onItemSelected(parent, view, position, id);
		}
		selectedPosition = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
}
