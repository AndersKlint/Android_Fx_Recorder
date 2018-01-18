package com.example.anders.wellactually;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class SoundHandler {
    private MediaPlayer mp;
    private MediaRecorder mr;
    private boolean isRecording;
    private String recordingPath;
    private static Handler handler = new Handler(); // works like mailbox i think
    private PlaybackParams params;
    private boolean shouldUpdateProgressbar;

    public SoundHandler(String recordingPath) {
        this.recordingPath = recordingPath;
        shouldUpdateProgressbar = false;
        mp = new MediaPlayer();
        mr = new MediaRecorder();
        params = new PlaybackParams();
    }

    private int getProgress(){
        return (int) (((float) mp.getCurrentPosition() / (float) mp.getDuration()) * 100);
    }
    public void setShouldUpdateProgressbar(boolean bool) {
        shouldUpdateProgressbar = bool;
    }

    private Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
            if(shouldUpdateProgressbar)
            MainActivity.progressBar.setProgress(getProgress());
            if(mp.getDuration() < 5000)
                handler.postDelayed(this, 40);
            else
                handler.postDelayed(this, 1000);
        }
    };

    public void updateParams(PlaybackParams params) {
        this.params = params;
        if (mp != null)
            if (mp.isPlaying())
                mp.setPlaybackParams(params);
    }

    public boolean togglePlay() {
        if (!mp.isPlaying()) {
            startPlaying();
            return true;
        }
        stopPlaying();
        return false;
    }

    private void stopPlaying(){
        mp.stop();
        handler.removeCallbacks(updateProgressBar);
        MainActivity.progressBar.setProgress(0);
    }

    private void startPlaying() {
        try {
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateParams(params);
        handler.post(updateProgressBar); // has to be called after mp.start
    }


    public boolean toggleRecord() {
        if (!isRecording)
            startRecording();
        else
            stopRecording();
        return isRecording = !isRecording;
    }

   private void startRecording() {
        if (mp.isPlaying())
            stopPlaying();
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setOutputFile(recordingPath);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mr.prepare();
        } catch (IOException e) {
            Log.e("Audio recording:", "prepare() failed");
        }
        mr.start();
    }

   private void stopRecording() {
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
            initMediaPlayer();
        }
    }

  private  void initMediaPlayer() {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(recordingPath);
            mp.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlaybackParams getParams() {
        return params;
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }
}