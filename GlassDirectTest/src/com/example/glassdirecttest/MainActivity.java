package com.example.glassdirecttest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.google.glass.companion.CompanionConstants;
import com.google.glass.companion.CompanionMessagingUtil;
import com.google.glass.companion.GlassConnection;
import com.google.glass.companion.GlassConnection.GlassConnectionListener;
import com.google.glass.companion.GlassMessagingUtil;
import com.google.glass.companion.GlassProtocol;
import com.google.glass.companion.Proto;
import com.google.glass.companion.Proto.Envelope;
import com.google.glass.companion.Proto.GlassInfoRequest;
import com.google.glass.companion.Proto.ScreenShot;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	
	public static BluetoothDevice device;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		
		Set<BluetoothDevice> devices =  BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		
		for (BluetoothDevice d : devices){
			if (d.getName().contains("Glass")){
				
				Log.d("JOE","Talking to " + d.getName());
				
				device = d;
				}
		}	
			
				
		JoeMessageUtil.send();


		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	
}




