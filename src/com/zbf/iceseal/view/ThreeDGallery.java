package com.zbf.iceseal.view;

import java.util.List;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zbf.iceseal.bean.SongBean;

public class ThreeDGallery extends SpecialGallery implements SpecialGallery.OnGalleryChildMoveListener{

	/**
	 * 相机类对象，可能时要使用其中某些功能
	 */
	private Camera mCamera = new Camera();

	/**
	 * 最大旋转角度
	 */
	private int mMaxRotationAngle = 60;

	/**
	 * 最大缩放值
	 */
	private int mMaxZoom = -380;

	/**
	 * 半径值(貌似是 当前窗口的 横坐标的中心点)
	 */
	private int mCoveflowCenter;

	/**
	 * 是否透明
	 */
	private boolean mAlphaMode = true;

	/**
	 * 是否循环
	 */
	private boolean mCircleMode = false;

	public ThreeDGallery(Context context) {
		super(context);
		/**
		 * 支持转换，执行getChildStaticTransformation方法 Transformation英文意思是转化
		 */
		this.setStaticTransformationsEnabled(true);
		setOnGalleryChildMoveListener(this);
	}

	public ThreeDGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
		setOnGalleryChildMoveListener(this);
	}

	public ThreeDGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
		setOnGalleryChildMoveListener(this);
	}

	/**
	 * 得到最大旋转角度
	 * 
	 * @return
	 */
	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	/**
	 * 设置最大旋转角度
	 * 
	 * @param mMaxRotationAngle
	 */
	public void setmMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	/**
	 * 得到最大缩放值
	 * 
	 * @return
	 */
	public int getMaxZoom() {
		return mMaxZoom;
	}

	/**
	 * 设置最大缩放值
	 * 
	 * @param maxZoom
	 */
	public void setmMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	/**
	 * 得到半径值
	 * 
	 * @return
	 */
	public int getCoveflowCenter() {
		return mCoveflowCenter;
	}

	/**
	 * 设置半径值
	 * 
	 * @param mCoveflowCenter
	 */
	public void setCoveflowCenter(int coveflowCenter) {
		mCoveflowCenter = coveflowCenter;
	}

	/**
	 * 得到是否透明模式
	 * 
	 * @return
	 */
	public boolean isAlphaMode() {
		return mAlphaMode;
	}

	/**
	 * 设置是否允许透明模式
	 * 
	 * @param mAlphaMode
	 */
	public void setAlphaMode(boolean alphaMode) {
		mAlphaMode = alphaMode;
	}

	/**
	 * 得到当前是否循环模式
	 * 
	 * @return
	 */
	public boolean isCircleMode() {
		return mCircleMode;
	}

	/**
	 * 设置循环与否
	 * 
	 * @param mCircleMode
	 */
	public void setCircleMode(boolean circleMode) {
		mCircleMode = circleMode;
	}

	/**
	 * 貌似是得到当前Gallery的中心点
	 * 
	 * @return
	 */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	/**
	 * 得到view中心点的横坐标
	 * 
	 * @param view
	 * @return view.getLeft() + view.getWidth() / 2;
	 */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;

	}
	
	@Override
	public void onChildMoving(int offset, View child, Transformation t) {
		// 得到child横坐标的中心
		final int childCenter = getCenterOfView(child);
		// 得到child的宽度
		final int childWidth = child.getWidth();
		// 旋转角度
		int rotationAngle = 0;
		// 重置转换状态
		t.clear();
		// 设置转换类型(MATRIX----矩阵)
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if (childCenter == mCoveflowCenter) {
			// 如果是在中间则 旋转角度为0 三个参数分别是(要操作的图片，Transformation对象， 旋转角度)
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// 如果没在中间则 用下面的代码处理这张图片
			// 计算旋转角度
			// 值1= 屏幕中心点横坐标-当前图片中心点横坐标
			// 值2=(float)值1/图片宽度
			// 值3=值2*最大旋转角度
			// 当前照片应该旋转的角度= (int)值3
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			/*
			 * 下面的意思是 如果 当前旋转角度的绝对值 大于 最大旋转角度 则设置旋转角度为 (rotationAngle < 0) ?
			 * -mMaxRotationAngle : mMaxRotationAngle;意思是 当前旋转角度 大于0 则设置为 最大旋转角度
			 * 否则 设置为 最大旋转角度 的 负值
			 */
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			// 将此 旋转角度设置进去 三个参数分别是(要操作的ImageView，Transformation对象， 旋转角度)
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
	}

	/**
	 * 这就是所谓的在大小的布局时,这一观点已经发生了改变。如果 你只是添加到视图层次,有人叫你旧的观念 价值观为0。
	 * 根据方法名判断当视图大小改变时会调用此方法
	 * 
	 * @param w
	 *            Current width of this view. 当前视图的宽
	 * @param h
	 *            Current height of this view. 当前视图的高
	 * @param oldw
	 *            Old width of this view. 之前视图的宽
	 * @param oldh
	 *            Old height of this view. 之前视图的高
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 重写的原因在于 当视图大小改变时需要重新获取 屏幕中心的 横坐标
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 把图像位图的角度通过
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate 要被旋转的图片
	 * @param t
	 *            transformation 变换
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap 要旋转的角度
	 */
	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {

		// 对效果进行保存
		// 每次 save()都必须对应的在后面又一次 restore() ！！！
		mCamera.save();
		// 得到 用于当前转换 的 矩阵
		final Matrix imageMatrix = t.getMatrix();
		// 得到当前图片的高
		final int imageHeight = child.getLayoutParams().height;
		// 得到当前图片的宽
		final int imageWidth = child.getLayoutParams().width;
		// 旋转角度 ， 对传进来的旋转角度 取绝对值
		final int rotation = Math.abs(rotationAngle);
		//平移
		mCamera.translate(0.0f, 0.0f, 100.0f);
		// 如视图的角度更少,放大
		if (rotation <= mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			
			//translate  平移貌似
			mCamera.translate(0.0f, 0.0f, zoomAmount);
			if (mCircleMode) {
				if (rotation < 40)
					//translate  平移貌似
					mCamera.translate(0.0f, 155, 0.0f);
				else
					mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
			}
			if (mAlphaMode) {
				// 根据旋转角度设置透明度
				((ImageView) (child)).setAlpha((int) (255 - rotation * 2.5));
			}

		}

		//绕Y轴旋转
		mCamera.rotateY(rotationAngle);
		//将mCamera的效果应用到矩阵中
		mCamera.getMatrix(imageMatrix);
		//设置中心点
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		
		//重置mCamera对象
		mCamera.restore();
	}

	public static class ThreeDGalleryAdapter extends BaseAdapter {

		private List<SongBean> songlist;
		private Context context;

		public ThreeDGalleryAdapter(Context context, List<SongBean> songlist) {
			this.context = context;
			this.songlist = songlist;
		}

		@Override
		public int getCount() {
			if (songlist != null) {
				return songlist.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (songlist != null) {
				return songlist.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// ImageTools.getMirrorBitmap(source, angle, rotateWhere,
			// mirrorLength);
			return null;
		}

	}

}
