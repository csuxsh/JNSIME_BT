package com.viaplay.ime.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 * 数据库增删改查的操控类
 * 
 * 
 * @author Steven.xu
 *
 */
public class AppHelper {

	public final DBHelper dbh ;
	private Context context;

	public AppHelper(Context context)
	{
		dbh  = DBHelper.getDBHelper(context);
		this.context = context;
	}
	/**
	 * 向数据库插入一个应用的信息，并且将应用的图标半场到sdcard上。如果该应用已经存在于数据则先删除记录再插入
	 * 
	 * @param 应用的包名
	 * @param 应用是否已经安装
	 * @return 操作成功返回true,失败返回false
	 */
	@SuppressLint("SdCardPath")
	synchronized public boolean Insert(String name, String exists)
	{
		PackageManager pm = context.getPackageManager();
		String lable = "";
		Bitmap icon = null;
		try {
			lable = (String) pm.getApplicationLabel(pm.getApplicationInfo(name, PackageManager.GET_UNINSTALLED_PACKAGES));
			icon = DrawableUtil.drawableToBitmap(pm.getApplicationIcon(name));
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		File icon_file = new File("/mnt/sdcard/viaplay/app_icon/"+name+".icon");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(icon_file);
			icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.delete(DBHelper.TABLE, "_name=?", new String[] { name });
		ContentValues cv = new ContentValues();
		cv.put("_name", name);
		cv.put("_lable", lable);
		cv.put("_lable_zh", lable);
		cv.put("_control", "F");
		cv.put("_exists", exists);
		try {
			if(db.insert(DBHelper.TABLE, "", cv)> -1)
				return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 在数据库中删除一个应用。
	 * 
	 * @param 应用的包名
	 * @return 操作成功返回true,失败返回false
	 */
	synchronized public boolean delete(String name)
	{
		SQLiteDatabase db = dbh.getReadableDatabase();
		File file = new File("mnt/sdcard/viaplay/app_icon/"+name+".icon");
		if(file.exists())
			file.delete();
		if(db.delete(DBHelper.TABLE, "_name=?", new String[] { name }) >0)
			return false;
		return true;
	}
	/**
	 * 在数据库中查询指定应用
	 * 
	 * @param 应用的包名
	 * @param 操控器型号
	 * @return 数据库的cursor
	 */
	@SuppressWarnings("null")
	synchronized public Cursor Qurey(String arg, String controll)
	{
		//String arg = startdate+" and "+enddate;
		String selection = null;
		String args[] = new String[1];
		if((controll != null) || (!controll.equals("")))
		{
			selection = "_control like ?";
			args[0] = "%"+controll+"%";
		}
		else
			args = null;
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = null;
		String order = null;
		if(Locale.getDefault().getLanguage().startsWith("zh"))
			order= "_lable_zh COLLATE LOCALIZED";
		else
			order = "_lable";
		try {
			cursor = db.query(DBHelper.TABLE, null, selection,
					args, null, null, order);
			if(cursor.moveToFirst())
			{
				System.out.println("cuisor has content");
			}
			else
			{
				System.out.println("cuisor has none");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return cursor;
	}
}
