package the.joeglass;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class SendToGlass extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_send_to_glass);
		
		Intent intent = getIntent();
		
		String subject = intent.getStringExtra("android.intent.extra.SUBJECT");
		String text = intent.getStringExtra(Intent.EXTRA_TEXT);
		
		Log.d("JOE", "Sending message to phone: " + subject);
		
		try {
			JoeMessageUtil.sendText(subject, text);
			Toast.makeText(this, "Sent to glass", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, "Error sending to glass", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		
		
//		for(String s : intent.getExtras().keySet()){
//			
//			Log.d("JOE", "Extra Key: " + s + " " + intent.getExtras().get(s));
//		}
		
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_to_glass, menu);
		return true;
	}

}
