package com.example.anders.wellactually;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;

public class SoundHandler {
    private MediaPlayer mp;
    private MediaRecorder mr;
    private boolean isRecording;
    private String recordingPath;
    private PlaybackParams params;
    private static Handler handler = new Handler(); // works like mailbox i think
    private ProgressBar progressBar;

    public SoundHandler(String recordingPath, ProgressBar bar) {
        this.recordingPath = recordingPath;
        progressBar = bar;
        mp = new MediaPlayer();
        mr = new MediaRecorder();
        params = new PlaybackParams();
    }

    private int getProgress(){
        return (int) (((float) mp.getCurrentPosition() / (float) mp.getDuration()) * 100);
    }

    private Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(getProgress());
            if(mp.getDuration() < 5000)
                handler.postDelayed(this, 24);
            else
                handler.postDelayed(this, 1000);
        }
    };

    public void updateParams() {
        if (mp != null)
            if (mp.isPlaying())
                mp.setPlaybackParams(params);
    }

    public boolean tryPlay() {  // play button
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
        progressBar.setProgress(0);
    }

    private void startPlaying() {
        try {
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateParams();
        handler.post(updateProgressBar); // has to be called after mp.start
    }


    public boolean tryRecord() {
        if (!isRecording)
            startRecording();
        else
            stopRecording();
        return isRecording = !isRecording;
    }

    void startRecording() {
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

    void stopRecording() {
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
            initMediaPlayer();
        }
    }

    void initMediaPlayer() {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(recordingPath);
            mp.setLooping(true);
            mp.setPlaybackParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlaybackParams getParams() {
        return params;
    }
}