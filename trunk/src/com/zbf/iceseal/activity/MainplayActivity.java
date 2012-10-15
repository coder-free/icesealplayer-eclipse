package com.zbf.iceseal.activity;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zbf.iceseal.R;
import com.zbf.iceseal.base.BaseActivity;
import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.lyric.Lyric;
import com.zbf.iceseal.lyric.Sentence;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.ImageTools;
import com.zbf.iceseal.util.UtilTools;

public class MainplayActivity extends BaseActivity {

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
	boolean isSeeking;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				final WindowManager wm = (WindowManager) mContext.getSystemService(Service.WINDOW_SERVICE);
				final ListView lvSonglist = (ListView) mContext.getLayoutInflater().inflate(R.layout.list_minisong, null);
				if(songlist == null || songlist.size() == 0) {
					return;
				}
				lvSonglist.setAdapter(new SongAdapter(songlist));
				lvSonglist.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
							wm.removeView(lvSonglist);
							return true;
						}
						return false;
					}
				});
				lvSonglist.setOnKeyListener(new View.OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
							wm.removeView(lvSonglist);
							return true;
						}
						return false;
					}
				});
				lvSonglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
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
				p.width = (int)w;
				p.height = (int)h;
				p.token = gAlbum.getWindowToken();
				p.format = PixelFormat.TRANSLUCENT;
				p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
				p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
				p.y = (p.height - wm.getDefaultDisplay().getHeight())/4;
				wm.addView(lvSonglist, p);
			}
		});
		sbPlayCtrl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tempProgress = progress;
				String[] times = UtilTools.getCurShowtime(seekBar.getMax(), progress);
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
			int progress = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_PROGRESS, 0);
			if(!isSeeking) {
				sbPlayCtrl.setProgress(progress);
			}
			if(!btnPlay.isChecked()) {
				btnPlay.setChecked(true);
			}
			if(lrc != null && lrc.isInitDone()) {
				int index = lrc.getNowSentenceIndex(progress);
				if(curIndex != index) {
					curIndex = index;
					setLrc(tvLyrics1, lrc.getNowSentence(index-1));
					setLrc(tvLyrics2, lrc.getNowSentence(index));
					setLrc(tvLyrics3, lrc.getNowSentence(index+1));
				}
			}
			break;
			
		case CommonData.PLAYER_EVENT_SONG_CHANGE:
			if(songlist == null || songlist.size() == 0) {
				System.out.println("songlist is null");
				break;
			}
			boolean isUpdateImage = true;
			if(position == intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, 0) && position !=0) {
				isUpdateImage = false;
			}
			position = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, 0);
			if(position == -1) {
				position = 0;
				btnPlay.setChecked(false);
				SongBean song = songlist.get(position);
				Bitmap bm = ImageTools.getMirrorBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumimage), 0, ImageTools.ROTATE_LEFT, 1);
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
				if(isUpdateImage) {
					ImageTools.loadBigMirrorAlbumImage(gAlbum, song.getPath(), MainplayActivity.this.getResources(), 0, ImageTools.ROTATE_LEFT, 1);
				}
				sbPlayCtrl.setMax(song.getDuration().intValue());
				sbPlayCtrl.setProgress(0);
				tvSongName.setText(song.getName());
				tvArtist.setText(song.getArtist());
			}
			break;
			
		case CommonData.PLAYER_EVENT_SONGLIST_CHANGE:
			int type = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, 0);
			String paramete = intent.getStringExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE);
			BaseDao dao = new BaseDao(mContext);
			try {
				songlist = dao.getSongList(type, paramete);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case CommonData.PLAYER_EVENT_PAUSE:
			if(btnPlay.isChecked()) {
				btnPlay.setChecked(false);
			}
			break;
			
		case CommonData.PLAYER_EVENT_LOOPMODE_CHANGE:
			int mode = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_LOOPMODE_CHANGE, CommonData.LOOP_MODE[0]);
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
		if(s != null) {
			tv.setText(s.getContent());
		} else {
			tv.setText("");
		}
	}

    public class SongAdapter extends BaseAdapter{
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
			if(viewArray.get(position) != null) {
				convertView = viewArray.get(position);
			} else {
				convertView = MainplayActivity.this.getLayoutInflater().inflate(
						R.layout.listitem_minisong, null);
				ImageView ivSongPic = (ImageView) convertView
						.findViewById(R.id.ivsongpic);
				TextView tvSongName = (TextView) convertView
						.findViewById(R.id.tvsongname);
				TextView tvSongArtist = (TextView) convertView
						.findViewById(R.id.tvsongartist);
				TextView tvSongLength = (TextView) convertView
						.findViewById(R.id.tvsonglength);
				ImageTools.loadAlbumImage(R.drawable.defaultalbumimage, itemData.getPath(), ivSongPic, null);
				tvSongName.setText(itemData.getName());
				tvSongArtist.setText(itemData.getArtist());
				tvSongLength.setText(UtilTools.getShowTime(itemData.getDuration()));
				viewArray.put(position, convertView);
			}
			return convertView;
		}
		
	}

}
