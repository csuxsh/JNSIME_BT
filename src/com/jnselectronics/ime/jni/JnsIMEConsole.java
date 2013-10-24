package com.jnselectronics.ime.jni;

import java.io.FileDescriptor;

import android.util.Log;

/**
 * 用于程序读event数据所需要的运行环境初始化
 * 
 * @author Steven
 *
 */

public class JnsIMEConsole {
	private static final String TAG = "BlueOceanJNIAPI";
	static {
		try {
			System.loadLibrary("jni_console");
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, e.getMessage());
		}
	}
	public static native void close(FileDescriptor paramFileDescriptor);
	public static native FileDescriptor createSubprocess(String paramString1, String paramString2, String paramString3, int[] paramArrayOfInt);
	public static native void setPtyWindowSize(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
	public static native int waitFor(int paramInt);
	public static native String exeCommand(String cmd);
}
