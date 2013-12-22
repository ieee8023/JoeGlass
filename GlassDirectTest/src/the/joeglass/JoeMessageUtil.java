package the.joeglass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothClass.Device;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.glass.companion.CompanionMessagingUtil;
import com.google.glass.companion.GlassConnection;
import com.google.glass.companion.GlassMessagingUtil;
import com.google.glass.companion.Proto;
import com.google.glass.companion.GlassConnection.GlassConnectionListener;
import com.google.glass.companion.Proto.CompanionInfo;
import com.google.glass.companion.Proto.Envelope;
import com.google.glass.companion.Proto.GlassInfoRequest;
import com.google.glass.companion.Proto.GlassInfoResponse;
import com.google.glass.companion.Proto.ScreenShot;

public class JoeMessageUtil {
	
	private static GlassConnection c = null;
	public static Bitmap recentImage = null;
	
	
	
	public static void sendInfoRequest() throws Exception{
		
        Envelope envelope2 = CompanionMessagingUtil.newEnvelope();
        GlassInfoRequest glassInfoRequest = new GlassInfoRequest();
        glassInfoRequest.requestBatteryLevel = true;
        glassInfoRequest.requestStorageInfo = true;
        glassInfoRequest.requestDeviceName = true;
        glassInfoRequest.requestSoftwareVersion = true;
       // glassInfoRequest.requestNeedSetup = true;
        envelope2.glassInfoRequestC2G = glassInfoRequest;
        send(envelope2);
	}

	public static void sendText(String subject, String text) throws Exception{
	
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		
		Proto.Envelope envelope = 
				GlassMessagingUtil.createTimelineMessage(
		"<article> <section><p>" + subject + "</p> <p class=\"text-auto-size\">" + text + "</p> </section> </article>"				
						, String.valueOf(Math.random()*10000.0));

		
		send(envelope);
		
	}	
	
	
	
	
	public static void takePicture() throws Exception{
		
		// Envelope is the root of the message hierarchy.
		Proto.Envelope envelope = CompanionMessagingUtil.newEnvelope();
		// This example is for obtaining screenshot.
		ScreenShot screenShot = new ScreenShot();
		screenShot.startScreenshotRequestC2G = true;
		envelope.screenshot = screenShot;
		send(envelope) ;
		
	}
	
	
	

	public static void send(Proto.Envelope envelope) throws Exception{
		
		
		Log.d("JOE","Sending Envelope");
		
		
		
		if (c != null){
			c.close();
		}		
		
		Log.d("JOE","Finding Glass");
    	MainActivity.context.runOnUiThread(new Runnable(){
    	    public void run(){
				TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
		    	tv.setText("Finding Glass");
		    	tv.invalidate();
    	    }
    	});
		
		BluetoothDevice device = null;
		
		Set<BluetoothDevice> devices =  BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		
		for (final BluetoothDevice d : devices){
			if (d.getName().contains("Glass")){
				
				Log.d("JOE","Talking to " + d.getName());
				
				device = d;
				
		    	MainActivity.context.runOnUiThread(new Runnable(){
		    	    public void run(){
						TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
				    	tv.setText("Found " + d.getName());
				    	tv.invalidate();
		    	    }
		    	});
				
				}
		}	
		

		
		
		c = new GlassConnection();
		
		c.connect(device);
		
		
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
				
				Log.d("JOE", "=============onReceivedEnvelope");
				Log.d("JOE", envelope.toString());
				
				if (envelope.screenshot != null) {
		            if (envelope.screenshot.screenshotBytesG2C != null) {
		                InputStream in = new ByteArrayInputStream(envelope.screenshot.screenshotBytesG2C);
		                try {
		                	
		                	recentImage = BitmapFactory.decodeStream(in);
		                	
		                	
		                	MainActivity.context.runOnUiThread(new Runnable(){
		                	    public void run(){
		                	        
		                	    	ImageView v = (ImageView)MainActivity.context.findViewById(R.id.imageView1);
				                    v.setImageBitmap(recentImage);

		                	    }
		                	});
		                	
		                	
		                    
		                	
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
		        }
				
			 if (envelope.glassInfoResponseG2C != null) {
		            final GlassInfoResponse response = envelope.glassInfoResponseG2C;

			    	MainActivity.context.runOnUiThread(new Runnable(){
			    	    public void run(){
			    	    	
				            String info = "";
				            info += "Device name: " + response.deviceName + "\n";
				            info += "Battery: " + response.batteryLevel + "%" + "\n";
				            info += "Software: " + response.softwareVersion + "\n";
				            info += "NeedSetup: " + response.needSetup + "\n";
				            info += "Storage: " + response.externalStorageAvailableBytes/1000/1000 + "/" + response.externalStorageTotalBytes/1000/1000
				                    + " MB available";
			    	    				    	    	
							TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView3);
					    	tv.setText(info);
					    	tv.invalidate();
			    	    }
			    	});
		            
		            
		        }
		        if (envelope.companionInfo != null) {
		            CompanionInfo companionInfo = envelope.companionInfo;
		            String log = companionInfo.responseLog;
		            System.out.println(log);
		        }
				
			
				
				
				
				
				
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

		
    	MainActivity.context.runOnUiThread(new Runnable(){
    	    public void run(){
				TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
		    	tv.setText("Writing Envlope");
		    	tv.invalidate();
    	    }
    	});
		
		c.write(envelope);
		
		c.close();
		
		Log.d("JOE","Wrote Message");
		
		
	}
	
	

	

}
