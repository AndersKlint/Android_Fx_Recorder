package com.example.anders.wellactually;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String recordingPath;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean permissionToWriteAccepted;
    private boolean permissionToRecordAccepted;
    private SeekBar pitchBar;
    private SeekBar speedBar;
    private TabHost tabHost;
    private XyPad xyPad;
    private TabLayout trackTabs;
    private Spinner bpmSpinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;

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
        Button playButton = findViewById(R.id.button2);
        playButton.setEnabled(false);
        Button recordButton = findViewById(R.id.button_record);
        PlaybackButtons playbackButtons = new PlaybackButtons(playButton,recordButton);
        trackTabs = findViewById(R.id.trackTabs);
        trackTabs.addOnTabSelectedListener(customTabListener);
        trackTabs.setClickable(false);

        bpmSpinner = findViewById(R.id.bpmSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.bpm_dropdown, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        bpmSpinner.setAdapter(spinnerAdapter);
        bpmSpinner.setOnItemSelectedListener(customSpinnerListener);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordingPath = getExternalCacheDir().getAbsolutePath();  // order important, has to be done after permission
        AudioProgressBar audioProgressBar = new AudioProgressBar((ProgressBar) findViewById(R.id.progressBar));
        SoundMixer.init(this, recordingPath, 4, playbackButtons, audioProgressBar, trackTabs);
        SoundMixer.setCurrentTrack(0);
        xyPad = new XyPad((ImageView) findViewById(R.id.xyPadSeeker), findViewById(R.id.xyPad));

        EditText barsText = findViewById(R.id.barsText);
        barsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() <= 0)
                    text = "1";
                SoundMixer.setCurrentBars(Integer.parseInt(text));
            }
        });

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

    public void metronomeCheckBoxOnClick(View view) {
        SoundMixer.setUseMetronome(((CheckBox) view).isChecked());
    }

    public void resetXyPad(View view) {
        xyPad.resetXyPad();
    }

    public void onPlay(View view) {
        SoundMixer.togglePlay();

    }

    public void onRecord(View view) {
        SoundMixer.toggleRecord();

    }

    private void showBpmDialog() {
        LayoutInflater li = LayoutInflater.from(getBaseContext());
        View promptsView = li.inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(promptsView);
        adb.setTitle("Custom BPM");
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.dialogTextField);
        //adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String inputText = userInput.getText().toString();
                if (Integer.valueOf(inputText) < 10)
                    inputText = "10";
                else if(Integer.valueOf(inputText) > 500)
                    inputText = "500";
                SoundMixer.setBpm(inputText);
         //       spinnerAdapter.add(inputText);
       //         spinnerAdapter.notifyDataSetChanged();
            //    Spinner temp = findViewById(R.id.bpmSpinner);
           //     temp.setSelection(spinnerAdapter.getCount()-1);
            }
        });


        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        adb.show();
    }

    private AdapterView.OnItemSelectedListener customSpinnerListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (parent.getItemAtPosition(position).toString().equals("Custom")) {
                        showBpmDialog();
                    }
                    else
                        SoundMixer.setBpm((String) parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    private SeekBar.OnSeekBarChangeListener customSeekbarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                    SoundMixer.setCurrentPitch((progress + 1) / 4);
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
                    SoundMixer.setCurrentSpeed((float) (progress + 1) / 4);
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
                    if (SoundMixer.CURRENT_STATE == SoundMixer.STATE_RECORDING) {

                    }
                    else {

                        if (tab.getPosition() == trackTabs.getTabCount() - 1) // last tab is add new tab button
                            createNewTab(tab);
                        else
                            SoundMixer.setCurrentTrack(tab.getPosition());
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == trackTabs.getTabCount() - 1)
                        createNewTab(tab);

                }

                private void createNewTab(TabLayout.Tab tab) {
                    trackTabs.addTab(trackTabs.newTab().setText("Ch" + " " + (tab.getPosition() + 1)), trackTabs.getTabCount() - 1);
                    SoundMixer.addTrack(getBaseContext()); // context=???? will this work?=??
                    SoundMixer.setCurrentTrack(tab.getPosition() - 1);
                }
            };

}
