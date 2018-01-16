package com.example.anders.wellactually;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TabHost;

import java.io.IOException;

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
    private TabHost tabHost;
    private ImageView xySeeker;

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
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        initTabs();
        xySeeker = findViewById(R.id.xyPadSeeker);
        mp = new MediaPlayer();
        params = new PlaybackParams();
        playButton = (Button) findViewById(R.id.button2);
        recordButton = (Button) findViewById(R.id.button_record);
        pitchBar = (SeekBar) findViewById(R.id.seekBar);
        speedBar = (SeekBar) findViewById(R.id.seekBar3);
        playButton.setEnabled(false);
        pitchBar.setOnSeekBarChangeListener(customSeekbarListener);
        speedBar.setOnSeekBarChangeListener(customSeekbarListenerSpeed);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void initTabs() {
        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Sliders");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Sliders");
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec("XY-Pad");
        spec.setContent(R.id.tab2);
        spec.setIndicator("XY-Pad");
        tabHost.addTab(spec);
    }

    public void xyPadOnClick(View view) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                float seekerHalfWidth = xySeeker.getWidth()/2;
                float seekerHalfHeight = xySeeker.getHeight()/2;
                if (x <= 0)  // handles out of bounds
                    x = seekerHalfWidth;
                else if (x > v.getWidth() - seekerHalfWidth)
                    x = v.getWidth() - seekerHalfWidth;
                if (y <= 0)
                    y = seekerHalfHeight;
                else if (y > v.getHeight() - seekerHalfHeight)
                    y = v.getHeight() - seekerHalfHeight;
                float xScale = x / v.getWidth();
                float yScale = y / v.getHeight();
                boolean updated = false;
                xySeeker.setX(x - seekerHalfWidth);  // to fix origin to center
                xySeeker.setY(y - seekerHalfHeight);
                if (xScale > 0.05 && xScale < 0.95) { // safe limits for rounding
                    params.setSpeed(xScale * 2);
                    updated = true;
                }
                if (yScale > 0.05 && yScale < 0.95) {
                    params.setPitch((1 - yScale) * 2);
                    updated = true;
                }
                if (updated)
                    updateParams();
                return true;
            }
        });
    }

    public void resetXyPad(View view) {
        xySeeker.setX(findViewById(R.id.xyPad).getWidth()/2 - xySeeker.getWidth()/2);
        xySeeker.setY(findViewById(R.id.xyPad).getHeight()/2 - xySeeker.getHeight()/2);
        params.setSpeed(1f);
        params.setPitch(1f);
        updateParams();
    }

    private void updateParams() {
        if (mp != null)
            if (mp.isPlaying())
                mp.setPlaybackParams(params);
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
        updateParams();
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
                    params.setPitch((float) (progress + 1) / 4);
                    updateParams();
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
                    params.setSpeed((float) (progress + 1) / 4);
                    updateParams();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

}
