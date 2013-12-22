package the.joeglass;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;



public class NotificationService extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
	    // TODO Auto-generated method stub.
	
		Log.d("JOE","Received Notification: "  + event.toString());
		
		
	   }
	@Override
	public void onInterrupt() {
	    // TODO Auto-generated method stub.
	
	}
	
	@Override
	protected void onServiceConnected() {
		
		
		Log.d("JOE","onServiceConnected");
		
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	      info.feedbackType = 1;
	      info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
	      info.notificationTimeout = 100; 
	      setServiceInfo(info);
	     }
}