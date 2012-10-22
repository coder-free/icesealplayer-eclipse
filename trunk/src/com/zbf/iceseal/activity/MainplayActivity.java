package com.zbf.iceseal.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zbf.iceseal.R;
import com.zbf.iceseal.base.BaseActivity;
import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.lyric.Lyric;
import com.zbf.iceseal.lyric.Sentence;
import com.zbf.iceseal.service.PlayerService;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.ImageTools;
import com.zbf.iceseal.util.UtilTools;
import com.zbf.iceseal.view.ViewTools;
import com.zbf.iceseal.view.ViewTools.OnFileSelectButtonClickListener;

public class MainplayActivity extends BaseActivity {

	private static final float VISUALIZER_HEIGHT_DIP = 50f;

	private ToggleButton btnPlay;
	private TextView tvSongName;
	private TextView tvArtist;
	private TextView tvLyrics1;
	private TextView tvLyrics2;
	private TextView tvLyrics3;
	private TextView leftTime;
	private TextView rightTime;
	private ImageView gAlbum;
	private ImageButton btnLast;
	private ImageButton btnNext;
	private ImageView ivCirculMode;
	private View ivSonglist;
	private SeekBar sbPlayCtrl;
	private RelativeLayout rlPlay;
	private LinearLayout llMainEqualizer;
	boolean isSeeking;

