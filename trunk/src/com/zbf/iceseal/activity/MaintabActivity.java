package com.zbf.iceseal.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zbf.iceseal.R;
import com.zbf.iceseal.base.BaseActivity;
import com.zbf.iceseal.base.BaseBean;
import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.base.Config;
import com.zbf.iceseal.bean.AlbumBean;
import com.zbf.iceseal.bean.ArtistBean;
import com.zbf.iceseal.bean.FolderBean;
import com.zbf.iceseal.bean.GenreBean;
import com.zbf.iceseal.bean.PlaylistBean;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.DateTools;
import com.zbf.iceseal.view.ViewTools;

public class MaintabActivity extends BaseActivity {
	
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private int tabLocation;// tab游标初始位置
	private ImageView cursor;// tab游标
	private TextView tvTab1;
	private TextView tvTab2;
	private TextView tvTab3;
	private TextView tvTab4;
	private PlaylistAdapter playlistAdapter;
	private FolderAdapter folderAdapter;
	private AlbumAdapter albumAdapter;
	private ArtistAdapter artistAdapter;
	private float scalepx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(true);
		scalepx = this.getResources().getDisplayMetrics().density;
	}

	@Override
	protected void initData(Intent intent) {
		
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_maintab);
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.list_song, null));
		listViews.add(mInflater.inflate(R.layout.list_song, null));
		listViews.add(mInflater.inflate(R.layout.list_song, null));
		listViews.add(mInflater.inflate(R.layout.list_song, null));
		initImageView();
		initTabView();
	}

	@Override
	protected void setListener() {
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		for (int i = 0; i < listViews.size(); i++) {
			final ListView lv = (ListView) listViews.get(i);
			switch (-i - 101) {
			case CommonData.LT_PLAYLIST:
				playlistAdapter = new PlaylistAdapter(getList(PlaylistBean.class));
				lv.setAdapter(playlistAdapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(position == 0) {
							return;
						}
						if(position == playlistAdapter.getCount() - 1) {
							return;
						}
						position -- ;
						Intent intent = new Intent(MaintabActivity.this, SonglistActivity.class);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, CommonData.LT_PLAYLIST);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE, playlistAdapter.list.get(position).getId().toString());
						MaintabActivity.this.startActivity(intent);
					}
				});
				lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if(position == 0) {
							return false;
						}
						if(position == playlistAdapter.getCount() - 1) {
							return false;
						}
						position -- ;
						return true;
					}
				});
				break;
			case CommonData.LT_FOLDER:
				folderAdapter = new FolderAdapter(getList(FolderBean.class));
				lv.setAdapter(folderAdapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(position == 0) {
							return;
						}
						if(position == folderAdapter.getCount() - 1) {
							return;
						}
						position -- ;
						Intent intent = new Intent(MaintabActivity.this, SonglistActivity.class);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, CommonData.LT_FOLDER);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE, folderAdapter.list.get(position).getId().toString());
						MaintabActivity.this.startActivity(intent);
					}
				});
				lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if(position == 0) {
							return false;
						}
						if(position == folderAdapter.getCount() - 1) {
							return false;
						}
						position -- ;
						return true;
					}
				});
				break;
			case CommonData.LT_ALBUM:
				albumAdapter = new AlbumAdapter(getList(AlbumBean.class));
				lv.setAdapter(albumAdapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(position == 0) {
							return;
						}
						if(position == albumAdapter.getCount() - 2) {
							return;
						}
						position -- ;
						Intent intent = new Intent(MaintabActivity.this, SonglistActivity.class);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, CommonData.LT_ALBUM);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE, albumAdapter.list.get(position).getAlbum());
						MaintabActivity.this.startActivity(intent);
					}
				});
				lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if(position == 0) {
							return false;
						}
						if(position == albumAdapter.getCount() - 2) {
							return false;
						}
						position -- ;
						return true;
					}
				});
				break;
			case CommonData.LT_ARTIST:
				artistAdapter = new ArtistAdapter(getList(ArtistBean.class));
				lv.setAdapter(artistAdapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(position == 0) {
							return;
						}
						if(position == artistAdapter.getCount() - 2) {
							return;
						}
						position -- ;
						Intent intent = new Intent(MaintabActivity.this, SonglistActivity.class);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, CommonData.LT_ARTIST);
						intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE, artistAdapter.list.get(position).getArtist());
						MaintabActivity.this.startActivity(intent);
					}
				});
				lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if(position == 0) {
							return false;
						}
						if(position == artistAdapter.getCount() - 2) {
							return false;
						}
						position -- ;
						String artist = artistAdapter.list.get(position).getArtist();
						System.out.println("artist:" + artist);
						return true;
					}
				});
				break;

			default:
				break;
			}
		}
		
	}

	@Override
	protected void initOther() {
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		
		musicImportListener = new OnMusicImportListener() {
			
			@Override
			public void onImportUpdate(String message) {
				
			}
			
			@Override
			public void onImportStart() {
				
			}
			
			@Override
			public void onImportEnd(int count) {
				reLoadData();
			}
		};
	}

	@Override
	protected void PlayerEvent(Intent intent) {
		switch (intent.getIntExtra(CommonData.IK_PLAYER_EVENT_TYPE, 0)) {
		case CommonData.PLAYER_EVENT_SONG_CHANGE:
			reLoadData();
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化头标
	 */
	private void initTabView() {
		tvTab1 = (TextView) findViewById(R.id.ivTab1);
		tvTab2 = (TextView) findViewById(R.id.ivTab2);
		tvTab3 = (TextView) findViewById(R.id.ivTab3);
		tvTab4 = (TextView) findViewById(R.id.ivTab4);
		tvTab1.setOnClickListener(new MyOnClickListener(0));
		tvTab2.setOnClickListener(new MyOnClickListener(1));
		tvTab3.setOnClickListener(new MyOnClickListener(2));
		tvTab4.setOnClickListener(new MyOnClickListener(3));
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};
	
	private int width;
	/**
	 * 初始化动画
	 */
	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.ivCursor);
		int bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.tab_cursor).getWidth();// 获取图片宽度
		Display display = this.getWindowManager().getDefaultDisplay();
		// float scale = this.getResources().getDisplayMetrics().density;
		width = display.getWidth() / 4;
		tabLocation = -(int) ((width - bmpW) / 2);
		cursor.scrollTo(tabLocation, 0);
	}
	

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageSelected(int tab) {

		}

		public void onPageScrolled(int tab, float percent, int pixel) {
			cursor.scrollTo(tabLocation - (int) (width * (tab + percent)), 0);
		}

		public void onPageScrollStateChanged(int state) {
		}

	}

	@Override
	protected void onRestart() {
		reLoadData();
		super.onRestart();
	}

	private void reLoadData(){
		 playlistAdapter.list = getList(PlaylistBean.class);
		 folderAdapter.list = getList(FolderBean.class);
		 albumAdapter.list = getList(AlbumBean.class);
		 artistAdapter.list = getList(ArtistBean.class);
		 playlistAdapter.notifyDataSetChanged();
		 folderAdapter.notifyDataSetChanged();
		 albumAdapter.notifyDataSetChanged();
		 artistAdapter.notifyDataSetChanged();
	}

	private <T extends BaseBean> List<T> getList(Class<T> cls) {
		List<T> list = new ArrayList<T>();
		BaseDao dao = new BaseDao(this);
		if("com.zbf.iceseal.bean.PlaylistBean".equals(cls.getName())) {
			try {
				JSONObject[] datas = dao.queryArray(CommonData.DBT_PLAYLISTTABLENAME, new String[]{"id", "name", "sort"}, null, null, null, null, "sort asc", null);
				for(int i = 0; i < datas.length; i++) {
					JSONObject data = datas[i];
					T bean = cls.newInstance();
					if("所有歌曲".equals(data.getString("name"))) {
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, null, null);
						data.put("count", jsonObject.getString("count(id)"));
					} else if("经常播放".equals(data.getString("name"))) {
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "playcount>?", new String[]{"0"});
						int count = Integer.parseInt(jsonObject.getString("count(id)"));
						data.put("count", count < Config.getConfig().oftenPlayCount ? count : Config.getConfig().oftenPlayCount);
					} else if("最近播放".equals(data.getString("name"))) {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_MONTH, -10);
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "lastplaytime>?", new String[]{DateTools.getDate(cal)});
						data.put("count", jsonObject.getString("count(id)"));
					} else if("最近添加".equals(data.getString("name"))) {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_MONTH, -10);
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "createtime>?", new String[]{DateTools.getDate(cal)});
						data.put("count", jsonObject.getString("count(id)"));
					} else if("我的最爱".equals(data.getString("name"))) {
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "islike=?", new String[]{"1"});
						data.put("count", jsonObject.getString("count(id)"));
					} else {
						JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "listid=?", new String[]{data.getString("id")});
						data.put("count", jsonObject.getString("count(id)"));
					}
					bean.setJsonObject(data);
					list.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if("com.zbf.iceseal.bean.FolderBean".equals(cls.getName())) {
			try {
				JSONObject[] datas = dao.queryArray(CommonData.DBT_SONGSFOLDERTABLENAME, new String[]{"id", "name", "path"}, null, null, null, null, null, null);
				for(JSONObject data : datas) {
					T bean = cls.newInstance();
					JSONObject jsonObject = dao.queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "count(id)"}, "folderid=?", new String[]{data.getString("id")});
					data.put("count", jsonObject.getString("count(id)"));
					bean.setJsonObject(data);
					list.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if("com.zbf.iceseal.bean.GenreBean".equals(cls.getName())) {
			try {
				JSONObject[] datas = dao.queryArray(CommonData.DBT_SONGSTABLENAME, new String[]{"genre", "count(id)"}, null, null, "genre", null, null, null);
				for(JSONObject data : datas) {
					T bean = cls.newInstance();
					data.put("count", data.getString("count(id)"));
					bean.setJsonObject(data);
					list.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if("com.zbf.iceseal.bean.AlbumBean".equals(cls.getName())) {
			try {
				JSONObject[] datas = dao.queryArray(CommonData.DBT_SONGSTABLENAME, new String[]{"album", "count(id)"}, null, null, "album", null, null, null);
				for(JSONObject data : datas) {
					T bean = cls.newInstance();
					data.put("count", data.getString("count(id)"));
					bean.setJsonObject(data);
					list.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if("com.zbf.iceseal.bean.ArtistBean".equals(cls.getName())) {
			try {
				JSONObject[] datas = dao.queryArray(CommonData.DBT_SONGSTABLENAME, new String[]{"artist", "count(id)"}, null, null, "artist", null, null, null);
				for(JSONObject data : datas) {
					T bean = cls.newInstance();
					data.put("count", data.getString("count(id)"));
					bean.setJsonObject(data);
					list.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	private int headdp = 47;
	
	public class PlaylistAdapter extends BaseAdapter {
		public List<PlaylistBean> list;

		public PlaylistAdapter(List<PlaylistBean> list) {
			this.list = list;
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
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class PlayListHolder {
			TextView tvPlayListName;
			TextView tvPlayListCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0) {
				int headHeight = (int)(headdp * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
			} else if(position == list.size() + 1) {
				int footHeight = (int)(62 * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
			} else {
				PlayListHolder holder = null;
				if (convertView == null || convertView.getTag() == null) {
					holder = new PlayListHolder();
					convertView = MaintabActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_playlist, null);
					holder.tvPlayListName = (TextView) convertView
							.findViewById(R.id.tvplaylistname);
					holder.tvPlayListCount = (TextView) convertView
							.findViewById(R.id.tvplaylistcount);
					convertView.setTag(holder);
				} else {
					holder = (PlayListHolder) convertView.getTag();
				}
				if(holder != null && position > 0 && position <= list.size()) {
					PlaylistBean itemData = list.get(position - 1);
					holder.tvPlayListName.setText(itemData.getName());
					holder.tvPlayListCount.setText(itemData.getCount().toString());
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					} else {
						convertView.findViewById(R.id.vdivider).setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}
	}
	
	public class FolderAdapter extends BaseAdapter {
		public List<FolderBean> list;

		public FolderAdapter(List<FolderBean> list) {
			this.list = list;
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
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class FolderHolder {
			TextView tvFolderName;
			TextView tvFolderCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0) {
				int headHeight = (int)(headdp * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
			} else if(position == list.size() + 1) {
				int footHeight = (int)(62 * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
			} else {
				FolderHolder holder = null;
				if (convertView == null || convertView.getTag() == null) {
					holder = new FolderHolder();
					convertView = MaintabActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_folder, null);
					holder.tvFolderName = (TextView) convertView
							.findViewById(R.id.tvfoldername);
					holder.tvFolderCount = (TextView) convertView
							.findViewById(R.id.tvfoldercount);
					convertView.setTag(holder);
				} else {
					holder = (FolderHolder) convertView.getTag();
				}
				if(holder != null && position > 0 && position <= list.size()) {
					FolderBean itemData = list.get(position - 1);
					holder.tvFolderName.setText(itemData.getName());
					holder.tvFolderCount.setText(itemData.getCount().toString());
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					} else {
						convertView.findViewById(R.id.vdivider).setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}
	}


	public class GenreAdapter extends BaseAdapter {
		public List<GenreBean> list;

		public GenreAdapter(List<GenreBean> list) {
			this.list = list;
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
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class GenreHolder {
			TextView tvGenreName;
			TextView tvGenreCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0) {
				int headHeight = (int)(headdp * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
			} else if(position == list.size() + 1) {
				int footHeight = (int)(62 * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
			} else {
				GenreHolder holder = null;
				if (convertView == null || convertView.getTag() == null) {
					holder = new GenreHolder();
					convertView = MaintabActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_genre, null);
					holder.tvGenreName = (TextView) convertView
							.findViewById(R.id.tvgenrename);
					holder.tvGenreCount = (TextView) convertView
							.findViewById(R.id.tvgenrecount);
					convertView.setTag(holder);
				} else {
					holder = (GenreHolder) convertView.getTag();
				}
				if(holder != null && position > 0 && position <= list.size()) {
					GenreBean itemData = list.get(position - 1);
					holder.tvGenreName.setText(itemData.getGenre());
					holder.tvGenreCount.setText(itemData.getCount().toString());
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					} else {
						convertView.findViewById(R.id.vdivider).setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}
	}

	public class AlbumAdapter extends BaseAdapter {
		public List<AlbumBean> list;

		public AlbumAdapter(List<AlbumBean> list) {
			this.list = list;
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
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class GenreHolder {
			TextView tvGenreName;
			TextView tvGenreCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0) {
				int headHeight = (int)(headdp * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
			} else if(position == list.size() + 1) {
				int footHeight = (int)(62 * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
			} else {
				GenreHolder holder = null;
				if (convertView == null || convertView.getTag() == null) {
					holder = new GenreHolder();
					convertView = MaintabActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_genre, null);
					holder.tvGenreName = (TextView) convertView
							.findViewById(R.id.tvgenrename);
					holder.tvGenreCount = (TextView) convertView
							.findViewById(R.id.tvgenrecount);
					convertView.setTag(holder);
				} else {
					holder = (GenreHolder) convertView.getTag();
				}
				if(holder != null && position > 0 && position <= list.size()) {
					AlbumBean itemData = list.get(position - 1);
					holder.tvGenreName.setText(itemData.getAlbum());
					holder.tvGenreCount.setText(itemData.getCount().toString());
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					} else {
						convertView.findViewById(R.id.vdivider).setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}
	}


	public class ArtistAdapter extends BaseAdapter {
		public List<ArtistBean> list;

		public ArtistAdapter(List<ArtistBean> list) {
			this.list = list;
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
			if (list != null) {
				return list.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ArtistHolder {
			TextView tvArtistName;
			TextView tvArtistCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0) {
				int headHeight = (int)(headdp * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, headHeight));
			} else if(position == list.size() + 1) {
				int footHeight = (int)(62 * scalepx);
				convertView = new View(mContext);
				convertView.setBackgroundColor(Color.parseColor("#00000000"));
				convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, footHeight));
			} else {
				ArtistHolder holder = null;
				if (convertView == null || convertView.getTag() == null) {
					holder = new ArtistHolder();
					convertView = MaintabActivity.this.getLayoutInflater().inflate(
							R.layout.listitem_artist, null);
					holder.tvArtistName = (TextView) convertView
							.findViewById(R.id.tvartistname);
					holder.tvArtistCount = (TextView) convertView
							.findViewById(R.id.tvartistcount);
					convertView.setTag(holder);
				} else {
					holder = (ArtistHolder) convertView.getTag();
				}
				if(holder != null && position > 0 && position <= list.size()) {
					ArtistBean itemData = list.get(position - 1);
					holder.tvArtistName.setText(itemData.getArtist());
					holder.tvArtistCount.setText(itemData.getCount().toString());
					if(position == list.size()) {
						convertView.findViewById(R.id.vdivider).setVisibility(View.INVISIBLE);
					} else {
						convertView.findViewById(R.id.vdivider).setVisibility(View.VISIBLE);
					}
				}
			}
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "添加歌曲");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			ViewTools.createFolderSelectWindow(mContext, mPager, new ViewTools.OnFileSelectButtonClickListener() {
				
				@Override
				public void onFileSelectButtonClick(ArrayList<String> selectedPaths) {
					bindImportMusicService(selectedPaths);
				}

				@Override
				public void onCancelButtonClick() {
					ViewTools.showShortToast(mContext, "您没有选择任何歌曲...");
				}
				
			});
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
