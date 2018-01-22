package com.example.anders.wellactually;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;

import java.util.LinkedList;

/**
 * Created by Anders on 18/01/2018.
 */

public final class SoundMixer implements OnStateChangedListener {
    private LinkedList<AudioPlayer> audioPlayers = new LinkedList<AudioPlayer>();
    private AudioPlayer currentPlayer;
    private AudioRecorder audioRecorder;
    private String soundPath;
    private int nbrOfTracks;
    private int currentBpmDuration;
    private int currentBars;
    private AudioProgressBar audioProgressBar;
    private TabLayout tabLayout;
    private boolean useMetronome = false;
    private Thread recordingThread;
    private OnStateChangedListener buttonListener;
    private Handler handler;

    public int CURRENT_STATE;
    public static final int STATE_PLAYING = 100;
    public static final int STATE_IDLE = 101;
    public static final int STATE_NO_PLAYBACK_FILE = 102;
    public static final int STATE_RECORDING = 103;
    public static final int STATE_METRONOME_PLAYING = 104;


    public void init(Context context, String path, int defaultNbrOfTracks, AudioProgressBar progressBar, TabLayout trackTabs) {
        this.handler = new Handler();
        tabLayout = trackTabs;
        soundPath = path;
        audioProgressBar = progressBar;
        currentBars = 1;
        CURRENT_STATE = STATE_NO_PLAYBACK_FILE;
        audioRecorder = new AudioRecorder(this);
        audioRecorder.setCustomStateChangedListener(this);
        for (int i = 0; i < defaultNbrOfTracks; i++)
            addTrack(context);
    }

    public void setCustomStateChangedListener(OnStateChangedListener listener) {
        buttonListener = listener;
    }

    public void addTrack(Context context) {
        audioPlayers.add(new AudioPlayer(context, soundPath + "/mock_recording" + ++nbrOfTracks + ".3gp"));
    }

    public void setCurrentTrack(int index) {
        currentPlayer = audioPlayers.get(index);
        if (currentPlayer.isPlaying())
            audioProgressBar.setEnable(true);
        else {
            audioProgressBar.setEnable(false);
            audioProgressBar.resetPosition();
        }
        audioProgressBar.setPlayer(currentPlayer);
        updateStateOnCurrentPlayerChange();
        buttonListener.stateChanged(CURRENT_STATE);
    }


    public void togglePlay() {
        if (!currentPlayer.isInitialized())
            currentPlayer.init();
        if (currentPlayer.togglePlay()) {
            audioProgressBar.setEnable(true);
            CURRENT_STATE = STATE_PLAYING;
        } else {
            audioProgressBar.setEnable(false);
            CURRENT_STATE = STATE_IDLE;
        }
        buttonListener.stateChanged(CURRENT_STATE);
    }

    public void toggleRecord() {
        if(!audioRecorder.isRecording()) {
            currentPlayer.release();
            recordingThread = new Thread(RecordingThread);
            recordingThread.start();
            buttonListener.stateChanged(CURRENT_STATE);
        }
        else {
            recordingThread.run();
            buttonListener.stateChanged(CURRENT_STATE);
        }
    }

    private void setEnableTrackTabs(boolean b) {
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

    public void setUseMetronome(boolean use) {
        useMetronome = use;
    }

    private void updateStateOnCurrentPlayerChange() {
        if (currentPlayer.isInitialized()) {
            if (currentPlayer.isPlaying())
                CURRENT_STATE = STATE_PLAYING;
            else
                CURRENT_STATE = STATE_IDLE;
        } else
            CURRENT_STATE = STATE_NO_PLAYBACK_FILE;
    }


    public void setCurrentPitch(float pitch) {
        currentPlayer.setPitch(pitch);
    }

    public void setCurrentSpeed(float speed) {
        currentPlayer.setSpeed(speed);
    }

    public void setBpm(String bpm) {
        if (bpm.equals("Free")) {
            currentBpmDuration = -1;
        } else
            currentBpmDuration = (int) (1 / (((float) Integer.valueOf(bpm)) / 60) * 4000);
    }

    public void setCurrentBars(int currentBars) {
        this.currentBars = currentBars;
    }

    public void tryPlayMetronome() {
        if (useMetronome && currentBpmDuration > 0) {
            CURRENT_STATE = STATE_METRONOME_PLAYING;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    buttonListener.stateChanged(STATE_METRONOME_PLAYING);
                }
            });
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            try {
                for (int i = 0; i < 3; i++) {
                    toneG.startTone(ToneGenerator.TONE_DTMF_0, 200);
                    Thread.sleep((currentBpmDuration / 4));
                }
                toneG.startTone(ToneGenerator.TONE_DTMF_1, 200);
                if (currentBpmDuration > 200)
                Thread.sleep((currentBpmDuration / 4) - 50); //!!! fix when i can think again
                else
                    Thread.sleep((currentBpmDuration / 4));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable RecordingThread = new Runnable() {
        @Override
        public void run() {
            if (audioRecorder.toggleRecord(currentBpmDuration * currentBars, currentPlayer.getReadPath())) {
                CURRENT_STATE = STATE_RECORDING;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonListener.stateChanged(CURRENT_STATE);
                    }
                });
            //    setEnableTrackTabs(false);
            } else {
              //  setEnableTrackTabs(true);
                CURRENT_STATE = STATE_IDLE;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonListener.stateChanged(CURRENT_STATE);
                    }
                });
            }
        }
    };

    @Override
    public void stateChanged(int state) {
CURRENT_STATE = state;
buttonListener.stateChanged(state);
    }
}