	VisualizerView mVisualizerView;
	private Equalizer mEqualizer; // 均衡器
	private Visualizer mVisualizer;
	private TextView mStatusTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		init(false);
	}

	@Override
	protected void initData(Intent intent) {

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_mainplay);
		btnPlay = (ToggleButton) findViewById(R.id.btnPlay);
		tvSongName = (TextView) findViewById(R.id.tvSongName);
		tvArtist = (TextView) findViewById(R.id.tvArtist);
		tvLyrics1 = (TextView) findViewById(R.id.tvLyrics1);
		tvLyrics2 = (TextView) findViewById(R.id.tvLyrics2);
		tvLyrics3 = (TextView) findViewById(R.id.tvLyrics3);
		gAlbum = (ImageView) findViewById(R.id.gAlbum);
		sbPlayCtrl = (SeekBar) findViewById(R.id.sbPlayCtrl);
		btnLast = (ImageButton) findViewById(R.id.btnLast);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		rightTime = (TextView) findViewById(R.id.rightTime);
		leftTime = (TextView) findViewById(R.id.leftTime);
		ivCirculMode = (ImageView) findViewById(R.id.ivCirculMode);
		ivSonglist = findViewById(R.id.ivSonglist);
		rlPlay = (RelativeLayout) findViewById(R.id.rlPlay);
		llMainEqualizer = (LinearLayout) findViewById(R.id.llMainEqualizer);
	}

	@Override
	protected void setListener() {
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mPlayerService.changeState();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		btnLast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mPlayerService.last();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mPlayerService.next();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		ivCirculMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mPlayerService.changeMode();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		ivSonglist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final WindowManager wm = (WindowManager) mContext
						.getSystemService(Service.WINDOW_SERVICE);
				final ListView lvSonglist = (ListView) mContext
						.getLayoutInflater().inflate(R.layout.list_minisong,
								null);
				if (songlist == null || songlist.size() == 0) {
					return;
				}
				lvSonglist.setAdapter(new SongAdapter(songlist));
				lvSonglist.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
							wm.removeView(lvSonglist);
							return true;
						}
						return false;
					}
				});
				lvSonglist.setOnKeyListener(new View.OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_DOWN
								&& keyCode == KeyEvent.KEYCODE_BACK) {
							wm.removeView(lvSonglist);
							return true;
						}
						return false;
					}
				});
				lvSonglist
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								wm.removeView(lvSonglist);
								try {
									mPlayerService.playThis(position, 0, null);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						});
				WindowManager.LayoutParams p = new WindowManager.LayoutParams();
				p.gravity = Gravity.CENTER;
				float h = wm.getDefaultDisplay().getHeight() * 0.55f;
				float w = wm.getDefaultDisplay().getWidth() * 0.75f;
				p.width = (int) w;
				p.height = (int) h;
				p.token = gAlbum.getWindowToken();
				p.format = PixelFormat.TRANSLUCENT;
				p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
				p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
				p.y = (p.height - wm.getDefaultDisplay().getHeight()) / 4;
				wm.addView(lvSonglist, p);
			}
		});
		sbPlayCtrl
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					int tempProgress;

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						try {
							mPlayerService.seekTo(tempProgress);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						isSeeking = false;
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						isSeeking = true;
						tempProgress = sbPlayCtrl.getProgress();
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						tempProgress = progress;
						String[] times = UtilTools.getCurShowtime(
								seekBar.getMax(), progress);
						leftTime.setText(times[0]);
						rightTime.setText(times[1]);
					}
				});
	}

	@Override
	protected void initOther() {

	}

	private int curIndex;
	private Lyric lrc;

	@Override
	protected void PlayerEvent(Intent intent) {

		switch (intent.getIntExtra(CommonData.IK_PLAYER_EVENT_TYPE, 0)) {

		case CommonData.PLAYER_EVENT_PROGRESS:
			int progress = intent.getIntExtra(
					CommonData.IK_PLAYER_EVENT_PROGRESS, 0);
			if (!isSeeking) {
				sbPlayCtrl.setProgress(progress);
			}
			if (!btnPlay.isChecked()) {
				btnPlay.setChecked(true);
			}
			if (lrc != null && lrc.isInitDone()) {
				int index = lrc.getNowSentenceIndex(progress);
				if (curIndex != index) {
					curIndex = index;
					setLrc(tvLyrics1, lrc.getNowSentence(index - 1));
					setLrc(tvLyrics2, lrc.getNowSentence(index));
					setLrc(tvLyrics3, lrc.getNowSentence(index + 1));
				}
			}
			break;

		case CommonData.PLAYER_EVENT_SONG_CHANGE:
			if (songlist == null || songlist.size() == 0) {
				System.out.println("songlist is null");
				break;
			}
			boolean isUpdateImage = true;
			if (position == intent.getIntExtra(
					CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, 0)
					&& position != 0) {
				isUpdateImage = false;
			}
			position = intent.getIntExtra(
					CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, 0);
			if (position == -1) {
				position = 0;
				btnPlay.setChecked(false);
				SongBean song = songlist.get(position);
				Bitmap bm = ImageTools.getMirrorBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.defaultalbumimage), 0,
						ImageTools.ROTATE_LEFT, 1);
				gAlbum.setImageBitmap(bm);
				sbPlayCtrl.setMax(song.getDuration().intValue());
				sbPlayCtrl.setProgress(0);
				tvSongName.setText("IceSealPlayer");
				tvArtist.setText("");
				setLrc(tvLyrics1, null);
				setLrc(tvLyrics2, null);
				setLrc(tvLyrics3, null);
			} else {
				SongBean song = songlist.get(position);
				lrc = new Lyric(song);
				if (isUpdateImage) {
					ImageTools.loadBigMirrorAlbumImage(gAlbum, song.getPath(),
							MainplayActivity.this.getResources(), 0,
							ImageTools.ROTATE_LEFT, 1);
				}
				sbPlayCtrl.setMax(song.getDuration().intValue());
				sbPlayCtrl.setProgress(0);
				tvSongName.setText(song.getName());
				tvArtist.setText(song.getArtist());
			}
			break;

		case CommonData.PLAYER_EVENT_SONGLIST_CHANGE:
			int type = intent.getIntExtra(
					CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, 0);
			String paramete = intent
					.getStringExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE);
			BaseDao dao = new BaseDao(mContext);
			try {
				songlist = dao.getSongList(type, paramete);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case CommonData.PLAYER_EVENT_PAUSE:
			if (btnPlay.isChecked()) {
				btnPlay.setChecked(false);
			}
			break;

		case CommonData.PLAYER_EVENT_LOOPMODE_CHANGE:
			int mode = intent.getIntExtra(
					CommonData.IK_PLAYER_EVENT_LOOPMODE_CHANGE,
					CommonData.LOOP_MODE[0]);
			setCirculBtnBg(mode);
			break;

		default:
			break;
		}

	}

	private void setCirculBtnBg(int mode) {
		switch (mode) {
		case CommonData.LOOP_MODE_LIST_ONCE:
			ivCirculMode.setImageResource(R.drawable.circul_list_once);
			break;
		case CommonData.LOOP_MODE_LIST_REPEAT:
			ivCirculMode.setImageResource(R.drawable.circul_list_repeat);
			break;
		case CommonData.LOOP_MODE_SINGLE_REPEAT:
			ivCirculMode.setImageResource(R.drawable.circul_single_repeat);
			break;
		case CommonData.LOOP_MODE_LIST_RANDOM:
			ivCirculMode.setImageResource(R.drawable.circul_random);
			break;

		default:
			break;
		}
	}

	public void setLrc(TextView tv, Sentence s) {
		if (s != null) {
			tv.setText(s.getContent());
		} else {
			tv.setText("");
		}
	}

	public class SongAdapter extends BaseAdapter {
		public List<SongBean> list;
		public SparseArray<View> viewArray;

		public SongAdapter(List<SongBean> list) {
			this.list = list;
			viewArray = new SparseArray<View>();
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SongBean itemData = list.get(position);
			if (viewArray.get(position) != null) {
				convertView = viewArray.get(position);
			} else {
				convertView = MainplayActivity.this.getLayoutInflater()
						.inflate(R.layout.listitem_minisong, null);
				ImageView ivSongPic = (ImageView) convertView
						.findViewById(R.id.ivsongpic);
				TextView tvSongName = (TextView) convertView
						.findViewById(R.id.tvsongname);
				TextView tvSongArtist = (TextView) convertView
						.findViewById(R.id.tvsongartist);
				TextView tvSongLength = (TextView) convertView
						.findViewById(R.id.tvsonglength);
				ImageTools.loadAlbumImage(R.drawable.defaultalbumimage,
						itemData.getPath(), ivSongPic, null);
				tvSongName.setText(itemData.getName());
				tvSongArtist.setText(itemData.getArtist());
				tvSongLength.setText(UtilTools.getShowTime(itemData
						.getDuration()));
				viewArray.put(position, convertView);
			}
			return convertView;
		}

	}

	/**
	 * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
	 */
	private void setupVisualizerFxAndUi(LinearLayout llEqualizer) {
		mVisualizerView = new VisualizerView(this);
		mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				(int) (VISUALIZER_HEIGHT_DIP * getResources()
						.getDisplayMetrics().density)));
		llEqualizer.addView(mVisualizerView);
		System.out.println("haha");
		mVisualizer = new Visualizer(PlayerService.mPlayer.getAudioSessionId());
		// 参数内必须是2的位数
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

		// 设置允许波形表示，并且捕获它
		mVisualizer.setDataCaptureListener(new OnDataCaptureListener() {

			@Override
			public void onWaveFormDataCapture(Visualizer visualizer,
					byte[] waveform, int samplingRate) {
				// TODO Auto-generated method stub
				mVisualizerView.updateVisualizer(waveform);
			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				// TODO Auto-generated method stub

			}
		}, Visualizer.getMaxCaptureRate() / 2, true, false);

	}

	/**
	 * 通过mMediaPlayer返回的AudioSessionId创建一个优先级为0均衡器对象 并且通过频谱生成相应的UI和对应的事件
	 */
	private void setupEqualizeFxAndUi(LinearLayout llEqualizer) {
		mEqualizer = new Equalizer(0, PlayerService.mPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);// 启用均衡器
		TextView eqTextView = new TextView(this);
		eqTextView.setText("均衡器：");
		llEqualizer.addView(eqTextView);

		// 通过均衡器得到其支持的频谱引擎
		short bands = mEqualizer.getNumberOfBands();

		// getBandLevelRange 是一个数组，返回一组频谱等级数组，
		// 第一个下标为最低的限度范围
		// 第二个下标为最大的上限,依次取出
		final short minEqualizer = mEqualizer.getBandLevelRange()[0];
		final short maxEqualizer = mEqualizer.getBandLevelRange()[1];

		for (short i = 0; i < bands; i++) {
			final short band = i;

			TextView freqTextView = new TextView(this);
			freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));

			freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);

			// 取出中心频率
			freqTextView
					.setText((mEqualizer.getCenterFreq(band) / 1000) + "HZ");
			llEqualizer.addView(freqTextView);

			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);

			TextView minDbTextView = new TextView(this);
			minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));

			minDbTextView.setText((minEqualizer / 100) + " dB");

			TextView maxDbTextView = new TextView(this);
			maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			maxDbTextView.setText((maxEqualizer / 100) + " dB");

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			layoutParams.weight = 1;

			SeekBar seekbar = new SeekBar(this);
			seekbar.setLayoutParams(layoutParams);
			seekbar.setMax(maxEqualizer - minEqualizer);
			seekbar.setProgress(mEqualizer.getBandLevel(band));

			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					mEqualizer.setBandLevel(band,
							(short) (progress + minEqualizer));
				}
			});
			row.addView(minDbTextView);
			row.addView(seekbar);
			row.addView(maxDbTextView);

			llEqualizer.addView(row);
		}

	}

	class VisualizerView extends View {

		private byte[] mBytes;
		private float[] mPoints;
		// 矩形区域
		private Rect mRect = new Rect();
		// 画笔
		private Paint mPaint = new Paint();

		// 初始化画笔
		private void init() {
			mBytes = null;
			mPaint.setStrokeWidth(1f);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.BLUE);
		}

		public VisualizerView(Context context) {
			super(context);
			init();
		}

		public void updateVisualizer(byte[] mbyte) {
			mBytes = mbyte;
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);

			if (mBytes == null) {
				return;
			}
			if (mPoints == null || mPoints.length < mBytes.length * 4) {
				mPoints = new float[mBytes.length * 4];
			}

			mRect.set(0, 0, getWidth(), getHeight());

			for (int i = 0; i < mBytes.length - 1; i++) {
				mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
				mPoints[i * 4 + 1] = mRect.height() / 2
						+ ((byte) (mBytes[i] + 128)) * (mRect.height() / 2)
						/ 128;
				mPoints[i * 4 + 2] = mRect.width() * (i + 1)
						/ (mBytes.length - 1);
				mPoints[i * 4 + 3] = mRect.height() / 2
						+ ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
						/ 128;
			}

			canvas.drawLines(mPoints, mPaint);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "均衡器");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
