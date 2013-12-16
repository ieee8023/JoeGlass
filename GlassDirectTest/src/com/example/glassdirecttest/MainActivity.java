package com.example.glassdirecttest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import com.google.glass.companion.CompanionConstants;
import com.google.glass.companion.CompanionMessagingUtil;
import com.google.glass.companion.GlassProtocol;
import com.google.glass.companion.Proto;
import com.google.glass.companion.Proto.Envelope;
import com.google.glass.companion.Proto.ScreenShot;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Set<BluetoothDevice> devices =  BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		
		for (BluetoothDevice d : devices){
			if (d.getName().contains("Glass")){
				
				Log.d("JOE","Talking to " + d.getName());
				BluetoothSocket socket;
				try {
					
					
					Log.d("JOE",Arrays.toString(d.getUuids()));
					
					socket = d.createRfcommSocketToServiceRecord(CompanionConstants.SECURE_UUID);
					
					socket.connect();
					
					if (!socket.isConnected()){
						Log.e("JOE","Could not connect");
						
					}
					
					OutputStream os = socket.getOutputStream();
					
					
					if (os == null){
						Log.e("JOE","Could not connect os is null");
						
					}
					
					// Envelope is the root of the message hierarchy.
					Proto.Envelope envelope = CompanionMessagingUtil.newEnvelope();
					// This example is for obtaining screenshot.
					ScreenShot screenShot = new ScreenShot();
					screenShot.startScreenshotRequestC2G = true;
					envelope.screenshot = screenShot;
					GlassProtocol.writeMessage(envelope, socket.getOutputStream());
					Log.d("JOE","Wrote Message");
					
					os.close();
				
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
			
		}
		
		//Log.d("JOE", Arrays.toString(BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray()));
		
		

//		
//		
//		Envelope envelope1 = (Envelope) GlassProtocol.readMessage(new Envelope(), socket.getInputStream());
//		if (envelope1.screenshot != null) {
//		// screenshot response includes screenshot field in envelope
//		// …do something…
//		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
