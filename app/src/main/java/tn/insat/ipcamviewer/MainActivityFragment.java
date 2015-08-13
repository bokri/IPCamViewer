package tn.insat.ipcamviewer;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import tn.insat.ipcamviewer.mjpegplayer.MjpegInputStream;
import tn.insat.ipcamviewer.mjpegplayer.MjpegView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements NetworkListener {

    private static final String TAG = "MainActivity";
    private static final String URL = "http://194.218.96.93/mjpg/video.mjpg";

    protected static final int START_VIDEO = 0;

    private NetworkObserver listener;
    //private MjpegView mv;

    private Button button_video;

    private MjpegView videoView = null;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case START_VIDEO:
    	    	 /*Refresh UI*/
                    //showVideo();

                            showMjpegVideo();
                    break;
            }
        }
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        button_video = (Button) rootView.findViewById(R.id.button_video);
        button_video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (videoView == null) {
                    showMjpegVideo();
                }
                else {
                    if (videoView.isPlaying()) {
                        Log.v(TAG, "video is already playing, stopping now...");
                        videoView.stopPlayback();
                    } else {
                        Log.v(TAG, "video is not playing, starting it now...");
                        videoView.startPlayback();
                    }
                }
            }
        });

        startNetworkListener();

        Thread NeObTh = new Thread(listener);
        NeObTh.start();

        return rootView;
    }

    private void startNetworkListener() {
        Log.v(TAG, "starting Network Observer");
        listener = new NetworkObserver();
        listener.addNetworkListener(this);
    }
    private void showMjpegVideo() {

        videoView = (MjpegView) getView().findViewById(R.id.videoView1);
        final String url = URL;
        Log.v(TAG, "starting video playback: " + url);

        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);

        Uri video = Uri.parse(url);
        videoView.setMediaController(mediaController);
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoView.setSource(MjpegInputStream.read(url, getActivity().getCacheDir()));
            }
        }).start();
        videoView.start();
    }


    public void eventReceived(NetworkEvent event) {
        String type = event.eventtype;
        Log.v(TAG, "event received: " + type);

        if (type.equals("video")) {

            mHandler.sendEmptyMessage(START_VIDEO);
        }

    }
}