//			ViewTools.createEqualizerWindow(mContext, rlPlay, 1);

			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			createEqualizerWindow(new OnButtonClickListener() {
				
				@Override
				public void onSureButtonClick() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onCancelButtonClick() {
					// TODO Auto-generated method stub
					
				}
			});
			mVisualizer.setEnabled(true);
			PlayerService.mPlayer
					.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mVisualizer.setEnabled(false);
						}
					});
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public  void createEqualizerWindow(final OnButtonClickListener listener) {

		final WindowManager wm = (WindowManager) mContext
				.getSystemService(Service.WINDOW_SERVICE);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		final View equalizerView = inflater.inflate(R.layout.equalizer, null);
		RelativeLayout rlEqualizer = (RelativeLayout) equalizerView.findViewById(R.id.rlEqualizer);

		LinearLayout llEqualizer = (LinearLayout) equalizerView.findViewById(R.id.llEqualizer);
		setupVisualizerFxAndUi(llEqualizer);
		setupEqualizeFxAndUi(llEqualizer);
		Button btnSure = (Button) rlEqualizer.findViewById(R.id.btnSure);
		Button btnCancel = (Button) rlEqualizer.findViewById(R.id.btnCancel);
		btnSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				wm.removeView(equalizerView);
				mVisualizer.release();
				listener.onCancelButtonClick();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				wm.removeView(equalizerView);
				mVisualizer.release();
				listener.onCancelButtonClick();
			}
		});
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.gravity = Gravity.CENTER;
		float h = wm.getDefaultDisplay().getHeight() * 0.8f;
		float w = wm.getDefaultDisplay().getWidth() * 0.9f;
		p.width = (int) w;
		p.height = (int) h;
		p.token = rlPlay.getWindowToken();
		p.format = PixelFormat.TRANSLUCENT;
		p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
		wm.addView(equalizerView, p);
	}
	

	public interface OnButtonClickListener {
		public void onSureButtonClick();
		public void onCancelButtonClick();
	}
	
}
