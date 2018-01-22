package com.example.anders.wellactually;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;

import java.util.LinkedList;

/**
 * Created by Anders on 18/01/2018.
 */

public final class SoundMixer {
    private LinkedList<AudioPlayer> audioPlayers = new LinkedList<AudioPlayer>();
    private AudioPlayer currentPlayer;
    private AudioRecorder audioRecorder;
    private String recordingPath;
    private int currentTrack;
    private int currentBpmDuration;
    private int currentBars;
    private AudioProgressBar audioProgressBar;
    private boolean useMetronome = false;
    private Thread recordingThread;
    private OnStateChangedListener stateChangedListener;
    private Handler handler;
    private Uri currentUri;

    public int CURRENT_STATE;
    public static final int STATE_PLAYING = 100;
    public static final int STATE_READY_TO_PLAY = 101;
    public static final int STATE_NO_PLAYBACK_FILE = 102;
    public static final int STATE_RECORDING = 103;
    public static final int STATE_METRONOME_PLAYING = 104;


    public void init(Context context, String path, int defaultNbrOfTracks, AudioProgressBar progressBar) {
        this.handler = new Handler();
        recordingPath = path;
        audioProgressBar = progressBar;
        currentBars = 1;
        updateState(STATE_NO_PLAYBACK_FILE);
        audioRecorder = new AudioRecorder(this);
        for (int i = 0; i < defaultNbrOfTracks; i++)
            addTrack(context);
    }

    public void setCustomStateChangedListener(OnStateChangedListener listener) {
        stateChangedListener = listener;
    }

    public void addTrack(Context context) {
        audioPlayers.add(new AudioPlayer(context));
    }

    public void setCurrentTrack(int index) {
        currentTrack = index +1;
        currentPlayer = audioPlayers.get(index);
        if (currentPlayer.isInitialized())
            currentUri = currentPlayer.getUri();
        if (currentPlayer.isPlaying())
            audioProgressBar.setEnable(true);
        else {
            audioProgressBar.setEnable(false);
            audioProgressBar.resetPosition();
        }
        audioProgressBar.setPlayer(currentPlayer);
        updateStateOnCurrentPlayerChange();
    }


    public void togglePlay() {
        if (!currentPlayer.isInitialized())
            currentPlayer.init(currentUri);
        if (currentPlayer.togglePlay()) {
            audioProgressBar.setEnable(true);
            updateState(STATE_PLAYING);
        } else {
            audioProgressBar.setEnable(false);
            audioProgressBar.resetPosition();
            updateState(STATE_READY_TO_PLAY);
        }
    }

    public void toggleRecord() {
        if (!audioRecorder.isRecording()) {
            currentUri = Uri.parse(recordingPath + "/mock_recording_" + currentTrack + ".3gp");  //dont wanna overwrite loaded file
            if (currentPlayer.isPlaying())
                togglePlay();
            currentPlayer.release();
            recordingThread = new Thread(RecordingThread);
            recordingThread.start();
        } else {
            recordingThread.run();
        }
    }

    public void setUseMetronome(boolean use) {
        useMetronome = use;
    }

    private void updateStateOnCurrentPlayerChange() {
        if (currentPlayer.isInitialized()) {
            if (currentPlayer.isPlaying())
                updateState(STATE_PLAYING);
            else
                updateState(STATE_READY_TO_PLAY);
        } else
            updateState(STATE_NO_PLAYBACK_FILE);
    }


    public void setCurrentPitch(float pitch) {
        currentPlayer.setPitch(pitch);
    }

    public void setCurrentSpeed(float speed) {
        currentPlayer.setSpeed(speed);
    }

    public void setCurrentPlaybackParams(float pitch, float speed) {
        currentPlayer.setPlaybackParams(pitch, speed);
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
            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateState(STATE_METRONOME_PLAYING);
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
            if (audioRecorder.toggleRecord(currentBpmDuration * currentBars, currentUri.toString())) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateState(STATE_RECORDING);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        togglePlay();
                    }
                });
            }
        }
    };

    public void setFile(Uri file) {
        currentUri = file;
        currentPlayer.release();
        updateState(STATE_READY_TO_PLAY);
    }

    private void updateState(int newState) {
        CURRENT_STATE = newState;
        stateChangedListener.stateChanged(newState);
    }
}
