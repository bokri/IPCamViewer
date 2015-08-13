package tn.insat.ipcamviewer;

import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkObserverThread extends Thread {
	
	private static final String TAG = "NetworkObserverThread";
	private static final String EVENTSTR = "event=";
    private Socket socket = null;
	private NetworkObserver parent;

    public NetworkObserverThread(Socket socket, NetworkObserver parent) {
    	super("NetworkObserverThread");
    	this.socket = socket;
    	this.parent = parent;
    }

    public void run() {
    	Looper.prepare();

    	try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
					    new InputStreamReader(
					    socket.getInputStream()));
	
		    String inputLine;

		    String input = "";
		    
		    while ((inputLine = in.readLine()) != null) {

		    	if (inputLine.startsWith(EVENTSTR)) {
		    		String event = inputLine.substring(EVENTSTR.length());
		    		this.fireEvent(event);
		    		out.println("fire event: " + event);
		    		break;
		    	}
		    	else {
		    		out.println("Message must start with 'event='");
		    	}
		    	
		    	if (inputLine.equals("exit"))
				    break;
		    	
		    	input=input + inputLine;
		    }

		    out.close();
		    in.close();
		    socket.close();
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Looper.loop();
    }
    
	private void fireEvent(String eventtype) {
		this.parent._fireEvent(eventtype); 
	}
}


