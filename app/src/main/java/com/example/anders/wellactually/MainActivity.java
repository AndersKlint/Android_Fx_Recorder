package com.example.anders.wellactually;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MediaPlayer mp;
    private PlaybackParams params;
    private boolean recording;
    private MediaRecorder mr;
    private boolean permissionToWriteAccepted;
    private boolean permissionToRecordAccepted;
    private Button playButton;
    private Button recordButton;
    private SeekBar pitchBar;
    private SeekBar speedBar;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted || !permissionToWriteAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = new MediaPlayer();
        params = new PlaybackParams();
        playButton = (Button) findViewById(R.id.button2);
        recordButton = (Button) findViewById(R.id.button_record);
        pitchBar = (SeekBar) findViewById(R.id.seekBar);
        speedBar = (SeekBar) findViewById(R.id.seekBar3);
        playButton.setEnabled(false);
        pitchBar.setEnabled(false);
        speedBar.setEnabled(false);
        pitchBar.setOnSeekBarChangeListener(customSeekbarListener);
        speedBar.setOnSeekBarChangeListener(customSeekbarListenerSpeed);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    public void onPlay(View view) {  // play button
        if (!mp.isPlaying())
            startPlaying();
        else
            stopPlaying();
    }

    private void startPlaying() {
        playButton.setText("Pause");
        try {
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        playButton.setText("Play");
        mp.stop();
    }

    public void onRecord(View view) {
        if (!recording)
            startRecording();
        else
            stopRecording();
    }

    private void startRecording() {
        if (mp.isPlaying())
            stopPlaying();
        playButton.setEnabled(false);
        recordButton.setText("Stop");
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setOutputFile(getExternalCacheDir().getAbsolutePath() + "mock_recording.3gp");
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mr.prepare();
        } catch (IOException e) {
            Log.e("Audio recording:", "prepare() failed");
        }
        mr.start();
        recording = true;
    }

    private void stopRecording() {
        recordButton.setText("Record");
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
            initMediaPlayer();
        }
        playButton.setEnabled(true);
        pitchBar.setEnabled(true);
        speedBar.setEnabled(true);
        recording = false;
    }

    private void initMediaPlayer() {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(getExternalCacheDir().getAbsolutePath() + "mock_recording.3gp");
            mp.setLooping(true);
            mp.setPlaybackParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seekBarPitch(View view, float value) {

    }

    private SeekBar.OnSeekBarChangeListener customSeekbarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    mp.setPlaybackParams(params.setPitch((float) (progress + 1) / 4));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    private SeekBar.OnSeekBarChangeListener customSeekbarListenerSpeed =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    mp.setPlaybackParams(params.setSpeed((float) (progress + 1) / 4));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

}
