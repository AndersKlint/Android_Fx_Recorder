package com.example.anders.wellactually;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TabHost;

import com.google.android.exoplayer2.PlaybackParameters;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private  String recordingPath;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean permissionToWriteAccepted;
    private boolean permissionToRecordAccepted;
    private Button playButton;
    private Button recordButton;
    private SeekBar pitchBar;
    private SeekBar speedBar;
    private TabHost tabHost;
    private XyPad xyPad;
    private TabLayout trackTabs;

    public static ProgressBar progressBar;

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
        trackTabs = findViewById(R.id.trackTabs);
        trackTabs.addOnTabSelectedListener(customTabListener);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordingPath = getExternalCacheDir().getAbsolutePath(); // order important, has to be done after permission
        progressBar = findViewById(R.id.progressBar);
        SoundMixer.addMultipleTracks(this,recordingPath, 4);
        SoundMixer.setCurrentTrack(0);
        xyPad = new XyPad((ImageView) findViewById(R.id.xyPadSeeker));

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
    }

    public void onPlay(View view) {
        if(SoundMixer.togglePlay())
            playButton.setText("Stop");
        else
            playButton.setText("Play");
    }

    public void onRecord(View view) {
        if(SoundMixer.toggleRecord()) {
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

    private SeekBar.OnSeekBarChangeListener customSeekbarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    PlaybackParameters params = SoundMixer.getParams();
                    SoundMixer.updateParams(new PlaybackParameters(params.speed,((float) (progress + 1) / 4)));
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
                    PlaybackParameters params = SoundMixer.getParams();
                    SoundMixer.updateParams(new PlaybackParameters((float) (progress + 1) / 4, params.pitch));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    private TabLayout.OnTabSelectedListener customTabListener =
            new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(tab.getPosition() == trackTabs.getTabCount() -1) // last tab is add new tab button
                creatNewTab(tab);
            else
            SoundMixer.setCurrentTrack(tab.getPosition());
            if(SoundMixer.currentTrackInitialized()) {
                playButton.setEnabled(true);
                if(SoundMixer.currentTrackPlaying())
                    playButton.setText("Stop"); //!!
                else {
                    playButton.setText("Play");
                    progressBar.setProgress(0);
                }
            }
            else {
                playButton.setEnabled(false);
                playButton.setText("Stop"); //!!
                progressBar.setProgress(0);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if(tab.getPosition() == trackTabs.getTabCount() -1)
                creatNewTab(tab);

        }

        private void creatNewTab(TabLayout.Tab tab) {
            trackTabs.addTab(trackTabs.newTab().setText("Ch" + " " + (tab.getPosition()+1)), trackTabs.getTabCount() - 1);
            SoundMixer.addTrack(getBaseContext()); // context=???? will this work?=??
            SoundMixer.setCurrentTrack(tab.getPosition()-1);
        }
    };

}
