package com.example.anders.wellactually;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;

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
    private static AudioProgressBar audioProgressBar;
    private static TabLayout tabLayout;
    private static boolean useMetronome = false;

    public static int CURRENT_STATE;
    public static final int STATE_PLAYING = 100;
    public static final int STATE_IDLE = 101;
    public static final int STATE_NO_PLAYBACK_FILE = 102;
    public static final int STATE_RECORDING = 103;


    public static void init(Context context, String path, int defaultNbrOfTracks, PlaybackButtons buttons, AudioProgressBar progressBar, TabLayout trackTabs) {
        tabLayout = trackTabs;
        soundPath = path;
        audioProgressBar = progressBar;
        PLAYBACK_BUTTONS = buttons;
        currentBars = 1;
        CURRENT_STATE = STATE_NO_PLAYBACK_FILE;
        audioRecorder = new AudioRecorder();
        for (int i = 0; i < defaultNbrOfTracks; i++)
            addTrack(context);
    }

    public static void addTrack(Context context) {
        audioPlayers.add(new AudioPlayer(context, soundPath + "/mock_recording" + ++nbrOfTracks + ".3gp"));
    }

    public static void setCurrentTrack(int index) {
        currentPlayer = audioPlayers.get(index);
        if (currentPlayer.isPlaying())
            audioProgressBar.setEnable(true);
        else {
            audioProgressBar.setEnable(false);
            audioProgressBar.resetPosition();
        }
        audioProgressBar.setPlayer(currentPlayer);
        updateStateOnCurrentPlayerChange();
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }


    public static void togglePlay() {
        if (currentPlayer.togglePlay()) {
            audioProgressBar.setEnable(true);
            CURRENT_STATE = STATE_PLAYING;
        } else {
            audioProgressBar.setEnable(false);
            CURRENT_STATE = STATE_IDLE;
        }
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }

    public static void toggleRecord() {
        currentPlayer.release();  // Called twice but I don't care.
        if (audioRecorder.toggleRecord(currentBpmDuration * currentBars, currentPlayer.getReadPath())) {
            CURRENT_STATE = STATE_RECORDING;
            setEnableTrackTabs(false);
        } else {
            currentPlayer.init();
            togglePlay();
            setEnableTrackTabs(true);
        }
        PLAYBACK_BUTTONS.updateState(CURRENT_STATE);
    }

    private static void setEnableTrackTabs(boolean b) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        if (b) {
            tabStrip.setEnabled(true);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(true);
            }
        } else {
            tabStrip.setEnabled(false);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(false);
            }
        }
    }

    public static void setUseMetronome(boolean use) {
        useMetronome = use;
    }

    private static void updateStateOnCurrentPlayerChange() {
        if (currentPlayer.isInitialized()) {
            if (currentPlayer.isPlaying())
                CURRENT_STATE = STATE_PLAYING;
            else
                CURRENT_STATE = STATE_IDLE;
        } else
            CURRENT_STATE = STATE_NO_PLAYBACK_FILE;
    }


    public static void setCurrentPitch(float pitch) {
        currentPlayer.setPitch(pitch);
    }

    public static void setCurrentSpeed(float speed) {
        currentPlayer.setSpeed(speed);
    }

    public static void setBpm(String bpm) {
        if (bpm.equals("Free")) {
            currentBpmDuration = -1;
        } else
            currentBpmDuration = (int) (1 / (((float) Integer.valueOf(bpm)) / 60) * 4000);
    }

    public static void setCurrentBars(int currentBars) {
        SoundMixer.currentBars = currentBars;
    }

    public static void tryPlayMetronome() {
        if (useMetronome) {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            try {
                for (int i = 0; i < 3; i++) {
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, 200);
                    Thread.sleep((currentBpmDuration / 4));
                }
                toneG.startTone(ToneGenerator.TONE_DTMF_1, 200);
                Thread.sleep((currentBpmDuration / 4) - 50); //!!! fix when i can think again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
