package com.google.glass.companion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;

import com.google.glass.companion.CompanionMessagingUtil;
import com.google.glass.companion.GlassProtocol;
import com.google.glass.companion.Proto.Envelope;
import com.google.glass.companion.Proto.GlassInfoRequest;

/**
 * Handling connection with Google Glass companion service
 * 
 * @author poly
 * 
 */
public class GlassConnection {

    public static final boolean DEBUG = false;
	private String mConnectionURL;
   // private StreamConnection mStreamConnection;
	private BluetoothSocket mSocket;
    private OutputStream mOutStream;
    private InputStream mInStream;
    private List<GlassConnectionListener> mListeners = new ArrayList<GlassConnection.GlassConnectionListener>();
    private GlassReaderThread mGlassReaderThread;
    private final ExecutorService mWriteThread = Executors.newSingleThreadExecutor();
    private final Object STREAM_WRITE_LOCK = new Object();

    public GlassConnection() {

    }

    public Set<BluetoothDevice> getBondedDevices() {

        return BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    }

    public void connect(BluetoothDevice d) throws IOException {
    	
    	mSocket = d.createRfcommSocketToServiceRecord(CompanionConstants.SECURE_UUID);
    	
    	mSocket.connect();
    	
        mOutStream = mSocket.getOutputStream();
        mInStream = mSocket.getInputStream();
        mGlassReaderThread = new GlassReaderThread();
        mGlassReaderThread.start();
        
        
        handshake();
        
        //tell the people!
        synchronized (mListeners) {
            for (GlassConnectionListener listener : mListeners) {
                listener.onConnectionOpened();
            }
        }
        
        
        
        
    }

