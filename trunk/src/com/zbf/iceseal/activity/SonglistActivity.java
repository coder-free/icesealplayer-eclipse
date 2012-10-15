package com.zbf.iceseal.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.zbf.iceseal.R;
import com.zbf.iceseal.base.BaseActivity;
import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.bean.SonglistBean;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.ImageTools;
import com.zbf.iceseal.util.UtilTools;
import com.zbf.iceseal.view.ViewTools;

public class SonglistActivity extends BaseActivity {
	
	private ListView lvSongList;
	private Button btnBack;
	private Button btnMenu;
	private TextView tvTitleName;
	private TextView tvTitleType;
	private SongAdapter adapter;
	private SonglistBean songlistBean;
	private RelativeLayout rlTitleBar;
	private float scalepx;
	private int mListType;
	private String mListParamete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(true);
		scalepx = this.getResources().getDisplayMetrics().density;
	}

	@Override
	protected void initData(Intent intent) {
		mListType = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, 0);
		mListParamete = intent.getStringExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE);
		try {
			BaseDao dao = new BaseDao(mContext);
			songlistBean = dao.getSongListbean(mListType, mListParamete);
			adapter = new SongAdapter(songlistBean.getSongList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_songlist);
		lvSongList = (ListView) findViewById(R.id.lvSongList);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnMenu = (Button) findViewById(R.id.btnMenu);
		tvTitleName = (TextView) findViewById(R.id.tvTitle);
		tvTitleType = (TextView) findViewById(R.id.tvType);
		rlTitleBar = (RelativeLayout) findViewById(R.id.rlTitleBar);
		final View alphabet_scroller = (ImageView) findViewById(R.id.alphaBetscroller);
		if(alphabet_scroller != null) {
			LayoutParams params = (LayoutParams) alphabet_scroller.getLayoutParams();
			params.addRule(RelativeLayout.ABOVE, R.id.rlCurplay);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			alphabet_scroller.setClickable(true);
			alphabet_scroller.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:// 0
						alphabet_scroller.setPressed(true);
	//					first_letter_overlay.setVisibility(View.VISIBLE);
	//					mathScrollerPosition(event.getY());
						break;
					case MotionEvent.ACTION_UP:// 1
						alphabet_scroller.setPressed(false);
	//					first_letter_overlay.setVisibility(View.GONE);
						break;
					case MotionEvent.ACTION_MOVE:
	//					mathScrollerPosition(event.getY());
						break;
					}
					return false;
				}
			});
		}
	}

	@Override
	protected void setListener() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(UtilTools.isLastActivity(SonglistActivity.this)) {
					System.out.println("SongListActivity is last");
				}
				SonglistActivity.this.finish();
			}
		});
		btnMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewTools.showLongToast(mContext, "帅哥，这个按钮还没有作用。。。。");
			}
		});
		lvSongList.setAdapter(adapter);
		lvSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0) {
					return;
				}
				if(position == lvSongList.getCount() - 1) {
					return;
				}
				position -- ;
				songlistBean.setPosition(position);
				try {
					mPlayerService.playThis(position, mListType, mListParamete);
					SharedPreferences sp = SonglistActivity.this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putInt(CommonData.SPK_PLAYER_STATE_LISTTYPE, mListType);
					editor.putString(CommonData.SPK_PLAYER_STATE_LISTPARAMETE, mListParamete);
					editor.commit();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		lvSongList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0) {
					return false;
				}
				if(position == lvSongList.getCount() - 1) {
					return false;
				}
				position -- ;
				int songId = adapter.list.get(position).getId();
				System.out.println("LongClick" + songId);
				return true;
			}
		});
		rlTitleBar.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				return true;
			}
		});
	}

	@Override
	protected void initOther() {
		tvTitleName.setText(songlistBean.getTitleName());
		tvTitleName.setSelected(true);
		tvTitleType.setText(songlistBean.getTitleType());
	}

	@Override
	protected void PlayerEvent(Intent intent) {
		
	}

	public class SongAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
		public List<SongBean> list;
		public SparseArray<View> viewArray;
		public SongAdapter(List<SongBean> list) {
			this.list = list;
			viewArray = new SparseArray<View>();
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size() + 2;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null && position <= list.size() && position != 0) {
				return list.get(position -- );
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position -- ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(viewArray.get(position) != null) {
				convertView = viewArray.get(position);
			} else {
				if(position == 0) {
					int headHeight = (int)(52 * scalepx);
					convertView = new View(mContext);
					convertView.setBackgroundColor(Color.parseColor("#00000000"));
					convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
				} else if(position == list.size() + 1) {
					int footHeight = (int)(62 * scalepx);
					convertView = new View(mContext);
					convertView.setBackgroundColor(Color.parseColor("#00000000"));
					convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
				} else {
					position--;
					SongBean itemData = list.get(position);
					convertView = SonglistActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_song, null);
					ImageView ivSongPic = (ImageView) convertView
							.findViewById(R.id.ivsongpic);
					TextView tvSongName = (TextView) convertView
							.findViewById(R.id.tvsongname);
					TextView tvSongArtist = (TextView) convertView
							.findViewById(R.id.tvsongartist);
					TextView tvSongLength = (TextView) convertView
							.findViewById(R.id.tvsonglength);
					TextView tvSongPath = (TextView) convertView
							.findViewById(R.id.tvsongpath);
					CheckBox cbIsLike = (CheckBox) convertView
							.findViewById(R.id.cbislike);
					ImageTools.loadAlbumImage(R.drawable.defaultalbumimage, itemData.getPath(), ivSongPic, null);
					tvSongName.setText(itemData.getName());
					tvSongArtist.setText(itemData.getArtist());
					tvSongLength.setText(UtilTools.getShowTime(itemData.getDuration()));
					tvSongPath.setText(itemData.getPath().replace(CommonData.EXTERNALSTORAGEDIRECTORY, ""));
					tvSongPath.setSelected(true);
					cbIsLike.setTag(position);
					cbIsLike.setChecked(itemData.getIslike() == 1 ? true : false);
					cbIsLike.setOnCheckedChangeListener(this);
					position++;
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					}
				}
				viewArray.put(position, convertView);
			}
			return convertView;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			int position = (Integer) buttonView.getTag();
			SongBean curSong = list.get(position);
			new BaseDao(mContext).setLike(curSong.getId(), isChecked);
			curSong.setIslike(isChecked ? 1 : 0);
		}
	}

}
