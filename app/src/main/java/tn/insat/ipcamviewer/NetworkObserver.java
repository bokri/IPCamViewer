package tn.insat.ipcamviewer;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkObserver implements Runnable {
	private static final String TAG = "NetworkObserver";
	private List<NetworkListener> _listeners = new ArrayList<NetworkListener>();
	
	public NetworkObserver() {
	}

	public void run() {
		
		Log.v(TAG, "Starting Network Listener");
		
		ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket();
        } catch (IOException e) {
        	Log.e(TAG, "Could not create socket");
			e.printStackTrace();
            System.exit(-1);
        }
        
        try {
        	while (listening)
        		new NetworkObserverThread(serverSocket.accept(), this).start();

			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public synchronized void addNetworkListener( NetworkListener l ) {
        _listeners.add( l );
    }
    
    public synchronized void removeNetworkListener( NetworkListener l ) {
        _listeners.remove( l );
    }
    
    protected synchronized void _fireEvent(String event) {
		Log.v(TAG, "Event fired: " + event);
    	NetworkEvent netevent = new NetworkEvent(event);
        Iterator<NetworkListener> listeners = _listeners.iterator();
        while( listeners.hasNext() ) {
            ( (NetworkListener) listeners.next() ).eventReceived( netevent );
        }
    }
}
