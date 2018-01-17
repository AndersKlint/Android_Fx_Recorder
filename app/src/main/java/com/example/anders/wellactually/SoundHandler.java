package com.example.anders.wellactually;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.util.Log;

import java.io.IOException;

public class SoundHandler {
    private MediaPlayer mp;
    private MediaRecorder mr;
    private boolean isRecording;
    private String recordingPath;
    private PlaybackParams params;

    public SoundHandler(String recordingPath) {
        this.recordingPath = recordingPath;
        mp = new MediaPlayer();
        mr = new MediaRecorder();
        params = new PlaybackParams();
    }

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
        mp.stop();
        return false;
    }

    void startPlaying() {
        try {
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateParams();
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
            mp.stop();
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