package com.jnselectronics.ime.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jnselectronics.ime.JnsIMERoot;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

/**
 * 应用的运行环境初始化类
 * 
 * @author steven.xu
 *
 */
public class JnsEnvInit {
	
	private final static String TAG = "JnsEnvInit";
	/**
	 * 用与修改/dev/input目下设备文件权限的进程,需要root。蓝牙模式下可以不用修改权限。
	 */
	private static  Process process = null;
	private static  DataOutputStream dos = null;
	private static  DataInputStream dis = null;
	/**
	 * 由{@link JnsIMECoreServie}JnsIMECoreServie提供过来的context引用
	 */
	public static Context mContext;
	/**
	 * 标记当前应用是否已经获得root授权
	 */
	public static boolean rooted = false;

   
	/**
	 * 用于检测 {@link process} process的运行的错误状况。主要用于调试。
	 * 
	 * @author steven.xu
	 *
	 */
	private static  class ErrorOutThread extends  Thread
	{
		private  static ErrorOutThread  mErrorOutThread = null;
		private ErrorOutThread()
		{

		}

		private static ErrorOutThread getErrorOutThread()
		{
			if(mErrorOutThread == null)
				mErrorOutThread = new ErrorOutThread();
			return mErrorOutThread;
		}

		public void run()
		{
			String line="";
			InputStreamReader peis = new InputStreamReader(process.getErrorStream());
			BufferedReader ber = new BufferedReader(peis);
			Log.d(TAG,"star error output");
			try {
				while((line = ber.readLine())!=null)
				{
					Log.d(TAG, "erro  "+line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	@SuppressLint("SdCardPath")
	/**
	 * 初始化运行环境，启动jnsinput。jar 需root授权
	 * 
	 * @return 
	 */
	public static boolean startJnsInputServier()
	{
		JnsIMERoot.setContext(mContext);
		JnsIMERoot.initConsole();
		movingFile("/mnt/sdcard/jnsinput/screencap.sh", "screencap.sh");
		if(movingFile("/mnt/sdcard/jnsinput/jnsinput.jar", "jnsinput.jar"))
			if(movingFile("/mnt/sdcard/jnsinput/jnsinput.sh", "jnsinput.sh"))
				runJnsInput();
		return false;
	}
	/**
	 *  修改/dev/input下设备文件读写权限，需root，蓝牙版本可以不执行。
	 * 
	 * @return 
	 */
	public static boolean root()
	{
		/*
		try {
			process = Runtime.getRuntime().exec("su");
			if(process ==null)
				return false;
			if(process.getOutputStream()==null)
			{	
				Log.d(TAG, "roo失败t");
				return false;
			}
			dos = new DataOutputStream(process.getOutputStream());
			dis = new DataInputStream(process.getInputStream());

			ErrorOutThread errothread = ErrorOutThread.getErrorOutThread();
			try
			{
				errothread.start();
			}
			catch(Exception e)
			{
				Log.d(TAG,"errothread start faield");
			}
			if(!checkRooted(dos, dis))
			{	
				Log.d(TAG, "check root failed");
				return false;
			}
			rooted = true;
			new Thread(new Runnable()
			{

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						while(true)
						{	
							chmodDevicdeFile();
							Thread.sleep(500);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		*/
		return false;
	}
	/**
	 * 检查设备是否已经获得root授权
	 * 
	 * @return 获得root返回true,否则返回false
	 */
	private static boolean checkRooted(DataOutputStream dos, DataInputStream dis)
	{
		try {
			dos.write("id \n".getBytes());
			dos.flush();
			@SuppressWarnings("deprecation")
			String line = dis.readLine();
			Log.e(TAG, "fffffffff line = " + line);
			if (line == null) return false;
			if (line.contains("uid=0(root)")) 
				return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static void runJnsInput()
	{
		if(rooted)
		{	
			try {
				//Process process = Runtime.getRuntime().exec("sh /mnt/sdcard/jnsinput/jnsinput.sh");
				Process jarprocess = Runtime.getRuntime().exec("su");
				DataOutputStream jardos = new DataOutputStream(jarprocess.getOutputStream());
				jardos.write("rm /data/jnsinput/jnsinput.jar \n".getBytes());
				jardos.flush();
				String cmd = "export LD_LIBRARY_PATH=/vender/lib; export CLASSPATH=/mnt/sdcard/jnsinput/jnsinput.jar; exec app_process /system/bin com.blueocean.jnsinput.JNSInputServer \n";
				jardos.write(cmd.getBytes());
				jardos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void chmodDevicdeFile() throws IOException
	{
		dos.flush();
		dos.writeBytes("chmod 777 /dev/input/* \n");
		dos.flush();
		dos.write("chmod 777 /dev/graphics/fb* \n".getBytes());
		dos.flush();
	}

	public static boolean movingFile(String dst, String src) {
		try {
			InputStream is = mContext.getAssets().open(src);
			int size = is.available();
			if (size > 0) {
				File file = new File(dst);
				byte[] buffer = new byte[size];
				is.read(buffer);
				FileOutputStream os = new FileOutputStream(file);
				os.write(buffer);
				os.flush();
				os.close();
				os = null;
				file = null;
			} else {
				return false;
			} 
			is.close();
			is = null;
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
