package com.viaplay.ime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.viaplay.im.hardware.JoyStickTypeF;
import com.viaplay.ime.R;
import com.viaplay.ime.util.DBHelper;
import com.viaplay.ime.util.JnsEnvInit;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ó|ó??÷????,μúò?′???DDê±oò??ê?settingò3￡?????ê±oò??ê?ó??·áD±í
 * 
 * @author Steveb
 *
 */
@SuppressWarnings("deprecation")
public class JnsIME extends TabActivity {
	
	private TabHost mTabHost;
	private LinearLayout ll; 
	private TabWidget tw;
	private final static String KEY_MAP_FILE_TAG = ".keymap";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createTab();
		JnsIMECoreService.activitys.add(this);
		createTmpDir();
    	initData();
    }
    
	private void createDefautKeyFile()
	{
		File selffile = new File(this.getFilesDir() + "/" + this.getPackageName()+KEY_MAP_FILE_TAG);
		if(!selffile.exists())
		{	
			FileOutputStream fos = null;
			try {
				fos = this.openFileOutput(this.getPackageName()+KEY_MAP_FILE_TAG  , Context.MODE_PRIVATE);
				writeDefaut(fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File default1 = new File(this.getFilesDir() + "/default1"+KEY_MAP_FILE_TAG);
		if(!default1.exists())
		{
			FileOutputStream fos = null;
			try {
				fos = this.openFileOutput("default1"+KEY_MAP_FILE_TAG  , Context.MODE_PRIVATE);
				writeDefaut(fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void writeDefaut(FileOutputStream fos)
	{
		try {
			fos.write((JoyStickTypeF.BUTTON_A+":GameA"+":"+JoyStickTypeF.BUTTON_A_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_A+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_B+":GameB"+":"+JoyStickTypeF.BUTTON_B_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_B+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_X+":GameX"+":"+JoyStickTypeF.BUTTON_X_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_X+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_Y+":GameY"+":"+JoyStickTypeF.BUTTON_Y_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_Y+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_L1+":L1"+":"+JoyStickTypeF.BUTTON_L1_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_L1+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_L2+":L2"+":"+JoyStickTypeF.BUTTON_GAS_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_L2+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_R1+":R1"+":"+JoyStickTypeF.BUTTON_R1_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_R1+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_R2+":R2"+":"+JoyStickTypeF.BUTTON_BRAKE_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_R2+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_DOWN+":Down"+":"+JoyStickTypeF.BUTTON_DOWN_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_DOWN+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_UP+":Up"+":"+JoyStickTypeF.BUTTON_UP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_UP+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_LEFT+":Left"+":"+JoyStickTypeF.BUTTON_LEFT_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_LEFT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_RIGHT+":Right"+":"+JoyStickTypeF.BUTTON_RIGHT_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_RIGHT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_YI+":Down"+":"+JoyStickTypeF.BUTTON_YI_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_DOWN+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_YP+":Up"+":"+JoyStickTypeF.BUTTON_YP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_UP+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_XI+":Left"+":"+JoyStickTypeF.BUTTON_XI_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_LEFT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_XP+":Right"+":"+JoyStickTypeF.BUTTON_XP_SCANCODE+":"+KeyEvent.KEYCODE_DPAD_RIGHT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_SELECT+":Select"+":"+JoyStickTypeF.BUTTON_SELECT_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_SELECT+"\n").getBytes());
			fos.write((JoyStickTypeF.BUTTON_START+":Start"+":"+JoyStickTypeF.BUTTON_START_SCANCODE+":"+KeyEvent.KEYCODE_BUTTON_START+"\n").getBytes());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
    private void createTab() {
    	mTabHost = getTabHost();
    	ll = (LinearLayout)mTabHost.getChildAt(0);
    	tw = (TabWidget)ll.getChildAt(0);

    	RelativeLayout tabIndicator1 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab = (TextView)tabIndicator1.findViewById(R.id.title);
    	tvTab.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.gamelist_title), null, null);
    
    	RelativeLayout tabIndicator2 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab1 = (TextView)tabIndicator2.findViewById(R.id.title);
    	tvTab1.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.controller_title), null, null);
 	
    	RelativeLayout tabIndicator3 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab2 = (TextView)tabIndicator3.findViewById(R.id.title);
    	tvTab2.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.settings_title), null, null);
  	
    	
    	Intent gameIntent = new Intent();
    	gameIntent.setClass(this, JnsIMEGameListActivity.class);
    	TabHost.TabSpec gameSpec = mTabHost.newTabSpec("GameList").setIndicator(tabIndicator1).setContent(gameIntent);
    	mTabHost.addTab(gameSpec);
    	
    	Intent controlIntent = new Intent();
    	controlIntent.setClass(this, JnsIMEControllerActivity.class);
    	TabHost.TabSpec controlSpec = mTabHost.newTabSpec("control").setIndicator(tabIndicator2).setContent(controlIntent);
    	mTabHost.addTab(controlSpec);
    	
    	Intent settingsIntent = new Intent();
    	settingsIntent.setClass(this, JnsIMESettingActivity.class);
    	TabHost.TabSpec settingsSpec = mTabHost.newTabSpec("setting").setIndicator(tabIndicator3).setContent(settingsIntent);
    	mTabHost.addTab(settingsSpec);
    	SharedPreferences perfer = this.getSharedPreferences("guide", Activity.MODE_PRIVATE);
    	boolean guide = perfer.getBoolean("guide", true);
    	if(guide)
    	{
    		mTabHost.setCurrentTab(2);
    		SharedPreferences.Editor  edit = perfer.edit();
    		edit.putBoolean("guide", false);
    		Intent intent = new Intent();
			intent.setAction("android.settings.SHOW_INPUT_METHOD_PICKER");
			edit.commit();
			this.sendBroadcast(intent);
			
    	}
    	else
    		mTabHost.setCurrentTab(0); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
	public void onDestroy()
	{
		super.onDestroy();
		JnsIMECoreService.activitys.remove(this);
	}
    void createTmpDir()
	{
		File rdir = new File("mnt/sdcard/jnsinput");
		if(!rdir.exists())
			rdir.mkdir();
		File dir = new File("mnt/sdcard/jnsinput/app_icon");
		if(!dir.exists())
			dir.mkdir();
	}
    @SuppressLint("SdCardPath")
    /**
     *  初始化数据库以及运行环境
     */
	private void initData()
	{
    	JnsEnvInit.mContext = this;
		SharedPreferences sp = this.getApplicationContext(). getSharedPreferences("init", Context.MODE_PRIVATE); 
		SharedPreferences.Editor  edit = sp.edit();
		SharedPreferences versionsp = this.getApplicationContext(). getSharedPreferences("init", Context.MODE_PRIVATE); 
		SharedPreferences.Editor  versionedit = sp.edit();
		PackageManager packageManager = getPackageManager();
		int cVersionNum = 0;
		int Version = versionsp.getInt("version", 0);
		try 
		{
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			cVersionNum = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int i = sp.getInt("boolean", 0);
		if(i == 0)
		{
			if(CopyDatabase())
			{
				createDefautKeyFile();
				edit.putInt("boolean", 1);
				edit.commit();
				CopyMappings();
				versionedit.putInt("version", cVersionNum);
				versionedit.commit();
			}
			else
			{
				Toast.makeText(this, "Init failed", Toast.LENGTH_SHORT).show();
			}
		}
		else if(Version < cVersionNum)
		{
			if(updataDatabase())
			{	
				versionedit.putInt("version", cVersionNum);
				versionedit.commit();
				Toast.makeText(this, this.getText(R.string.update_list), Toast.LENGTH_SHORT).show();
			}
		}
	}
	@SuppressLint("SdCardPath")
	private boolean updataDatabase()
	{
		if(!JnsEnvInit.movingFile("/mnt/sdcard/viaplay/_via_game","_via_game"))
		{	
			Toast.makeText(this, "Copy databases failed", Toast.LENGTH_SHORT).show();
			return false;
		}
		String filename = "/mnt/sdcard/viaplay/_via_game";

		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(filename, null);
		SQLiteDatabase db = JnsIMECoreService.aph.dbh.getReadableDatabase();
		Cursor cursor= null;

		cursor = sqLiteDatabase.query("_via_game", null, null,
				null, null, null, "_description");
		cursor.moveToFirst();

		while(!cursor.isLast())
		{
			String name = cursor.getString(cursor.getColumnIndex("_name"));
			String selection[]  = {name};

			// 获得原数据库游戏列表
			Cursor tmpC = db.query("_via_game", null, "_name=?", selection, null, null, null);

			// 向数据库中插入更新的游戏内容
			if(tmpC.getCount() == 0)
			{	
				ContentValues cv = new ContentValues();
				cv.put("_name", cursor.getString(cursor.getColumnIndex("_name")));
				cv.put("_description", cursor.getString(cursor.getColumnIndex("_description")));
				cv.put("_lable", cursor.getString(cursor.getColumnIndex("_lable")));
				cv.put("_url",  cursor.getString(cursor.getColumnIndex("_url")));
				cv.put("_control", cursor.getString(cursor.getColumnIndex("_control")));
				cv.put("_exists", "false");
				cv.put("_lable_zh", cursor.getString(cursor.getColumnIndex("_lable_zh")));
				try 
				{
					if(db.insert(DBHelper.TABLE, "", cv) < 0)
					{	
						Toast.makeText(this, "Init databases failed", Toast.LENGTH_SHORT).show();
						return false;
					}
					String apkname = cursor.getString(cursor.getColumnIndex("_name"));
					JnsEnvInit.movingFile(this.getFilesDir()+"/"+ apkname + ".keymap", apkname+ ".keymap") ;
					JnsEnvInit.movingFile("/mnt/sdcard/viaplay/app_icon/"+ apkname + ".icon", apkname + ".icon.png");
					tmpC.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return false;
				}
			}
			cursor.moveToNext();
		}
		cursor.close();
		cursor = db.query("_via_game", null, null,
				null, null, null, "_description");
		if(JnsIMEGameListActivity.gameAdapter != null)
		{
			JnsIMEGameListActivity.gameAdapter.setCursor(cursor);
			JnsIMEGameListActivity.gameAdapter.notifyDataSetChanged();
		}
		return true;
	}
	@SuppressLint("SdCardPath")
	private void CopyMappings()
	{
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/mnt/sdcard/viaplay/_via_game", null);
		Cursor cursor= null;

		cursor = sqLiteDatabase.query("_via_game", null, null,
				null, null, null, "_lable");
		cursor.moveToFirst();
		while(!cursor.isLast())
		{
			String apkname = cursor.getString(cursor.getColumnIndex("_name"));
			JnsEnvInit.movingFile(this.getFilesDir()+"/"+ apkname + ".keymap", apkname+ ".keymap") ;
			JnsEnvInit.movingFile("/mnt/sdcard/viaplay/app_icon/"+ apkname + ".icon", apkname + ".icon.png");
			cursor.moveToNext();
		}
	}
	@SuppressLint("SdCardPath")
	private boolean CopyDatabase()
	{
		if(!JnsEnvInit.movingFile("/mnt/sdcard/viaplay/_via_game","_via_game"))
		{	
			Toast.makeText(this, "Copy databases failed", Toast.LENGTH_SHORT).show();
			return false;
		}
		String filename = "/mnt/sdcard/viaplay/_via_game";

		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(filename, null);
		Cursor cursor= null;

		cursor = sqLiteDatabase.query("_via_game", null, null,
				null, null, null, "_lable");
		cursor.moveToFirst();

		while(!cursor.isLast())
		{
			SQLiteDatabase db = JnsIMECoreService.aph.dbh.getReadableDatabase();
			try
			{
				db.delete(DBHelper.TABLE, "_name=?", new String[] { cursor.getString(cursor.getColumnIndex("_name")) });
			}
			catch(Exception e)
			{

			}
			ContentValues cv = new ContentValues();
			cv.put("_name", cursor.getString(cursor.getColumnIndex("_name")));
			cv.put("_description", cursor.getString(cursor.getColumnIndex("_description")));
			cv.put("_lable", cursor.getString(cursor.getColumnIndex("_lable")));
			cv.put("_url",  cursor.getString(cursor.getColumnIndex("_url")));
			cv.put("_control", cursor.getString(cursor.getColumnIndex("_control")));
			cv.put("_exists", "false");
			cv.put("_lable_zh", cursor.getString(cursor.getColumnIndex("_lable_zh")));
			try {
				if(db.insert(DBHelper.TABLE, "", cv) < 0)
				{	
					Toast.makeText(this, "Init databases failed", Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
			cursor.moveToNext();
		}
		if(JnsIMEGameListActivity.gameAdapter != null)
		{
			JnsIMEGameListActivity.gameAdapter.setCursor(cursor);
			JnsIMEGameListActivity.gameAdapter.notifyDataSetChanged();
		}
		return true;
	}

}
