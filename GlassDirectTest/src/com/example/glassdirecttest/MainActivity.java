package com.example.glassdirecttest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
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

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothClass.Device;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static Activity context = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		//JoeMessageUtil.sendText("Title","body");

		
		Button buttonOne = (Button) findViewById(R.id.button1);
		buttonOne.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		           
		    	EditText text = (EditText)findViewById(R.id.editText1);
		    	
		    	Toast.makeText(MainActivity.context, "Sending to glass", Toast.LENGTH_LONG);
		    	
		    	JoeMessageUtil.sendText("Message Test",text.getText().toString());
		    }
		});
		
		
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		          
		    	Toast.makeText(MainActivity.context, "Taking Screenshot", Toast.LENGTH_LONG);
		    	JoeMessageUtil.takePicture();
		    	
		    	
		    	//scan for pictures
		    	sendBroadcast(new Intent(
		    			Intent.ACTION_MEDIA_MOUNTED,
		    			            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		    }
		});
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
	
	
	
	
	public void killPackageProcesses(String packagename) {
	    int pid = 0;
	    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> pids = am.getRunningAppProcesses();
	    for (int i = 0; i < pids.size(); i++) {
	        ActivityManager.RunningAppProcessInfo info = pids.get(i);
	        Log.d("JOE","process: " + info.processName);
	        if (info.processName.equalsIgnoreCase(packagename)) {
	        	Log.d("JOE","killing " + info.processName);
	            pid = info.pid;
	        }
	    }
	    android.os.Process.killProcess(pid);

	}
	
	public void killService(String packagename) {
	    int pid = 0;
	    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    am.killBackgroundProcesses("com.google.glass.companion");
//	    List<RunningServiceInfo> pids = am
//	            .getRunningServices(9999);
//	    for (int i = 0; i < pids.size(); i++) {
//	        ActivityManager.RunningServiceInfo info = pids.get(i);
//	        Log.d("JOE","service: " + info.service.getClassName());
//	        if (info.service.getClassName().equalsIgnoreCase(packagename)) {
//	        	Log.d("JOE","killing " + info.service.getClassName());
//	            pid = info.pid;
//	        }
//	    }
//	    android.os.Process.killProcess(pid);

	}
	

	
}




