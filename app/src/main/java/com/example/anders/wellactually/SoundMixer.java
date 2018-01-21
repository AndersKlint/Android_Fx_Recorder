package com.example.anders.wellactually;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.LinkedList;

/**
 * Created by Anders on 18/01/2018.
 */

public final class SoundMixer {
    private static LinkedList<AudioPlayer> audioPlayers = new LinkedList<AudioPlayer>();
    private static AudioPlayer currentPlayer;
    private static AudioRecorder audioRecorder;
    private static String soundPath;
    private static int nbrOfTracks;
    private static int currentBpmDuration;
    private static int currentBars;
    private static PlaybackButtons PLAYBACK_BUTTONS;
    private static int CURRENT_STATE;

    public static final int STATE_PLAYING = 100;
    public static final int STATE_IDLE = 101;
    public static final int STATE_NO_PLAYBACK_FILE = 102;
    public static final int STATE_RECORDING = 103;


    public static void init(Context context, String path, int defaultNbrOfTracks, PlaybackButtons buttons){
        soundPath = path;
        PLAYBACK_BUTTONS = buttons;
        audioRecorder = new AudioRecorder();
        for (int i = 0; i<defaultNbrOfTracks;i++)
            addTrack(context);
    }

    public static void addTrack(Context context) {
        audioPlayers.add(new AudioPlayer(context, soundPath + "/mock_recording" + ++nbrOfTracks + ".3gp"));
    }

    public static void setCurrentTrack(int index){
        currentPlayer = audioPlayers.get(index);
        if (currentPlayer.isPlaying())
            AudioProgressBar.setEnable(true);
        else {
            AudioProgressBar.setEnable(false);
            AudioProgressBar.resetPosition();
        }
        AudioProgressBar.setPlayer(currentPlayer);
        updateStateOnPlayerChange();
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }


    public static void togglePlay(){
        if(currentPlayer.togglePlay()) {
            AudioProgressBar.setEnable(true);
            CURRENT_STATE = STATE_PLAYING;
        }
        else {
            AudioProgressBar.setEnable(false);
            CURRENT_STATE = STATE_IDLE;
        }
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }

    public static void toggleRecord(){
        if( audioRecorder.toggleRecord(currentBpmDuration + currentBars, currentPlayer.getReadPath())) {
            currentPlayer.release();
            CURRENT_STATE = STATE_RECORDING;
        }
        else {
            currentPlayer.init();
            togglePlay();
        }
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }

    private static void updateStateOnPlayerChange(){
        if (currentPlayer.isInitialized()) {
            if (currentPlayer.isPlaying())
                CURRENT_STATE = STATE_PLAYING;
            else
                CURRENT_STATE = STATE_IDLE;
        }
        else
            CURRENT_STATE = STATE_NO_PLAYBACK_FILE;
    }


    public static void updatePitch(float pitch){
        currentPlayer.setPitch(pitch);
    }
    public static void updateSpeed(float speed){
        currentPlayer.setSpeed(speed);
    }

    public static void setBpm(String bpm) {
        if (bpm.equals("Free")){
        currentBpmDuration = -1;
        }
        else
            currentBpmDuration = (int) (1/(((float) Integer.valueOf(bpm))/60)*4000);
    }

    public static void setCurrentBars(int currentBars) {
      SoundMixer.currentBars = currentBars;
    }
}
