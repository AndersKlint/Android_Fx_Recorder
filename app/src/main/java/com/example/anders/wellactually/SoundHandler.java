/*
package com.example.anders.wellactually;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class SoundHandler {
    private ExoPlayer exoPlayer;
    private MediaRecorder mr;
    private boolean isRecording;
    private String recordingPath;
    private static Handler handler = new Handler(); // works like mailbox i think
    private PlaybackParameters params;
    private boolean shouldUpdateProgressbar;
    private Context context;
    private MediaSource mediaSource;
    private boolean isInitialized;

    public SoundHandler(Context context, String recordingPath) {
        this.context = context;
        this.recordingPath = recordingPath;
        shouldUpdateProgressbar = false;
        exoPlayer =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        mr = new MediaRecorder();
        params = new PlaybackParameters(1, 1);
    }

    private int getProgress() {
        return (int) (((float) exoPlayer.getCurrentPosition() / (float) exoPlayer.getDuration()) * 100);
    }

    public void setShouldUpdateProgressbar(boolean bool) {
        shouldUpdateProgressbar = bool;
    }

    private Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
            if (shouldUpdateProgressbar)
                MainActivity.progressBar.setProgress(getProgress());
            if (exoPlayer.getDuration() < 5000)
                handler.postDelayed(this, 40);
            else
                handler.postDelayed(this, 1000);
        }
    };

    public void updateParams(PlaybackParameters params) {
        this.params = params;
        if (exoPlayer != null)
            if (exoPlayer.getPlayWhenReady())
                exoPlayer.setPlaybackParameters(params);
    }

    public boolean togglePlay() {
        if (!exoPlayer.getPlayWhenReady()) {
            startPlaying();
            return true;
        }
        stopPlaying();
        return false;
    }

    private void stopPlaying() {
        MainActivity.playButton.setText("Play");
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.seekTo(0);    // stop workaround without having to init again
        handler.removeCallbacks(updateProgressBar);
        MainActivity.progressBar.setProgress(0);
    }

    private void startPlaying() {
        MainActivity.playButton.setText("Stop");
        exoPlayer.setPlayWhenReady(true);
        updateParams(params);
        handler.post(updateProgressBar); // has to be called after exoPlayer.start
    }


    public boolean toggleRecord(int duration) {
        if (!isRecording)
            startRecording(duration);
        else
            stopRecording();
        return isRecording = !isRecording;
    }

    private void startRecording(int duration) {
        stopPlaying();
        MainActivity.recordButton.setText("Stop");
        MainActivity.playButton.setEnabled(false);
        exoPlayer.release();    // important to free up memory, should also be called on program exit i think
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setOutputFile(recordingPath);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            if (duration > 0) {
                mr.setMaxDuration(duration);
                mr.setOnInfoListener(mediaRecorderListener);
                MainActivity.recordButton.setEnabled(false);
            }
                mr.prepare();
                mr.start();
        } catch (IOException e) {
            Log.e("Audio recording:", "prepare() failed");
        }
    }

    private MediaRecorder.OnInfoListener mediaRecorderListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                isRecording = !isRecording; // code smells
                stopRecording();
                MainActivity.recordButton.setEnabled(true);
            }
        }
    };

    private void stopRecording() {
        if (mr != null) {
            MainActivity.recordButton.setText("Record");
            MainActivity.playButton.setText("Stop");
            MainActivity.playButton.setEnabled(true);
            mr.stop();
            mr.release();
            mr = null;
            initMediaPlayer();
            startPlaying();
        }
    }

    private void initMediaPlayer() {
        exoPlayer =
                ExoPlayerFactory.newSimpleInstance(
                        context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Well Actually"), null);
        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(recordingPath));
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        exoPlayer.prepare(mediaSource);
        isInitialized = true;

    }

    public PlaybackParameters getParams() {
        return params;
    }

    public boolean isPlaying() {
        return exoPlayer.getPlayWhenReady();
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}*/
