package com.zbf.iceseal.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.zbf.iceseal.R;
import com.zbf.iceseal.base.BaseDao;


public class ViewTools {
	
	public static ProgressDialog showHorizontalProgressDialog(Context context, String title, String message, int max, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(false);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.setProgressStyle(R.style.ProgressStyle_Horizontal);
		dialog.setMax(max);
		dialog.show();
		return dialog;
	}
	public static void showShortToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	public static void showLongToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	public static void showConfirmDialog(Context context, int iconId, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveListener, String negativeText, DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setIcon(iconId);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setPositiveButton(positiveText, positiveListener);
		dialog.setNegativeButton(negativeText, negativeListener);
		dialog.create();
		dialog.show();
	}
	public static void showConfirmDialog(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveListener, String negativeText, DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setPositiveButton(positiveText, positiveListener);
		dialog.setNegativeButton(negativeText, negativeListener);
		dialog.create();
		dialog.show();
	}
	public static boolean checkMedialib(final Context context, final DialogInterface.OnClickListener addListener) {
		if(BaseDao.hasSongs(context)) {
			return true;
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("提示");
		dialog.setMessage("媒体库中还没有歌曲哦\n现在添加？");
		dialog.setPositiveButton("取消", null);
		dialog.setNegativeButton("添加", addListener);
		dialog.create();
		dialog.show();
		return false;
	}
	
	public static void createFolderSelectWindow(final Context context, View windowToken, final OnFileSelectButtonClickListener listener) {
		final WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		final View fileSelectView = inflater.inflate(R.layout.window_filebrowser, null);
		final MyFileBrowser fileList = (MyFileBrowser) fileSelectView.findViewById(R.id.myFileBrowser);
		Button btnOk = (Button) fileSelectView.findViewById(R.id.btnOK);
		Button btnAll = (Button) fileSelectView.findViewById(R.id.btnAll);
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wm.removeView(fileSelectView);
				ArrayList<String> selectedPaths = (ArrayList<String>) fileList.getSelectedFilepath();
				if(selectedPaths != null && selectedPaths.size() > 0) {
					listener.onFileSelectButtonClick(selectedPaths);
				} else {
					listener.onCancelButtonClick();
				}
			}
		});
		btnAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wm.removeView(fileSelectView);
				listener.onCancelButtonClick();
			}
		});
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.gravity = Gravity.CENTER;
		float h = wm.getDefaultDisplay().getHeight() * 0.75f;
		float w = wm.getDefaultDisplay().getWidth() * 0.9f;
		p.width = (int)w;
		p.height = (int)h;
		p.token = windowToken.getWindowToken();
		p.format = PixelFormat.TRANSLUCENT;
		p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
		wm.addView(fileSelectView, p);
	}
	
	public interface OnFileSelectButtonClickListener {
		public void onFileSelectButtonClick(ArrayList<String> selectedPaths);
		public void onCancelButtonClick();
	}
	
}
