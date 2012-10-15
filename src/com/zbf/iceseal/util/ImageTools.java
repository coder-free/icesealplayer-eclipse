package com.zbf.iceseal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.zbf.iceseal.R;
import com.zbf.iceseal.tag.id3.ID3Wrapper;

public class ImageTools {
	private static List<String> loadingPath;
	private static List<String> loadingUrl;
	public static void loadAlbumImage(final int defaultResid, final String mp3FilePath,
			final ImageView imageView, final Quality qualitys) {
		if(loadingPath == null) {
			loadingPath = new ArrayList<String>();
		}
		final String tempPath = getImageCachePath(mp3FilePath);
		if (!new File(tempPath).exists() && !loadingPath.contains(mp3FilePath)) {
			final int quality;
			final int inSampleSize;
			if(qualitys != null) {
				quality = qualitys.quality;
				inSampleSize = qualitys.inSampleSize;
			} else {
				quality = 75;
				inSampleSize = 4;
			}
			loadingPath.add(mp3FilePath);
			new AsyncTask<String, Integer, Bitmap>() {
				@Override
				protected void onPreExecute() {
					imageView.setImageResource(defaultResid);
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					loadingPath.remove(mp3FilePath);
					if(result != null && imageView != null) {
						imageView.setImageBitmap(result);
					}
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
				}

				@Override
				protected Bitmap doInBackground(String... params) {
					Bitmap bmp = getAlbumImage(params[0], inSampleSize);
					if (bmp != null) {
						try {
							new File(tempPath.substring(0,
									tempPath.lastIndexOf("/"))).mkdirs();
							File file = new File(tempPath);
							file.createNewFile();
							FileOutputStream stream = new FileOutputStream(file);
							bmp.compress(CompressFormat.PNG, quality, stream);
							stream.flush();
							stream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return bmp;
				}
			}.execute(mp3FilePath);
		} else if (loadingPath.contains(mp3FilePath)) {
			imageView.setImageResource(defaultResid);
		} else {
			imageView.setImageBitmap(BitmapFactory.decodeFile(tempPath));
		}
	}
	
	public static void loadAlbumImage(final String mp3FilePath,
			final ImageView imageView, final Quality qualitys) {
		if(loadingPath == null) {
			loadingPath = new ArrayList<String>();
		}
		final String tempPath = getImageCachePath(mp3FilePath);
		if (!new File(tempPath).exists() && !loadingPath.contains(mp3FilePath)) {
			System.out.println("加载新");
			final int quality;
			final int inSampleSize;
			if(qualitys != null) {
				quality = qualitys.quality;
				inSampleSize = qualitys.inSampleSize;
			} else {
				quality = 75;
				inSampleSize = 4;
			}
			loadingPath.add(mp3FilePath);
			new AsyncTask<String, Integer, Bitmap>() {
				@Override
				protected void onPreExecute() {
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					loadingPath.remove(mp3FilePath);
					if(result != null && imageView != null) {
						imageView.setImageBitmap(result);
					}
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
				}

				@Override
				protected Bitmap doInBackground(String... params) {
					Bitmap bmp = getAlbumImage(params[0], inSampleSize);
					if (bmp != null) {
						try {
							new File(tempPath.substring(0,
									tempPath.lastIndexOf("/"))).mkdirs();
							File file = new File(tempPath);
							file.createNewFile();
							FileOutputStream stream = new FileOutputStream(file);
							bmp.compress(CompressFormat.PNG, quality, stream);
							stream.flush();
							stream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return bmp;
				}
			}.execute(mp3FilePath);
		} else {
			imageView.setImageBitmap(BitmapFactory.decodeFile(tempPath));
		}
	}
	
	public static void loadWebImage(final int defaultResid, final String imageUrl,
			final ImageView imageView, final Quality qualitys) {
		if(loadingUrl == null) {
			loadingUrl = new ArrayList<String>();
		}
		final String tempPath = getImageCachePath(imageUrl);
		final File file = new File(tempPath);
		if (!file.exists() && !loadingUrl.contains(imageUrl)) {
			final int quality;
			final int inSampleSize;
			if(qualitys != null) {
				quality = qualitys.quality;
				inSampleSize = qualitys.inSampleSize;
			} else {
				quality = 75;
				inSampleSize = 4;
			}
			loadingUrl.add(imageUrl);
			new AsyncTask<String, Integer, Bitmap>() {
				@Override
				protected void onPreExecute() {
					imageView.setImageResource(defaultResid);
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					loadingUrl.remove(imageUrl);
					if(result != null) {
						imageView.setImageBitmap(result);
					}
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
				}

				@Override
				protected Bitmap doInBackground(String... params) {
					Bitmap bmp = null;
					try {
						bmp = getWebImage(params[0], inSampleSize);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (bmp != null) {
						try {
							File dirs = new File(tempPath.substring(0,
									tempPath.lastIndexOf("/")));
							if(!dirs.exists()) {
								dirs.mkdirs();
							}
							file.createNewFile();
							FileOutputStream stream = new FileOutputStream(file);
							bmp.compress(CompressFormat.PNG, quality, stream);
							stream.flush();
							stream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return bmp;
				}
			}.execute(imageUrl);
		} else if (loadingUrl.contains(imageUrl)) {
			imageView.setImageResource(defaultResid);
		} else {
			imageView.setImageBitmap(BitmapFactory.decodeFile(tempPath));
		}
	}
	
	public static Bitmap getAlbumImage(String path, int inSampleSize) {
		byte[] imageByte;
		try {
			imageByte = new ID3Wrapper(path).getAlbumImage();
			if(imageByte != null) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = inSampleSize;
				return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getAlbumImage(String path, Resources res, int defaultId) {
		Bitmap bmp = BitmapFactory.decodeFile(getImageCachePath(path));
		if(bmp == null) {
			bmp= BitmapFactory.decodeResource(res, defaultId);
		}
		return bmp;
	}

	public static Bitmap getWebImage(String imageUrl, int inSampleSize) throws Exception {
		URL url = new URL(imageUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("POST");
		httpConn.setReadTimeout(6 * 1000);
		if(httpConn.getResponseCode() == 200) {
			InputStream stream = httpConn.getInputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = inSampleSize;
			Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);
			stream.close();
			return bmp;
		}
		return null;
	}
	
	public static String getImageCachePath(String path) {
		if(path.startsWith("http://")) {
			return CommonData.IMG_TEMPPATH + "/web" + path.replace("http://", "/");
		} else if(path.startsWith("https://")) {
			return CommonData.IMG_TEMPPATH + "/web" + path.replace("https://", "/");
		}
		return path.replace(CommonData.EXTERNALSTORAGEDIRECTORY, CommonData.IMG_TEMPPATH + "/local").replace(".mp3", "-");
	}
	
	public static class Quality {
		/** 缓存图片质量: 0~100. 默认为 75. */
		public int quality = 75;
		/** 取样值,取原图 1/inSampleSize 的数据. 默认为 4. */
		public int inSampleSize = 4;
		public Quality() {
		}
		public Quality(int quality, int inSampleSize) {
			this.quality = quality;
			this.inSampleSize = inSampleSize;
		}
	}
	
	public static final int ROTATE_CENTER = 0;
	public static final int ROTATE_LEFT = 1;
	public static final int ROTATE_RIGHT = 2;
	
	public static Bitmap getMirrorBitmap(Bitmap source, int angle, int rotateWhere, int mirrorLength) {
		final int reflectionGap = 2;
		int width = source.getWidth();
		int height = source.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage;
		Bitmap bitmapWithReflection;
		if(mirrorLength > 0) {
			reflectionImage = Bitmap.createBitmap(source, 0,
					height * (mirrorLength - 1) / mirrorLength, width, height * 1 / mirrorLength, matrix, false);
	
			bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / mirrorLength + reflectionGap), Config.ARGB_8888);
		} else {
			mirrorLength = 2;
			reflectionImage = Bitmap.createBitmap(source, 0,
					height * (mirrorLength - 1) / mirrorLength, width, height * 1 / mirrorLength, matrix, false);
	
			bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / mirrorLength + reflectionGap), Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(source, 0, 0, null);

		Paint deafaultPaint = new Paint();
		deafaultPaint.setColor(Color.parseColor("#00ffffff"));
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				source.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0xc0ffffff, 0x00ffffff,
				TileMode.MIRROR);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		Matrix m = new Matrix();
		Camera camera = new Camera();
		camera.save();
		camera.rotateY(angle);
		camera.getMatrix(m);
		//旋转轴
		if(rotateWhere == ROTATE_LEFT) {
			m.preTranslate(0, -(height / 2));
			m.postTranslate(0, (height / 2));
		} else if(rotateWhere == ROTATE_RIGHT) {
			m.preTranslate(-width, -(height / 2));
			m.postTranslate(width, (height / 2));
		} else {
			m.preTranslate(-(width / 2), -(height / 2));
			m.postTranslate((width / 2), (height / 2));
		}
		bitmapWithReflection = Bitmap.createBitmap(bitmapWithReflection, 0, 0, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), m, false);
		reflectionImage.recycle();
		source.recycle();
        return bitmapWithReflection;
	}
	
	public static void loadAlbumImage(final ImageView imageView, final String path, final Resources res) {
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected void onPreExecute() {
				imageView.setImageBitmap(getAlbumImage(path, res, R.drawable.defaultalbumimage));
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if(result != null) {
					imageView.setImageBitmap(result);
				}
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap bmp = getAlbumImage(params[0], 1);
				return bmp;
			}
		}.execute(path);
	}
	
	public static void loadBigMirrorAlbumImage(final ImageView imageView, final String path, final Resources res, final int angle, final int rotateWhere, final int mirrorLength) {
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected void onPreExecute() {
				Bitmap mirrorBmp = getMirrorBitmap(getAlbumImage(path, res, R.drawable.defaultalbumimage), angle, rotateWhere, mirrorLength);
				imageView.setImageBitmap(mirrorBmp);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if(result != null) {
					result = getMirrorBitmap(result, angle, rotateWhere, mirrorLength);
					imageView.setImageBitmap(result);
				}
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap bmp = getAlbumImage(params[0], 1);
				return bmp;
			}
		}.execute(path);
	}
}
