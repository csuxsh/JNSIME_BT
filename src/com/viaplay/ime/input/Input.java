package com.viaplay.ime.input;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Build;
import android.view.InputDevice.MotionRange;

public class Input {


	@SuppressWarnings("rawtypes")
	protected static int[] getConnectedJoysticks()
	{
		if(Build.VERSION.SDK_INT < 9)
			return null;
		int i = 0;
		int[] validDeviceID;
		try
		{
			Class<?> android_view_InputDevice = Class.forName("android.view.InputDevice");
			int[] deviceIds = (int[])android_view_InputDevice.getDeclaredMethod("getDeviceIds", new Class[0]).invoke(android_view_InputDevice, new Object[0]);
			Arrays.sort(deviceIds);
			while (i < deviceIds.length)
			{
				Class[] parameters = new Class[1];
				parameters[0] = Integer.TYPE;
				Method getDevice = android_view_InputDevice.getMethod("getDevice", parameters);
				Object[] deviceId = new Object[1];
				deviceId[0] = Integer.valueOf(deviceIds[i]);
				Object device = getDevice.invoke(android_view_InputDevice, deviceId);
				if (device == null)
					break;
				if ((0x1000010 & ((Integer)device.getClass().getMethod("getSources", new Class[0]).invoke(device, new Object[0])).intValue()) == 16777232)
				{
					i++;
				}
				else
				{	
					deviceIds[i] = -1;
					i++;
				}
			}
			validDeviceID = new int[i];
			int i3 = 0;
			int i4 = 0;
			while (i3 < deviceIds.length)
			{
				if (deviceIds[i3] != -1)
				{
					int i11 = i4 + 1;
					validDeviceID[i4] = deviceIds[i3];
					i4 = i11;
				}
				i3++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		LinkedHashMap joysticks = new LinkedHashMap();
		int i5 = validDeviceID.length;
		int i6 = 0;
		while (i6 < i5)
		{
			int validId = validDeviceID[i6];
			int[] axes = getJoystickAxes(validId);
			if (axes != null)
			{
				Integer[] arrayOfInteger = new Integer[1 + axes.length];
				arrayOfInteger[0] = Integer.valueOf(i6);
				for (int i10 = 0; i10 < axes.length; i10++)
					arrayOfInteger[(i10 + 1)] = Integer.valueOf(axes[i10]);
				joysticks.put(Integer.valueOf(validId), arrayOfInteger);
			}
			i6++;
		}
		return validDeviceID;
	}

	@SuppressWarnings("rawtypes")
	protected static int[] getJoystickAxes(int device)
	{
		if (Build.VERSION.SDK_INT < 9)
			return null;
		try
		{
			Class<?> android_view_InputDevice = Class.forName("android.view.InputDevice");
			Class[] paramters = new Class[1];
			paramters[0] = Integer.TYPE;
			Method getDevice = android_view_InputDevice.getMethod("getDevice", paramters);
			Object[] deviceId = new Object[1];
			deviceId[0] = Integer.valueOf(device);
			Object devices = getDevice.invoke(android_view_InputDevice, deviceId);
			if (devices != null)
			{
				@SuppressWarnings("unchecked")
				List<MotionRange> getMotionRangesList = (List<MotionRange>)devices.getClass().getMethod("getMotionRanges", new Class[0]).invoke(android_view_InputDevice, new Object[0]);
				int[] motionRanges = new int[getMotionRangesList.size()];
				Iterator<MotionRange> getMotionRangesItertor = getMotionRangesList.iterator();
				int i = 0;
				while (getMotionRangesItertor.hasNext())
				{
					Object getMotionRange = getMotionRangesItertor.next();
					if ((0x1000010 & ((Integer)getMotionRange.getClass().getMethod("getSource", new Class[0]).invoke(getMotionRange, new Object[0])).intValue()) != 16777232)
						continue;
					motionRanges[i] = ((Integer)getMotionRange.getClass().getMethod("getAxis", new Class[0]).invoke(getMotionRange, new Object[0])).intValue();
					i++;
				}

				Arrays.sort(motionRanges);
				return motionRanges;
			}
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
			return null;
		}
		return null;
	}

	protected String getJoystickName(int paramInt)
	{
		if (Build.VERSION.SDK_INT < 9)
			return null;
		try
		{
			Class<?> android_view_InputDevice = Class.forName("android.view.InputDevice");
			@SuppressWarnings("rawtypes")
			Class[] paramters = new Class[1];
			paramters[0] = Integer.TYPE;
			Method localMethod = android_view_InputDevice.getMethod("getDevice", paramters);
			Object[] deviceId = new Object[1];
			deviceId[0] = Integer.valueOf(paramInt);
			Object device = localMethod.invoke(android_view_InputDevice, deviceId);
			if (device != null)
			{
				String str = (String)device.getClass().getMethod("getName", new Class[0]).invoke(device, new Object[0]);
				return str;
			}
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
			return null;
		}
		return null;
	}
}
