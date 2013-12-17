package com.example.glassdirecttest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.bluetooth.BluetoothClass.Device;
import android.util.Log;

import com.google.glass.companion.GlassConnection;
import com.google.glass.companion.GlassMessagingUtil;
import com.google.glass.companion.Proto;
import com.google.glass.companion.GlassConnection.GlassConnectionListener;
import com.google.glass.companion.Proto.Envelope;

public class JoeMessageUtil {


	public static void send() {
		
		GlassConnection c = new GlassConnection();
		
		try {
			c.connect(MainActivity.device);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		c.registerListener(new GlassConnectionListener() {
			
			@Override
			public void onServiceSearchError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onServiceNotFound() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivedEnvelope(Envelope envelope) {
				Log.d("JOE - received", envelope.toString());
				
			}
			
			@Override
			public void onDeviceScanCompleted() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDeviceDiscovered(Device device) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onConnectionOpened() {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		
		Proto.Envelope envelope = GlassMessagingUtil.createTimelineMessage("Test Message:" + dateFormat.format(date), String.valueOf(Math.random()*10000.0));

		c.write(envelope);
		
		Log.d("JOE","Wrote Message");
		
		
	}
	
	
	
}
