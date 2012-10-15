package com.zbf.iceseal.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zbf.iceseal.R;
import com.zbf.iceseal.util.ImageTools;

public class MyFileBrowser extends ListView implements AdapterView.OnItemClickListener{
	
	private Stack<String> pathStack=null;
	private List<String> selectedPaths=null;
	private Map<String, List<File>> fileLists= null;
	private static final String SDCardPath=Environment.getExternalStorageDirectory().getPath();
	private Context context=null;
	private OnFileListItemClickListener onFileListItemClickListener;
	
	private String[] filteredSuffixs = new String[]{".mp3",".wma"};

	public MyFileBrowser(Context context) {
		super(context);
		this.context=context;
		init();
	}

	public MyFileBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
	}

	public MyFileBrowser(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		init();
	}
	
	private void init(){
		pathStack=new Stack<String>();
		fileLists=new HashMap<String, List<File>>();
		pathStack.push(SDCardPath);
		getFilesSync();
		this.setOnItemClickListener(this);
	}
	
	public List<String> getSelectedFilepath(){
		return selectedPaths;
	}
	
	public boolean isTop() {
		return pathStack.size()<=1;
	}
	
	public void backLastGrade() {
		if(!isTop()) {
			pathStack.pop();
			getFilesSync();
		}
	}
	
	
	private void getFilesSync(){
		new AsyncTask<String, String, String>() {

			@Override
			protected void onPostExecute(String result) {
				updateList();
			}
			
			@Override
			protected void onProgressUpdate(String... values) {
				updateList();
			}

			@Override
			protected String doInBackground(String... params) {
				getFiles();
				return null;
			}

			private void getFiles(){
				String path=getCurPath();
				File f=new File(path);
				File[] files=f.listFiles();
				if(files==null) {
					return;
				}
				if(fileLists.containsKey(path)) {
					return;
				}
				List<File> fileList = new ArrayList<File>();
				if(pathStack.size()>1){
					fileList.add(null);
				}
				for(File file:files) {
					if(!file.getName().startsWith(".")) {
						if(filterFile(file)) {
							fileList.add(file);
							publishProgress("");
						}
					}
				}
				fileLists.put(path, fileList);
			}
			
			protected boolean filterFile(File file) {
				for(String suffix : filteredSuffixs) {
					if(file.isDirectory()) {
						File[] files = file.listFiles();
						if(files == null) {
							return false;
						}
						for(File subFile : files) {
							return filterFile(subFile);
						}
					} else if(file.isFile() && file.getName().toLowerCase().endsWith(suffix)){
						return true;
					}
				}
				return false;
			}
		}.execute("");
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String path = getCurPath();
		File file=fileLists.get(path).get(position);
		if(file==null && !isTop()){
			pathStack.pop();
			getFilesSync();
		} else if(file.isDirectory()){
			CheckBox cb = (CheckBox)view.findViewById(R.id.cb);
			if(cb.isChecked()) {
				cb.setChecked(false);
			} else {
				pathStack.push(file.getName());
				getFilesSync();
			}
		} else {
			if(onFileListItemClickListener==null) {
				return;
			}
			onFileListItemClickListener.onItemClick(file);
		}
		
	}
	
	MyFileListAdapter adapter = new MyFileListAdapter();
	
	private void updateList(){
		if(getAdapter() == null) {
			setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	
	public void setOnFileClickListener(OnFileListItemClickListener onFileListItemClickListener){
		this.onFileListItemClickListener=onFileListItemClickListener;
	}
	

	class MyFileListAdapter extends BaseAdapter implements OnCheckedChangeListener{
		public SparseArray<View> viewArray;
		public MyFileListAdapter() {
			viewArray = new SparseArray<View>();
		}
		public int getCount() {
			if(pathStack.size() != 0) {
				String path=getCurPath();
				if( fileLists.get(path) != null) {
					return fileLists.get(path).size();
				}
			}
			return 0;
		}
	
		public Object getItem(int position) {
			if(pathStack.size() != 0) {
				String path=getCurPath();
				if( fileLists.get(path) != null) {
					return fileLists.get(path).get(position);
				}
			}
			return position;
		}
	
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String path=getCurPath();
			File subFile = fileLists.get(path).get(position);
			if(viewArray.get(position) != null) {
				convertView = viewArray.get(position);
			} else {
				LayoutInflater inflater = (LayoutInflater)MyFileBrowser.this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listitem_filelist, null);
				CheckBox cb = (CheckBox)convertView.findViewById(R.id.cb);
				ImageView ivFileImage=(ImageView)convertView.findViewById(R.id.file_image);
				TextView tvFileName=(TextView)convertView.findViewById(R.id.file_name);
				if(selectedPaths != null && subFile != null && selectedPaths.contains(subFile.getPath())) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
				cb.setTag(position);
				cb.setOnCheckedChangeListener(this);
				if(subFile != null && subFile.isFile() && subFile.getName().endsWith(".mp3")) {
					ImageTools.loadAlbumImage(R.drawable.defaultalbumimage, subFile.getAbsolutePath(), ivFileImage, null);
				} else if(subFile != null && subFile.isFile()){
					ivFileImage.setImageResource(R.drawable.defaultalbumimage);
				} else {
					ivFileImage.setImageResource(R.drawable.dir);
				}
				String fileName="..";
				if(subFile != null) {
					fileName=subFile.getName();
					if(fileName.equals("")) {
						fileName="文件名未知";
					}
				}
				tvFileName.setText(fileName);
			}
			return convertView;
		}

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(selectedPaths == null) {
				selectedPaths = new ArrayList<String>();
			}
			String path=getCurPath();
			if(isChecked){
				selectedPaths.add(fileLists.get(path).get((Integer)buttonView.getTag()).getPath());
			}else{
				selectedPaths.remove(fileLists.get(path).get((Integer)buttonView.getTag()).getPath());
			}
			
		}
		
	}
	
	private String getCurPath() {
		String path="";
		for(int i=0;i<pathStack.size();i++){
			path+=pathStack.get(i)+"/";
		}
		path=path.substring(0, path.length()-1);
		return path;
	}

	public interface OnFileListItemClickListener {
		public void onDirClick(File dir);
		public void onItemClick(File file);
	}

}
