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
    private static String soundPath;
    private static int nbrOfTracks;


    public static void addMultipleTracks(Context context, String path, int nbrOfTracks){
        soundPath = path;
        for (int i = 0; i<nbrOfTracks;i++)
            addTrack(context);
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
        return currentHandler.toggleRecord();
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


}
