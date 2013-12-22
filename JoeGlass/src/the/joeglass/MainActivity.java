package the.joeglass;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import com.google.glass.companion.CompanionMessagingUtil;
import com.google.glass.companion.Proto.Envelope;
import com.google.glass.companion.Proto.GlassInfoRequest;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static Activity context = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		//JoeMessageUtil.sendText("Title","body");

		
		if (!myGlassRunning()){ 

			try {
				JoeMessageUtil.sendInfoRequest();
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		
		
		Button buttonOne = (Button) findViewById(R.id.button1);
		buttonOne.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		           
		    	TextView tv = (TextView) findViewById(R.id.textView2);
		    	tv.setText("Connecting..");
		    	tv.invalidate();
		    	
		    	new Thread(new Runnable() {
					
					@Override
					public void run() {

				    	EditText text = (EditText)findViewById(R.id.editText1);
				    	
				    	//Toast.makeText(MainActivity.context, "Sending to glass", Toast.LENGTH_LONG).show();
				    	
				    	try{
				    		JoeMessageUtil.sendText("Message Test",text.getText().toString());
				    		
					    	MainActivity.context.runOnUiThread(new Runnable(){
		                	    public void run(){
		    						TextView tv = (TextView) findViewById(R.id.textView2);
		    				    	tv.setText("");
		    				    	tv.invalidate();
		                	    }
		                	});
				    		
				    		
				    	}catch(final Exception e){
				    		
					    	MainActivity.context.runOnUiThread(new Runnable(){
					    	    public void run(){
									TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
							    	tv.setText("Cannot connect: " + e.getMessage());
							    	tv.invalidate();
					    	    }
					    	});
				    	}
				    	
				    	

				    	
					}
		    	}).start();
		    }
		});
		
		
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		        
				TextView tv = (TextView) findViewById(R.id.textView2);
		    	tv.setText("Connecting..");
		    	tv.invalidate();
		    	//Toast.makeText(MainActivity.context, "Taking Screenshot", Toast.LENGTH_LONG).show();
		    	
		    	new Thread(new Runnable() {
					
					@Override
					public void run() {
						try{
							
							
							JoeMessageUtil.takePicture();
					    	MainActivity.context.runOnUiThread(new Runnable(){
		                	    public void run(){
		    						TextView tv = (TextView) findViewById(R.id.textView2);
		    				    	tv.setText("");
		    				    	tv.invalidate();
		                	    }
		                	});			    		
				    	}catch(final Exception e){
				    		
					    	MainActivity.context.runOnUiThread(new Runnable(){
					    	    public void run(){
									TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
							    	tv.setText("Cannot connect: " + e.getMessage());
							    	tv.invalidate();
					    	    }
					    	});
				    	}
				    	
					}
				}).start();
		    	

		    	
		    }
		});
		
		
		Button button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
                
		    	if(JoeMessageUtil.recentImage != null){
		    		
			    	MainActivity.context.runOnUiThread(new Runnable(){
			    	    public void run(){
							TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
					    	tv.setText("Saving...");
					    	tv.invalidate();
			    	    }
			    	});
		    		
		    		
		    		try{
		                final String loc = saveImage(JoeMessageUtil.recentImage);
				    	
				    	MainActivity.context.runOnUiThread(new Runnable(){
				    	    public void run(){
								TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
						    	tv.setText("Saved to " + loc);
						    	tv.invalidate();
				    	    }
				    	});
		    		}catch (final Exception e){
		    			
				    	MainActivity.context.runOnUiThread(new Runnable(){
				    	    public void run(){
								TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
						    	tv.setText("Error saving: " + e.getMessage());
						    	tv.invalidate();
				    	    }
				    	});
		    		}
		    	}
		    	MainActivity.context.runOnUiThread(new Runnable(){
		    	    public void run(){
						TextView tv = (TextView) MainActivity.context.findViewById(R.id.textView2);
				    	tv.setText("Take a screenshot first");
				    	tv.invalidate();
		    	    }
		    	});
		    	
		    }
		});
		
		
		
		
	}

	
	
	@Override
	protected void onResume() {
		

		myGlassRunning();
		
		
		
		super.onResume();
	}
	
	boolean myglassrunning = false;
	
	public boolean myGlassRunning(){
		
		
		TextView tv = (TextView) findViewById(R.id.textView2);
    	tv.setText("");
	    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	   // am.killBackgroundProcesses("com.google.glass.companion");
	    List<RunningServiceInfo> pids = am.getRunningServices(9999);
	    for (int i = 0; i < pids.size(); i++) {
	        ActivityManager.RunningServiceInfo info = pids.get(i);
	        //Log.d("JOE",info.service.getClassName());
	        if (info.service.getClassName().equals("com.google.glass.companion.service.CompanionService")){
	        	Log.d("JOE","MyGlass is running");
	        	myglassrunning = true;
	        	tv = (TextView) findViewById(R.id.textView2);
	        	tv.setText("MyGlass is running. It blocks the Bluetooth channel needed to talk to glass.  In order to use this app please Force Stop or uninstall it.");
	        	return true;
	        }
	        
	    }
		
		return false;
		
	}
	
	
	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

	

	
	
	private static String saveImage(Bitmap finalBitmap) throws Exception{

	    String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + "/joeglass");    
	    myDir.mkdirs();
	    Random generator = new Random();
	    int n = 10000;
	    n = generator.nextInt(n);
	    String fname = "Image-"+ n +".jpg";
	    File file = new File (myDir, fname);
	    if (file.exists ()) file.delete (); 
       FileOutputStream out = new FileOutputStream(file);
       finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
       out.flush();
       out.close();

	    return file.getAbsolutePath();
	}
	
	
	
}