    public void close() {
        if (mGlassReaderThread != null) {
            mGlassReaderThread.interrupt();
            try {
                mGlassReaderThread.join(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        if (mSocket != null) {
            try {
            	mSocket.close();
            	mSocket = null;
                mOutStream = null;
                mInStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerListener(GlassConnectionListener glassConnectionListener) {
        if (glassConnectionListener == null) {
            return;
        }
        synchronized (mListeners) {
            final int size = mListeners.size();
            for (int i = 0; i < size; i++) {
                GlassConnectionListener listener = mListeners.get(i);
                if (listener == glassConnectionListener) {
                    return;
                }
            }
            this.mListeners.add(glassConnectionListener);
        }
    }

    public void unregisterListener(GlassConnectionListener glassConnectionListener) {
        if (glassConnectionListener == null) {
            return;
        }
        synchronized (mListeners) {
            final int size = mListeners.size();
            for (int i = 0; i < size; i++) {
                GlassConnectionListener listener = mListeners.get(i);
                if (listener == glassConnectionListener) {
                    mListeners.remove(i);
                    break;
                }
            }
        }
    }

//    private class Client implements DiscoveryListener {
//
//        @Override
//        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
//            // TODO Auto-generated method stub
//
//            // TODO filter glass?
//            if (App.DEBUG) {
//                System.out.println("deviceDiscovered");
//            }
//            synchronized (mListeners) {
//                for (GlassConnectionListener listener : mListeners) {
//                    listener.onDeviceDiscovered(new Device(btDevice));
//                }
//            }
//        }
//
//        @Override
//        public void inquiryCompleted(int discType) {
//            if (App.DEBUG) {
//                System.out.println("inquiryCompleted");
//            }
//            synchronized (mListeners) {
//                for (GlassConnectionListener listener : mListeners) {
//                    listener.onDeviceScanCompleted();
//                }
//            }
//        }
//
//        @Override
//        public void serviceSearchCompleted(int transID, int respCode) {
//            if (App.DEBUG) {
//                System.out.println("serviceSearchCompleted:" + respCode);
//            }
//            if (respCode == DiscoveryListener.SERVICE_SEARCH_NO_RECORDS) {
//                synchronized (mListeners) {
//                    for (GlassConnectionListener listener : mListeners) {
//                        listener.onServiceNotFound();
//                    }
//                }
//            } else if (respCode == DiscoveryListener.SERVICE_SEARCH_ERROR) {
//                synchronized (mListeners) {
//                    for (GlassConnectionListener listener : mListeners) {
//                        listener.onServiceSearchError();
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
//            mConnectionURL = null;
//            if (servRecord == null || servRecord.length <= 0) {
//                System.err.println("invalid service discovered");
//                return;
//            }
//
//            ServiceRecord service = servRecord[0];
//            mConnectionURL = service.getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
//            if (App.DEBUG) {
//                System.out.println(mConnectionURL);
//            }
//
//            try {
//                mStreamConnection = (StreamConnection) Connector.open(mConnectionURL);
//                mOutStream = mStreamConnection.openOutputStream();
//                mInStream = mStreamConnection.openInputStream();
//                mGlassReaderThread = new GlassReaderThread();
//                mGlassReaderThread.start();
//
//                // handshaking
//                Envelope envelope = CompanionMessagingUtil.newEnvelope();
//                envelope.timezoneC2G = TimeZone.getDefault().getID();
//                write(envelope);
//
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                // handshaking
//                Envelope envelope2 = CompanionMessagingUtil.newEnvelope();
//                GlassInfoRequest glassInfoRequest = new GlassInfoRequest();
//                glassInfoRequest.requestBatteryLevel = true;
//                glassInfoRequest.requestStorageInfo = true;
//                glassInfoRequest.requestDeviceName = true;
//                envelope2.glassInfoRequestC2G = glassInfoRequest;
//                write(envelope2);
//
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                synchronized (mListeners) {
//                    for (GlassConnectionListener listener : mListeners) {
//                        listener.onConnectionOpened();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    
    public void handshake(){
    	
    	
        // handshaking
        Envelope envelope = CompanionMessagingUtil.newEnvelope();
        envelope.timezoneC2G = TimeZone.getDefault().getID();
        write(envelope);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // handshaking
        Envelope envelope2 = CompanionMessagingUtil.newEnvelope();
        GlassInfoRequest glassInfoRequest = new GlassInfoRequest();
        glassInfoRequest.requestBatteryLevel = true;
        glassInfoRequest.requestStorageInfo = true;
        glassInfoRequest.requestDeviceName = true;
        envelope2.glassInfoRequestC2G = glassInfoRequest;
        write(envelope2);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    	
    	
    	
    }
    
    
    public void write(Envelope envelope) {
        synchronized (STREAM_WRITE_LOCK) {
            try {
                if (DEBUG) {
                    System.out.println("write:" + envelope);
                }
                if (mOutStream != null) {
                    GlassProtocol.writeMessage(envelope, mOutStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeAsync(final Envelope envelope) {
        mWriteThread.execute(new Runnable() {
            @Override
            public void run() {
                write(envelope);
            }
        });
    }

    public void writeAsync(final List<Envelope> envelopes) {
        mWriteThread.execute(new Runnable() {
            @Override
            public void run() {
                for (Envelope envelope : envelopes) {
                    write(envelope);
                }
            }
        });
    }

    public interface GlassConnectionListener {
        public abstract void onDeviceDiscovered(Device device);

        public abstract void onDeviceScanCompleted();

        public abstract void onServiceSearchError();

        public abstract void onReceivedEnvelope(Envelope envelope);

        public abstract void onServiceNotFound();

        public abstract void onConnectionOpened();
    }

    private class GlassReaderThread extends Thread {
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Envelope envelope = (Envelope) GlassProtocol.readMessage(new Envelope(), mInStream);
                        if (DEBUG) {
                            if (envelope.screenshot == null) {
                                System.out.println("read:" + envelope);
                            }
                        }
                        if (envelope != null) {
                            synchronized (mListeners) {
                                for (GlassConnectionListener listener : mListeners) {
                                    listener.onReceivedEnvelope(envelope);
                                }
                            }
                        }
                    } catch (InterruptedIOException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (DEBUG) {
                    System.out.println("Reader thread finished.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    };

}