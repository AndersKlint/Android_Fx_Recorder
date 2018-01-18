package com.example.anders.wellactually;

import android.location.Location;
import android.media.PlaybackParams;

import java.util.LinkedList;

/**
 * Created by Anders on 18/01/2018.
 */

public final class SoundMixer {
    private static LinkedList<SoundHandler> trackList = new LinkedList<SoundHandler>();
    private static SoundHandler currentHandler;
    private static String soundPath;
    private static int nbrOfTracks;


    public static void addMultipleTracks(String path, int nbrOfTracks){
        soundPath = path;
        for (int i = 0; i<nbrOfTracks;i++)
            addTrack();
    }

    public static void addTrack() {
        trackList.add(new SoundHandler(soundPath + ++nbrOfTracks));
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

    public static void updateParams(PlaybackParams params){
        currentHandler.updateParams(params);
    }

    public static PlaybackParams getParams() {
        return currentHandler.getParams();
    }

    public static boolean currentTrackPlaying() {
        return currentHandler.isPlaying();
    }


}
