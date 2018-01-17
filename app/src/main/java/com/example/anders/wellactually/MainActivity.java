package com.example.anders.wellactually;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private  String recordingPath;
    private SoundHandler soundHandler;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean permissionToWriteAccepted;
    private boolean permissionToRecordAccepted;
    private Button playButton;
    private Button recordButton;
    private SeekBar pitchBar;
    private SeekBar speedBar;
    private TabHost tabHost;
    private XyPad xyPad;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        initTabs();
        pitchBar = findViewById(R.id.seekBar);
        speedBar = findViewById(R.id.seekBar3);
        pitchBar.setOnSeekBarChangeListener(customSeekbarListener);
        speedBar.setOnSeekBarChangeListener(customSeekbarListenerSpeed);
        playButton = findViewById(R.id.button2);
        playButton.setEnabled(false);
        recordButton =  findViewById(R.id.button_record);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordingPath = getExternalCacheDir().getAbsolutePath() + "mock_recording.3gp"; // order important, has to be done after permission

        soundHandler = new SoundHandler(recordingPath, (ProgressBar) findViewById(R.id.progressBar));
        xyPad = new XyPad((ImageView) findViewById(R.id.xyPadSeeker),soundHandler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        xyPad.onClick(view);
    }

    public void resetXyPad(View view) {
        xyPad.resetXyPad();
        soundHandler.updateParams();
    }

    public void onPlay(View view) {
        if(soundHandler.tryPlay())
            playButton.setText("Stop");
        else
            playButton.setText("Play");
    }

    public void onRecord(View view) {
        if(soundHandler.tryRecord()) {
            playButton.setEnabled(false);
            playButton.setText("Play");
            recordButton.setText("Stop");
        }
        else {
            recordButton.setText("Record");
            playButton.setText("Play");
            playButton.setEnabled(true);
        }
    }

    public void seekBarPitch(View view, float value) {

    }

    private SeekBar.OnSeekBarChangeListener customSeekbarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    soundHandler.getParams().setPitch((float) (progress + 1) / 4);
                    soundHandler.updateParams();
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
                    soundHandler.getParams().setSpeed((float) (progress + 1) / 4);
                    soundHandler.updateParams();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

}
