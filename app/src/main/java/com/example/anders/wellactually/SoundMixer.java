package com.example.anders.wellactually;

import android.content.Context;
import android.location.Location;
import android.media.PlaybackParams;

import com.google.android.exoplayer2.PlaybackParameters;

import java.util.LinkedList;

/**
 * Created by Anders on 18/01/2018.
 */

public final class SoundMixer {
    private static LinkedList<SoundHandler> trackList = new LinkedList<SoundHandler>();
    private static SoundHandler currentHandler;
    private static SoundHandler mainTrack; // will determine beat behaviour (this length is one bar)
    private static String soundPath;
    private static int nbrOfTracks;
    private static int currentBpmDuration;


    public static void init(Context context, String path, int defaultNbrOfTracks){
        soundPath = path;
        for (int i = 0; i<defaultNbrOfTracks;i++)
            addTrack(context);
        mainTrack = trackList.get(0);
    }

    public static void addTrack(Context context) {
        trackList.add(new SoundHandler(context, soundPath + "mock_recording" + ++nbrOfTracks + ".3gp"));
    }

    public static void setCurrentTrack(int index){
        if (currentHandler != null)
        currentHandler.setShouldUpdateProgressbar(false);
        currentHandler = trackList.get(index);
        currentHandler.setShouldUpdateProgressbar(true);
    }


    public static boolean togglePlay(){
       return currentHandler.togglePlay();
    }

    public static boolean toggleRecord(){
        return currentHandler.toggleRecord(currentBpmDuration);
    }

    public static void updateParams(PlaybackParameters params){
        currentHandler.updateParams(params);
    }

    public static PlaybackParameters getParams() {
        return currentHandler.getParams();
    }

    public static boolean currentTrackPlaying() {
        return currentHandler.isPlaying();
    }

    public static boolean currentTrackInitialized() {
        return currentHandler.isInitialized();
    }


    public static void setBpm(String bpm) {
        if (bpm.equals("Free")){
        currentBpmDuration = -1;
        }
        else
            currentBpmDuration = (int) (1/(((float) Integer.valueOf(bpm))/60)*4000);
    }

}
